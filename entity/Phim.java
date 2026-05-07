package entity;

public class Phim {
    private String maPhim;
    private String tenPhim;
    private String theLoai;
    private double giaVe;
    private String hinhAnh; 


    public Phim(String maPhim, String tenPhim, String theLoai, double giaVe, String hinhAnh) {
        this.maPhim = maPhim;
        this.tenPhim = tenPhim;
        this.theLoai = theLoai;
        this.giaVe = giaVe;
        this.hinhAnh = hinhAnh;
    }

    public Phim(String maPhim, String tenPhim, String theLoai, double giaVe) {
        this.maPhim = maPhim;
        this.tenPhim = tenPhim;
        this.theLoai = theLoai;
        this.giaVe = giaVe;
        this.hinhAnh = ""; 
    }

    public Phim() {
    }

    public String getMaPhim() {
        return maPhim;
    }

    public void setMaPhim(String maPhim) {
        this.maPhim = maPhim;
    }

    public String getTenPhim() {
        return tenPhim;
    }

    public void setTenPhim(String tenPhim) {
        this.tenPhim = tenPhim;
    }

    public String getTheLoai() {
        return theLoai;
    }

    public void setTheLoai(String theLoai) {
        this.theLoai = theLoai;
    }

    public double getGiaVe() {
        return giaVe;
    }

    public void setGiaVe(double giaVe) {
        this.giaVe = giaVe;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
}