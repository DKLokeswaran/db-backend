package com.lokeswarandk.db_backend.controller;

import com.lokeswarandk.db_backend.common.ApiResponseBuilder;
import com.lokeswarandk.db_backend.dto.request.UpsertUserRequest;
import com.lokeswarandk.db_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UpsertUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @GetMapping("/search/mobile")
    public ResponseEntity<Object> searchMobileByPrefix(
            @RequestParam String prefix, @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(userService.searchMobileNosByPrefix(prefix, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Object> listUsers(@RequestParam(required = false) String mobile) {
        if (mobile != null) {
            return ResponseEntity.ok(userService.findByMobileNo(mobile));
        }
        return ResponseEntity.ok(userService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(
            @PathVariable Long id, @Valid @RequestBody UpsertUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(
                ApiResponseBuilder.messagePayload("User deleted successfully", "id", id));
    }
}
