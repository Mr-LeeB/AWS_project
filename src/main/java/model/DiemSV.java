package model;

import java.io.Serializable;

public class DiemSV implements Serializable {

    int id;
    String maSV, tenSV;
    double Anh, Tin, Gdtc, Avg;

    public DiemSV() {
    }

    public DiemSV(int id, String maSV, String tenSV, double Anh, double Tin, double Gdtc, double Avg) {
        this.id = id;
        this.maSV = maSV;
        this.tenSV = tenSV;
        this.Anh = Anh;
        this.Tin = Tin;
        this.Gdtc = Gdtc;
        this.Avg = Avg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getTenSV() {
        return tenSV;
    }

    public void setTenSV(String tenSV) {
        this.tenSV = tenSV;
    }

    public double getAnh() {
        return Anh;
    }

    public void setAnh(double Anh) {
        this.Anh = Anh;
    }

    public double getTin() {
        return Tin;
    }

    public void setTin(double Tin) {
        this.Tin = Tin;
    }

    public double getGdtc() {
        return Gdtc;
    }

    public void setGdtc(double Gdtc) {
        this.Gdtc = Gdtc;
    }

    public double getAvg() {
        return (Anh + Tin + Gdtc) / 3;
    }

    public void setAvg(double Avg) {
        this.Avg = Avg;
    }
}
