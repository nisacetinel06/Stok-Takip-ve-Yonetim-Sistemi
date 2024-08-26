package com.example.stokTakipProjesi.Controller.POST;

import com.example.stokTakipProjesi.Connection.DBconnection;
import com.example.stokTakipProjesi.Model.Tedarikciler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class TedarikciPost implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Giriş verisini (JSON) okuyalım
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
            Tedarikciler yeniTedarikci = new Tedarikciler();

            // Gelen JSON verilerini tedarikçi nesnesine set etme
            if (jsonObject.has("ad")) {
                yeniTedarikci.setAd(jsonObject.get("ad").getAsString());
            }
            if (jsonObject.has("iletisim_bilgileri")) {
                yeniTedarikci.setIletisim_bilgileri(jsonObject.get("iletisim_bilgileri").getAsString());
            }

            // Veritabanına ekleme işlemi
            String url = "jdbc:mysql://localhost:3306/demir_celik";
            String username = "root";
            String password = "112606";
            DBconnection con = new DBconnection(url, username, password);
            Connection connection = null;

            String jsonResponse = "";
            try {
                connection = con.createConnection();
                String sql = "INSERT INTO tedarikciler (ad, iletisim_bilgileri) VALUES (?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, yeniTedarikci.getAd());
                pstmt.setString(2, yeniTedarikci.getIletisim_bilgileri());
                int check = pstmt.executeUpdate();

                if (check > 0) {
                    jsonResponse = "{\"status\": \"success\", \"message\": \"Tedarikçi başarıyla eklendi!\"}";
                } else {
                    jsonResponse = "{\"status\": \"failure\", \"message\": \"Tedarikçi eklenemedi!\"}";
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
            exchange.sendResponseHeaders(405, -1); // Yalnızca POST yöntemine izin verildiğini belirten yanıt kodu
        }
    }
}

