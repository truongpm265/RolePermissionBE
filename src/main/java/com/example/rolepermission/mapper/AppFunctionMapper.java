package com.example.rolepermission.mapper;

import com.example.rolepermission.dto.request.AppFunctionRequest;
import com.example.rolepermission.dto.request.RoleRequest;
import com.example.rolepermission.dto.response.AppFunctionResponse;
import com.example.rolepermission.dto.response.RoleResponse;
import com.example.rolepermission.entity.AppFunction;
import com.example.rolepermission.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;


@Mapper(componentModel = "spring")
public interface AppFunctionMapper {
    @Mapping(target = "permissions", ignore = true)
    AppFunction toAppFunction(AppFunctionRequest request);

     AppFunctionResponse toAppFunctionResponse(AppFunction appFunction);

    List<AppFunctionResponse> toFunctionResponseList(List<AppFunction> appFunctions);

}
