package com.media_sanctum.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserModel {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
