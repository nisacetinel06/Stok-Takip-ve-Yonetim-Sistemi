package com.example.stokTakipProjesi.Controller.PUT;

import com.example.stokTakipProjesi.Connection.DBconnection;
import com.example.stokTakipProjesi.Model.urunler;
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

public class UrunPut implements HttpHandler {
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
            //JSON OKUMA
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
            urunler guncellenecekUrun = new urunler();

            //Gelen JSON verilerini ürün nesnesine set etme
            if (jsonObject.has("id")) {
                guncellenecekUrun.setId(jsonObject.get("id").getAsInt());
            }
            if (jsonObject.has("urun_adi")) {
                guncellenecekUrun.setUrun_adi(jsonObject.get("urun_adi").getAsString());
            }
            if (jsonObject.has("fiyat")) {
                guncellenecekUrun.setFiyat(jsonObject.get("fiyat").getAsDouble());
            }
            if (jsonObject.has("stok_durumu")) {
                guncellenecekUrun.setStok_durumu(jsonObject.get("stok_durumu").getAsInt());
            }
            //Veritabanında güncelleme işlemi
            String url = "jdbc:mysql://localhost:3306/demir_celik";
            String username = "root";
            String password = "112606";
            DBconnection con = new DBconnection(url, username, password);
            Connection connection = null;

            String jsonResponse  ="";
            try {
                connection = con.createConnection();
                String sql = "UPDATE urunler SET urun_adi = ?, fiyat = ?, stok_durumu =? WHERE id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, guncellenecekUrun.getUrun_adi());
                pstmt.setDouble(2, guncellenecekUrun.getFiyat());
                pstmt.setInt(3, guncellenecekUrun.getStok_durumu());
                pstmt.setInt(4, guncellenecekUrun.getId());
                int check = pstmt.executeUpdate();
                System.out.println("Insert Result: " + check);

                if (check > 0) {
                    jsonResponse = "{\"status\": \"success\", \"message\": \"Ürünler başarıyla güncellendi!\"}";
                } else {
                    jsonResponse = "{\"status\": \"failure\", \"message\": \"Ürünler güncellenmedi!\"}";
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
            exchange.sendResponseHeaders(405, -1);
        }
    }
}