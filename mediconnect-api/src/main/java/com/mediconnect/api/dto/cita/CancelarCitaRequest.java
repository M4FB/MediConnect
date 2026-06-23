package com.mediconnect.api.dto.cita;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelarCitaRequest {
    @NotBlank(message = "El motivo de cancelación es obligatorio")
    private String motivoCancelacion;
}
