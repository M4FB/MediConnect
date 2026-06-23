package com.mediconnect.api.dto.receta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRecetaDto {
    private UUID id;
    private String medicamento;
    private String dosis;
    private String frecuencia;
    private String duracion;
    private String instrucciones;
}
