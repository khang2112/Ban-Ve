package dao;

import connect.Database;
import entity.Phim;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import entity.TopPhim;   
import java.util.List;   

public class PhimDAO {
    
    // 1. Hàm ĐỌC danh sách phim (Đã thêm cột HinhAnh)
    public ArrayList<Phim> docTuBang() {
        ArrayList<Phim> dsPhim = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM Phim";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            
            while (rs.next()) {
                String ma = rs.getString("MaPhim");
                String ten = rs.getString("TenPhim");
                String theLoai = rs.getString("TheLoai");
                double gia = rs.getDouble("GiaVe");
                String hinhAnh = rs.getString("HinhAnh"); // Lấy thêm đường dẫn ảnh
                
                // Sử dụng constructor 5 tham số của entity.Phim
                Phim p = new Phim(ma, ten, theLoai, gia, hinhAnh);
                dsPhim.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsPhim;
    }
    
    // 2. Hàm THÊM phim (Đã thêm cột HinhAnh)
    public boolean themPhim(Phim p) {
        Connection con = Database.getInstance().getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            // Thêm HinhAnh vào câu lệnh INSERT và thêm 1 dấu ?
            String sql = "INSERT INTO Phim (MaPhim, TenPhim, TheLoai, GiaVe, HinhAnh) VALUES(?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, p.getMaPhim());
            stmt.setString(2, p.getTenPhim());
            stmt.setString(3, p.getTheLoai());
            stmt.setDouble(4, p.getGiaVe());
            stmt.setString(5, p.getHinhAnh()); // Lưu đường dẫn ảnh
            
            n = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // 3. Hàm XÓA phim (Không cần thay đổi)
    public boolean xoaPhim(String maPhim) {
        Connection con = Database.getInstance().getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            String sql = "DELETE FROM Phim WHERE MaPhim = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhim);
            n = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // 4. Hàm SỬA phim (Đã thêm cột HinhAnh)
    public boolean suaPhim(Phim p) {
        Connection con = Database.getInstance().getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            // Thêm HinhAnh = ? vào câu lệnh UPDATE
            String sql = "UPDATE Phim SET TenPhim = ?, TheLoai = ?, GiaVe = ?, HinhAnh = ? WHERE MaPhim = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, p.getTenPhim());
            stmt.setString(2, p.getTheLoai());
            stmt.setDouble(3, p.getGiaVe());
            stmt.setString(4, p.getHinhAnh()); // Cập nhật đường dẫn ảnh
            stmt.setString(5, p.getMaPhim());
            
            n = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n > 0;
    }
    
    // 5. Hàm lấy top phim (Giữ nguyên của bạn)
    public List<TopPhim> getTop3PhimHomNay() {
        List<TopPhim> list = new ArrayList<>();
        try {
            Connection con = connect.Database.getInstance().getConnection();
            // Nối qua 4 bảng để đếm chính xác số lượng vé bán ra trong ngày
            String sql = 
                "SELECT TOP 3 p.TenPhim, COUNT(v.MaVe) AS SoVeBan " +
                "FROM VePhim v " +
                "JOIN SuatChieu s ON v.MaSuat = s.MaSuat " +
                "JOIN Phim p ON s.MaPhim = p.MaPhim " +
                "JOIN HoaDon hd ON v.MaHD = hd.MaHD " +
                "WHERE CAST(hd.NgayLap AS DATE) = CAST(GETDATE() AS DATE) " +
                "GROUP BY p.TenPhim " +
                "ORDER BY SoVeBan DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new TopPhim(
                    rs.getString("TenPhim"),
                    rs.getInt("SoVeBan")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    // =========================================================
    // HÀM TỰ ĐỘNG PHÁT SINH MÁ PHIM MỚI (P001, P002...)
    // =========================================================
    public String phatSinhMaPhim() {
        String maMoi = "P001"; // Mã mặc định nếu database trống
        try {
            Connection con = Database.getInstance().getConnection();
            // Lấy mã phim lớn nhất hiện tại
            String sql = "SELECT MAX(MaPhim) FROM Phim";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                String maxMa = rs.getString(1);
                if (maxMa != null && maxMa.length() > 1) {
                    // Cắt chữ 'P' ở đầu, lấy phần số, cộng thêm 1
                    int so = Integer.parseInt(maxMa.substring(1)) + 1;
                    // Format lại thành chuỗi P kèm 3 chữ số (VD: 6 -> P006)
                    maMoi = String.format("P%03d", so);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maMoi;
    }

    // =========================================================
    // HÀM LẤY DANH SÁCH THỂ LOẠI (KHÔNG TRÙNG LẶP)
    // =========================================================
    public ArrayList<String> layDanhSachTheLoai() {
        ArrayList<String> list = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            // Lấy các thể loại phân biệt từ bảng Phim
            String sql = "SELECT DISTINCT TheLoai FROM Phim WHERE TheLoai IS NOT NULL AND LTRIM(RTRIM(TheLoai)) <> ''";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                list.add(rs.getString(1).trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Nếu DB chưa có phim nào, add tạm một số thể loại cơ bản
        if (list.isEmpty()) {
            list.add("Hành động");
            list.add("Tình cảm");
            list.add("Kinh dị");
            list.add("Hài hước");
            list.add("Hoạt hình");
            list.add("Khoa học viễn tưởng");
        }
        return list;
    }
}