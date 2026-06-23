package com.mediconnect.api.controller;

import com.mediconnect.api.dto.common.ApiResponse;
import com.mediconnect.api.dto.doctor.DoctorDto;
import com.mediconnect.api.dto.doctor.HorarioDisponibleDto;
import com.mediconnect.api.dto.valoracion.CrearValoracionRequest;
import com.mediconnect.api.dto.valoracion.ValoracionDto;
import com.mediconnect.api.service.DoctorService;
import com.mediconnect.api.service.ValoracionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@Tag(name = "Doctores", description = "Búsqueda y gestión de doctores")
public class DoctorController {

    private final DoctorService doctorService;
    private final ValoracionService valoracionService;

    public DoctorController(DoctorService doctorService, ValoracionService valoracionService) {
        this.doctorService = doctorService;
        this.valoracionService = valoracionService;
    }

    @GetMapping
    @Operation(summary = "Listar doctores con filtros opcionales")
    public ResponseEntity<ApiResponse<List<DoctorDto>>> getAllDoctors(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String especialidad) {
        List<DoctorDto> doctors = doctorService.getAllDoctors(search, especialidad);
        return ResponseEntity.ok(ApiResponse.success("Doctores obtenidos", doctors));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener doctor por ID")
    public ResponseEntity<ApiResponse<DoctorDto>> getDoctorById(@PathVariable UUID id) {
        DoctorDto doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor obtenido", doctor));
    }

    @GetMapping("/{id}/horarios-disponibles")
    @Operation(summary = "Obtener horarios disponibles de un doctor para una fecha")
    public ResponseEntity<ApiResponse<List<HorarioDisponibleDto>>> getHorariosDisponibles(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<HorarioDisponibleDto> horarios = doctorService.getHorariosDisponibles(id, fecha);
        return ResponseEntity.ok(ApiResponse.success("Horarios disponibles obtenidos", horarios));
    }

    @GetMapping("/{id}/valoraciones")
    @Operation(summary = "Obtener valoraciones de un doctor")
    public ResponseEntity<ApiResponse<List<ValoracionDto>>> getValoracionesDoctor(@PathVariable UUID id) {
        List<ValoracionDto> valoraciones = valoracionService.getValoracionesDoctor(id);
        return ResponseEntity.ok(ApiResponse.success("Valoraciones obtenidas", valoraciones));
    }

    @PostMapping("/{id}/valoraciones")
    @Operation(summary = "Crear valoración para un doctor")
    public ResponseEntity<ApiResponse<ValoracionDto>> crearValoracion(
            @PathVariable UUID id,
            @Valid @RequestBody CrearValoracionRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        request.setDoctorId(id);
        ValoracionDto valoracion = valoracionService.crearValoracion(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Valoración creada", valoracion));
    }
}
