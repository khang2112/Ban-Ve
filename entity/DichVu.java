package entity;

public class DichVu {
    private String maDV;
    private String tenDV;
    private double giaDV;

    public DichVu(String maDV, String tenDV, double giaDV) {
        this.maDV = maDV;
        this.tenDV = tenDV;
        this.giaDV = giaDV;
    }

    public String getMaDV() { return maDV; }
    public String getTenDV() { return tenDV; }
    public double getGiaDV() { return giaDV; }
}