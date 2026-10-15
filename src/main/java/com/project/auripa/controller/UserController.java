package com.project.auripa.controller;

import com.project.auripa.dto.request.user.UserCreateRequest;
import com.project.auripa.dto.request.user.UserUpdateRequest;
import com.project.auripa.dto.response.ResData;
import com.project.auripa.dto.response.user.UserResponse;
import com.project.auripa.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ResData<UserResponse>> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ResData.of("Berhasil mengambil profil saat ini", user));
    }

    @GetMapping
    public ResponseEntity<ResData<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ResData.of("Berhasil mengambil data semua user", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResData<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ResData.of("Berhasil menemukan user", user));
    }

    @PostMapping
    public ResponseEntity<ResData<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse newUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResData.created(newUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResData<UserResponse>> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(ResData.of("User berhasil diupdate", updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResData<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResData.deleted());
    }
}