package com.lokeswarandk.db_backend.mapper;

import com.lokeswarandk.db_backend.dto.request.UpsertUserRequest;
import com.lokeswarandk.db_backend.dto.response.UserResponse;
import com.lokeswarandk.db_backend.model.User;
import java.util.List;

public final class UserMapper {

    private UserMapper() {
        // Utility class.
    }

    public static User toEntity(UpsertUserRequest request) {
        User user = new User();
        applyFields(user, request);
        return user;
    }

    public static void applyFields(User user, UpsertUserRequest request) {
        user.setName(request.getName());
        user.setMobileNo(request.getMobileNo());
        user.setAddressLine(request.getAddressLine());
        user.setLocality(request.getLocality());
        user.setState(request.getState());
        user.setCountry(request.getCountry());
        user.setPincode(request.getPincode());
    }

    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setMobileNo(user.getMobileNo());
        response.setAddressLine(user.getAddressLine());
        response.setLocality(user.getLocality());
        response.setState(user.getState());
        response.setCountry(user.getCountry());
        response.setPincode(user.getPincode());
        return response;
    }

    public static List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(UserMapper::toResponse).toList();
    }
}
