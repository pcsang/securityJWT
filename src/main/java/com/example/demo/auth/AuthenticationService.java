package com.example.demo.auth;

import com.example.demo.model.UserInfoDetails;
import com.example.demo.service.JwtService;
import com.example.demo.model.AuthenticationRequest;
import com.example.demo.model.AuthenticationResponse;
import com.example.demo.model.RegisterRequest;
import com.example.demo.model.Role;
import com.example.demo.model.UserInfo;
import com.example.demo.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        UserInfo userInfo = UserInfo.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(userInfo);
        UserInfoDetails userInfoDetails = new UserInfoDetails(userInfo);
        String jwtToken = jwtService.generateToken(userInfoDetails);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserInfo user = repository.findByEmail(request.getEmail()).orElse(new UserInfo());
        UserInfoDetails userInfoDetails = new UserInfoDetails(user);
        String jwtToken = jwtService.generateToken(userInfoDetails);
        return AuthenticationResponse.builder()
                .token(jwtToken).build();
    }
}
