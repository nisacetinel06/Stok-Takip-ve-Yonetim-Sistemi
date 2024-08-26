package com.example.stokTakipProjesi.Controller.GET;

import com.example.stokTakipProjesi.Connection.DBconnection;
import com.example.stokTakipProjesi.Model.urunler;
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

public class UrunGet implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException{
        System.out.println("handle");
        OutputStream os=exchange.getResponseBody();
        Gson gson=new Gson();
        // CORS başlıklarını ekle
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        ResultSet rs=null;
        Statement statement=null;
        String url="jdbc:mysql://localhost:3306/demir_celik";
        String password="112606";
        String username="root";
        DBconnection con=new DBconnection(url,username,password);

        List<urunler> values=new ArrayList<>();
        try
        {
            statement=con.createConnection().createStatement();
            String sql="SELECT * FROM urunler";
            rs=statement.executeQuery(sql);
            while(rs.next())
            {
               urunler urun=new urunler();
               urun.setId(rs.getInt("id"));
               urun.setUrun_adi(rs.getString("urun_adi"));
               urun.setFiyat(rs.getDouble("fiyat"));
               urun.setStok_durumu(rs.getInt("stok_durumu"));
               values.add(urun);
                System.out.println(urun.getId());
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        finally {
            con.closeConnection();
        }
        exchange.getResponseHeaders().set("Content-Type","application/json");
        exchange.sendResponseHeaders(200,0);
        os.write(gson.toJson(values).getBytes(StandardCharsets.UTF_8));
        os.close();

    }
}
