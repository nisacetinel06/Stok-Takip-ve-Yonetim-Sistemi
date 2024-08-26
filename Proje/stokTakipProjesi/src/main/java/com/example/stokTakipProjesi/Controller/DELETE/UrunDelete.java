package com.example.stokTakipProjesi.Controller.DELETE;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.stokTakipProjesi.Connection.DBconnection;

public class UrunDelete implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS ayarları
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Giriş verisini (JSON) oku
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            int urunId = 0;
            if (jsonObject.has("id")) {
                urunId = jsonObject.get("id").getAsInt();
            }

            // Veritabanında silme işlemi
            String url = "jdbc:mysql://localhost:3306/demir_celik";
            String username = "root";
            String password = "112606";
            DBconnection con = new DBconnection(url, username, password);
            Connection connection = null;
            String jsonResponse = "";

            try {
                connection = con.createConnection();
                String sql = "DELETE FROM urunler WHERE id=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, urunId);
                int check = pstmt.executeUpdate();

                System.out.println("Delete Result: " + check);
                if (check > 0) {
                    jsonResponse = "{\"status\": \"success\", \"message\": \"Ürün başarıyla silindi!\"}";
                } else {
                    jsonResponse = "{\"status\": \"failure\", \"message\": \"Ürün bulunamadı veya silinemedi!\"}";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                jsonResponse = "{\"status\": \"error\", \"message\": \"Veritabanı hatası!\"}";
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                con.closeConnection();
            }

            // Yanıtı gönder
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            os.close();
        } else {
            exchange.sendResponseHeaders(405, -1); // Yalnızca DELETE yöntemine izin verildiğini belirten yanıt kodu
        }
    }
}
