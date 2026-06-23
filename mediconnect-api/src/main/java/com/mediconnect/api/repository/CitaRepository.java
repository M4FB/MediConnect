package com.mediconnect.api.repository;

import com.mediconnect.api.entity.Cita;
import com.mediconnect.api.entity.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CitaRepository extends JpaRepository<Cita, UUID> {

    List<Cita> findByPacienteIdOrderByFechaHoraDesc(UUID pacienteId);

    List<Cita> findByDoctorIdOrderByFechaHoraDesc(UUID doctorId);

    List<Cita> findByPacienteIdAndEstadoOrderByFechaHoraDesc(UUID pacienteId, EstadoCita estado);

    List<Cita> findByDoctorIdAndEstadoOrderByFechaHoraDesc(UUID doctorId, EstadoCita estado);

    List<Cita> findByDoctorIdAndFechaHoraBetweenAndEstadoNotIn(UUID doctorId, LocalDateTime start, LocalDateTime end, Collection<EstadoCita> estados);

    List<Cita> findByDoctorIdAndFechaHoraAndEstadoNotIn(UUID doctorId, LocalDateTime fechaHora, Collection<EstadoCita> estados);

    boolean existsByPacienteIdAndDoctorIdAndEstado(UUID pacienteId, UUID doctorId, EstadoCita estado);

    Optional<Cita> findByCodigoQr(String codigoQr);
}
