package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class SuatChieu {
    private String maSuat;
    private String maPhim;
    private String phongChieu;
    private LocalDate ngayChieu; 
    private LocalTime gioChieu;

    public SuatChieu(String maSuat, String maPhim, String phongChieu, LocalDate ngay, LocalTime gio) {
        this.maSuat = maSuat;
        this.maPhim = maPhim;
        this.phongChieu = phongChieu;
        this.ngayChieu = ngay;
        this.gioChieu = gio;
    }

    public String getMaSuat() { return maSuat; }
    public String getMaPhim() { return maPhim; }
    public String getPhongChieu() { return phongChieu; }
    public LocalDate getNgayChieu() { return ngayChieu; }
    public LocalTime getGioChieu() { return gioChieu; }
}
