package com.lokeswarandk.db_backend.controller;

import com.lokeswarandk.db_backend.common.ApiResponseBuilder;
import com.lokeswarandk.db_backend.model.User;
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

    private static final String USER_NOT_FOUND = "User not found";
    private static final String USER_ID_PREFIX = "No user with id ";
    private static final String USER_ID_SUFFIX = " exists";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody User user) {
        User savedUser = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/search/mobile")
    public ResponseEntity<Object> searchMobileByPrefix(
            @RequestParam String prefix, @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(userService.searchMobileNosByPrefix(prefix, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        return userService
                .findById(id)
                .map(user -> ResponseEntity.ok((Object) user))
                .orElseGet(
                        () ->
                                ApiResponseBuilder.error(
                                        HttpStatus.NOT_FOUND,
                                        USER_NOT_FOUND,
                                        userNotFoundMessage(id)));
    }

    @GetMapping
    public ResponseEntity<Object> listUsers(@RequestParam(required = false) String mobile) {
        if (mobile != null && !mobile.isBlank()) {
            return ResponseEntity.ok(userService.findByMobileNo(mobile));
        }
        return ResponseEntity.ok(userService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(
            @PathVariable Long id, @Valid @RequestBody User updatedUser) {
        return userService
                .update(id, updatedUser)
                .map(user -> ResponseEntity.ok((Object) user))
                .orElseGet(
                        () ->
                                ApiResponseBuilder.error(
                                        HttpStatus.NOT_FOUND,
                                        USER_NOT_FOUND,
                                        userNotFoundMessage(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        if (!userService.deleteById(id)) {
            return ApiResponseBuilder.error(
                    HttpStatus.NOT_FOUND, USER_NOT_FOUND, userNotFoundMessage(id));
        }
        return ResponseEntity.ok(
                ApiResponseBuilder.messagePayload("User deleted successfully", "id", id));
    }

    private String userNotFoundMessage(Long id) {
        return USER_ID_PREFIX + id + USER_ID_SUFFIX;
    }
}
