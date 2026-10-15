package com.project.auripa.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "Username tidak boleh kosong")
    private String username;

    @NotBlank(message = "Password tidak boleh kosong")
    private String password;

    @NotBlank(message = "Role tidak boleh kosong (contoh: ADMIN atau CUSTOMER)")
    private String role;
}