package com.mediconnect.api.service;

import com.mediconnect.api.dto.cita.CancelarCitaRequest;
import com.mediconnect.api.dto.cita.CitaDto;
import com.mediconnect.api.dto.cita.CrearCitaRequest;
import com.mediconnect.api.entity.Cita;
import com.mediconnect.api.entity.Doctor;
import com.mediconnect.api.entity.Paciente;
import com.mediconnect.api.entity.User;
import com.mediconnect.api.entity.enums.EstadoCita;
import com.mediconnect.api.entity.enums.Role;
import com.mediconnect.api.entity.enums.TipoNotificacion;
import com.mediconnect.api.repository.CitaRepository;
import com.mediconnect.api.repository.DoctorRepository;
import com.mediconnect.api.repository.PacienteRepository;
import com.mediconnect.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;
    private final UserRepository userRepository;
    private final NotificacionService notificacionService;

    public CitaService(CitaRepository citaRepository,
                       DoctorRepository doctorRepository,
                       PacienteRepository pacienteRepository,
                       UserRepository userRepository,
                       NotificacionService notificacionService) {
        this.citaRepository = citaRepository;
        this.doctorRepository = doctorRepository;
        this.pacienteRepository = pacienteRepository;
        this.userRepository = userRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public CitaDto crearCita(String emailPaciente, CrearCitaRequest request) {
        User userPaciente = userRepository.findByEmail(emailPaciente)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Paciente paciente = pacienteRepository.findByUserId(userPaciente.getId())
                .orElseThrow(() -> new IllegalArgumentException("Perfil de paciente no encontrado"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

        // Check slot availability
        List<Cita> citasExistentes = citaRepository.findByDoctorIdAndFechaHoraAndEstadoNotIn(
                doctor.getId(), request.getFechaHora(), List.of(EstadoCita.CANCELADA));

        if (!citasExistentes.isEmpty()) {
            throw new IllegalArgumentException("El horario seleccionado no está disponible");
        }

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setDoctor(doctor);
        cita.setFechaHora(request.getFechaHora());
        cita.setMotivo(request.getMotivo());
        cita.setNotas(request.getNotas());
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setCodigoQr(UUID.randomUUID().toString());
        cita.setCreatedAt(LocalDateTime.now());
        cita.setUpdatedAt(LocalDateTime.now());
        cita = citaRepository.save(cita);

        // Notify doctor
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        notificacionService.crearNotificacion(
                doctor.getUser().getId(),
                "Nueva cita programada",
                "El paciente " + userPaciente.getNombre() + " " + userPaciente.getApellido()
                        + " ha programado una cita para el " + request.getFechaHora().format(formatter),
                TipoNotificacion.CITA
        );

        return mapToDto(cita);
    }

    public List<CitaDto> getMisCitas(String email, String estado) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<Cita> citas;

        if (user.getRole() == Role.PACIENTE) {
            Paciente paciente = pacienteRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Perfil de paciente no encontrado"));
            if (estado != null && !estado.isBlank()) {
                citas = citaRepository.findByPacienteIdAndEstadoOrderByFechaHoraDesc(
                        paciente.getId(), EstadoCita.valueOf(estado.toUpperCase()));
            } else {
                citas = citaRepository.findByPacienteIdOrderByFechaHoraDesc(paciente.getId());
            }
        } else if (user.getRole() == Role.DOCTOR) {
            Doctor doctor = doctorRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Perfil de doctor no encontrado"));
            if (estado != null && !estado.isBlank()) {
                citas = citaRepository.findByDoctorIdAndEstadoOrderByFechaHoraDesc(
                        doctor.getId(), EstadoCita.valueOf(estado.toUpperCase()));
            } else {
                citas = citaRepository.findByDoctorIdOrderByFechaHoraDesc(doctor.getId());
            }
        } else {
            citas = citaRepository.findAll();
        }

        return citas.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public CitaDto getCitaById(UUID id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con id: " + id));
        return mapToDto(cita);
    }

    @Transactional
    public CitaDto confirmarCita(UUID id, String emailDoctor) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        User doctorUser = userRepository.findByEmail(emailDoctor)
                .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

        if (!cita.getDoctor().getUser().getId().equals(doctorUser.getId())) {
            throw new IllegalArgumentException("No tienes permiso para confirmar esta cita");
        }

        if (cita.getEstado() != EstadoCita.PENDIENTE) {
            throw new IllegalArgumentException("Solo se pueden confirmar citas pendientes");
        }

        cita.setEstado(EstadoCita.CONFIRMADA);
        cita.setUpdatedAt(LocalDateTime.now());
        cita = citaRepository.save(cita);

        // Notify paciente
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        notificacionService.crearNotificacion(
                cita.getPaciente().getUser().getId(),
                "Cita confirmada",
                "Tu cita con el Dr. " + doctorUser.getNombre() + " " + doctorUser.getApellido()
                        + " para el " + cita.getFechaHora().format(formatter) + " ha sido confirmada",
                TipoNotificacion.CITA
        );

        return mapToDto(cita);
    }

    @Transactional
    public CitaDto cancelarCita(UUID id, String email, CancelarCitaRequest request) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (cita.getEstado() == EstadoCita.COMPLETADA || cita.getEstado() == EstadoCita.CANCELADA) {
            throw new IllegalArgumentException("No se puede cancelar una cita " + cita.getEstado().name().toLowerCase());
        }

        cita.setEstado(EstadoCita.CANCELADA);
        cita.setNotasCancelacion(request.getMotivoCancelacion());
        cita.setUpdatedAt(LocalDateTime.now());
        cita = citaRepository.save(cita);

        // Notify the other party
        UUID notifyUserId;
        String cancelledBy;
        if (user.getRole() == Role.PACIENTE) {
            notifyUserId = cita.getDoctor().getUser().getId();
            cancelledBy = "el paciente " + user.getNombre() + " " + user.getApellido();
        } else {
            notifyUserId = cita.getPaciente().getUser().getId();
            cancelledBy = "el Dr. " + user.getNombre() + " " + user.getApellido();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        notificacionService.crearNotificacion(
                notifyUserId,
                "Cita cancelada",
                "La cita del " + cita.getFechaHora().format(formatter)
                        + " ha sido cancelada por " + cancelledBy
                        + ". Motivo: " + request.getMotivoCancelacion(),
                TipoNotificacion.CITA
        );

        return mapToDto(cita);
    }

    @Transactional
    public CitaDto completarCita(UUID id, String emailDoctor) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        User doctorUser = userRepository.findByEmail(emailDoctor)
                .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

        if (!cita.getDoctor().getUser().getId().equals(doctorUser.getId())) {
            throw new IllegalArgumentException("No tienes permiso para completar esta cita");
        }

        if (cita.getEstado() != EstadoCita.CONFIRMADA && cita.getEstado() != EstadoCita.EN_CURSO) {
            throw new IllegalArgumentException("Solo se pueden completar citas confirmadas o en curso");
        }

        cita.setEstado(EstadoCita.COMPLETADA);
        cita.setUpdatedAt(LocalDateTime.now());
        cita = citaRepository.save(cita);

        // Notify paciente
        notificacionService.crearNotificacion(
                cita.getPaciente().getUser().getId(),
                "Cita completada",
                "Tu cita con el Dr. " + doctorUser.getNombre() + " " + doctorUser.getApellido()
                        + " ha sido completada. Puedes dejar una valoración.",
                TipoNotificacion.CITA
        );

        return mapToDto(cita);
    }

    @Transactional
    public CitaDto checkIn(String codigoQr) {
        Cita cita = citaRepository.findByCodigoQr(codigoQr)
                .orElseThrow(() -> new IllegalArgumentException("Código QR no válido"));

        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new IllegalArgumentException("Solo se puede hacer check-in en citas confirmadas");
        }

        cita.setEstado(EstadoCita.EN_CURSO);
        cita.setUpdatedAt(LocalDateTime.now());
        cita = citaRepository.save(cita);

        return mapToDto(cita);
    }

    private CitaDto mapToDto(Cita cita) {
        return CitaDto.builder()
                .id(cita.getId())
                .pacienteId(cita.getPaciente().getId())
                .pacienteNombre(cita.getPaciente().getUser().getNombre() + " " + cita.getPaciente().getUser().getApellido())
                .doctorId(cita.getDoctor().getId())
                .doctorNombre(cita.getDoctor().getUser().getNombre() + " " + cita.getDoctor().getUser().getApellido())
                .doctorEspecialidad(cita.getDoctor().getEspecialidad())
                .fechaHora(cita.getFechaHora())
                .motivo(cita.getMotivo())
                .notas(cita.getNotas())
                .estado(cita.getEstado().name())
                .codigoQr(cita.getCodigoQr())
                .notasCancelacion(cita.getNotasCancelacion())
                .createdAt(cita.getCreatedAt())
                .build();
    }
}
