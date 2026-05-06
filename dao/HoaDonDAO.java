package dao;

import connect.Database;
import entity.HoaDon;
import entity.VePhim;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class HoaDonDAO {

    public boolean thanhToanHoaDon(String maHD, String maNV, String maSuat, ArrayList<String> danhSachGhe, double giaVe, String maKH) {
        Connection con = Database.getInstance().getConnection();
        try {
            con.setAutoCommit(false); 

            // 1. LƯU HÓA ĐƠN
            String sqlHD = "INSERT INTO HoaDon (MaHD, MaNV, NgayLap, TongTien, MaKH) VALUES (?, ?, GETDATE(), ?, ?)";
            PreparedStatement pstHD = con.prepareStatement(sqlHD);
            pstHD.setString(1, maHD);
            pstHD.setString(2, maNV); 
            
            double tongTien = danhSachGhe.size() * giaVe;
            pstHD.setDouble(3, tongTien);
            
            if (maKH == null || maKH.trim().isEmpty()) {
                pstHD.setNull(4, java.sql.Types.VARCHAR);
            } else {
                pstHD.setString(4, maKH);
            }
            pstHD.executeUpdate();

            // 2. LƯU VÉ PHIM
            String sqlVe = "INSERT INTO VePhim (MaHD, MaSuat, MaGhe, GiaVe) VALUES (?, ?, ?, ?)";
            PreparedStatement pstVe = con.prepareStatement(sqlVe);
            for (String ghe : danhSachGhe) {
                pstVe.setString(1, maHD);
                pstVe.setString(2, maSuat);
                pstVe.setString(3, ghe);
                pstVe.setDouble(4, giaVe);
                pstVe.executeUpdate();
            }

            // 3. TÍCH ĐIỂM TỰ ĐỘNG
            if (maKH != null && !maKH.trim().isEmpty()) {
                int diemCong = (int) (tongTien / 10000); 
                String sqlDiem = "UPDATE KhachHang SET DiemTichLuy = DiemTichLuy + ? WHERE MaKH = ?";
                PreparedStatement pstDiem = con.prepareStatement(sqlDiem);
                pstDiem.setInt(1, diemCong);
                pstDiem.setString(2, maKH);
                pstDiem.executeUpdate();
            }

            con.commit(); 
            return true;

        } catch (Exception e) {
            try { con.rollback(); } catch (Exception ex) {} 
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (Exception ex) {}
        }
    }

    public ArrayList<String> layDanhSachGheDaBan(String maSuat) {
        ArrayList<String> dsGhe = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT MaGhe FROM VePhim WHERE MaSuat = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maSuat);
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                dsGhe.add(rs.getString("MaGhe")); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsGhe;
    }

    public ArrayList<HoaDon> layDanhSachHoaDon() {
        ArrayList<HoaDon> dsHD = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM HoaDon ORDER BY NgayLap DESC"; 
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String maHD = rs.getString("MaHD");
                String maNV = rs.getString("MaNV");
                String ngay = rs.getString("NgayLap");
                double tong = rs.getDouble("TongTien");
                String maKH = rs.getString("MaKH"); 
                
                dsHD.add(new HoaDon(maHD, maNV, ngay, tong, maKH));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsHD;
    }

    public ArrayList<VePhim> layChiTietVe(String maHD) {
        ArrayList<VePhim> dsVe = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM VePhim WHERE MaHD = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maHD);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                int maVe = rs.getInt("MaVe");
                String mHD = rs.getString("MaHD");
                String maSuat = rs.getString("MaSuat");
                String maGhe = rs.getString("MaGhe");
                double gia = rs.getDouble("GiaVe");
                dsVe.add(new VePhim(maVe, mHD, maSuat, maGhe, gia));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsVe;
    }
}