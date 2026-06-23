package com.mediconnect.api.repository;

import com.mediconnect.api.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    List<Doctor> findByEspecialidadContainingIgnoreCase(String especialidad);

    Optional<Doctor> findByUserId(UUID userId);

    List<Doctor> findByEspecialidadContainingIgnoreCaseAndUser_NombreContainingIgnoreCase(String especialidad, String nombre);

    List<Doctor> findByUser_NombreContainingIgnoreCaseOrUser_ApellidoContainingIgnoreCase(String nombre, String apellido);
}
