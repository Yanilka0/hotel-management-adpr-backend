package com.grandvista.backend.presentation.controller;

import com.grandvista.backend.presentation.dto.LoginRequest;
import com.grandvista.backend.presentation.dto.RegisterRequest;
import com.grandvista.backend.presentation.dto.ResetPasswordRequest;
import com.grandvista.backend.data.model.StaffUser;
import com.grandvista.backend.business.service.AuthService;
import com.grandvista.backend.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AuthController implements HttpHandler {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if ("POST".equals(method)) {
                if (path.endsWith("/register")) {
                    handleRegister(exchange);
                } else if (path.endsWith("/login")) {
                    handleLogin(exchange);
                } else if (path.endsWith("/reset-password")) {
                    handleResetPassword(exchange);
                } else {
                    sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange.getRequestBody());
        RegisterRequest request = JsonUtil.fromJson(body, RegisterRequest.class);

        if (request == null || request.getEmail() == null || request.getFullName() == null
                || request.getRole() == null) {
            sendResponse(exchange, 400, "{\"error\": \"Missing required fields\"}");
            return;
        }

        StaffUser user = authService.createStaffUser(request.getEmail(), request.getFullName(), request.getRole());
        String response = JsonUtil.toJson(user);
        sendResponse(exchange, 200, response);
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange.getRequestBody());
        LoginRequest request = JsonUtil.fromJson(body, LoginRequest.class);

        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            sendResponse(exchange, 400, "{\"error\": \"Missing required fields\"}");
            return;
        }

        StaffUser user = authService.login(request.getEmail(), request.getPassword());
        String response = JsonUtil.toJson(user);
        sendResponse(exchange, 200, response);
    }

    private void handleResetPassword(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange.getRequestBody());
        ResetPasswordRequest request = JsonUtil.fromJson(body, ResetPasswordRequest.class);

        if (request == null || request.getEmail() == null) {
            sendResponse(exchange, 400, "{\"error\": \"Missing required fields\"}");
            return;
        }

        authService.resetPassword(request.getEmail());
        String response = "{\"message\": \"Password reset email sent.\"}";
        sendResponse(exchange, 200, response);
    }

    private String readRequestBody(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
