package com.lokeswarandk.db_backend.service;

import com.lokeswarandk.db_backend.common.StringUtils;
import com.lokeswarandk.db_backend.dto.request.UpsertUserRequest;
import com.lokeswarandk.db_backend.dto.response.MobilePrefixSearchResponse;
import com.lokeswarandk.db_backend.dto.response.UserResponse;
import com.lokeswarandk.db_backend.exception.ResourceNotFoundException;
import com.lokeswarandk.db_backend.mapper.UserMapper;
import com.lokeswarandk.db_backend.model.User;
import com.lokeswarandk.db_backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public static final int MOBILE_PREFIX_MIN_LENGTH = 2;
    public static final int MOBILE_SEARCH_DEFAULT_LIMIT = 5;
    public static final int MOBILE_SEARCH_MAX_LIMIT = 10;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse create(UpsertUserRequest request) {
        User user = UserMapper.toEntity(request);
        user.setId(null);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        return UserMapper.toResponse(userRepository.save(user));
    }

    public UserResponse findById(Long id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> ResourceNotFoundException.forResourceWithId("User", id));
        return UserMapper.toResponse(user);
    }

    public List<UserResponse> findAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return UserMapper.toResponseList(users);
    }

    public List<UserResponse> findByMobileNo(String mobileNo) {
        return UserMapper.toResponseList(
                userRepository.findByMobileNo(StringUtils.requireNonBlank(mobileNo, "mobile")));
    }

    public MobilePrefixSearchResponse searchMobileNosByPrefix(String prefix, Integer limit) {
        String normalizedPrefix = StringUtils.requireNonBlank(prefix, "prefix");
        if (normalizedPrefix.length() < MOBILE_PREFIX_MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "prefix must be at least " + MOBILE_PREFIX_MIN_LENGTH + " characters");
        }
        List<String> mobileNos =
                userRepository.findDistinctMobileNosByPrefix(normalizedPrefix, resolveLimit(limit));
        return new MobilePrefixSearchResponse(mobileNos);
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

    public UserResponse update(Long id, UpsertUserRequest request) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> ResourceNotFoundException.forResourceWithId("User", id));
        UserMapper.applyFields(user, request);
        return UserMapper.toResponse(userRepository.save(user));
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException.forResourceWithId("User", id);
        }
        userRepository.deleteById(id);
    }
}
