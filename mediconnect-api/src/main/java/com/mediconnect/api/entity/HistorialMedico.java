package com.mediconnect.api.entity;

import com.mediconnect.api.entity.enums.TipoHistorial;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_medico")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHistorial tipo;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "doctor_nombre")
    private String doctorNombre;

    @Column(name = "archivo_url", columnDefinition = "TEXT")
    private String archivoUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
