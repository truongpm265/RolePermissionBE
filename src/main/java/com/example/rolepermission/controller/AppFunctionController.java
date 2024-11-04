package com.example.rolepermission.controller;




import com.example.rolepermission.dto.request.AppFunctionRequest;
import com.example.rolepermission.dto.request.PermissionRequest;
import com.example.rolepermission.dto.request.RoleRequest;
import com.example.rolepermission.dto.request.UserUpdateRequest;
import com.example.rolepermission.dto.response.AppFunctionResponse;
import com.example.rolepermission.dto.response.PermissionResponse;
import com.example.rolepermission.dto.response.RoleResponse;
import com.example.rolepermission.dto.response.UserResponse;
import com.example.rolepermission.entity.Permission;
import com.example.rolepermission.service.AppFunctionService;
import com.example.rolepermission.service.PermissionService;
import com.example.rolepermission.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/functions")
@RequiredArgsConstructor
public class AppFunctionController {


    @Autowired
    AppFunctionService appFunctionService;

//    @PreAuthorize("hasAuthority('MANAGE_FUNCTION') and hasAuthority('CREATE_FUNCTION')")
    @PostMapping("/create")
    ResponseEntity<AppFunctionResponse> create(@RequestBody AppFunctionRequest request){
        return ResponseEntity.ok(appFunctionService.create(request));
    }

//    @PreAuthorize("hasAuthority('VIEW_FUNCTION')")
    @GetMapping("/get-all")
    ResponseEntity<List<AppFunctionResponse>> getAll(){
        return ResponseEntity.ok(appFunctionService.getAllFunctions());
    }
//
//    @GetMapping("/{roleId}")
//    ResponseEntity<RoleResponse> getRoleId(@PathVariable("roleId") String roleId) {
//        return ResponseEntity.ok(roleService.getRoleById(roleId));
//    }
//
//    @PreAuthorize("hasAuthority('MANAGE_FUNCTION') and hasAuthority('DELETE_FUNCTION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        appFunctionService.delete(id);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Xóa thành công"));
    }

//    @PreAuthorize("hasAuthority('MANAGE_FUNCTION') and hasAuthority('EDIT_FUNCTION')")
    @PutMapping("/update/{id}")
    ResponseEntity<AppFunctionResponse> updateFunction(@PathVariable Long id, @RequestBody AppFunctionRequest request) {
        return ResponseEntity.ok(appFunctionService.update(id, request));
    }

    @GetMapping("/{functionId}/permissions")
    public ResponseEntity<List<Permission>> getPermissionsByFunctionId(@PathVariable Long functionId) {
        List<Permission> permissions = appFunctionService.getPermissionsByFunctionId(functionId);
        return ResponseEntity.ok(permissions);
    }
}
