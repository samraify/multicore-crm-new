package com.multicore.crm.service;

import com.multicore.crm.entity.User;
import com.multicore.crm.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository repo) {
        this.userRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new UsernameNotFoundException("User is not active");
        }

        // Build authorities with ROLE_ prefix (required by Spring Security's hasRole())
        var authorities = user.getRoles().stream()
                .map(role -> {
                    String roleName = role.getRoleName().name();
                    String authority = "ROLE_" + roleName;
                    return new org.springframework.security.core.authority.SimpleGrantedAuthority(authority);
                })
                .toList();

        // Log authorities for debugging (remove in production if needed)
        if (authorities.isEmpty()) {
            throw new UsernameNotFoundException("User has no roles assigned: " + user.getEmail());
        }

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(false)
                .disabled(false)
                .build();
    }
}
