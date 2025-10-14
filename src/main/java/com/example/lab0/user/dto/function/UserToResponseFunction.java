package com.example.lab0.user.dto.function;

import com.example.lab0.User;
import com.example.lab0.user.dto.GetUserResponse;

import java.util.function.Function;

public class UserToResponseFunction implements Function<User, GetUserResponse> {
    @Override
    public GetUserResponse apply(User user) {
        return GetUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .registrationDate(user.getRegistrationDate())
                .build();
    }
}