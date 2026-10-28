package com.media_sanctum.backend.resource;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Every field is optional; null means "leave unchanged". */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @Email
    @Pattern(regexp = ".*\\S.*", message = "must not be blank")
    private String email;
    @Size(min = 8, max = 128)
    private String password;
    @Size(max = 100)
    private String firstName;
    @Size(max = 100)
    private String lastName;
}
