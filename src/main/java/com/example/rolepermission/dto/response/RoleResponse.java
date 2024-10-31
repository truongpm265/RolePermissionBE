package com.example.rolepermission.dto.response;


import com.example.rolepermission.entity.AppFunction;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    String name;
    String description;
    Set<AppFunctionResponse> functions;
}
