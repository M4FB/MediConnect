package com.mediconnect.api.dto.historial;

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
public class HistorialMedicoDto {
    private UUID id;
    private UUID pacienteId;
    private String tipo;
    private String titulo;
    private String descripcion;
    private LocalDateTime fecha;
    private String doctorNombre;
    private String archivoUrl;
    private LocalDateTime createdAt;
}
