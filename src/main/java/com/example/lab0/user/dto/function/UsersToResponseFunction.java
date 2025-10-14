package com.example.lab0.user.dto.function;

import com.example.lab0.User;
import com.example.lab0.user.dto.GetUsersResponse;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UsersToResponseFunction implements Function<List<User>, GetUsersResponse> {
    @Override
    public GetUsersResponse apply(List<User> users) {
        return GetUsersResponse.builder()
                .users(users.stream()
                        .map(user -> GetUsersResponse.User.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}