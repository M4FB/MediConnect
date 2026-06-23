package com.mediconnect.api.service;

import com.mediconnect.api.dto.receta.CrearRecetaRequest;
import com.mediconnect.api.dto.receta.DetalleRecetaDto;
import com.mediconnect.api.dto.receta.RecetaDto;
import com.mediconnect.api.entity.Cita;
import com.mediconnect.api.entity.DetalleReceta;
import com.mediconnect.api.entity.Doctor;
import com.mediconnect.api.entity.Paciente;
import com.mediconnect.api.entity.Receta;
import com.mediconnect.api.entity.User;
import com.mediconnect.api.entity.enums.EstadoCita;
import com.mediconnect.api.entity.enums.TipoNotificacion;
import com.mediconnect.api.repository.CitaRepository;
import com.mediconnect.api.repository.DetalleRecetaRepository;
import com.mediconnect.api.repository.DoctorRepository;
import com.mediconnect.api.repository.PacienteRepository;
import com.mediconnect.api.repository.RecetaRepository;
import com.mediconnect.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final DetalleRecetaRepository detalleRecetaRepository;
    private final CitaRepository citaRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;
    private final UserRepository userRepository;
    private final NotificacionService notificacionService;

    public RecetaService(RecetaRepository recetaRepository,
                         DetalleRecetaRepository detalleRecetaRepository,
                         CitaRepository citaRepository,
                         DoctorRepository doctorRepository,
                         PacienteRepository pacienteRepository,
                         UserRepository userRepository,
                         NotificacionService notificacionService) {
        this.recetaRepository = recetaRepository;
        this.detalleRecetaRepository = detalleRecetaRepository;
        this.citaRepository = citaRepository;
        this.doctorRepository = doctorRepository;
        this.pacienteRepository = pacienteRepository;
        this.userRepository = userRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public RecetaDto crearReceta(String emailDoctor, CrearRecetaRequest request) {
        User doctorUser = userRepository.findByEmail(emailDoctor)
                .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

        Doctor doctor = doctorRepository.findByUserId(doctorUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Perfil de doctor no encontrado"));

        Cita cita = citaRepository.findById(request.getCitaId())
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        if (cita.getEstado() != EstadoCita.COMPLETADA) {
            throw new IllegalArgumentException("Solo se pueden crear recetas para citas completadas");
        }

        if (!cita.getDoctor().getId().equals(doctor.getId())) {
            throw new IllegalArgumentException("No tienes permiso para crear una receta para esta cita");
        }

        Receta receta = new Receta();
        receta.setCita(cita);
        receta.setDoctor(doctor);
        receta.setPaciente(cita.getPaciente());
        receta.setDiagnostico(request.getDiagnostico());
        receta.setObservaciones(request.getObservaciones());
        receta.setFechaEmision(LocalDateTime.now());
        receta.setCreatedAt(LocalDateTime.now());
        receta = recetaRepository.save(receta);

        List<DetalleReceta> detalles = new ArrayList<>();
        if (request.getDetalles() != null) {
            for (CrearRecetaRequest.DetalleRequest detalleReq : request.getDetalles()) {
                DetalleReceta detalle = new DetalleReceta();
                detalle.setReceta(receta);
                detalle.setMedicamento(detalleReq.getMedicamento());
                detalle.setDosis(detalleReq.getDosis());
                detalle.setFrecuencia(detalleReq.getFrecuencia());
                detalle.setDuracion(detalleReq.getDuracion());
                detalle.setInstrucciones(detalleReq.getInstrucciones());
                detalles.add(detalle);
            }
            detalleRecetaRepository.saveAll(detalles);
        }

        // Notify paciente
        notificacionService.crearNotificacion(
                cita.getPaciente().getUser().getId(),
                "Nueva receta disponible",
                "El Dr. " + doctorUser.getNombre() + " " + doctorUser.getApellido()
                        + " ha emitido una receta para tu consulta. Revisa los detalles.",
                TipoNotificacion.RECETA
        );

        return mapToDto(receta, detalles);
    }

    public RecetaDto getRecetaById(UUID id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada con id: " + id));
        List<DetalleReceta> detalles = detalleRecetaRepository.findByRecetaId(receta.getId());
        return mapToDto(receta, detalles);
    }

    public List<RecetaDto> getMisRecetas(String emailPaciente) {
        User user = userRepository.findByEmail(emailPaciente)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Paciente paciente = pacienteRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Perfil de paciente no encontrado"));

        List<Receta> recetas = recetaRepository.findByPacienteIdOrderByFechaEmisionDesc(paciente.getId());
        return recetas.stream().map(receta -> {
            List<DetalleReceta> detalles = detalleRecetaRepository.findByRecetaId(receta.getId());
            return mapToDto(receta, detalles);
        }).collect(Collectors.toList());
    }

    public RecetaDto getRecetaByCitaId(UUID citaId) {
        Receta receta = recetaRepository.findByCitaId(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada para la cita: " + citaId));
        List<DetalleReceta> detalles = detalleRecetaRepository.findByRecetaId(receta.getId());
        return mapToDto(receta, detalles);
    }

    private RecetaDto mapToDto(Receta receta, List<DetalleReceta> detalles) {
        List<DetalleRecetaDto> detallesDto = detalles.stream()
                .map(d -> DetalleRecetaDto.builder()
                        .id(d.getId())
                        .medicamento(d.getMedicamento())
                        .dosis(d.getDosis())
                        .frecuencia(d.getFrecuencia())
                        .duracion(d.getDuracion())
                        .instrucciones(d.getInstrucciones())
                        .build())
                .collect(Collectors.toList());

        return RecetaDto.builder()
                .id(receta.getId())
                .citaId(receta.getCita().getId())
                .doctorId(receta.getDoctor().getId())
                .doctorNombre(receta.getDoctor().getUser().getNombre() + " " + receta.getDoctor().getUser().getApellido())
                .pacienteId(receta.getPaciente().getId())
                .pacienteNombre(receta.getPaciente().getUser().getNombre() + " " + receta.getPaciente().getUser().getApellido())
                .diagnostico(receta.getDiagnostico())
                .observaciones(receta.getObservaciones())
                .fechaEmision(receta.getFechaEmision())
                .detalles(detallesDto)
                .createdAt(receta.getCreatedAt())
                .build();
    }
}
