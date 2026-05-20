package com.lokeswarandk.db_backend.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lokeswarandk.db_backend.common.ApiResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lokeswarandk.db_backend.model.User;
import com.lokeswarandk.db_backend.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final String USER_NOT_FOUND = "User not found";
    private static final String USER_ID_PREFIX = "No user with id ";
    private static final String USER_ID_SUFFIX = " exists";

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody User user) {
        // Ensure DB-generated ID is used for create.
        user.setId(null);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ApiResponseBuilder.error(HttpStatus.NOT_FOUND, USER_NOT_FOUND, userNotFoundMessage(id));
        }
        return ResponseEntity.ok(user.get());
    }

    @GetMapping
    public ResponseEntity<Object> listUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            return ApiResponseBuilder.error(HttpStatus.NOT_FOUND, USER_NOT_FOUND, userNotFoundMessage(id));
        }

        updatedUser.setId(id);
        User savedUser = userRepository.save(updatedUser);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ApiResponseBuilder.error(HttpStatus.NOT_FOUND, USER_NOT_FOUND, userNotFoundMessage(id));
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponseBuilder.messagePayload("User deleted successfully", "id", id));
    }

    private String userNotFoundMessage(Long id) {
        return USER_ID_PREFIX + id + USER_ID_SUFFIX;
    }
}
