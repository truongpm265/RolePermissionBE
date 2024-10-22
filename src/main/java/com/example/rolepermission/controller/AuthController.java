package com.example.rolepermission.controller;

import com.example.rolepermission.dto.request.AuthRequest;
import com.example.rolepermission.dto.request.IntrospectRequest;
import com.example.rolepermission.dto.response.AuthResponse;
import com.example.rolepermission.dto.response.IntrospectResponse;
import com.example.rolepermission.service.AuthService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/log-in")
    ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request){
        var result = authService.authenticate(request);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/introspect")
    ResponseEntity<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authService.introspect(request);
        return ResponseEntity.ok(result);
    }
}
