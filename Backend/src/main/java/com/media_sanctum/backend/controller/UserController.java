package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.model.CreateUserModel;
import com.media_sanctum.backend.model.UpdateUserModel;
import com.media_sanctum.backend.model.UserModel;
import com.media_sanctum.backend.resource.CreateUserRequest;
import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.ErrorResponse;
import com.media_sanctum.backend.resource.UpdateUserRequest;
import com.media_sanctum.backend.resource.UserDetailResponse;
import com.media_sanctum.backend.resource.UserResponse;
import com.media_sanctum.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
        var users = userService.getUserModelss();
        var usersForResponse = users.stream().map(user ->
                UserResponse.builder()
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .build()
        ).toList();
        return ResponseEntity.ok(DataResponse.data(usersForResponse));
    }

    @PostMapping
    public ResponseEntity<DataResponse<UserDetailResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        if (userService.getUserModelByEmail(request.getEmail()).isPresent()) {
            return emailTakenError(request.getEmail());
        }
        var created = userService.createUser(CreateUserModel.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse.data(toDetailResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResponse<UserDetailResponse>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        if (userService.getUserModelById(id).isEmpty()) {
            var error = ErrorResponse.builder()
                    .error("USER_NOT_FOUND")
                    .message("User with id %s not found".formatted(id))
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            DataResponse<UserDetailResponse> dataResponse = DataResponse.error(error);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dataResponse);
        }
        if (request.getEmail() != null) {
            var owner = userService.getUserModelByEmail(request.getEmail());
            if (owner.isPresent() && !owner.get().getId().equals(id)) {
                return emailTakenError(request.getEmail());
            }
        }
        var updated = userService.updateUser(UpdateUserModel.builder()
                .id(id)
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build());
        return ResponseEntity.ok(DataResponse.data(toDetailResponse(updated)));
    }

    private ResponseEntity<DataResponse<UserDetailResponse>> emailTakenError(String email) {
        var error = ErrorResponse.builder()
                .error("EMAIL_ALREADY_EXISTS")
                .message("A user with email %s already exists".formatted(email))
                .timestamp(LocalDateTime.now().toString())
                .build();
        DataResponse<UserDetailResponse> dataResponse = DataResponse.error(error);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(dataResponse);
    }

    private static UserDetailResponse toDetailResponse(UserModel user) {
        return UserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
