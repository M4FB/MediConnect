package com.mediconnect.api.config;

import com.mediconnect.api.entity.*;
import com.mediconnect.api.entity.enums.*;
import com.mediconnect.api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;
    private final RecetaRepository recetaRepository;
    private final DetalleRecetaRepository detalleRecetaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      DoctorRepository doctorRepository,
                      PacienteRepository pacienteRepository,
                      CitaRepository citaRepository,
                      RecetaRepository recetaRepository,
                      DetalleRecetaRepository detalleRecetaRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
        this.recetaRepository = recetaRepository;
        this.detalleRecetaRepository = detalleRecetaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping data initialization.");
            return;
        }

        log.info("Seeding database with initial data...");

        String encodedPassword = passwordEncoder.encode("password123");
        String adminPassword = passwordEncoder.encode("admin123");

        // ── Admin User ──────────────────────────────────────────────
        User adminUser = new User();
        adminUser.setEmail("admin@mediconnect.com");
        adminUser.setPasswordHash(adminPassword);
        adminUser.setNombre("Admin");
        adminUser.setApellido("Sistema");
        adminUser.setTelefono("+52 55 1234 0000");
        adminUser.setRole(Role.ADMIN);
        adminUser.setActivo(true);
        adminUser = userRepository.save(adminUser);
        log.info("Created admin user: {}", adminUser.getEmail());

        // ── Doctor Users & Profiles ─────────────────────────────────
        List<Doctor> doctors = new ArrayList<>();

        // Doctor 1 - Cardiología
        User doctorUser1 = new User();
        doctorUser1.setEmail("dr.martinez@mediconnect.com");
        doctorUser1.setPasswordHash(encodedPassword);
        doctorUser1.setNombre("Carlos");
        doctorUser1.setApellido("Martínez López");
        doctorUser1.setTelefono("+52 55 1234 0001");
        doctorUser1.setRole(Role.DOCTOR);
        doctorUser1.setActivo(true);
        doctorUser1 = userRepository.save(doctorUser1);

        Doctor doctor1 = new Doctor();
        doctor1.setUser(doctorUser1);
        doctor1.setEspecialidad("Cardiología");
        doctor1.setNumeroColegiado("COL-CARD-001");
        doctor1.setDescripcion("Cardiólogo con más de 15 años de experiencia en el diagnóstico y tratamiento de enfermedades cardiovasculares.");
        doctor1.setCostoCita(new BigDecimal("800.00"));
        doctor1.setHorarioInicio(LocalTime.of(8, 0));
        doctor1.setHorarioFin(LocalTime.of(17, 0));
        doctor1.setDiasAtencion("Lunes,Martes,Miércoles,Jueves,Viernes");
        doctor1 = doctorRepository.save(doctor1);
        doctors.add(doctor1);

        // Doctor 2 - Dermatología
        User doctorUser2 = new User();
        doctorUser2.setEmail("dra.garcia@mediconnect.com");
        doctorUser2.setPasswordHash(encodedPassword);
        doctorUser2.setNombre("Ana");
        doctorUser2.setApellido("García Hernández");
        doctorUser2.setTelefono("+52 55 1234 0002");
        doctorUser2.setRole(Role.DOCTOR);
        doctorUser2.setActivo(true);
        doctorUser2 = userRepository.save(doctorUser2);

        Doctor doctor2 = new Doctor();
        doctor2.setUser(doctorUser2);
        doctor2.setEspecialidad("Dermatología");
        doctor2.setNumeroColegiado("COL-DERM-002");
        doctor2.setDescripcion("Dermatóloga certificada especializada en dermatología clínica, cosmética y quirúrgica.");
        doctor2.setCostoCita(new BigDecimal("650.00"));
        doctor2.setHorarioInicio(LocalTime.of(9, 0));
        doctor2.setHorarioFin(LocalTime.of(18, 0));
        doctor2.setDiasAtencion("Lunes,Martes,Miércoles,Jueves,Viernes,Sábado");
        doctor2 = doctorRepository.save(doctor2);
        doctors.add(doctor2);

        // Doctor 3 - Pediatría
        User doctorUser3 = new User();
        doctorUser3.setEmail("dr.rodriguez@mediconnect.com");
        doctorUser3.setPasswordHash(encodedPassword);
        doctorUser3.setNombre("Miguel");
        doctorUser3.setApellido("Rodríguez Sánchez");
        doctorUser3.setTelefono("+52 55 1234 0003");
        doctorUser3.setRole(Role.DOCTOR);
        doctorUser3.setActivo(true);
        doctorUser3 = userRepository.save(doctorUser3);

        Doctor doctor3 = new Doctor();
        doctor3.setUser(doctorUser3);
        doctor3.setEspecialidad("Pediatría");
        doctor3.setNumeroColegiado("COL-PED-003");
        doctor3.setDescripcion("Pediatra con amplia experiencia en el cuidado integral de niños y adolescentes.");
        doctor3.setCostoCita(new BigDecimal("550.00"));
        doctor3.setHorarioInicio(LocalTime.of(8, 30));
        doctor3.setHorarioFin(LocalTime.of(16, 30));
        doctor3.setDiasAtencion("Lunes,Martes,Miércoles,Jueves,Viernes");
        doctor3 = doctorRepository.save(doctor3);
        doctors.add(doctor3);

        log.info("Created {} doctors", doctors.size());

        // ── Patient Users & Profiles ────────────────────────────────
        List<Paciente> pacientes = new ArrayList<>();

        String[][] patientData = {
                {"paciente1@email.com", "Juan", "Pérez Gómez", "+52 55 9876 0001", "1990-05-15", "Masculino", "O+", "Av. Siempre Viva 123"},
                {"paciente2@email.com", "María", "López Torres", "+52 55 9876 0002", "1985-08-22", "Femenino", "A+", "Av. Siempre Viva 124"},
                {"paciente3@email.com", "Roberto", "Sánchez Díaz", "+52 55 9876 0003", "1978-12-03", "Masculino", "B+", "Av. Siempre Viva 125"},
                {"paciente4@email.com", "Laura", "Fernández Ruiz", "+52 55 9876 0004", "1995-03-10", "Femenino", "AB-", "Av. Siempre Viva 126"},
                {"paciente5@email.com", "Diego", "Morales Castro", "+52 55 9876 0005", "2000-07-28", "Masculino", "O-", "Av. Siempre Viva 127"},
        };

        for (String[] pd : patientData) {
            User patientUser = new User();
            patientUser.setEmail(pd[0]);
            patientUser.setPasswordHash(encodedPassword);
            patientUser.setNombre(pd[1]);
            patientUser.setApellido(pd[2]);
            patientUser.setTelefono(pd[3]);
            patientUser.setRole(Role.PACIENTE);
            patientUser.setActivo(true);
            patientUser = userRepository.save(patientUser);

            Paciente paciente = new Paciente();
            paciente.setUser(patientUser);
            paciente.setFechaNacimiento(LocalDate.parse(pd[4]));
            paciente.setGenero(pd[5]);
            paciente.setGrupoSanguineo(pd[6]);
            paciente.setDireccion(pd[7]);
            paciente = pacienteRepository.save(paciente);
            pacientes.add(paciente);
        }

        log.info("Created {} patients", pacientes.size());

        // ── Appointments (Citas) ────────────────────────────────────
        LocalDateTime now = LocalDateTime.now();
        List<Cita> citas = new ArrayList<>();

        // Past completed appointments
        citas.add(createCita(pacientes.get(0), doctors.get(0), now.minusDays(30), EstadoCita.COMPLETADA, "Dolor en el pecho y dificultad para respirar", "Paciente estable. ECG normal. Se recomienda seguimiento en 3 meses."));
        citas.add(createCita(pacientes.get(1), doctors.get(1), now.minusDays(20), EstadoCita.COMPLETADA, "Erupciones cutáneas en brazos", "Diagnóstico: dermatitis atópica. Se prescribe tratamiento tópico."));
        citas.add(createCita(pacientes.get(2), doctors.get(2), now.minusDays(15), EstadoCita.COMPLETADA, "Control pediátrico de rutina para mi hijo", "Niño en buen estado de salud. Vacunas al día."));

        // Cancelled appointment
        citas.add(createCita(pacientes.get(3), doctors.get(0), now.minusDays(5), EstadoCita.CANCELADA, "Revisión cardiológica", null));

        // Upcoming confirmed appointments
        citas.add(createCita(pacientes.get(0), doctors.get(1), now.plusDays(3), EstadoCita.CONFIRMADA, "Revisión de lunares", null));
        citas.add(createCita(pacientes.get(1), doctors.get(0), now.plusDays(5), EstadoCita.CONFIRMADA, "Control de presión arterial", null));

        // Upcoming pending appointments
        citas.add(createCita(pacientes.get(2), doctors.get(0), now.plusDays(7), EstadoCita.PENDIENTE, "Chequeo cardíaco anual", null));
        citas.add(createCita(pacientes.get(3), doctors.get(2), now.plusDays(10), EstadoCita.PENDIENTE, "Consulta pediátrica para mi hija", null));
        citas.add(createCita(pacientes.get(4), doctors.get(2), now.plusDays(14), EstadoCita.PENDIENTE, "Vacunación infantil", null));

        citaRepository.saveAll(citas);
        log.info("Created {} appointments", citas.size());

        // ── Prescriptions (Recetas) ─────────────────────────────────
        // Receta 1 - from completed cita 1 (Cardiología)
        Receta receta1 = new Receta();
        receta1.setCita(citas.get(0));
        receta1.setDoctor(doctors.get(0));
        receta1.setPaciente(pacientes.get(0));
        receta1.setDiagnostico("Hipertensión arterial leve. Sin hallazgos cardíacos significativos en ECG.");
        receta1.setObservaciones("Reducir consumo de sal. Realizar ejercicio cardiovascular moderado 30 min/día.");
        receta1.setFechaEmision(now.minusDays(30));
        receta1 = recetaRepository.save(receta1);

        DetalleReceta detalle1a = new DetalleReceta();
        detalle1a.setReceta(receta1);
        detalle1a.setMedicamento("Losartán");
        detalle1a.setDosis("50mg");
        detalle1a.setFrecuencia("Cada 24 horas");
        detalle1a.setDuracion("30 días");
        detalle1a.setInstrucciones("Tomar por la mañana con el desayuno. No suspender sin indicación médica.");
        detalleRecetaRepository.save(detalle1a);

        DetalleReceta detalle1b = new DetalleReceta();
        detalle1b.setReceta(receta1);
        detalle1b.setMedicamento("Aspirina");
        detalle1b.setDosis("100mg");
        detalle1b.setFrecuencia("Cada 24 horas");
        detalle1b.setDuracion("30 días");
        detalle1b.setInstrucciones("Tomar después de la comida principal.");
        detalleRecetaRepository.save(detalle1b);

        // Receta 2 - from completed cita 2 (Dermatología)
        Receta receta2 = new Receta();
        receta2.setCita(citas.get(1));
        receta2.setDoctor(doctors.get(1));
        receta2.setPaciente(pacientes.get(1));
        receta2.setDiagnostico("Dermatitis atópica moderada en ambos brazos.");
        receta2.setObservaciones("Evitar jabones con fragancia. Hidratar la piel después del baño.");
        receta2.setFechaEmision(now.minusDays(20));
        receta2 = recetaRepository.save(receta2);

        DetalleReceta detalle2a = new DetalleReceta();
        detalle2a.setReceta(receta2);
        detalle2a.setMedicamento("Betametasona crema 0.05%");
        detalle2a.setDosis("Aplicar capa fina");
        detalle2a.setFrecuencia("Cada 12 horas");
        detalle2a.setDuracion("14 días");
        detalle2a.setInstrucciones("Aplicar en áreas afectadas. No usar en cara ni pliegues.");
        detalleRecetaRepository.save(detalle2a);

        DetalleReceta detalle2b = new DetalleReceta();
        detalle2b.setReceta(receta2);
        detalle2b.setMedicamento("Loratadina");
        detalle2b.setDosis("10mg");
        detalle2b.setFrecuencia("Cada 24 horas");
        detalle2b.setDuracion("14 días");
        detalle2b.setInstrucciones("Tomar por la noche para controlar la comezón.");
        detalleRecetaRepository.save(detalle2b);

        // Receta 3 - from completed cita 3 (Pediatría)
        Receta receta3 = new Receta();
        receta3.setCita(citas.get(2));
        receta3.setDoctor(doctors.get(2));
        receta3.setPaciente(pacientes.get(2));
        receta3.setDiagnostico("Control pediátrico sin hallazgos patológicos. Desarrollo normal para la edad.");
        receta3.setObservaciones("Próxima vacuna en 6 meses. Alimentación balanceada.");
        receta3.setFechaEmision(now.minusDays(15));
        receta3 = recetaRepository.save(receta3);

        DetalleReceta detalle3a = new DetalleReceta();
        detalle3a.setReceta(receta3);
        detalle3a.setMedicamento("Vitamina D3 gotas");
        detalle3a.setDosis("400 UI (2 gotas)");
        detalle3a.setFrecuencia("Cada 24 horas");
        detalle3a.setDuracion("90 días");
        detalle3a.setInstrucciones("Administrar por la mañana directamente o mezclado con alimento.");
        detalleRecetaRepository.save(detalle3a);

        log.info("Created 3 prescriptions with details");
        log.info("Database seeding completed successfully!");
    }

    private Cita createCita(Paciente paciente, Doctor doctor, LocalDateTime fechaHora,
                            EstadoCita estado, String motivo, String notas) {
        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setDoctor(doctor);
        cita.setFechaHora(fechaHora);
        cita.setEstado(estado);
        cita.setMotivo(motivo);
        cita.setNotas(notas);
        cita.setCodigoQr(UUID.randomUUID().toString());
        return cita;
    }
}
