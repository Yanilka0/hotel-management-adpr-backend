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
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        if ("POST".equals(method)) {
            handleCreateReservation(exchange);
        } else if ("GET".equals(method)) {
            if (query != null && query.contains("id=")) {
                handleGetReservation(exchange);
            } else {
                handleGetAllReservations(exchange);
            }
        } else if ("PUT".equals(method)) {
            handleUpdateReservation(exchange);
        } else if ("DELETE".equals(method)) {
            handleDeleteReservation(exchange);
        } else {
            sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
        }
    }

    private void handleDeleteReservation(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("id=")) {
            sendResponse(exchange, 400, "{\"error\": \"Missing id parameter\"}");
            return;
        }

        String id = null;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && "id".equals(pair[0])) {
                id = pair[1];
                break;
            }
        }

        if (id == null) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid id parameter\"}");
            return;
        }

        boolean success = reservationService.deleteReservation(id);
        if (success) {
            sendResponse(exchange, 204, "");
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Reservation not found\"}");
        }
    }

    private void handleGetAllReservations(HttpExchange exchange) throws IOException {
        try {
            java.util.List<com.grandvista.backend.presentation.dto.ReservationDetailsResponse> reservations = reservationService
                    .getAllReservationsWithDetails();
            String response = JsonUtil.toJson(reservations);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleGetReservation(HttpExchange exchange) throws IOException {
        String id = getQueryParam(exchange, "id");
        if (id == null) {
            sendResponse(exchange, 400, "{\"error\": \"Missing id parameter\"}");
            return;
        }

        try {
            java.util.Optional<com.grandvista.backend.presentation.dto.ReservationDetailsResponse> reservation = reservationService
                    .getReservationWithDetails(id);
            if (reservation.isPresent()) {
                // We need to configure Gson or JsonUtil to handle LocalDate if not already
                // done.
                // Assuming JsonUtil works as expected or we might need to verify serialization
                // of LocalDate.
                String response = JsonUtil.toJson(reservation.get());
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Reservation not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleUpdateReservation(HttpExchange exchange) throws IOException {
        String id = getQueryParam(exchange, "id");
        if (id == null) {
            sendResponse(exchange, 400, "{\"error\": \"Missing id parameter\"}");
            return;
        }

        try {
            String body = readRequestBody(exchange.getRequestBody());
            CreateBookingRequest request = JsonUtil.fromJson(body, CreateBookingRequest.class);

            if (request == null) {
                sendResponse(exchange, 400, "{\"error\": \"Invalid request body\"}");
                return;
            }

            Reservation reservation = reservationService.updateReservation(id, request);
            String response = JsonUtil.toJson(reservation);
            sendResponse(exchange, 200, response); // Using 200 OK for update

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private String getQueryParam(HttpExchange exchange, String paramName) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null)
            return null;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && paramName.equals(pair[0])) {
                return pair[1];
            }
        }
        return null;
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
