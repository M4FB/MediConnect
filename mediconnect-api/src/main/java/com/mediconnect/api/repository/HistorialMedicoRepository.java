package com.mediconnect.api.repository;

import com.mediconnect.api.entity.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, UUID> {

    List<HistorialMedico> findByPacienteIdOrderByFechaDesc(UUID pacienteId);
}
