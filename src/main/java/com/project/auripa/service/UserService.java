package com.project.auripa.service;

import com.project.auripa.dto.request.user.UserCreateRequest;
import com.project.auripa.dto.request.user.UserUpdateRequest;
import com.project.auripa.dto.response.user.UserResponse;
import com.project.auripa.entity.User;
import com.project.auripa.handler.ResourceNotFoundException;
import com.project.auripa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Helper untuk mapping Entity ke DTO
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
        return mapToResponse(user);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User dengan ID " + id + " tidak ditemukan"));
        return mapToResponse(user);
    }

    public UserResponse createUser(UserCreateRequest request) {
        // Cek apakah username sudah ada
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username sudah terdaftar!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password!
        user.setRole(request.getRole().toUpperCase());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User dengan ID " + id + " tidak ditemukan"));

        // Update field
        user.setUsername(request.getUsername());
        user.setRole(request.getRole().toUpperCase());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User dengan ID " + id + " tidak ditemukan");
        }
        userRepository.deleteById(id);
    }
}