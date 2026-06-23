package com.mediconnect.api.dto.notificacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDto {
    private UUID id;
    private String titulo;
    private String mensaje;
    private String tipo;
    private boolean leida;
    private LocalDateTime createdAt;
}
