package dao;

import connect.Database;
import entity.DichVu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DichVuDAO {
    public ArrayList<DichVu> docTuBang() {
        ArrayList<DichVu> ds = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM DichVu";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ds.add(new DichVu(rs.getString("MaDV"), rs.getString("TenDV"), rs.getDouble("GiaDV")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
}