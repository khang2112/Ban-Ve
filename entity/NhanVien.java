package entity;

public class NhanVien {
    private String maNV;
    private String hoNV;
    private String tenNV;
    private int tuoi;
    private String phongBan;
    private double tienLuong;

    public NhanVien(String maNV, String hoNV, String tenNV, int tuoi, String phongBan, double tienLuong) {
        this.maNV = maNV;
        this.hoNV = hoNV;
        this.tenNV = tenNV;
        this.tuoi = tuoi;
        this.phongBan = phongBan;
        this.tienLuong = tienLuong;
    }

    public String getMaNV() { return maNV; }
    public String getHoNV() { return hoNV; }
    public String getTenNV() { return tenNV; }
    public int getTuoi() { return tuoi; }
    public String getPhongBan() { return phongBan; }
    public double getTienLuong() { return tienLuong; }
}