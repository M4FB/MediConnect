package com.mediconnect.api.repository;

import com.mediconnect.api.entity.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, UUID> {

    List<Valoracion> findByDoctorIdOrderByCreatedAtDesc(UUID doctorId);

    boolean existsByPacienteIdAndDoctorId(UUID pacienteId, UUID doctorId);

    @Query("SELECT AVG(v.calificacion) FROM Valoracion v WHERE v.doctor.id = :doctorId")
    Double findAverageCalificacionByDoctorId(@Param("doctorId") UUID doctorId);

    Long countByDoctorId(UUID doctorId);
}
