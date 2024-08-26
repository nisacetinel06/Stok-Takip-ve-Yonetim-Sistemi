package com.example.stokTakipProjesi.Controller.PUT;

import com.example.stokTakipProjesi.Connection.DBconnection;
import com.example.stokTakipProjesi.Model.kullaniciUrun;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.stream.Collectors;

public class KullaniciUrunPut implements HttpHandler {
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
            // JSON OKUMA
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
            kullaniciUrun guncellenecekKullaniciUrun = new kullaniciUrun();
            // Gelen JSON verilerini okuma
            if (jsonObject.has("id")) {
                guncellenecekKullaniciUrun.setId(jsonObject.get("id").getAsInt());
            }
            if (jsonObject.has("kullanici_id")) {
                guncellenecekKullaniciUrun.setKullanici_id(jsonObject.get("kullanici_id").getAsInt());
            }
            if (jsonObject.has("urun_id")) {
                guncellenecekKullaniciUrun.setUrun_id(jsonObject.get("urun_id").getAsInt());
            }
            if (jsonObject.has("islem_tarihi")) {
                guncellenecekKullaniciUrun.setIslem_tarihi(Timestamp.valueOf(jsonObject.get("islem_tarihi").getAsString()));
            }
            if (jsonObject.has("islem_turu")) {
                guncellenecekKullaniciUrun.setIslem_turu(jsonObject.get("islem_turu").getAsString());
            }
            if (jsonObject.has("miktar")) {
                guncellenecekKullaniciUrun.setMiktar(jsonObject.get("miktar").getAsInt());
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
                String sql = "UPDATE kullanici_urun SET kullanici_id=?, urun_id=?, islem_tarihi=?, islem_turu=?, miktar=? WHERE id=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, guncellenecekKullaniciUrun.getKullanici_id());
                pstmt.setInt(2, guncellenecekKullaniciUrun.getUrun_id());
                pstmt.setTimestamp(3, guncellenecekKullaniciUrun.getIslem_tarihi());
                pstmt.setString(4, guncellenecekKullaniciUrun.getIslem_turu());
                pstmt.setInt(5, guncellenecekKullaniciUrun.getMiktar());
                pstmt.setInt(6, guncellenecekKullaniciUrun.getId());
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

