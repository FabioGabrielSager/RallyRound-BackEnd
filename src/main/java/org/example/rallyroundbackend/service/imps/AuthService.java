package org.example.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.rallyroundbackend.dto.AuthResponse;
import org.example.rallyroundbackend.dto.LoginRequest;
import org.example.rallyroundbackend.dto.RegisterRequest;
import org.example.rallyroundbackend.entity.users.ParticipantEntity;
import org.example.rallyroundbackend.entity.users.RoleEntity;
import org.example.rallyroundbackend.entity.users.UserEntity;
import org.example.rallyroundbackend.repository.user.RoleRepository;
import org.example.rallyroundbackend.repository.user.UserRepository;
import org.example.rallyroundbackend.service.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserEntity userEntity = userRepository.findByEmail(request.getUsername()).orElseThrow();


        String token=jwtService.getToken(userEntity);
        return AuthResponse.builder()
            .token(token)
            .build();

    }

    public AuthResponse register(RegisterRequest request) {
        ParticipantEntity participantEntity = modelMapper.map(request, ParticipantEntity.class);
        participantEntity.setPassword(passwordEncoder.encode(participantEntity.getPassword()));

        RoleEntity role = this.roleRepository.findByName("ROLE_PARTICIPANT").orElseThrow(
                () -> new EntityNotFoundException("Role not found")
        );

        participantEntity.setRoles(Set.of(role));

        userRepository.save(participantEntity);

        return AuthResponse.builder()
            .token(jwtService.getToken(participantEntity))
            .build();
        
    }

}
