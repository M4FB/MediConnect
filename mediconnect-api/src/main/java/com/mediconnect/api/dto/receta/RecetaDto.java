package com.mediconnect.api.dto.receta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecetaDto {
    private UUID id;
    private UUID citaId;
    private UUID doctorId;
    private String doctorNombre;
    private UUID pacienteId;
    private String pacienteNombre;
    private String diagnostico;
    private String observaciones;
    private LocalDateTime fechaEmision;
    private List<DetalleRecetaDto> detalles;
    private LocalDateTime createdAt;
}
