package entity;

public class HoaDon {
    private String maHD;
    private String maNV;
    private String ngayLap;
    private double tongTien;
    private String maKH; // Bổ sung biến Khách Hàng

    public HoaDon(String maHD, String maNV, String ngayLap, double tongTien, String maKH) {
        this.maHD = maHD;
        this.maNV = maNV;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.maKH = maKH;
    }

    public String getMaHD() { return maHD; }
    public String getMaNV() { return maNV; }
    public String getNgayLap() { return ngayLap; }
    public double getTongTien() { return tongTien; }
    public String getMaKH() { return maKH; } 
    public void setMaKH(String maKH) { this.maKH = maKH; }
}