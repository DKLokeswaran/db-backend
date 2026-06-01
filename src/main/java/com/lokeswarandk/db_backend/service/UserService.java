package com.lokeswarandk.db_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lokeswarandk.db_backend.common.StringUtils;
import com.lokeswarandk.db_backend.model.User;
import com.lokeswarandk.db_backend.repository.UserRepository;

@Service
public class UserService {

    public static final int MOBILE_PREFIX_MIN_LENGTH = 2;
    public static final int MOBILE_SEARCH_DEFAULT_LIMIT = 5;
    public static final int MOBILE_SEARCH_MAX_LIMIT = 10;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        user.setId(null);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public List<User> findByMobileNo(String mobileNo) {
        return userRepository.findByMobileNo(StringUtils.requireNonBlank(mobileNo, "mobile"));
    }

    public List<String> searchMobileNosByPrefix(String prefix, Integer limit) {
        String normalizedPrefix = StringUtils.requireNonBlank(prefix, "prefix");
        if (normalizedPrefix.length() < MOBILE_PREFIX_MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "prefix must be at least " + MOBILE_PREFIX_MIN_LENGTH + " characters");
        }
        return userRepository.findDistinctMobileNosByPrefix(normalizedPrefix, resolveLimit(limit));
    }

    private int resolveLimit(Integer limit) {
        if (limit == null) {
            return MOBILE_SEARCH_DEFAULT_LIMIT;
        }
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be at least 1");
        }
        return Math.min(limit, MOBILE_SEARCH_MAX_LIMIT);
    }

    public Optional<User> update(Long id, User updatedUser) {
        if (userRepository.findById(id).isEmpty()) {
            return Optional.empty();
        }
        updatedUser.setId(id);
        return Optional.of(userRepository.save(updatedUser));
    }

    public boolean deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}
