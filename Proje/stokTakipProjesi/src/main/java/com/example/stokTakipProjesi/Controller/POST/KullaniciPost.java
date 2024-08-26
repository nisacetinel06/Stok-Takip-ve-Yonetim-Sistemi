package com.example.stokTakipProjesi.Controller.POST;

import com.example.stokTakipProjesi.Connection.DBconnection;
import com.example.stokTakipProjesi.Model.kullanicilar;
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

public class KullaniciPost implements HttpHandler {

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
            kullanicilar yeniKullanici = new kullanicilar();

            // Gelen JSON verilerini kullanıcı nesnesine set etme
            if (jsonObject.has("id")){
                yeniKullanici.setId(jsonObject.get("id").getAsInt());
            }
            if (jsonObject.has("ad")) {
                yeniKullanici.setAd(jsonObject.get("ad").getAsString());
            }
            if (jsonObject.has("soyad")) {
                yeniKullanici.setSoyad(jsonObject.get("soyad").getAsString());
            }
            if (jsonObject.has("telefon")) {
                yeniKullanici.setTelefon(jsonObject.get("telefon").getAsString());
            }
            if (jsonObject.has("sifre")) {
                yeniKullanici.setSifre(jsonObject.get("sifre").getAsString());
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
                String sql = "INSERT INTO kullanicilar (id, ad, soyad, telefon, sifre) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, yeniKullanici.getId());
                pstmt.setString(2, yeniKullanici.getAd());
                pstmt.setString(3, yeniKullanici.getSoyad());
                pstmt.setString(4, yeniKullanici.getTelefon());
                pstmt.setString(5, yeniKullanici.getSifre());
                int check = pstmt.executeUpdate();
                System.out.println("Insert Result: " + check);

                if (check > 0) {
                    jsonResponse = "{\"status\": \"success\", \"message\": \"Kullanıcı başarıyla eklendi!\"}";
                } else {
                    jsonResponse = "{\"status\": \"failure\", \"message\": \"Kullanıcı eklenemedi!\"}";
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
