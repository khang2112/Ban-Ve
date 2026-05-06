package dao;

import connect.Database;
import entity.NhanVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class NhanVienDAO {
    
    public ArrayList<NhanVien> docTuBang() {
        ArrayList<NhanVien> dsNV = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM NhanVien";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String ma = rs.getString("MaNV");
                String ho = rs.getString("HoNV");
                String ten = rs.getString("TenNV");
                int tuoi = rs.getInt("Tuoi");
                String phong = rs.getString("PhongBan");
                double luong = rs.getDouble("TienLuong");
                dsNV.add(new NhanVien(ma, ho, ten, tuoi, phong, luong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsNV;
    }

    public boolean themNhanVien(NhanVien nv) {
        Connection con = Database.getInstance().getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            // FIX: Ghi rõ tên cột
            String sql = "INSERT INTO NhanVien (MaNV, HoNV, TenNV, Tuoi, PhongBan, TienLuong) VALUES(?, ?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, nv.getMaNV());
            stmt.setString(2, nv.getHoNV());
            stmt.setString(3, nv.getTenNV());
            stmt.setInt(4, nv.getTuoi());
            stmt.setString(5, nv.getPhongBan());
            stmt.setDouble(6, nv.getTienLuong());
            n = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    public boolean xoaNhanVien(String maNV) {
        Connection con = Database.getInstance().getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maNV);
            n = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    public boolean suaNhanVien(NhanVien nv) {
        Connection con = Database.getInstance().getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            String sql = "UPDATE NhanVien SET HoNV=?, TenNV=?, Tuoi=?, PhongBan=?, TienLuong=? WHERE MaNV=?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, nv.getHoNV());
            stmt.setString(2, nv.getTenNV());
            stmt.setInt(3, nv.getTuoi());
            stmt.setString(4, nv.getPhongBan());
            stmt.setDouble(5, nv.getTienLuong());
            stmt.setString(6, nv.getMaNV());
            n = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n > 0;
    }
    
    public ArrayList<NhanVien> timNhanVien(String tuKhoa) {
        ArrayList<NhanVien> ds = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM NhanVien WHERE MaNV LIKE ? OR TenNV LIKE ? OR HoNV LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            String pattern = "%" + tuKhoa + "%"; 
            pst.setString(1, pattern);
            pst.setString(2, pattern);
            pst.setString(3, pattern);
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String ma = rs.getString("MaNV");
                String ho = rs.getString("HoNV");
                String ten = rs.getString("TenNV");
                int tuoi = rs.getInt("Tuoi");
                String phong = rs.getString("PhongBan");
                double luong = rs.getDouble("TienLuong");
                ds.add(new NhanVien(ma, ho, ten, tuoi, phong, luong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
}