package com.mediconnect.api.controller;

import com.mediconnect.api.dto.common.ApiResponse;
import com.mediconnect.api.dto.doctor.DoctorDto;
import com.mediconnect.api.dto.doctor.DoctorRegistrationRequest;
import com.mediconnect.api.dto.user.UserDto;
import com.mediconnect.api.service.DoctorService;
import com.mediconnect.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@Tag(name = "Administración", description = "Endpoints de administración (solo ADMIN)")
public class AdminController {

    private final DoctorService doctorService;
    private final UserService userService;

    public AdminController(DoctorService doctorService, UserService userService) {
        this.doctorService = doctorService;
        this.userService = userService;
    }

    @PostMapping("/doctores")
    @Operation(summary = "Registrar nuevo doctor")
    public ResponseEntity<ApiResponse<DoctorDto>> registerDoctor(
            @Valid @RequestBody DoctorRegistrationRequest request) {
        DoctorDto doctor = doctorService.registerDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Doctor registrado exitosamente", doctor));
    }

    @GetMapping("/users")
    @Operation(summary = "Listar todos los usuarios del sistema")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Usuarios obtenidos exitosamente", users));
    }

    @PutMapping("/users/{id}/toggle-active")
    @Operation(summary = "Activar o desactivar un usuario")
    public ResponseEntity<ApiResponse<UserDto>> toggleUserActive(@PathVariable UUID id) {
        UserDto user = userService.toggleUserActive(id);
        return ResponseEntity.ok(ApiResponse.success("Estado de usuario modificado exitosamente", user));
    }
}
