package com.taskmanager.controller;

import com.taskmanager.dto.request.AuthRequest;
import com.taskmanager.dto.response.ApiResponse;
import com.taskmanager.dto.response.JwtResponse;
import com.taskmanager.dto.response.UserResponse;
import com.taskmanager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration and login APIs")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with USER role by default")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody AuthRequest.Register request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(201).body(ApiResponse.created(user, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and receive JWT token")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody AuthRequest.Login request) {
        JwtResponse jwt = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(jwt, "Login successful"));
    }
}
