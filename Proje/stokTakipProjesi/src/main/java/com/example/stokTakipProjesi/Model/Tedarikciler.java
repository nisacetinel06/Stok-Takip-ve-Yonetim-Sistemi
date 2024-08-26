package com.example.stokTakipProjesi.Model;

public class Tedarikciler {

    private int id;
    private String ad;
    private String iletisim_bilgileri;

    // Getter ve Setter metodları

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getIletisim_bilgileri(){
        return iletisim_bilgileri;
    }

    public void setIletisim_bilgileri(String iletisim_bilgileri) {
        this.iletisim_bilgileri = iletisim_bilgileri;
    }

    @Override
    public String toString() {
        return "Tedarikciler{" +
                "id=" + id +
                ", ad='" + ad + '\'' +
                ", iletisimBilgileri='" + iletisim_bilgileri+ '\'' +
                '}';
    }
}
