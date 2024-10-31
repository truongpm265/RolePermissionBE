package com.example.rolepermission.dto.response;

import com.example.rolepermission.entity.AppFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private String username;
    private List<String> roles;
    private List<AppFunction> functions; // Giả sử bạn cũng có AppFunctionDTO
}