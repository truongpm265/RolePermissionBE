package com.example.rolepermission.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {
    @Id
    private String name;

    private String description;
    @ManyToMany
    Set<Permission> permissions;

    public Role(String admin) {

    }
}
