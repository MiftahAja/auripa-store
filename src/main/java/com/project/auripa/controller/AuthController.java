package com.project.auripa.controller;

import com.project.auripa.dto.request.login.LoginRequest;
import com.project.auripa.dto.response.ResData;
import com.project.auripa.dto.response.login.LoginResponse;
import com.project.auripa.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResData<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        
        LoginResponse response = authService.loginAdmin(request);
        
        return ResponseEntity.ok(ResData.of("Login berhasil", response));
    }
}