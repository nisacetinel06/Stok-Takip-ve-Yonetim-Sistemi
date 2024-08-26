package com.example.stokTakipProjesi.Controller.POST;

import com.example.stokTakipProjesi.Connection.DBconnection;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class StokGuncelle implements HttpHandler {
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

            String urunAdi= jsonObject.get("urunAdi").getAsString();
            int satinAlinanMiktar = jsonObject.get("miktar").getAsInt();

            // Veritabanı bağlantısı
            String url = "jdbc:mysql://localhost:3306/demir_celik";
            String username = "root";
            String password = "112606";
            DBconnection con = new DBconnection(url, username, password);
            Connection connection = null;

            String jsonResponse = "";
            try {
                connection = con.createConnection();
                // Mevcut stok miktarını sorgulama
                String selectQuery = "SELECT stok_durumu FROM urunler WHERE urun_adi = ?";
                PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
                selectStmt.setString(1, urunAdi);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int mevcutStok = rs.getInt("stok_durumu");
                    // Yeni stok miktarını hesaplama
                    int kalanStok = mevcutStok - satinAlinanMiktar;

                    if (kalanStok >= 0) {
                        // Stok durumunu güncelleme
                        String updateQuery = "UPDATE urunler SET stok_durumu = ? WHERE urun_adi = ?";
                        PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                        updateStmt.setInt(1, kalanStok);
                        updateStmt.setString(2, urunAdi);
                        int updateResult = updateStmt.executeUpdate();

                        if (updateResult > 0) {
                            jsonResponse = "{\"success\": true, \"message\": \"Stok başarıyla güncellendi!\", \"kalanStok\": " + kalanStok + "}";
                        } else {
                            jsonResponse = "{\"success\": false, \"message\": \"Stok güncellenemedi!\"}";
                        }
                    } else {
                        jsonResponse = "{\"success\": false, \"message\": \"Yeterli stok yok!\"}";
                    }
                } else {
                    jsonResponse = "{\"success\": false, \"message\": \"Ürün bulunamadı!\"}";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                jsonResponse = "{\"success\": false, \"message\": \"Veritabanı hatası!\"}";
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
            exchange.sendResponseHeaders(405, -1); // Sadece POST yöntemine izin verildiğini belirten yanıt kodu
        }
    }
}
