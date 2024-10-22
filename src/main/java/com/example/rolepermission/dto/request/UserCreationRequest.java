package com.example.rolepermission.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 2, message = "USERNAME_INVALID")
    String username;

    @Size(min = 2, message = "INVALID_PASSWORD")
    String password;

    String email;
}