package com.milanpopovic.hotelinventory.controller;

import com.milanpopovic.hotelinventory.dto.LoginRequestDTO;
import com.milanpopovic.hotelinventory.dto.LoginResponseDTO;
import com.milanpopovic.hotelinventory.security.JwtService;
import com.milanpopovic.hotelinventory.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        var user = userService.getUserByName(request.getUsername());

        if (!userService.checkPassword(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new LoginResponseDTO(token, user.getId(), user.getUsername(), user.getRole());
    }
}
