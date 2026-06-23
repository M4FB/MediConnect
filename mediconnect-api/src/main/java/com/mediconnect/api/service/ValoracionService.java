package com.mediconnect.api.service;

import com.mediconnect.api.dto.valoracion.CrearValoracionRequest;
import com.mediconnect.api.dto.valoracion.ValoracionDto;
import com.mediconnect.api.entity.Doctor;
import com.mediconnect.api.entity.Paciente;
import com.mediconnect.api.entity.User;
import com.mediconnect.api.entity.Valoracion;
import com.mediconnect.api.entity.enums.EstadoCita;
import com.mediconnect.api.repository.CitaRepository;
import com.mediconnect.api.repository.DoctorRepository;
import com.mediconnect.api.repository.PacienteRepository;
import com.mediconnect.api.repository.UserRepository;
import com.mediconnect.api.repository.ValoracionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;
    private final UserRepository userRepository;
    private final CitaRepository citaRepository;

    public ValoracionService(ValoracionRepository valoracionRepository,
                             DoctorRepository doctorRepository,
                             PacienteRepository pacienteRepository,
                             UserRepository userRepository,
                             CitaRepository citaRepository) {
        this.valoracionRepository = valoracionRepository;
        this.doctorRepository = doctorRepository;
        this.pacienteRepository = pacienteRepository;
        this.userRepository = userRepository;
        this.citaRepository = citaRepository;
    }

    @Transactional
    public ValoracionDto crearValoracion(String emailPaciente, CrearValoracionRequest request) {
        User user = userRepository.findByEmail(emailPaciente)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Paciente paciente = pacienteRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Perfil de paciente no encontrado"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

        // Validate that the patient had a completed appointment with this doctor
        boolean tuvoConsulta = citaRepository.existsByPacienteIdAndDoctorIdAndEstado(
                paciente.getId(), doctor.getId(), EstadoCita.COMPLETADA);

        if (!tuvoConsulta) {
            throw new IllegalArgumentException("Solo puedes valorar a un doctor con quien hayas tenido una consulta completada");
        }

        // Check if already rated
        if (valoracionRepository.existsByPacienteIdAndDoctorId(paciente.getId(), doctor.getId())) {
            throw new IllegalArgumentException("Ya has valorado a este doctor");
        }

        Valoracion valoracion = new Valoracion();
        valoracion.setPaciente(paciente);
        valoracion.setDoctor(doctor);
        valoracion.setCalificacion(request.getCalificacion());
        valoracion.setComentario(request.getComentario());
        valoracion.setCreatedAt(LocalDateTime.now());
        valoracion = valoracionRepository.save(valoracion);

        return mapToDto(valoracion);
    }

    public List<ValoracionDto> getValoracionesDoctor(UUID doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

        List<Valoracion> valoraciones = valoracionRepository.findByDoctorIdOrderByCreatedAtDesc(doctor.getId());

        return valoraciones.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private ValoracionDto mapToDto(Valoracion valoracion) {
        return ValoracionDto.builder()
                .id(valoracion.getId())
                .pacienteId(valoracion.getPaciente().getId())
                .pacienteNombre(valoracion.getPaciente().getUser().getNombre() + " "
                        + valoracion.getPaciente().getUser().getApellido())
                .doctorId(valoracion.getDoctor().getId())
                .calificacion(valoracion.getCalificacion())
                .comentario(valoracion.getComentario())
                .createdAt(valoracion.getCreatedAt())
                .build();
    }
}
