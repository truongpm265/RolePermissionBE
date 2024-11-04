package com.example.rolepermission.controller;


import com.example.rolepermission.dto.request.PermissionRequest;
import com.example.rolepermission.dto.request.RoleRequest;
import com.example.rolepermission.dto.request.UserUpdateRequest;
import com.example.rolepermission.dto.response.PermissionResponse;
import com.example.rolepermission.dto.response.RoleResponse;
import com.example.rolepermission.dto.response.UserResponse;
import com.example.rolepermission.service.PermissionService;
import com.example.rolepermission.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {


    @Autowired
    RoleService roleService;

    @PreAuthorize("hasAuthority('MANAGE_ROLE') and hasAuthority('CREATE_ROLE')")
    @PostMapping("/create")
    ResponseEntity<RoleResponse> create(@RequestBody RoleRequest request){
        return ResponseEntity.ok(roleService.create(request));
    }

//    @PreAuthorize("hasAuthority('VIEW_ROLE')")
    @GetMapping("/get-all")
    ResponseEntity<List<RoleResponse>> getAll(){
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PreAuthorize("hasAuthority('MANAGE_ROLE') and hasAuthority('VIEW_ROLE')")
    @GetMapping("/{roleId}")
    ResponseEntity<RoleResponse> getRoleId(@PathVariable("roleId") String roleId) {
        return ResponseEntity.ok(roleService.getByName(roleId));
    }

    @PreAuthorize("hasAuthority('MANAGE_ROLE') and hasAuthority('DELETE_ROLE')")
    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> delete(@PathVariable String roleId) {
        roleService.delete(roleId);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Xóa thành công"));
    }
    @PreAuthorize("hasAuthority('MANAGE_ROLE') and hasAuthority('EDIT_ROLE')")
        @PutMapping("/update/{roleId}")
    ResponseEntity<RoleResponse> updateRole(@PathVariable String roleId, @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.edit(roleId, request));
    }

}
