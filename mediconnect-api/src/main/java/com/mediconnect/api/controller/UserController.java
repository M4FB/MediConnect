package com.mediconnect.api.controller;

import com.mediconnect.api.dto.common.ApiResponse;
import com.mediconnect.api.dto.user.ChangePasswordRequest;
import com.mediconnect.api.dto.user.UpdateUserRequest;
import com.mediconnect.api.dto.user.UserDto;
import com.mediconnect.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Gestión de perfil de usuario")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener perfil del usuario autenticado")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto user = userService.getCurrentUser(email);
        return ResponseEntity.ok(ApiResponse.success("Perfil obtenido", user));
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar perfil del usuario autenticado")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(@Valid @RequestBody UpdateUserRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto user = userService.updateProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success("Perfil actualizado", user));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Cambiar contraseña")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada", null));
    }
}
