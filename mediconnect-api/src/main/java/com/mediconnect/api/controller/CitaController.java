package com.mediconnect.api.controller;

import com.mediconnect.api.dto.cita.CancelarCitaRequest;
import com.mediconnect.api.dto.cita.CheckInRequest;
import com.mediconnect.api.dto.cita.CitaDto;
import com.mediconnect.api.dto.cita.CrearCitaRequest;
import com.mediconnect.api.dto.common.ApiResponse;
import com.mediconnect.api.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "Gestión de citas médicas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping
    @Operation(summary = "Crear nueva cita")
    public ResponseEntity<ApiResponse<CitaDto>> crearCita(@Valid @RequestBody CrearCitaRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CitaDto cita = citaService.crearCita(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cita creada exitosamente", cita));
    }

    @GetMapping
    @Operation(summary = "Obtener mis citas (filtrables por estado)")
    public ResponseEntity<ApiResponse<List<CitaDto>>> getMisCitas(
            @RequestParam(required = false) String estado) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CitaDto> citas = citaService.getMisCitas(email, estado);
        return ResponseEntity.ok(ApiResponse.success("Citas obtenidas", citas));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una cita")
    public ResponseEntity<ApiResponse<CitaDto>> getCitaById(@PathVariable UUID id) {
        CitaDto cita = citaService.getCitaById(id);
        return ResponseEntity.ok(ApiResponse.success("Cita obtenida", cita));
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar cita (solo doctor)")
    public ResponseEntity<ApiResponse<CitaDto>> confirmarCita(@PathVariable UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CitaDto cita = citaService.confirmarCita(id, email);
        return ResponseEntity.ok(ApiResponse.success("Cita confirmada", cita));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar cita")
    public ResponseEntity<ApiResponse<CitaDto>> cancelarCita(
            @PathVariable UUID id,
            @Valid @RequestBody CancelarCitaRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CitaDto cita = citaService.cancelarCita(id, email, request);
        return ResponseEntity.ok(ApiResponse.success("Cita cancelada", cita));
    }

    @PutMapping("/{id}/completar")
    @Operation(summary = "Completar cita (solo doctor)")
    public ResponseEntity<ApiResponse<CitaDto>> completarCita(@PathVariable UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CitaDto cita = citaService.completarCita(id, email);
        return ResponseEntity.ok(ApiResponse.success("Cita completada", cita));
    }

    @PostMapping("/{id}/check-in")
    @Operation(summary = "Check-in con código QR")
    public ResponseEntity<ApiResponse<CitaDto>> checkIn(
            @PathVariable UUID id,
            @Valid @RequestBody CheckInRequest request) {
        CitaDto cita = citaService.checkIn(request.getCodigoQr());
        return ResponseEntity.ok(ApiResponse.success("Check-in realizado", cita));
    }
}
