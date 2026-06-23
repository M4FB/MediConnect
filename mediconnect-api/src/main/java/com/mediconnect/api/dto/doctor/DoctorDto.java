package com.mediconnect.api.dto.doctor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    private UUID id;
    private UUID userId;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String fotoUrl;
    private String especialidad;
    private String numeroColegiado;
    private String descripcion;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private BigDecimal costoCita;
    private Double promedioValoracion;
    private Long totalValoraciones;
}
