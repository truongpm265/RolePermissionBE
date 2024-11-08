package com.example.rolepermission.service;

import java.util.List;

import com.example.rolepermission.dto.request.PermissionRequest;
import com.example.rolepermission.dto.response.PermissionResponse;
import com.example.rolepermission.entity.Permission;
import com.example.rolepermission.exception.AppException;
import com.example.rolepermission.exception.ErrorCode;
import com.example.rolepermission.mapper.PermissionMapper;
import com.example.rolepermission.repository.AppFunctionRepository;
import com.example.rolepermission.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    @Autowired
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;
    AppFunctionRepository functionRepository;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }

    public PermissionResponse update(String name, PermissionRequest request) {

        Permission permission = permissionRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        permission.setName(request.getName());

        permission = permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

//    public List<Permission> getPermissionsByFunctionId(Long functionId) {
//        List<Permission> permissions = permissionRepository.findPermissionsByFunctionId(functionId);
//
//        return permissions;
//    }

}
