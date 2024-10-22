package com.example.rolepermission.service;

import com.example.rolepermission.dto.request.RoleRequest;
import com.example.rolepermission.dto.response.RoleResponse;
import com.example.rolepermission.dto.response.UserResponse;
import com.example.rolepermission.entity.Role;
import com.example.rolepermission.exception.AppException;
import com.example.rolepermission.exception.ErrorCode;
import com.example.rolepermission.mapper.RoleMapper;
import com.example.rolepermission.repository.PermissionRepository;
import com.example.rolepermission.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }

    public RoleResponse updateRole(@PathVariable String roleId, @RequestBody RoleRequest request){
        Role role = roleRepository.findById(roleId).orElseThrow(()-> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        roleMapper.updateRole(role, request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public RoleResponse getRoleById(String id){
        return roleMapper.toRoleResponse(roleRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public List<Role> getAllRoles(){
        return roleRepository.findAll();
    }
}
