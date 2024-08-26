package com.example.stokTakipProjesi.Connection;


import com.example.stokTakipProjesi.Controller.DELETE.KullaniciDelete;
import com.example.stokTakipProjesi.Controller.DELETE.TedarikciDelete;
import com.example.stokTakipProjesi.Controller.DELETE.UrunDelete;
import com.example.stokTakipProjesi.Controller.GET.KullaniciGet;
import com.example.stokTakipProjesi.Controller.GET.KullaniciUrunGet;
import com.example.stokTakipProjesi.Controller.GET.TedarikciGet;
import com.example.stokTakipProjesi.Controller.GET.UrunGet;
import com.example.stokTakipProjesi.Controller.POST.*;
import com.example.stokTakipProjesi.Controller.PUT.KullaniciPut;
import com.example.stokTakipProjesi.Controller.PUT.KullaniciUrunPut;
import com.example.stokTakipProjesi.Controller.PUT.TedarikciPut;
import com.example.stokTakipProjesi.Controller.PUT.UrunPut;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class httpService {

    public void createServer() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.setExecutor(null);
        server.createContext("/GET/KullaniciGet",new KullaniciGet());
        server.createContext("/GET/TedarikciGet",new TedarikciGet());
        server.createContext("/GET/UrunGet",new UrunGet());
        server.createContext("/GET/KullaniciUrunGet", new KullaniciUrunGet());

        server.createContext("/POST/KullaniciPost",new KullaniciPost());
        server.createContext("/POST/UrunPost",new UrunPost());
        server.createContext("/POST/KullaniciUrunPost",new KullaniciUrunPost());
        server.createContext("/POST/TedarikciPost",new TedarikciPost());

        server.createContext("/POST/StokGuncelle",new StokGuncelle());
        server.createContext("/PUT/UrunPut",new UrunPut());
        server.createContext("/PUT/KullaniciPut",new KullaniciPut());
        server.createContext("/PUT/KullaniciUrunPut",new KullaniciUrunPut());
        server.createContext("/PUT/TedarikciPut",new TedarikciPut());

        server.createContext("/DELETE/KullaniciDelete",new KullaniciDelete());
        server.createContext("/DELETE/UrunDelete",new UrunDelete());
        server.createContext("/DELETE/TedarikciDelete",new TedarikciDelete());
        server.start();
        System.out.println("Server started at " + new InetSocketAddress(8080));


    }
}