package com.grandvista.backend.business.service;

import com.grandvista.backend.data.model.StaffUser;
import com.grandvista.backend.data.repository.StaffUserRepository;
import com.grandvista.backend.util.PasswordUtil;

import java.util.Optional;
import java.util.Random;

public class AuthService {

    private final StaffUserRepository staffUserRepository;
    private final EmailService emailService;

    public AuthService(StaffUserRepository staffUserRepository, EmailService emailService) {
        this.staffUserRepository = staffUserRepository;
        this.emailService = emailService;
    }

    public StaffUser createStaffUser(String email, String fullName, String role) {
        if (staffUserRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        String randomPassword = generateRandomPassword();
        StaffUser user = new StaffUser();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        user.setPasswordHash(PasswordUtil.hashPassword(randomPassword));

        StaffUser savedUser = staffUserRepository.save(user);

        emailService.sendEmail(email, "Welcome to Grand Vista",
                "Your account has been created.\\nUsername: " + email + "\\nPassword: " + randomPassword);

        return savedUser;
    }

    public StaffUser login(String email, String password) {
        Optional<StaffUser> userOpt = staffUserRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            StaffUser user = userOpt.get();
            if (PasswordUtil.checkPassword(password, user.getPasswordHash())) {
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
            user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
            staffUserRepository.save(user);

            emailService.sendEmail(email, "Password Reset",
                    "Your password has been reset.\\nNew Password: " + newPassword);
        } else {
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
