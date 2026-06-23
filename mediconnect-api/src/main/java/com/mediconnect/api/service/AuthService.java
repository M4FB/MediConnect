package com.mediconnect.api.service;

import com.mediconnect.api.dto.auth.AuthResponse;
import com.mediconnect.api.dto.auth.LoginRequest;
import com.mediconnect.api.dto.auth.RefreshTokenRequest;
import com.mediconnect.api.dto.auth.RegisterRequest;
import com.mediconnect.api.dto.user.UserDto;
import com.mediconnect.api.entity.Paciente;
import com.mediconnect.api.entity.User;
import com.mediconnect.api.entity.enums.Role;
import com.mediconnect.api.repository.PacienteRepository;
import com.mediconnect.api.repository.UserRepository;
import com.mediconnect.api.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PacienteRepository pacienteRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado: " + request.getEmail());
        }

        User user = new User();
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setTelefono(request.getTelefono());
        user.setRole(Role.PACIENTE);
        user.setActivo(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        Paciente paciente = new Paciente();
        paciente.setUser(user);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setDireccion(request.getDireccion());
        paciente.setGrupoSanguineo(request.getGrupoSanguineo());
        paciente.setAlergias(request.getAlergias());
        paciente.setCreatedAt(LocalDateTime.now());
        pacienteRepository.save(paciente);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        UserDto userDto = mapToUserDto(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(userDto)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        UserDto userDto = mapToUserDto(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(userDto)
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh token inválido o expirado");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(mapToUserDto(user))
                .build();
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .email(user.getEmail())
                .telefono(user.getTelefono())
                .role(user.getRole().name())
                .activo(user.isActivo())
                .fotoUrl(user.getFotoUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
