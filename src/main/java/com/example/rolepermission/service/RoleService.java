package com.example.rolepermission.service;

import com.example.rolepermission.dto.request.RoleRequest;
import com.example.rolepermission.dto.response.RoleResponse;
import com.example.rolepermission.entity.AppFunction;
import com.example.rolepermission.entity.Role;
import com.example.rolepermission.exception.AppException;
import com.example.rolepermission.exception.ErrorCode;
import com.example.rolepermission.mapper.RoleMapper;
import com.example.rolepermission.repository.AppFunctionRepository;
import com.example.rolepermission.repository.PermissionRepository;
import com.example.rolepermission.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class RoleService {
    private final RoleRepository roleRepository;
    private final AppFunctionRepository functionRepository;
    private final RoleMapper roleMapper;


    @Transactional
    public RoleResponse create(RoleRequest request) {

        var role = roleMapper.toRole(request);

        var functions = functionRepository.findAllById(request.getFunctions());

        if (functions.isEmpty()) {
            throw new IllegalArgumentException("Invalid function IDs provided");
        }

        role.setFunctions(new HashSet<>(functions));

        role = roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }
    public RoleResponse edit(String name, RoleRequest request) {

        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        // Cập nhật tên role từ request
        if (request.getName() != null) {
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }


        var functions = functionRepository.findAllById(request.getFunctions());

        if (functions.isEmpty() || functions.size() != request.getFunctions().size()) {
            throw new IllegalArgumentException("Invalid function IDs provided");
        }

        role.setFunctions(new HashSet<>(functions));

        role = roleRepository.save(role);


        return roleMapper.toRoleResponse(role);
    }

    @Transactional
    public void delete(String name) {
        if (!roleRepository.existsById(name)) {
            throw new AppException(ErrorCode.ROLE_NOT_EXISTED);
        }
        roleRepository.deleteById(name);
    }

    public RoleResponse getByName(String name) {

        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        return roleMapper.toRoleResponse(role);
    }
}