package com.mediconnect.api.dto.cita;

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
public class CitaDto {
    private UUID id;
    private UUID pacienteId;
    private String pacienteNombre;
    private UUID doctorId;
    private String doctorNombre;
    private String doctorEspecialidad;
    private LocalDateTime fechaHora;
    private String motivo;
    private String notas;
    private String estado;
    private String codigoQr;
    private String notasCancelacion;
    private LocalDateTime createdAt;
}
