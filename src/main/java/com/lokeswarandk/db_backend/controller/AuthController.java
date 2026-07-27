package com.lokeswarandk.db_backend.controller;

import com.lokeswarandk.db_backend.common.ApiResponseBuilder;
import com.lokeswarandk.db_backend.dto.request.LoginRequest;
import com.lokeswarandk.db_backend.dto.response.CurrentUserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository;

    private final SecurityContextLogoutHandler logoutHandler;

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    public AuthController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            SecurityContextLogoutHandler logoutHandler) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.logoutHandler = logoutHandler;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        Authentication authentication =
                authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated(
                                request.getUsername(), request.getPassword()));

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        return ResponseEntity.ok(toCurrentUserResponse(authentication));
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(
            Authentication authentication,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        logoutHandler.logout(httpRequest, httpResponse, authentication);

        return ResponseEntity.ok(ApiResponseBuilder.messagePayload("Logout successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<Object> me(Authentication authentication) {
        return ResponseEntity.ok(toCurrentUserResponse(authentication));
    }

    private static CurrentUserResponse toCurrentUserResponse(Authentication authentication) {
        String role =
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .findFirst()
                        .map(AuthController::stripRolePrefix)
                        .orElse(null);

        return new CurrentUserResponse(authentication.getName(), role);
    }

    private static String stripRolePrefix(String authority) {
        return authority.startsWith("ROLE_") ? authority.substring("ROLE_".length()) : authority;
    }
}
