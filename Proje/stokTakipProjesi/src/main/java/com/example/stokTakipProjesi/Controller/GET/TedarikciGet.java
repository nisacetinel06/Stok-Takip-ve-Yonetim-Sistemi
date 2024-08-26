package com.example.stokTakipProjesi.Controller.GET;

import com.example.stokTakipProjesi.Model.Tedarikciler;
import com.example.stokTakipProjesi.Connection.DBconnection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TedarikciGet implements HttpHandler {

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

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Veritabanından tedarikçileri al
            String url = "jdbc:mysql://localhost:3306/demir_celik";
            String username = "root";
            String password = "112606";
            DBconnection con = new DBconnection(url, username, password);
            Connection connection = null;
            String jsonResponse = "";

            try {
                connection = con.createConnection();
                String sql = "SELECT * FROM tedarikciler";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();

                List<Tedarikciler> tedarikcilerList = new ArrayList<>();
                while (rs.next()) {
                    Tedarikciler tedarikci = new Tedarikciler();
                    tedarikci.setId(rs.getInt("id"));
                    tedarikci.setAd(rs.getString("ad"));
                    tedarikci.setIletisim_bilgileri(rs.getString("iletisim_bilgileri"));
                    tedarikcilerList.add(tedarikci);
                }

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Tedarikciler>>() {}.getType();
                jsonResponse = gson.toJson(tedarikcilerList, listType);
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
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(405, -1); // Yalnızca GET yöntemine izin verildiğini belirten yanıt kodu
        }
    }
}
