package com.example.stokTakipProjesi;

import com.example.stokTakipProjesi.Connection.httpService;

public class StokTakipProjesiApplication {

    public static void main(String[] args) {
        try {
            // HTTP sunucusunu başlat
            httpService service = new httpService();
            service.createServer(); // HTTP sunucusunun oluşturulması ve başlatılması
            System.out.println("StokTakipProjesiApplication başlatıldı.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Uygulama başlatılırken hata oluştu.");
        }
    }
}
