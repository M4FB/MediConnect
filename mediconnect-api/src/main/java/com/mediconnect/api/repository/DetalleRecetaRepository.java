package com.mediconnect.api.repository;

import com.mediconnect.api.entity.DetalleReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DetalleRecetaRepository extends JpaRepository<DetalleReceta, UUID> {

    List<DetalleReceta> findByRecetaId(UUID recetaId);
}
