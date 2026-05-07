package ui;

import dao.HoaDonDAO;
import dao.CTHD_DAO;
import entity.HoaDon;
import entity.ChiTietHoaDon;
import entity.VePhim;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PNL_HoaDon extends JPanel implements MouseListener {
    private DefaultTableModel modelHoaDon;
    private JTable tblHoaDon;
    private DefaultTableModel modelChiTiet;
    private JTable tblChiTiet;
    private HoaDonDAO hoaDonDAO;
    private CTHD_DAO cthdDAO;
    
    private ArrayList<HoaDon> listHoaDonCache = new ArrayList<>();
    
    // Bổ sung thêm nhãn hiển thị tiền giảm giá
    private JLabel lblBillMaHD, lblBillNgay, lblBillNV, lblBillKH, lblBillTienGiam, lblBillTong;

    private Color bgDark = new Color(18, 18, 18);
    private Color bgPanel = new Color(30, 30, 30);
    private Color textWhite = new Color(240, 240, 240);
    private Color themeRed = new Color(229, 9, 20); 
    private Color colorGold = new Color(212, 175, 55); 

    public PNL_HoaDon() {
        hoaDonDAO = new HoaDonDAO();
        cthdDAO = new CTHD_DAO();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(bgDark);
        setBorder(new EmptyBorder(15, 20, 20, 20));

        // --- DANH SÁCH HÓA ĐƠN ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(bgPanel);
        TitledBorder borderHD = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), "LỊCH SỬ BÁN HÀNG",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), themeRed
        );
        pnlTop.setBorder(BorderFactory.createCompoundBorder(borderHD, new EmptyBorder(5, 5, 5, 5)));

        String[] colsHD = {"Mã Hóa Đơn", "Thu Ngân Lập", "Ngày Giờ Bán", "Tổng Tiền"};
        modelHoaDon = new DefaultTableModel(colsHD, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } 
        };
        tblHoaDon = new JTable(modelHoaDon);
        setupTableStyle(tblHoaDon);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblHoaDon.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane scrollHD = new JScrollPane(tblHoaDon);
        scrollHD.getViewport().setBackground(bgPanel);
        scrollHD.setBorder(BorderFactory.createEmptyBorder());
        scrollHD.setPreferredSize(new Dimension(0, 300));
        pnlTop.add(scrollHD, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // --- CHI TIẾT HÓA ĐƠN ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(bgPanel);
        TitledBorder borderCT = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), "CHI TIẾT MUA HÀNG",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), themeRed
        );
        pnlBottom.setBorder(BorderFactory.createCompoundBorder(borderCT, new EmptyBorder(5, 5, 5, 5)));

        JPanel pnlBillHeader = new JPanel(new BorderLayout());
        pnlBillHeader.setBackground(bgPanel);
        pnlBillHeader.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 70)), new EmptyBorder(10, 20, 15, 20)));

        JLabel lblBillTitle = new JLabel("HÓA ĐƠN THANH TOÁN", JLabel.CENTER);
        lblBillTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBillTitle.setForeground(colorGold);
        pnlBillHeader.add(lblBillTitle, BorderLayout.NORTH);

        // Tăng lên 4 dòng để chứa thêm dòng Giảm giá
        JPanel pnlBillInfo = new JPanel(new GridLayout(4, 2, 15, 8));
        pnlBillInfo.setBackground(bgPanel);
        pnlBillInfo.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        lblBillMaHD = new JLabel("Mã Hóa Đơn: ---"); lblBillMaHD.setForeground(Color.WHITE);
        lblBillNgay = new JLabel("Thời gian in: ---"); lblBillNgay.setForeground(Color.WHITE);
        lblBillNV = new JLabel("Thu Ngân: ---"); lblBillNV.setForeground(Color.WHITE);
        lblBillKH = new JLabel("Khách Hàng: ---"); lblBillKH.setForeground(Color.WHITE);
        
        lblBillTienGiam = new JLabel("Giảm giá Voucher: 0 đ"); 
        lblBillTienGiam.setForeground(new Color(46, 204, 113)); // Màu xanh lá cho phần giảm
        lblBillTienGiam.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        lblBillTong = new JLabel("TỔNG CỘNG: 0 đ"); 
        lblBillTong.setForeground(themeRed); 
        lblBillTong.setFont(new Font("Segoe UI", Font.BOLD, 16));

        pnlBillInfo.add(lblBillMaHD); pnlBillInfo.add(lblBillNgay);
        pnlBillInfo.add(lblBillNV); pnlBillInfo.add(lblBillKH);
        pnlBillInfo.add(new JLabel("")); pnlBillInfo.add(lblBillTienGiam);
        pnlBillInfo.add(new JLabel("")); pnlBillInfo.add(lblBillTong);

        pnlBillHeader.add(pnlBillInfo, BorderLayout.CENTER);
        pnlBottom.add(pnlBillHeader, BorderLayout.NORTH);

        // Bảng chi tiết kết hợp
        String[] colsCT = {"Phân Loại", "Số Lượng", "Chi Tiết Sản Phẩm/Ghế", "Đơn Giá", "Thành Tiền"};
        modelChiTiet = new DefaultTableModel(colsCT, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblChiTiet = new JTable(modelChiTiet);
        setupTableStyle(tblChiTiet);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); 
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); 
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); 

        JScrollPane scrollCT = new JScrollPane(tblChiTiet);
        scrollCT.getViewport().setBackground(bgPanel);
        scrollCT.setBorder(BorderFactory.createEmptyBorder());
        pnlBottom.add(scrollCT, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.CENTER);

        tblHoaDon.addMouseListener(this);
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadDataHoaDon(); 
                resetBillHeader();
                modelChiTiet.setRowCount(0); 
            }
        });
        loadDataHoaDon();
    }

    private void setupTableStyle(JTable table) {
        table.setRowHeight(35);
        table.setBackground(bgPanel);
        table.setForeground(textWhite);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setSelectionBackground(themeRed);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(20, 20, 20));
        headerRenderer.setForeground(themeRed);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    private void resetBillHeader() {
        lblBillMaHD.setText("Mã Hóa Đơn: ---"); lblBillNgay.setText("Thời gian in: ---");
        lblBillNV.setText("Thu Ngân: ---"); lblBillKH.setText("Khách Hàng: ---");
        lblBillTienGiam.setText("Giảm giá Voucher: 0 đ"); lblBillTong.setText("TỔNG CỘNG: 0 đ");
    }

    public void loadDataHoaDon() {
        modelHoaDon.setRowCount(0);
        listHoaDonCache = hoaDonDAO.layDanhSachHoaDon(); 
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

        for (HoaDon hd : listHoaDonCache) {
            String ngayHienThi = hd.getNgayLap();
            try {
                if (ngayHienThi != null && ngayHienThi.length() >= 19) {
                    Date date = sdfIn.parse(ngayHienThi.substring(0, 19));
                    ngayHienThi = sdfOut.format(date);
                }
            } catch (Exception e) {}
            modelHoaDon.addRow(new Object[]{ hd.getMaHD(), hd.getMaNV(), ngayHienThi, nf.format(hd.getTongTien()) + " đ" });
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tblHoaDon.getSelectedRow();
        if (row != -1) {
            HoaDon hdChon = listHoaDonCache.get(row);
            String maHD = hdChon.getMaHD();
            String ngayHienThi = hdChon.getNgayLap();
            try {
                SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy lúc HH:mm:ss");
                if (ngayHienThi != null && ngayHienThi.length() >= 19) {
                    Date date = sdfIn.parse(ngayHienThi.substring(0, 19));
                    ngayHienThi = sdfOut.format(date);
                }
            } catch (Exception ex) {}

            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            lblBillMaHD.setText("Mã Hóa Đơn: " + maHD);
            lblBillNgay.setText("Thời gian in: " + ngayHienThi);
            lblBillNV.setText("Thu Ngân: " + hdChon.getMaNV());
            
            String kh = (hdChon.getMaKH() == null || hdChon.getMaKH().trim().isEmpty()) ? "Khách Vãng Lai (Không Thẻ)" : hdChon.getMaKH();
            lblBillKH.setText("Thẻ Khách: " + kh);
            
            modelChiTiet.setRowCount(0); 
            double tamTinhGoc = 0; // Biến tính tổng tiền gốc (chưa giảm)
            
            // 1. In vé phim
            ArrayList<ChiTietHoaDon> dsCTHD = cthdDAO.layChiTietHoaDon(maHD);
            ArrayList<VePhim> dsVe = hoaDonDAO.layChiTietVe(maHD);
            for (ChiTietHoaDon cthd : dsCTHD) {
                StringBuilder danhSachGhe = new StringBuilder();
                for (VePhim ve : dsVe) {
                    if (ve.getMaSuat().equals(cthd.getMaSuat())) {
                        if (danhSachGhe.length() > 0) danhSachGhe.append(", ");
                        danhSachGhe.append(ve.getMaGhe());
                    }
                }
                double thanhTienVe = cthd.getThanhTien();
                tamTinhGoc += thanhTienVe;
                
                modelChiTiet.addRow(new Object[]{ "Vé Phim", cthd.getSoLuong() + " Vé", "Suất " + cthd.getMaSuat() + " - Ghế: " + danhSachGhe.toString(), nf.format(cthd.getGiaVe()) + " đ", nf.format(thanhTienVe) + " đ" });
            }
            
            // 2. In dịch vụ Bắp Nước
            ArrayList<Object[]> dsDV = hoaDonDAO.layChiTietDichVu(maHD);
            for(Object[] dv : dsDV) {
                double thanhTienDV = (double) dv[3];
                tamTinhGoc += thanhTienDV;
                
                modelChiTiet.addRow(new Object[]{ "Bắp Nước", dv[1] + " Phần", dv[0], nf.format(dv[2]) + " đ", nf.format(thanhTienDV) + " đ" });
            }
            
            // 3. Xử lý thuật toán tìm ra Tiền Giảm Giá
            double tongThucThu = hdChon.getTongTien();
            double tienGiam = tamTinhGoc - tongThucThu;
            
            // Khử sai số thập phân (nếu có)
            if (tienGiam < 1) tienGiam = 0; 

            if (tienGiam > 0) {
                lblBillTienGiam.setText("Giảm giá Voucher: -" + nf.format(tienGiam) + " đ");
            } else {
                lblBillTienGiam.setText("Giảm giá Voucher: 0 đ");
            }
            
            lblBillTong.setText("TỔNG CỘNG: " + nf.format(tongThucThu) + " đ");
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}