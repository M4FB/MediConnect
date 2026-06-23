package com.mediconnect.api.controller;

import com.mediconnect.api.dto.common.ApiResponse;
import com.mediconnect.api.dto.notificacion.NotificacionDto;
import com.mediconnect.api.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Gestión de notificaciones del usuario")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las notificaciones")
    public ResponseEntity<ApiResponse<List<NotificacionDto>>> getNotificaciones() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<NotificacionDto> notificaciones = notificacionService.getNotificaciones(email);
        return ResponseEntity.ok(ApiResponse.success("Notificaciones obtenidas", notificaciones));
    }

    @PutMapping("/{id}/leer")
    @Operation(summary = "Marcar notificación como leída")
    public ResponseEntity<ApiResponse<Void>> marcarLeida(@PathVariable UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        notificacionService.marcarLeida(id, email);
        return ResponseEntity.ok(ApiResponse.success("Notificación marcada como leída", null));
    }

    @PutMapping("/leer-todas")
    @Operation(summary = "Marcar todas las notificaciones como leídas")
    public ResponseEntity<ApiResponse<Void>> marcarTodasLeidas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        notificacionService.marcarTodasLeidas(email);
        return ResponseEntity.ok(ApiResponse.success("Todas las notificaciones marcadas como leídas", null));
    }

    @GetMapping("/no-leidas/count")
    @Operation(summary = "Contar notificaciones no leídas")
    public ResponseEntity<ApiResponse<Long>> contarNoLeidas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        long count = notificacionService.contarNoLeidas(email);
        return ResponseEntity.ok(ApiResponse.success("Conteo obtenido", count));
    }
}
