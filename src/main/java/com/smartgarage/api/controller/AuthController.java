package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.dto.request.LoginRequest;
import com.smartgarage.api.dto.request.RegisterRequest;
import com.smartgarage.api.dto.response.AuthResponse;
import com.smartgarage.api.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, response, ResponseMessage.SUCCESS_MESSAGE));
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, response, ResponseMessage.SUCCESS_MESSAGE));
    }
}
