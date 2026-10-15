package com.project.auripa.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.project.auripa.dto.request.login.LoginRequest;
import com.project.auripa.dto.response.login.LoginResponse;
import com.project.auripa.entity.User;
import com.project.auripa.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value ("${app.jwt.access-token-expiration}")
    private long jwtExpirationMs;

    public LoginResponse loginAdmin(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Username tidak ditemukan"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password salah");
        }

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new AccessDeniedException("Akses Ditolak: Akun ini bukan Admin!");
        }

        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        String token = JWT.create()
                .withSubject(user.getUsername())
                .withClaim("role", user.getRole())
                .withIssuer("AuripaStore")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .sign(algorithm);

        return new LoginResponse(token);
    }
}