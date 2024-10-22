package com.example.rolepermission.mapper;

import com.example.rolepermission.dto.request.PermissionRequest;
import com.example.rolepermission.dto.response.PermissionResponse;
import com.example.rolepermission.entity.Permission;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}