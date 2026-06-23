package com.mediconnect.api.dto.historial;

import com.mediconnect.api.entity.enums.TipoHistorial;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearHistorialRequest {
    @NotNull(message = "El tipo es obligatorio")
    private TipoHistorial tipo;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    private LocalDateTime fecha;

    private String doctorNombre;

    private String archivoUrl;
}
