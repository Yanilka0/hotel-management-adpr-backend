package com.grandvista.backend.service;

import com.grandvista.backend.model.StaffUser;
import com.grandvista.backend.repository.StaffUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public StaffUser createStaffUser(String email, String fullName, String role) {
        if (staffUserRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        String randomPassword = generateRandomPassword();
        StaffUser user = new StaffUser();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(randomPassword));

        StaffUser savedUser = staffUserRepository.save(user);

        emailService.sendEmail(email, "Welcome to Grand Vista",
                "Your account has been created.\nUsername: " + email + "\nPassword: " + randomPassword);

        return savedUser;
    }

    public StaffUser login(String email, String password) {
        Optional<StaffUser> userOpt = staffUserRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            StaffUser user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                return user;
            }
        }
        throw new RuntimeException("Invalid credentials");
    }

    public void resetPassword(String email) {
        Optional<StaffUser> userOpt = staffUserRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            StaffUser user = userOpt.get();
            String newPassword = generateRandomPassword();
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            staffUserRepository.save(user);

            emailService.sendEmail(email, "Password Reset",
                    "Your password has been reset.\nNew Password: " + newPassword);
        } else {
            // For security, don't reveal if user exists, or throw a generic error.
            // Here we might just log or do nothing if we want to be stealthy.
            // But for now let's throw to be explicit for the UI.
            throw new RuntimeException("User not found");
        }
    }

    private String generateRandomPassword() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
