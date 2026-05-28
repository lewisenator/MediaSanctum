package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.UserResponse;
import com.media_sanctum.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<DataResponse<List<UserResponse>>> getUsers() {
        var users = userService.getUsers();
        var usersForResponse = users.stream().map(user ->
                UserResponse.builder()
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .build()
        ).toList();
        return ResponseEntity.ok(DataResponse.data(usersForResponse));
    }
}
