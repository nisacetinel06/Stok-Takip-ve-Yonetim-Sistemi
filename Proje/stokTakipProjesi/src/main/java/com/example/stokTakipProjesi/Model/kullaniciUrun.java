package com.example.stokTakipProjesi.Model;


import java.sql.Timestamp;

public class kullaniciUrun {
    private int id;
    private int kullanici_id;
    private int urun_id;
    private Timestamp islem_tarihi;
    private String islem_turu;
    private int miktar;

    public int getId(){return id;}
    public void setId(int id){this.id=id;}
    public int getKullanici_id() {return kullanici_id;}
    public void setKullanici_id(int kullanici_id) {this.kullanici_id= kullanici_id;}
    public int getUrun_id(){return urun_id;}
    public void setUrun_id(int urun_id){this.urun_id=urun_id;}
    public Timestamp getIslem_tarihi(){return islem_tarihi;}
    public void setIslem_tarihi(Timestamp islem_tarihi){this.islem_tarihi = islem_tarihi;}
    public String getIslem_turu(){return islem_turu;}
    public void setIslem_turu(String islem_turu){this.islem_turu = islem_turu;}
    public int getMiktar(){return miktar;}
    public void setMiktar(int miktar){this.miktar=miktar;}
}

