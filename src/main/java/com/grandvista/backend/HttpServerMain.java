package com.grandvista.backend;

import com.grandvista.backend.database.MongoDBConnection;
import com.grandvista.backend.handler.AuthHandler;
import com.grandvista.backend.handler.HealthHandler;
import com.grandvista.backend.repository.StaffUserRepository;
import com.grandvista.backend.service.AuthService;
import com.grandvista.backend.service.EmailService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpServerMain {

    public static void main(String[] args) {
        try {
            // Initialize MongoDB connection
            MongoDBConnection.getInstance();

            // Initialize services
            StaffUserRepository staffUserRepository = new StaffUserRepository();
            EmailService emailService = new EmailService();
            AuthService authService = new AuthService(staffUserRepository, emailService);

            // Create HTTP server
            int port = 8081;
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // Register handlers
            server.createContext("/health", new HealthHandler());
            server.createContext("/api/auth/register", new AuthHandler(authService));
            server.createContext("/api/auth/login", new AuthHandler(authService));
            server.createContext("/api/auth/reset-password", new AuthHandler(authService));

            // Register Swagger UI handlers
            server.createContext("/api-docs", new com.grandvista.backend.handler.ApiDocsHandler());
            server.createContext("/swagger-ui", new com.grandvista.backend.handler.SwaggerUIHandler());

            // Start server
            server.setExecutor(null); // creates a default executor
            server.start();

            System.out.println("Grand Vista Backend Server started on port " + port);
            System.out.println("Health check: http://localhost:" + port + "/health");
            System.out.println("API endpoints:");
            System.out.println("  POST http://localhost:" + port + "/api/auth/register");
            System.out.println("  POST http://localhost:" + port + "/api/auth/login");
            System.out.println("  POST http://localhost:" + port + "/api/auth/reset-password");

        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
