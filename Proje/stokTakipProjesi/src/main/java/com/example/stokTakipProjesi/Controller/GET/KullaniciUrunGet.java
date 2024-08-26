package com.example.stokTakipProjesi.Controller.GET;

import com.example.stokTakipProjesi.Connection.DBconnection;
import com.example.stokTakipProjesi.Model.kullaniciUrun;
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

public class KullaniciUrunGet implements HttpHandler {
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
        DBconnection con= new DBconnection(url, username, password);

        List<kullaniciUrun> values = new ArrayList<>();
        try{
            statement = con.createConnection().createStatement();
            String sql="SELECT * FROM kullanici_urun";
            rs = statement.executeQuery(sql);
            while(rs.next()) {
                kullaniciUrun kullaniciUrun = new kullaniciUrun();
                kullaniciUrun.setId(rs.getInt("id"));
                kullaniciUrun.setKullanici_id(rs.getInt("kullanici_id"));
                kullaniciUrun.setUrun_id(rs.getInt("urun_id"));
                kullaniciUrun.setIslem_tarihi(rs.getTimestamp("islem_tarihi"));
                kullaniciUrun.setIslem_turu(rs.getString("islem_turu"));
                kullaniciUrun.setMiktar(rs.getInt("miktar"));
                values.add(kullaniciUrun);
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
