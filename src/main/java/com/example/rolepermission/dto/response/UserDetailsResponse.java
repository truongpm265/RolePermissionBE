package com.example.rolepermission.dto.response;

import com.example.rolepermission.entity.AppFunction;
import lombok.Data;

import java.util.Set;

@Data
public class UserDetailsResponse {
    private String username;
    private Set<String> roles;
    private Set<AppFunction> functions;
}
