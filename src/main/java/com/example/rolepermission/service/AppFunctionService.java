package com.example.rolepermission.service;

import com.example.rolepermission.dto.request.AppFunctionRequest;
import com.example.rolepermission.dto.response.AppFunctionResponse;
import com.example.rolepermission.entity.AppFunction;
import com.example.rolepermission.entity.Permission;
import com.example.rolepermission.exception.AppException;
import com.example.rolepermission.exception.ErrorCode;
import com.example.rolepermission.mapper.AppFunctionMapper;
import com.example.rolepermission.mapper.RoleMapper;
import com.example.rolepermission.repository.AppFunctionRepository;
import com.example.rolepermission.repository.PermissionRepository;
import com.example.rolepermission.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class AppFunctionService {
    private final PermissionRepository permissionRepository;
    private final AppFunctionRepository appFunctionRepository;
    private final AppFunctionMapper appFunctionMapper;


    @Transactional
    public AppFunctionResponse create(AppFunctionRequest request) {
        AppFunction appFunction = appFunctionMapper.toAppFunction(request);
        if (request.getPermissions() != null) {
            var permissions = permissionRepository.findAllById(request.getPermissions());
            appFunction.setPermissions(new HashSet<>(permissions));
        }
        appFunction = appFunctionRepository.save(appFunction);
        return appFunctionMapper.toAppFunctionResponse(appFunction);
    }

    public List<AppFunctionResponse> getAllFunctions() {
        List<AppFunction> functions = appFunctionRepository.findAll();
        return functions.stream()
                .map(appFunctionMapper::toAppFunctionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppFunctionResponse update(Long id, AppFunctionRequest request) {

        AppFunction appFunction = appFunctionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APP_FUNCTION_NOT_FOUND));

        appFunction.setName(request.getName());

        if (request.getPermissions() != null) {
            var permissions = permissionRepository.findAllById(request.getPermissions());
            appFunction.setPermissions(new HashSet<>(permissions));
        }
        appFunction = appFunctionRepository.save(appFunction);

        return appFunctionMapper.toAppFunctionResponse(appFunction);
    }

    @Transactional
    public void delete(Long id) {

        if (!appFunctionRepository.existsById(id)) {
            throw new AppException(ErrorCode.APP_FUNCTION_NOT_FOUND);
        }
        appFunctionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByFunctionId(Long functionId) {
        AppFunction appFunction = appFunctionRepository.findById(functionId)
                .orElseThrow(() -> new AppException(ErrorCode.APP_FUNCTION_NOT_FOUND));

        // Trả về danh sách các permission
        return new ArrayList<>(appFunction.getPermissions());
    }
}
