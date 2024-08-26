package com.example.stokTakipProjesi.Controller.PUT;

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

public class KullaniciPut implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Giriş verisini (JSON) okuyalım
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
            kullanicilar guncellenecekKullanici = new kullanicilar();
            // Gelen JSON verilerini kullanıcı nesnesine set etme
            if (jsonObject.has("id")) {
                guncellenecekKullanici.setId(jsonObject.get("id").getAsInt());
            }
            if (jsonObject.has("ad")) {
                guncellenecekKullanici.setAd(jsonObject.get("ad").getAsString());
            }
            if (jsonObject.has("soyad")) {
                guncellenecekKullanici.setSoyad(jsonObject.get("soyad").getAsString());
            }
            if (jsonObject.has("telefon")) {
                guncellenecekKullanici.setTelefon(jsonObject.get("telefon").getAsString());
            }
            if (jsonObject.has("sifre")) {
                guncellenecekKullanici.setSifre(jsonObject.get("sifre").getAsString());
            }
            // Veritabanında güncelleme işlemi
            String url = "jdbc:mysql://localhost:3306/demir_celik";
            String username = "root";
            String password = "112606";
            DBconnection con = new DBconnection(url, username, password);
            Connection connection = null;
            String jsonResponse = "";
            try {
                connection = con.createConnection();
                String sql = "UPDATE kullanicilar SET ad=?, soyad=?, telefon=?, sifre=? WHERE id=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, guncellenecekKullanici.getAd());
                pstmt.setString(2, guncellenecekKullanici.getSoyad());
                pstmt.setString(3, guncellenecekKullanici.getTelefon());
                pstmt.setString(4, guncellenecekKullanici.getSifre());
                pstmt.setInt(5, guncellenecekKullanici.getId());
                int check = pstmt.executeUpdate();
                System.out.println("Update Result: " + check);
                if (check > 0) {
                    jsonResponse = "{\"status\": \"success\", \"message\": \"Kullanıcı başarıyla güncellendi!\"}";
                } else {
                    jsonResponse = "{\"status\": \"failure\", \"message\": \"Kullanıcı güncellenemedi!\"}";
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
