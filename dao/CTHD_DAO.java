package dao;

import connect.Database;
import entity.ChiTietHoaDon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class CTHD_DAO {
    
    public ArrayList<ChiTietHoaDon> layChiTietHoaDon(String maHD) {
        ArrayList<ChiTietHoaDon> ds = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM ChiTietHoaDon WHERE MaHD = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maHD);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String mHD = rs.getString("MaHD");
                String mSuat = rs.getString("MaSuat");
                int sl = rs.getInt("SoLuong");
                double gia = rs.getDouble("GiaVe");
                double tong = rs.getDouble("ThanhTien");
                ds.add(new ChiTietHoaDon(mHD, mSuat, sl, gia, tong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
}