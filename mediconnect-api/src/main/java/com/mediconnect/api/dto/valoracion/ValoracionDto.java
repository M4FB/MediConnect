package com.mediconnect.api.dto.valoracion;

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
public class ValoracionDto {
    private UUID id;
    private UUID pacienteId;
    private String pacienteNombre;
    private UUID doctorId;
    private int calificacion;
    private String comentario;
    private LocalDateTime createdAt;
}
