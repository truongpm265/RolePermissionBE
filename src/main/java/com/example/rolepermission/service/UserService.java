package com.example.rolepermission.service;

import com.example.rolepermission.dto.request.UserCreationRequest;
import com.example.rolepermission.dto.request.UserUpdateRequest;
import com.example.rolepermission.dto.response.UserDetailsDTO;
import com.example.rolepermission.dto.response.UserResponse;

import com.example.rolepermission.entity.AppFunction;
import com.example.rolepermission.entity.Role;
import com.example.rolepermission.entity.User;
import com.example.rolepermission.exception.AppException;
import com.example.rolepermission.exception.ErrorCode;
import com.example.rolepermission.mapper.UserMapper;
import com.example.rolepermission.repository.RoleRepository;
import com.example.rolepermission.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserMapper userMapper;

    public UserResponse createUser(UserCreationRequest request) {
        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById("USER").ifPresent(roles::add);
        user.setRoles(roles);
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }


    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }


    public UserResponse getUserById(Long id){
        return userMapper.toUserResponse(userRepository.findById(id)
        .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }


    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }


    public void deleteUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        userRepository.delete(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public User getUserDetails() {
        User user = getCurrentUser();
        return user;
    }

    public UserDetailsDTO getUserDetailsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Chuyển đổi thành UserDetailsDTO
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName()) // Giả sử bạn có phương thức getName() trong Role
                .collect(Collectors.toList());

        List<AppFunction> functions = user.getRoles().stream()
                .flatMap(role -> role.getFunctions().stream()) // Lấy các function từ role
                .distinct() // Loại bỏ duplicates
                .map(appFunction -> new AppFunction(appFunction.getId(),appFunction.getName(), appFunction.getPermissions())) // Giả sử bạn có AppFunctionDTO
                .collect(Collectors.toList());

        return new UserDetailsDTO(username, roles, functions);
    }
}
