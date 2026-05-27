package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.model.DataResponse;
import com.media_sanctum.backend.model.UserResponse;
import com.media_sanctum.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<DataResponse<List<UserResponse>>> getUsers() {
        var allUserEntities = userRepository.findAll();
        var users = allUserEntities.stream().map(entity ->
                UserResponse.builder()
                    .email(entity.getEmail())
                    .firstName(entity.getFirstName())
                    .lastName(entity.getLastName())
                    .build()
        ).toList();
        return ResponseEntity.ok(DataResponse.data(users));
    }
}
