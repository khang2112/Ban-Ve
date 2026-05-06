package dao;

import connect.Database;
import entity.KhachHang;
import java.sql.*;
import java.util.ArrayList;

public class KhachHangDAO {
    
    public ArrayList<KhachHang> docTuBang() {
        ArrayList<KhachHang> ds = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM KhachHang";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ds.add(new KhachHang(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    public boolean themKhachHang(KhachHang kh) {
        Connection con = Database.getInstance().getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            stmt = con.prepareStatement("INSERT INTO KhachHang VALUES(?, ?, ?, ?)");
            stmt.setString(1, kh.getMaKH());
            stmt.setString(2, kh.getTenKH());
            stmt.setString(3, kh.getSoDienThoai());
            stmt.setInt(4, kh.getDiemTichLuy());
            n = stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return n > 0;
    }

    public boolean xoaKhachHang(String ma) {
        Connection con = Database.getInstance().getConnection();
        int n = 0;
        try {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM KhachHang WHERE MaKH = ?");
            stmt.setString(1, ma);
            n = stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return n > 0;
    }

    public boolean suaKhachHang(KhachHang kh) {
        Connection con = Database.getInstance().getConnection();
        int n = 0;
        try {
            PreparedStatement stmt = con.prepareStatement("UPDATE KhachHang SET TenKH=?, SoDienThoai=?, DiemTichLuy=? WHERE MaKH=?");
            stmt.setString(1, kh.getTenKH());
            stmt.setString(2, kh.getSoDienThoai());
            stmt.setInt(3, kh.getDiemTichLuy());
            stmt.setString(4, kh.getMaKH());
            n = stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return n > 0;
    }
    public boolean kiemTraTonTai(String maKH) {
        try {
            Connection con = connect.Database.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT MaKH FROM KhachHang WHERE MaKH = ?");
            pst.setString(1, maKH);
            java.sql.ResultSet rs = pst.executeQuery();
            return rs.next(); // Nếu tìm thấy dòng dữ liệu -> trả về true (Đã tồn tại)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}