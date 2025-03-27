package com.genysyxtechnologies.service_request_system.controller;

import com.genysyxtechnologies.service_request_system.config.JwtUtil;
import com.genysyxtechnologies.service_request_system.constant.Role;
import com.genysyxtechnologies.service_request_system.dtos.request.ChangePasswordRequest;
import com.genysyxtechnologies.service_request_system.dtos.security.LoginRequest;
import com.genysyxtechnologies.service_request_system.dtos.security.LoginResponse;
import com.genysyxtechnologies.service_request_system.dtos.security.SignupRequest;
import com.genysyxtechnologies.service_request_system.model.User;
import com.genysyxtechnologies.service_request_system.repository.UserRepository;
import com.genysyxtechnologies.service_request_system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.identifier(), request.password())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.identifier());
        String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.getRoles().add(Role.REQUESTER); // Auto-assign Requester role
        userRepository.save(user);
        return ResponseEntity.ok("User created successfully");
    }

    @Operation(summary = "Change password", description = "Allows a manager to change their own password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid old password or new password")
    })
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
