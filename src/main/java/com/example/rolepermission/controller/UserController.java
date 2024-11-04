package com.example.rolepermission.controller;

import com.example.rolepermission.dto.request.UserCreationRequest;
import com.example.rolepermission.dto.request.UserUpdateRequest;
import com.example.rolepermission.dto.response.UserDetailsDTO;
import com.example.rolepermission.dto.response.UserDetailsResponse;
import com.example.rolepermission.dto.response.UserResponse;
import com.example.rolepermission.entity.User;
import com.example.rolepermission.entity.UserSearchRequest;
import com.example.rolepermission.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    UserService userService;

    @PreAuthorize("hasAuthority('MANAGE_USER') and hasAuthority('CREATE_USER')")
    @PostMapping("/create-user")
    ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

//    @PreAuthorize("hasAuthority('VIEW_USER')")
    @GetMapping
    ResponseEntity<List<UserResponse>> getAllUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.getUsers());
    }

    @PreAuthorize("hasAuthority('VIEW_USER')")
    @GetMapping("/{userId}")
    ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PreAuthorize("hasAuthority('MANAGE_USER') and hasAuthority('EDIT_USER')")
    @PutMapping("/update/{userId}")
    ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @PreAuthorize("hasAuthority('MANAGE_USER') and hasAuthority('DELETE_USER')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Xóa thành công"));
    }

    @GetMapping("/user-details")
    public ResponseEntity<UserDetailsResponse> getUserDetails() {
        var user = userService.getUserDetails();
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        userDetailsResponse.setUsername(user.getUsername());
        userDetailsResponse.setRoles(user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()));
        userDetailsResponse.setFunctions(user.getRoles().stream()
                .flatMap(role -> role.getFunctions().stream())
                .collect(Collectors.toSet()));
        return ResponseEntity.ok(userDetailsResponse);
    }

    @GetMapping("/searchSQL")
    public Page<User> searchStudents(UserSearchRequest request, Pageable pageable) {
        return userService.search(request, pageable);
    }

    @PostMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestBody UserSearchRequest searchRequest) {
        List<User> users = userService.searchUsers(searchRequest.getName(), searchRequest.getEmail(), searchRequest.getRole());
        return ResponseEntity.ok(users);
    }


}
