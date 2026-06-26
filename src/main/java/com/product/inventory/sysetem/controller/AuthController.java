package com.product.inventory.sysetem.controller;

import com.product.inventory.sysetem.dto.AuthRequest;
import com.product.inventory.sysetem.dto.AuthResponse;
import com.product.inventory.sysetem.dto.RegisterRequest;
import com.product.inventory.sysetem.entity.Role;
import com.product.inventory.sysetem.entity.User;
import com.product.inventory.sysetem.repository.UserRepository;
import com.product.inventory.sysetem.security.JwtUtil;
import com.product.inventory.sysetem.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getUser().getRole().name());

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
                .role(userDetails.getUser().getRole().name())
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findAll().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(request.getUsername()))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getUsername(), saved.getRole().name());

        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.builder()
                .token(token)
                .username(saved.getUsername())
                .role(saved.getRole().name())
                .build());
    }
}
