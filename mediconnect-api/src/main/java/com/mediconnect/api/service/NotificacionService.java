package com.mediconnect.api.service;

import com.mediconnect.api.dto.notificacion.NotificacionDto;
import com.mediconnect.api.entity.Notificacion;
import com.mediconnect.api.entity.User;
import com.mediconnect.api.entity.enums.TipoNotificacion;
import com.mediconnect.api.repository.NotificacionRepository;
import com.mediconnect.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UserRepository userRepository;

    public NotificacionService(NotificacionRepository notificacionRepository,
                               UserRepository userRepository) {
        this.notificacionRepository = notificacionRepository;
        this.userRepository = userRepository;
    }

    public List<NotificacionDto> getNotificaciones(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<Notificacion> notificaciones = notificacionRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());

        return notificaciones.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void marcarLeida(UUID id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));

        if (!notificacion.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("No tienes permiso para modificar esta notificación");
        }

        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
    }

    @Transactional
    public void marcarTodasLeidas(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<Notificacion> noLeidas = notificacionRepository
                .findByUserIdAndLeidaFalse(user.getId());

        noLeidas.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(noLeidas);
    }

    public long contarNoLeidas(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return notificacionRepository.countByUserIdAndLeidaFalse(user.getId());
    }

    @Transactional
    public void crearNotificacion(UUID userId, String titulo, String mensaje, TipoNotificacion tipo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para notificación"));

        Notificacion notificacion = new Notificacion();
        notificacion.setUser(user);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setLeida(false);
        notificacion.setCreatedAt(LocalDateTime.now());
        notificacionRepository.save(notificacion);
    }

    private NotificacionDto mapToDto(Notificacion notificacion) {
        return NotificacionDto.builder()
                .id(notificacion.getId())
                .titulo(notificacion.getTitulo())
                .mensaje(notificacion.getMensaje())
                .tipo(notificacion.getTipo().name())
                .leida(notificacion.isLeida())
                .createdAt(notificacion.getCreatedAt())
                .build();
    }
}
