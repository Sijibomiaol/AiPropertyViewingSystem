package com.aol.AiPropertyVeiwingSystem.persistence.entity;

import com.aol.AiPropertyVeiwingSystem.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {
    @Column(unique=true, nullable=false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phoneNumber;

    private boolean enabled = true;

    @OneToMany(mappedBy = "landlord")
    private Set<Property> ownedProperties = new HashSet<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Property> rentedProperties = new HashSet<>();

    @OneToMany(mappedBy = "tenant")
    private Set<Appointment> tenantAppointments = new HashSet<>();

    @OneToMany(mappedBy = "landlord")
    private Set<Appointment> landlordAppointments = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
} 