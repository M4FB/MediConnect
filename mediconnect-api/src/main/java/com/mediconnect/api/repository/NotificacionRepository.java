package com.mediconnect.api.repository;

import com.mediconnect.api.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    List<Notificacion> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Notificacion> findByUserIdAndLeidaFalse(UUID userId);

    long countByUserIdAndLeidaFalse(UUID userId);
}
