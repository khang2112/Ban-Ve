package entity;

public class VePhim {
    private int maVe;
    private String maHD;
    private String maSuat;
    private String maGhe;
    private double giaVe;

    public VePhim(int maVe, String maHD, String maSuat, String maGhe, double giaVe) {
        this.maVe = maVe;
        this.maHD = maHD;
        this.maSuat = maSuat;
        this.maGhe = maGhe;
        this.giaVe = giaVe;
    }

    public int getMaVe() { return maVe; }
    public String getMaHD() { return maHD; }
    public String getMaSuat() { return maSuat; }
    public String getMaGhe() { return maGhe; }
    public double getGiaVe() { return giaVe; }
}