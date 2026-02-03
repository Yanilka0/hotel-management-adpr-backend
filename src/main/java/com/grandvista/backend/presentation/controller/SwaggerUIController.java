package com.grandvista.backend.presentation.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class SwaggerUIController implements HttpHandler {

    private static final String SWAGGER_HTML = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "  <meta charset=\"utf-8\" />\n" +
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n" +
            "  <meta name=\"description\" content=\"Swagger UI\" />\n" +
            "  <title>Grand Vista API Documentation</title>\n" +
            "  <link rel=\"stylesheet\" href=\"https://unpkg.com/swagger-ui-dist@5.11.0/swagger-ui.css\" />\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div id=\"swagger-ui\"></div>\n" +
            "  <script src=\"https://unpkg.com/swagger-ui-dist@5.11.0/swagger-ui-bundle.js\" crossorigin></script>\n" +
            "  <script src=\"https://unpkg.com/swagger-ui-dist@5.11.0/swagger-ui-standalone-preset.js\" crossorigin></script>\n"
            +
            "  <script>\n" +
            "    window.onload = () => {\n" +
            "      window.ui = SwaggerUIBundle({\n" +
            "        url: '/api-docs',\n" +
            "        dom_id: '#swagger-ui',\n" +
            "        presets: [\n" +
            "          SwaggerUIBundle.presets.apis,\n" +
            "          SwaggerUIStandalonePreset\n" +
            "        ],\n" +
            "        layout: \"StandaloneLayout\",\n" +
            "      });\n" +
            "    };\n" +
            "  </script>\n" +
            "</body>\n" +
            "</html>";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        if ("GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, SWAGGER_HTML.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(SWAGGER_HTML.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
