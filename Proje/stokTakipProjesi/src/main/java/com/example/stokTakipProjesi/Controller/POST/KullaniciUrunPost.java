package com.example.stokTakipProjesi.Controller.POST;

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
import java.util.function.ObjDoubleConsumer;
import java.util.stream.Collectors;

public class KullaniciUrunPost implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException{
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        if
        (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS"))
        {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())){
            // Giriş verisini (JSON) okuyalım
            InputStream inputStream=exchange.getRequestBody();
            String requestBody=new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            JsonObject jsonObject= JsonParser.parseString(requestBody).getAsJsonObject();
            kullaniciUrun yeniKullaniciUrun=new kullaniciUrun();

            //Gelen JSON verilerini kullaniciUrun nesnesine set etme
            if (jsonObject.has("id")){
                yeniKullaniciUrun.setId(jsonObject.get("id").getAsInt());
            }
            if (jsonObject.has("kullanici_id")){
                yeniKullaniciUrun.setKullanici_id(jsonObject.get("kullanici_id").getAsInt());
            }
            if (jsonObject.has("urun_id")){
                yeniKullaniciUrun.setUrun_id(jsonObject.get("urun_id").getAsInt());
            }
            if (jsonObject.has("islem_tarihi")){
                yeniKullaniciUrun.setIslem_tarihi(Timestamp.valueOf(jsonObject.get("islem_tarihi").getAsString()));
            }
            if (jsonObject.has("islem_turu")){
                yeniKullaniciUrun.setIslem_turu(jsonObject.get("islem_turu").getAsString());
            }
            if (jsonObject.has("miktar")){
                yeniKullaniciUrun.setMiktar(jsonObject.get("miktar").getAsInt());
            }

            // Veritabanına ekleme işlemi
            String url ="jdbc:mysql://localhost:3306/demir_celik";
            String username="root";
            String password="112606";
            DBconnection con=new DBconnection(url,username,password);
            Connection connection=null;

            String jsonResponse = "";
            try{
                connection=con.createConnection();
                String sql="INSERT INTO kullanici_urun (id, kullanici_id, urun_id, islem_tarihi, islem_turu, miktar) VALUES (?,?,?,?,?,?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, yeniKullaniciUrun.getId());
                pstmt.setInt(2, yeniKullaniciUrun.getKullanici_id());
                pstmt.setInt(3, yeniKullaniciUrun.getUrun_id());
                pstmt.setTimestamp(4, yeniKullaniciUrun.getIslem_tarihi());
                pstmt.setString(5, yeniKullaniciUrun.getIslem_turu());
                pstmt.setInt(6, yeniKullaniciUrun.getMiktar());
                int check = pstmt.executeUpdate();
                System.out.println("Insert Result: " + check);





                if (check > 0) {
                    jsonResponse = "{\"status\": \"success\", \"message\": \"İşlem başarıyla eklendi!\"}";
                } else {
                    jsonResponse = "{\"status\": \"failure\", \"message\": \"İşlem eklenemedi!\"}";
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
        }
        else {
            exchange.sendResponseHeaders(405,-1);
        }
    }
}
