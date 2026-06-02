package com.taskmanager.service;

import com.taskmanager.dto.request.AuthRequest;
import com.taskmanager.dto.response.JwtResponse;
import com.taskmanager.dto.response.UserResponse;

public interface AuthService {
    JwtResponse login(AuthRequest.Login loginRequest);
    UserResponse register(AuthRequest.Register registerRequest);
}
