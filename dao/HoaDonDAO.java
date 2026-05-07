package dao;

import connect.Database;
import entity.HoaDon;
import entity.VePhim;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class HoaDonDAO {

    // --- FIX: THÊM THAM SỐ tienGiam VÀO HÀM ĐỂ TRỪ TIỀN TRƯỚC KHI LƯU ---
    public boolean thanhToanHoaDon(String maHD, String maNV, String maSuat, ArrayList<String> danhSachGhe, double giaVe, String maKH, HashMap<String, Object[]> gioHangDV, double tienGiam) {
        Connection con = Database.getInstance().getConnection();
        try {
            con.setAutoCommit(false); 

            double tongTienVe = danhSachGhe.size() * giaVe;
            double tongTienDV = 0;
            if (gioHangDV != null) {
                for (Object[] data : gioHangDV.values()) {
                    tongTienDV += (int)data[0] * (double)data[1];
                }
            }
            
            // Tính tổng tiền cuối cùng sau khi áp mã
            double tongTien = (tongTienVe + tongTienDV) - tienGiam;
            if (tongTien < 0) tongTien = 0;

            // 1. LƯU HÓA ĐƠN
            String sqlHD = "INSERT INTO HoaDon (MaHD, MaNV, NgayLap, TongTien, MaKH) VALUES (?, ?, GETDATE(), ?, ?)";
            PreparedStatement pstHD = con.prepareStatement(sqlHD);
            pstHD.setString(1, maHD);
            pstHD.setString(2, maNV); 
            pstHD.setDouble(3, tongTien);
            if (maKH == null || maKH.trim().isEmpty()) pstHD.setNull(4, java.sql.Types.VARCHAR);
            else pstHD.setString(4, maKH);
            pstHD.executeUpdate();

            // 2. LƯU VÉ PHIM & CHI TIẾT
            if (!danhSachGhe.isEmpty()) {
                String sqlVe = "INSERT INTO VePhim (MaHD, MaSuat, MaGhe, GiaVe) VALUES (?, ?, ?, ?)";
                PreparedStatement pstVe = con.prepareStatement(sqlVe);
                for (String ghe : danhSachGhe) {
                    pstVe.setString(1, maHD); pstVe.setString(2, maSuat);
                    pstVe.setString(3, ghe); pstVe.setDouble(4, giaVe);
                    pstVe.executeUpdate();
                }

                String sqlCTHD = "INSERT INTO ChiTietHoaDon (MaHD, MaSuat, SoLuong, GiaVe, ThanhTien) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstCTHD = con.prepareStatement(sqlCTHD);
                pstCTHD.setString(1, maHD); pstCTHD.setString(2, maSuat);
                pstCTHD.setInt(3, danhSachGhe.size()); pstCTHD.setDouble(4, giaVe);
                pstCTHD.setDouble(5, tongTienVe);
                pstCTHD.executeUpdate();
            }

            // 3. LƯU CHI TIẾT BẮP NƯỚC
            if (gioHangDV != null && !gioHangDV.isEmpty()) {
                String sqlDV = "INSERT INTO ChiTietDichVu (MaHD, MaDV, SoLuong, GiaBan, ThanhTien) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstDV = con.prepareStatement(sqlDV);
                for (String maDV : gioHangDV.keySet()) {
                    Object[] data = gioHangDV.get(maDV);
                    int sl = (int) data[0];
                    double gia = (double) data[1];
                    pstDV.setString(1, maHD); pstDV.setString(2, maDV);
                    pstDV.setInt(3, sl); pstDV.setDouble(4, gia);
                    pstDV.setDouble(5, sl * gia);
                    pstDV.executeUpdate();
                }
            }

            // 4. TÍCH ĐIỂM
            if (maKH != null && !maKH.trim().isEmpty() && tongTien > 0) {
                int diemCong = (int) (tongTien / 10000); 
                String sqlDiem = "UPDATE KhachHang SET DiemTichLuy = DiemTichLuy + ? WHERE MaKH = ?";
                PreparedStatement pstDiem = con.prepareStatement(sqlDiem);
                pstDiem.setInt(1, diemCong); pstDiem.setString(2, maKH);
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

    public ArrayList<Object[]> layChiTietDichVu(String maHD) {
        ArrayList<Object[]> ds = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT dv.TenDV, ct.SoLuong, ct.GiaBan, ct.ThanhTien FROM ChiTietDichVu ct JOIN DichVu dv ON ct.MaDV = dv.MaDV WHERE ct.MaHD = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maHD);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ds.add(new Object[]{ rs.getString("TenDV"), rs.getInt("SoLuong"), rs.getDouble("GiaBan"), rs.getDouble("ThanhTien") });
            }
        } catch (Exception e) {}
        return ds;
    }

    public ArrayList<String> layDanhSachGheDaBan(String maSuat) {
        ArrayList<String> dsGhe = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT MaGhe FROM VePhim WHERE MaSuat = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maSuat);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) { dsGhe.add(rs.getString("MaGhe")); }
        } catch (Exception e) {}
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
                dsHD.add(new HoaDon(rs.getString("MaHD"), rs.getString("MaNV"), rs.getString("NgayLap"), rs.getDouble("TongTien"), rs.getString("MaKH")));
            }
        } catch (Exception e) {}
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
                dsVe.add(new VePhim(rs.getInt("MaVe"), rs.getString("MaHD"), rs.getString("MaSuat"), rs.getString("MaGhe"), rs.getDouble("GiaVe")));
            }
        } catch (Exception e) {}
        return dsVe;
    }
    
    public double getTongDoanhThuTheoThang(int thang, int nam) {
        double tong = 0;
        String sql = "SELECT SUM(TongTien) FROM HoaDon WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?";
        
        try {
            // Lấy connection dùng chung nhưng KHÔNG bỏ vào trong ngoặc của khối try()
            java.sql.Connection con = connect.Database.getInstance().getConnection();
            java.sql.PreparedStatement pstmt = con.prepareStatement(sql);
            
            pstmt.setInt(1, thang);
            pstmt.setInt(2, nam);
            java.sql.ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                tong = rs.getDouble(1);
            }
            
            // Chỉ đóng ResultSet và PreparedStatement, TUYỆT ĐỐI KHÔNG ĐÓNG Connection
            rs.close();
            pstmt.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return tong;
    }
}