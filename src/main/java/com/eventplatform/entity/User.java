package com.eventplatform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // First Name
    @Column(nullable = false)
    private String firstName;


    // Last Name
    @Column(nullable = false)
    private String lastName;


    // Full Name (Generated)
    public String getFullName() {
        return firstName + " " + lastName;
    }


    // Email
    @Column(nullable = false, unique = true)
    private String email;


    // Mobile number (Fetched from Aadhaar)
    @Column(nullable = false, unique = true)
    private String phone;


    // Aadhaar Number (NEW FIELD)
    @Column(nullable = false, unique = true, length = 12)
    private String aadhaarNumber;


    // Password
    @Column(nullable = false)
    private String passwordHash;


    // Role
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    // Email verified
    @Column(nullable = false)
    private boolean emailVerified;


    // Mobile verified
    @Column(nullable = false)
    private boolean phoneVerified;



    // ================= SPRING SECURITY =================

    @Override
    public String getPassword() {
        return passwordHash;
    }


    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
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
        return true;
    }
}
