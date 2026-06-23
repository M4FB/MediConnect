package com.mediconnect.api.repository;

import com.mediconnect.api.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, UUID> {

    List<Receta> findByPacienteIdOrderByFechaEmisionDesc(UUID pacienteId);

    Optional<Receta> findByCitaId(UUID citaId);
}
