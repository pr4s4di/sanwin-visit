package com.models;

public class ModelListAbsen {

    private String status;
    private String waktuHadir;
    private String waktuCheckOut;
    private String alamat;
    private String kodeKunjungan;

    public ModelListAbsen()
    {

    }

    public String getKodeKunjungan() {
        return kodeKunjungan;
    }

    public void setKodeKunjungan(String kodeKunjungan) {
        this.kodeKunjungan = kodeKunjungan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getWaktuHadir() {
        return waktuHadir;
    }

    public void setWaktuHadir(String waktuHadir) {
        this.waktuHadir = waktuHadir;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWaktuCheckOut() {
        return waktuCheckOut;
    }

    public void setWaktuCheckOut(String waktuCheckOut) {
        this.waktuCheckOut = waktuCheckOut;
    }
}
