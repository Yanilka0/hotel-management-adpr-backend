package com.grandvista.backend.controller;

import com.grandvista.backend.model.StaffUser;
import com.grandvista.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<StaffUser> register(@RequestParam String email,
            @RequestParam String fullName,
            @RequestParam String role) {
        StaffUser user = authService.createStaffUser(email, fullName, role);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<StaffUser> login(@RequestParam String email, @RequestParam String password) {
        StaffUser user = authService.login(email, password);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        authService.resetPassword(email);
        return ResponseEntity.ok("Password reset email sent.");
    }
}
