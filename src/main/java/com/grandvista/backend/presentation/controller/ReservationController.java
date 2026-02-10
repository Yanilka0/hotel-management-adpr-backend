package com.grandvista.backend.presentation.controller;

import com.grandvista.backend.business.service.ReservationService;
import com.grandvista.backend.data.model.Reservation;
import com.grandvista.backend.presentation.dto.CreateBookingRequest;
import com.grandvista.backend.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ReservationController implements HttpHandler {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
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

        if ("POST".equals(exchange.getRequestMethod())) {
            handleCreateReservation(exchange);
        } else {
            sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
        }
    }

    private void handleCreateReservation(HttpExchange exchange) throws IOException {
        try {
            String body = readRequestBody(exchange.getRequestBody());
            // Need to handle custom deserialization for LocalDate if JsonUtil doesn't
            // support it directly
            // For simplicity, assuming JsonUtil uses Gson with LocalDate adapter or
            // similar,
            // if not we might need to adjust JsonUtil or use a wrapper.
            // Let's assume standard JSON mapping for now.
            CreateBookingRequest request = JsonUtil.fromJson(body, CreateBookingRequest.class);

            if (request == null) {
                sendResponse(exchange, 400, "{\"error\": \"Invalid request body\"}");
                return;
            }

            Reservation reservation = reservationService.createReservation(request);
            String response = JsonUtil.toJson(reservation);
            sendResponse(exchange, 201, response);

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
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
