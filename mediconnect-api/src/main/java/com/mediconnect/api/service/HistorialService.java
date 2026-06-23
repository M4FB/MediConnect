package com.mediconnect.api.service;

import com.mediconnect.api.dto.historial.CrearHistorialRequest;
import com.mediconnect.api.dto.historial.HistorialMedicoDto;
import com.mediconnect.api.entity.HistorialMedico;
import com.mediconnect.api.entity.Paciente;
import com.mediconnect.api.entity.User;
import com.mediconnect.api.repository.HistorialMedicoRepository;
import com.mediconnect.api.repository.PacienteRepository;
import com.mediconnect.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HistorialService {

    private final HistorialMedicoRepository historialMedicoRepository;
    private final PacienteRepository pacienteRepository;
    private final UserRepository userRepository;

    public HistorialService(HistorialMedicoRepository historialMedicoRepository,
                            PacienteRepository pacienteRepository,
                            UserRepository userRepository) {
        this.historialMedicoRepository = historialMedicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.userRepository = userRepository;
    }

    public List<HistorialMedicoDto> getHistorial(String emailPaciente) {
        User user = userRepository.findByEmail(emailPaciente)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Paciente paciente = pacienteRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Perfil de paciente no encontrado"));

        List<HistorialMedico> historiales = historialMedicoRepository
                .findByPacienteIdOrderByFechaDesc(paciente.getId());

        return historiales.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public HistorialMedicoDto crearEntrada(String email, CrearHistorialRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Paciente paciente = pacienteRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Perfil de paciente no encontrado"));

        HistorialMedico historial = new HistorialMedico();
        historial.setPaciente(paciente);
        historial.setTipo(request.getTipo());
        historial.setTitulo(request.getTitulo());
        historial.setDescripcion(request.getDescripcion());
        historial.setFecha(request.getFecha() != null ? request.getFecha() : LocalDateTime.now());
        historial.setDoctorNombre(request.getDoctorNombre());
        historial.setArchivoUrl(request.getArchivoUrl());
        historial.setCreatedAt(LocalDateTime.now());
        historial = historialMedicoRepository.save(historial);

        return mapToDto(historial);
    }

    public HistorialMedicoDto getEntradaById(UUID id) {
        HistorialMedico historial = historialMedicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrada de historial no encontrada con id: " + id));
        return mapToDto(historial);
    }

    private HistorialMedicoDto mapToDto(HistorialMedico historial) {
        return HistorialMedicoDto.builder()
                .id(historial.getId())
                .pacienteId(historial.getPaciente().getId())
                .tipo(historial.getTipo().name())
                .titulo(historial.getTitulo())
                .descripcion(historial.getDescripcion())
                .fecha(historial.getFecha())
                .doctorNombre(historial.getDoctorNombre())
                .archivoUrl(historial.getArchivoUrl())
                .createdAt(historial.getCreatedAt())
                .build();
    }
}
