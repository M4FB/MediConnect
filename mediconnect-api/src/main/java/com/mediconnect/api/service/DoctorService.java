package com.mediconnect.api.service;

import com.mediconnect.api.dto.doctor.DoctorDto;
import com.mediconnect.api.dto.doctor.DoctorRegistrationRequest;
import com.mediconnect.api.dto.doctor.HorarioDisponibleDto;
import com.mediconnect.api.entity.Cita;
import com.mediconnect.api.entity.Doctor;
import com.mediconnect.api.entity.User;
import com.mediconnect.api.entity.enums.EstadoCita;
import com.mediconnect.api.entity.enums.Role;
import com.mediconnect.api.repository.CitaRepository;
import com.mediconnect.api.repository.DoctorRepository;
import com.mediconnect.api.repository.UserRepository;
import com.mediconnect.api.repository.ValoracionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final CitaRepository citaRepository;
    private final ValoracionRepository valoracionRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository,
                         UserRepository userRepository,
                         CitaRepository citaRepository,
                         ValoracionRepository valoracionRepository,
                         PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.citaRepository = citaRepository;
        this.valoracionRepository = valoracionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<DoctorDto> getAllDoctors(String search, String especialidad) {
        List<Doctor> doctors;

        if (search != null && !search.isBlank() && especialidad != null && !especialidad.isBlank()) {
            doctors = doctorRepository.findByEspecialidadContainingIgnoreCaseAndUser_NombreContainingIgnoreCase(
                    especialidad, search);
        } else if (especialidad != null && !especialidad.isBlank()) {
            doctors = doctorRepository.findByEspecialidadContainingIgnoreCase(especialidad);
        } else if (search != null && !search.isBlank()) {
            doctors = doctorRepository.findByUser_NombreContainingIgnoreCaseOrUser_ApellidoContainingIgnoreCase(
                    search, search);
        } else {
            doctors = doctorRepository.findAll();
        }

        return doctors.stream()
                .filter(d -> d.getUser().isActivo())
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DoctorDto getDoctorById(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con id: " + id));
        return mapToDto(doctor);
    }

    public List<HorarioDisponibleDto> getHorariosDisponibles(UUID doctorId, LocalDate fecha) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

        LocalTime horarioInicio = doctor.getHorarioInicio();
        LocalTime horarioFin = doctor.getHorarioFin();

        if (horarioInicio == null || horarioFin == null) {
            return List.of();
        }

        LocalDateTime fechaInicio = fecha.atStartOfDay();
        LocalDateTime fechaFin = fecha.atTime(23, 59, 59);

        List<Cita> citasExistentes = citaRepository.findByDoctorIdAndFechaHoraBetweenAndEstadoNotIn(
                doctorId, fechaInicio, fechaFin,
                List.of(EstadoCita.CANCELADA));

        Set<LocalTime> horasOcupadas = citasExistentes.stream()
                .map(cita -> cita.getFechaHora().toLocalTime())
                .collect(Collectors.toSet());

        List<HorarioDisponibleDto> horariosDisponibles = new ArrayList<>();
        LocalTime slotTime = horarioInicio;

        while (slotTime.isBefore(horarioFin)) {
            boolean disponible = !horasOcupadas.contains(slotTime);

            if (fecha.equals(LocalDate.now()) && slotTime.isBefore(LocalTime.now())) {
                disponible = false;
            }

            horariosDisponibles.add(HorarioDisponibleDto.builder()
                    .hora(slotTime)
                    .disponible(disponible)
                    .build());

            slotTime = slotTime.plusMinutes(30);
        }

        return horariosDisponibles;
    }

    @Transactional
    public DoctorDto registerDoctor(DoctorRegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado: " + request.getEmail());
        }

        User user = new User();
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setTelefono(request.getTelefono());
        user.setRole(Role.DOCTOR);
        user.setActivo(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setEspecialidad(request.getEspecialidad());
        doctor.setNumeroColegiado(request.getNumeroColegiado());
        doctor.setDescripcion(request.getDescripcion());
        doctor.setHorarioInicio(request.getHorarioInicio());
        doctor.setHorarioFin(request.getHorarioFin());
        doctor.setCostoCita(request.getCostoCita());
        doctor.setCreatedAt(LocalDateTime.now());
        doctor = doctorRepository.save(doctor);

        return mapToDto(doctor);
    }

    private DoctorDto mapToDto(Doctor doctor) {
        Double promedioValoracion = valoracionRepository.findAverageCalificacionByDoctorId(doctor.getId());
        Long totalValoraciones = valoracionRepository.countByDoctorId(doctor.getId());

        return DoctorDto.builder()
                .id(doctor.getId())
                .userId(doctor.getUser().getId())
                .nombre(doctor.getUser().getNombre())
                .apellido(doctor.getUser().getApellido())
                .email(doctor.getUser().getEmail())
                .telefono(doctor.getUser().getTelefono())
                .fotoUrl(doctor.getUser().getFotoUrl())
                .especialidad(doctor.getEspecialidad())
                .numeroColegiado(doctor.getNumeroColegiado())
                .descripcion(doctor.getDescripcion())
                .horarioInicio(doctor.getHorarioInicio())
                .horarioFin(doctor.getHorarioFin())
                .costoCita(doctor.getCostoCita())
                .promedioValoracion(promedioValoracion != null ? promedioValoracion : 0.0)
                .totalValoraciones(totalValoraciones != null ? totalValoraciones : 0L)
                .build();
    }
}
