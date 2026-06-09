#!/usr/bin/env bash
# Quiet Maven checks. Captures build output internally; prints a one-line
# summary on success or a short failure excerpt on error.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
LOG="$(mktemp)"
trap 'rm -f "$LOG"' EXIT

usage() {
    cat <<'EOF'
Usage: ./scripts/check.sh <command> [args]

Commands:
  compile                         Compile main sources (skip tests)
  test <TestClasses>              Run tests (-Dtest= comma-separated class names)
  spotless                        Check Java/YAML formatting (Spotless)
  verify                          Full gate: tests + spotless (run before finishing)

Examples:
  ./scripts/check.sh compile
  ./scripts/check.sh test UserServiceTests
  ./scripts/check.sh test UserControllerTests,UserServiceTests
  ./scripts/check.sh verify
EOF
}

run_maven() {
    (cd "$PROJECT_ROOT" && ./mvnw -q -B "$@" >"$LOG" 2>&1)
}

aggregate_surefire_summary() {
    local reports_dir="$PROJECT_ROOT/target/surefire-reports"
    local total=0 failures=0 errors=0 skipped=0

    if [[ ! -d "$reports_dir" ]]; then
        echo "0 tests, 0 failures"
        return
    fi

    local line
    while IFS= read -r line; do
        [[ "$line" =~ Tests\ run:\ ([0-9]+),\ Failures:\ ([0-9]+),\ Errors:\ ([0-9]+),\ Skipped:\ ([0-9]+) ]] || continue
        total=$((total + BASH_REMATCH[1]))
        failures=$((failures + BASH_REMATCH[2]))
        errors=$((errors + BASH_REMATCH[3]))
        skipped=$((skipped + BASH_REMATCH[4]))
    done < <(grep -h '^Tests run:' "$reports_dir"/*.txt 2>/dev/null || true)

    echo "$total tests, $((failures + errors)) failures"
}

clear_surefire_reports() {
    rm -rf "$PROJECT_ROOT/target/surefire-reports"
}

print_compile_failure() {
    echo "FAILED: compile"
    grep -E '\[ERROR\]' "$LOG" | head -20 || tail -20 "$LOG"
}

print_test_failure() {
    echo "FAILED: test"
    local reports_dir="$PROJECT_ROOT/target/surefire-reports"
    local report

    if [[ -d "$reports_dir" ]]; then
        for report in "$reports_dir"/*.txt; do
            [[ -f "$report" ]] || continue
            if grep -qE 'Failures: [1-9]|Errors: [1-9]' "$report"; then
                echo "--- $(basename "$report" .txt) ---"
                sed -n '/^Tests run:/,$p' "$report" | head -40
            fi
        done
    fi

    grep -E '\[ERROR\]|BUILD FAILURE|There are test failures' "$LOG" | head -20 || true
}

print_spotless_failure() {
    echo "FAILED: spotless"
    grep -Ei 'violat|not formatted|spotless|BUILD FAILURE|\[ERROR\]' "$LOG" | head -20 || tail -20 "$LOG"
}

print_verify_failure() {
    if grep -qi 'spotless' "$LOG"; then
        print_spotless_failure
        return
    fi
    print_test_failure
}

COMMAND="${1:-}"
shift || true

case "$COMMAND" in
    compile)
        if run_maven -DskipTests compile; then
            echo "compile: OK"
        else
            print_compile_failure
            exit 1
        fi
        ;;
    test)
        TEST_CLASSES="${1:-}"
        if [[ -z "$TEST_CLASSES" ]]; then
            echo "error: test requires at least one test class name" >&2
            usage
            exit 1
        fi
        clear_surefire_reports
        if run_maven test "-Dtest=$TEST_CLASSES"; then
            echo "test: BUILD SUCCESS, $(aggregate_surefire_summary)"
        else
            print_test_failure
            exit 1
        fi
        ;;
    spotless)
        if run_maven spotless:check; then
            echo "spotless: OK"
        else
            print_spotless_failure
            exit 1
        fi
        ;;
    verify)
        clear_surefire_reports
        if run_maven verify; then
            echo "verify: BUILD SUCCESS, $(aggregate_surefire_summary)"
        else
            print_verify_failure
            exit 1
        fi
        ;;
    -h | --help | help | "")
        usage
        [[ -n "$COMMAND" ]] || exit 1
        ;;
    *)
        echo "error: unknown command '$COMMAND'" >&2
        usage
        exit 1
        ;;
esac
