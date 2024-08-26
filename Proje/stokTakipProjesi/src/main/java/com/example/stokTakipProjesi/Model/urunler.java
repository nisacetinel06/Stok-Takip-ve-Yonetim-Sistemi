package com.example.stokTakipProjesi.Model;

public class urunler {

private int id;
private String urun_adi;
private double fiyat;
private int stok_durumu;

public int getId(){
    return id;
}
public void setId(int id){
    this.id=id;
}
public String getUrun_adi(){
    return urun_adi;
}
public void setUrun_adi(String urun_adi){
    this.urun_adi=urun_adi;
}
public double getFiyat(){
    return fiyat;
}
public void setFiyat(double fiyat){
    this.fiyat=fiyat;
}
public int getStok_durumu(){
    return stok_durumu;
}
public void setStok_durumu(int stok_durumu){this.stok_durumu=stok_durumu;}
}
