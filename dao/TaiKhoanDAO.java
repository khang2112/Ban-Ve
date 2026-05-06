package dao;

import connect.Database;
import entity.TaiKhoan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaiKhoanDAO {
    public TaiKhoan kiemTraDangNhap(String username, String password) {
        TaiKhoan tk = null;
        try {
            Connection con = connect.Database.getInstance().getConnection();
            String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                tk = new TaiKhoan();
                tk.setTenDangNhap(rs.getString("TenDangNhap"));
                tk.setMatKhau(rs.getString("MatKhau"));
                tk.setHoTen(rs.getString("HoTen"));
                tk.setVaiTro(rs.getString("VaiTro")); // PHỤC HỒI ĐỂ PHÂN QUYỀN
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tk;
    }
}