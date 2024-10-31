package com.example.rolepermission.controller;


import com.example.rolepermission.dto.request.PermissionRequest;
import com.example.rolepermission.dto.request.UserUpdateRequest;
import com.example.rolepermission.dto.response.PermissionResponse;
import com.example.rolepermission.dto.response.UserResponse;
import com.example.rolepermission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    @Autowired
    PermissionService permissionService;

    @PostMapping
    ResponseEntity<PermissionResponse> create(@RequestBody PermissionRequest request){
        return ResponseEntity.ok(permissionService.create(request));
    }

    @GetMapping
    ResponseEntity<List<PermissionResponse>> getAll(){
        return ResponseEntity.ok(permissionService.getAll());
    }

    @DeleteMapping("/{permission}")
    ResponseEntity<String> delete(@PathVariable String permission){
        permissionService.delete(permission);
        return ResponseEntity.ok("Delete Successfully");
    }

    @PutMapping("/update/{name}")
    ResponseEntity<PermissionResponse> updatePermission(@PathVariable String name, @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(permissionService.update(name, request));
    }

}
