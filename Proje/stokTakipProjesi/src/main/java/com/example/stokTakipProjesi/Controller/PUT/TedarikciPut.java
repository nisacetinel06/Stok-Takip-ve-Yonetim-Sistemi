package com.example.stokTakipProjesi.Controller.PUT;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.stokTakipProjesi.Connection.DBconnection;
import com.example.stokTakipProjesi.Model.Tedarikciler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class TedarikciPut implements HttpHandler {

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

        if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Giriş verisini (JSON) oku
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            int id = jsonObject.get("id").getAsInt();
            String ad = jsonObject.get("ad").getAsString();
            String iletisimBilgileri = jsonObject.get("iletisim_bilgileri").getAsString();

            // Veritabanında güncelleme işlemi
            String url = "jdbc:mysql://localhost:3306/demir_celik";
            String username = "root";
            String password = "112606";
            DBconnection con = new DBconnection(url, username, password);
            Connection connection = null;
            String jsonResponse;

            try {
                connection = con.createConnection();
                String sql = "UPDATE tedarikciler SET ad=?, iletisim_bilgileri=? WHERE id=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, ad);
                pstmt.setString(2, iletisimBilgileri);
                pstmt.setInt(3, id);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    jsonResponse = "{\"status\": \"success\", \"message\": \"Tedarikçi başarıyla güncellendi!\"}";
                } else {
                    jsonResponse = "{\"status\": \"failure\", \"message\": \"Tedarikçi bulunamadı veya güncellenemedi!\"}";
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
            exchange.sendResponseHeaders(405, -1); // Yalnızca PUT yöntemine izin verildiğini belirten yanıt kodu
        }
    }
}
