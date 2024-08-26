package com.example.stokTakipProjesi.Controller.GET;


import com.example.stokTakipProjesi.Connection.DBconnection;
import com.example.stokTakipProjesi.Model.kullanicilar;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class KullaniciGet implements HttpHandler {

    @Override
    public void handle (HttpExchange exchange) throws IOException
    {
        System.out.println("handle");
        OutputStream os=exchange.getResponseBody();
        Gson gson = new Gson();

        // CORS başlıklarını ekledim
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        ResultSet rs = null;
        Statement statement=null;
        String url ="jdbc:mysql://localhost:3306/demir_celik";
        String password = "112606";
        String username = "root";
        DBconnection con = new DBconnection(url, username, password);

        List<kullanicilar> values=new ArrayList<>();
        try {
            statement = con.createConnection().createStatement();
            String sql = "SELECT * FROM kullanicilar";
            rs = statement.executeQuery(sql);

            while (rs.next()) {
                kullanicilar kullanici = new kullanicilar();
                kullanici.setId(rs.getInt("id"));
                kullanici.setAd(rs.getString("ad"));
                kullanici.setSoyad(rs.getString("soyad"));
                kullanici.setTelefon(rs.getString("telefon"));
                kullanici.setSifre(rs.getString("sifre"));
                values.add(kullanici);
                System.out.println(kullanici.getId());
                System.out.println(kullanici.getAd());
                System.out.println(kullanici.getSoyad());
                System.out.println(kullanici.getTelefon());
                System.out.println(kullanici.getSifre());
            }

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            con.closeConnection();
        }
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, 0);
        os.write(gson.toJson(values).getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}

