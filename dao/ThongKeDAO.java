package dao;

import connect.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ThongKeDAO {

    // ==========================================
    // 1. LẤY THỐNG KÊ TỔNG QUAN (SỐ VÉ & TỔNG TIỀN)
    // ==========================================
    public double[] layThongKeTongQuan(int ngay, int thang, int nam, String theLoai) {
        double[] kq = new double[]{0, 0};

        try {
            Connection con = Database.getInstance().getConnection();

            // Kết nối từ Phim -> Hóa Đơn và tính tổng dựa trên GiaVe của từng VePhim
            StringBuilder sql = new StringBuilder(
                "SELECT COUNT(v.MaVe) as TongSoVe, " +
                "COALESCE(SUM(v.GiaVe), 0) as TongDoanhThu " +
                "FROM Phim p " +
                "JOIN SuatChieu s ON p.MaPhim = s.MaPhim " +
                "JOIN VePhim v ON s.MaSuat = v.MaSuat " +
                "JOIN HoaDon hd ON v.MaHD = hd.MaHD " + 
                "WHERE 1=1 "
            );

            // Điều kiện lọc dựa trên thời gian thực tế lập Hóa Đơn
            if (ngay > 0) sql.append("AND DAY(hd.NgayLap) = ? ");
            if (thang > 0) sql.append("AND MONTH(hd.NgayLap) = ? ");
            if (nam > 0) sql.append("AND YEAR(hd.NgayLap) = ? ");
            
            // Điều kiện lọc theo Thể Loại Phim
            if (theLoai != null && !theLoai.isEmpty()) {
                sql.append("AND p.TheLoai LIKE ? ");
            }

            PreparedStatement pst = con.prepareStatement(sql.toString());

            // Gán giá trị tham số
            int index = 1;
            if (ngay > 0) pst.setInt(index++, ngay);
            if (thang > 0) pst.setInt(index++, thang);
            if (nam > 0) pst.setInt(index++, nam);
            if (theLoai != null && !theLoai.isEmpty()) {
                pst.setString(index++, "%" + theLoai + "%");
            }

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                kq[0] = rs.getInt("TongSoVe");
                kq[1] = rs.getDouble("TongDoanhThu");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return kq;
    }

    // ==========================================
    // 2. LẤY THỐNG KÊ DOANH THU CHI TIẾT TỪNG PHIM
    // ==========================================
    public ArrayList<Object[]> thongKeDoanhThuTheoPhim(int ngay, int thang, int nam, String theLoai) {
        ArrayList<Object[]> list = new ArrayList<>();

        try {
            Connection con = Database.getInstance().getConnection();

            StringBuilder sql = new StringBuilder(
                "SELECT p.TenPhim, " +
                "COUNT(v.MaVe) as SoVe, " +
                "COALESCE(SUM(v.GiaVe), 0) as DoanhThu " +
                "FROM Phim p " +
                "JOIN SuatChieu s ON p.MaPhim = s.MaPhim " +
                "JOIN VePhim v ON s.MaSuat = v.MaSuat " +
                "JOIN HoaDon hd ON v.MaHD = hd.MaHD " + 
                "WHERE 1=1 "
            );

            // Điều kiện lọc dựa trên thời gian thực tế lập Hóa Đơn
            if (ngay > 0) sql.append("AND DAY(hd.NgayLap) = ? ");
            if (thang > 0) sql.append("AND MONTH(hd.NgayLap) = ? ");
            if (nam > 0) sql.append("AND YEAR(hd.NgayLap) = ? ");
            
            if (theLoai != null && !theLoai.isEmpty()) {
                sql.append("AND p.TheLoai LIKE ? ");
            }

            // Gom nhóm theo Tên Phim và sắp xếp Doanh Thu giảm dần
            sql.append("GROUP BY p.TenPhim ORDER BY DoanhThu DESC");

            PreparedStatement pst = con.prepareStatement(sql.toString());

            // Gán giá trị tham số
            int index = 1;
            if (ngay > 0) pst.setInt(index++, ngay);
            if (thang > 0) pst.setInt(index++, thang);
            if (nam > 0) pst.setInt(index++, nam);
            if (theLoai != null && !theLoai.isEmpty()) {
                pst.setString(index++, "%" + theLoai + "%");
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("TenPhim"),
                    rs.getInt("SoVe"),
                    rs.getDouble("DoanhThu")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}