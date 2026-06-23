package com.mediconnect.api.dto.receta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearRecetaRequest {
    @NotNull(message = "El ID de la cita es obligatorio")
    private UUID citaId;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    private String observaciones;

    @NotNull(message = "Los detalles de la receta son obligatorios")
    private List<DetalleRequest> detalles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleRequest {
        @NotBlank(message = "El medicamento es obligatorio")
        private String medicamento;

        @NotBlank(message = "La dosis es obligatoria")
        private String dosis;

        @NotBlank(message = "La frecuencia es obligatoria")
        private String frecuencia;

        @NotBlank(message = "La duración es obligatoria")
        private String duracion;

        private String instrucciones;
    }
}
