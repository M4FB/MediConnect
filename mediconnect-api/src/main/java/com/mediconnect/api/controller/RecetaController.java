package com.mediconnect.api.controller;

import com.mediconnect.api.dto.common.ApiResponse;
import com.mediconnect.api.dto.receta.CrearRecetaRequest;
import com.mediconnect.api.dto.receta.RecetaDto;
import com.mediconnect.api.service.RecetaService;
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
@RequestMapping("/api/recetas")
@Tag(name = "Recetas", description = "Gestión de recetas médicas")
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @PostMapping
    @Operation(summary = "Crear receta médica (solo doctor)")
    public ResponseEntity<ApiResponse<RecetaDto>> crearReceta(@Valid @RequestBody CrearRecetaRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        RecetaDto receta = recetaService.crearReceta(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Receta creada", receta));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener receta por ID")
    public ResponseEntity<ApiResponse<RecetaDto>> getRecetaById(@PathVariable UUID id) {
        RecetaDto receta = recetaService.getRecetaById(id);
        return ResponseEntity.ok(ApiResponse.success("Receta obtenida", receta));
    }

    @GetMapping("/mis-recetas")
    @Operation(summary = "Obtener mis recetas (paciente)")
    public ResponseEntity<ApiResponse<List<RecetaDto>>> getMisRecetas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<RecetaDto> recetas = recetaService.getMisRecetas(email);
        return ResponseEntity.ok(ApiResponse.success("Recetas obtenidas", recetas));
    }
}
