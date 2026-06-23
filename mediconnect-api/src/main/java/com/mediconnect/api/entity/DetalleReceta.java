package com.mediconnect.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "detalle_recetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleReceta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @Column(nullable = false)
    private String medicamento;

    @Column(nullable = false)
    private String dosis;

    @Column(nullable = false)
    private String frecuencia;

    private String duracion;

    @Column(name = "instrucciones", columnDefinition = "TEXT")
    private String instrucciones;
}
