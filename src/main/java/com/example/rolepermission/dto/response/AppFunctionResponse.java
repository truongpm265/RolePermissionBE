package com.example.rolepermission.dto.response;

import com.example.rolepermission.entity.Permission;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppFunctionResponse {
    private Long id;
    private String name;
    private Set<PermissionResponse> permissions;
}
