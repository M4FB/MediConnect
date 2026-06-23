package com.mediconnect.api.controller;

import com.mediconnect.api.dto.common.ApiResponse;
import com.mediconnect.api.dto.doctor.DoctorDto;
import com.mediconnect.api.dto.doctor.DoctorRegistrationRequest;
import com.mediconnect.api.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@Tag(name = "Administración", description = "Endpoints de administración (solo ADMIN)")
public class AdminController {

    private final DoctorService doctorService;

    public AdminController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/doctores")
    @Operation(summary = "Registrar nuevo doctor")
    public ResponseEntity<ApiResponse<DoctorDto>> registerDoctor(
            @Valid @RequestBody DoctorRegistrationRequest request) {
        DoctorDto doctor = doctorService.registerDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Doctor registrado exitosamente", doctor));
    }
}
