package com.aol.AiPropertyVeiwingSystem.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum UserRole {
    TENANT,
    LANDLORD,
    ADMIN;

    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

    private Set<Permission> getPermissions() {
        return switch (this) {
            case ADMIN -> Set.of(
                    Permission.USER_CREATE,
                    Permission.USER_READ,
                    Permission.USER_UPDATE,
                    Permission.USER_DELETE,
                    Permission.PROPERTY_CREATE,
                    Permission.PROPERTY_READ,
                    Permission.PROPERTY_UPDATE,
                    Permission.PROPERTY_DELETE,
                    Permission.APPOINTMENT_CREATE,
                    Permission.APPOINTMENT_READ,
                    Permission.APPOINTMENT_UPDATE,
                    Permission.APPOINTMENT_DELETE
            );
            case LANDLORD -> Set.of(
                    Permission.PROPERTY_CREATE,
                    Permission.PROPERTY_READ,
                    Permission.PROPERTY_UPDATE,
                    Permission.PROPERTY_DELETE,
                    Permission.APPOINTMENT_READ,
                    Permission.APPOINTMENT_UPDATE
            );
            case TENANT -> Set.of(
                    Permission.PROPERTY_READ,
                    Permission.APPOINTMENT_CREATE,
                    Permission.APPOINTMENT_READ,
                    Permission.APPOINTMENT_UPDATE
            );
        };
    }
} 