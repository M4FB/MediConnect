package com.mediconnect.api.controller;

import com.mediconnect.api.dto.common.ApiResponse;
import com.mediconnect.api.dto.historial.CrearHistorialRequest;
import com.mediconnect.api.dto.historial.HistorialMedicoDto;
import com.mediconnect.api.service.HistorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/historial")
@Tag(name = "Historial Médico", description = "Gestión del historial médico del paciente")
public class HistorialController {

    private final HistorialService historialService;

    public HistorialController(HistorialService historialService) {
        this.historialService = historialService;
    }

    @GetMapping
    @Operation(summary = "Obtener historial médico del paciente")
    public ResponseEntity<ApiResponse<List<HistorialMedicoDto>>> getHistorial() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<HistorialMedicoDto> historial = historialService.getHistorial(email);
        return ResponseEntity.ok(ApiResponse.success("Historial obtenido", historial));
    }

    @PostMapping
    @Operation(summary = "Crear entrada en historial médico")
    public ResponseEntity<ApiResponse<HistorialMedicoDto>> crearEntrada(
            @Valid @RequestBody CrearHistorialRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        HistorialMedicoDto entrada = historialService.crearEntrada(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Entrada creada", entrada));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener entrada del historial por ID")
    public ResponseEntity<ApiResponse<HistorialMedicoDto>> getEntradaById(@PathVariable UUID id) {
        HistorialMedicoDto entrada = historialService.getEntradaById(id);
        return ResponseEntity.ok(ApiResponse.success("Entrada obtenida", entrada));
    }
}
