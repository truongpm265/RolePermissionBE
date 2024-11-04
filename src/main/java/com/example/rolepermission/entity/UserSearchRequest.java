package com.example.rolepermission.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest {
    private String name;
    private String email;
    private String role;

    private Integer page = 0;
    private Integer size = 10;
}
