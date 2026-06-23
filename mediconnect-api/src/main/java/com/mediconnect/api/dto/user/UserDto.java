package com.mediconnect.api.dto.user;

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
public class UserDto {
    private UUID id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String role;
    private boolean activo;
    private String fotoUrl;
    private LocalDateTime createdAt;
}
