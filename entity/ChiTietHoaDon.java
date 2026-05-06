package entity;

public class ChiTietHoaDon {
    private String maHD;
    private String maSuat;
    private int soLuong;
    private double giaVe;
    private double thanhTien;

    public ChiTietHoaDon(String maHD, String maSuat, int soLuong, double giaVe, double thanhTien) {
        this.maHD = maHD;
        this.maSuat = maSuat;
        this.soLuong = soLuong;
        this.giaVe = giaVe;
        this.thanhTien = thanhTien;
    }

    public String getMaHD() { return maHD; }
    public String getMaSuat() { return maSuat; }
    public int getSoLuong() { return soLuong; }
    public double getGiaVe() { return giaVe; }
    public double getThanhTien() { return thanhTien; }
}