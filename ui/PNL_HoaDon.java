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
    
    // Lưu tạm danh sách hóa đơn để lấy thông tin chi tiết siêu tốc
    private ArrayList<HoaDon> listHoaDonCache = new ArrayList<>();

    // Các Label hiển thị như một tờ Bill thực tế
    private JLabel lblBillMaHD, lblBillNgay, lblBillNV, lblBillKH, lblBillTong;

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

        // ==========================================
        // 1. PHẦN TRÊN: DANH SÁCH HÓA ĐƠN
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(bgPanel);
        TitledBorder borderHD = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), "LỊCH SỬ BÁN VÉ",
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

        // ==========================================
        // 2. PHẦN DƯỚI: CHI TIẾT KẾT HỢP (GIAO DIỆN IN BILL)
        // ==========================================
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(bgPanel);
        TitledBorder borderCT = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), "HÓA ĐƠN CHI TIẾT",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), themeRed
        );
        pnlBottom.setBorder(BorderFactory.createCompoundBorder(borderCT, new EmptyBorder(5, 5, 5, 5)));

        // --- TẠO KHU VỰC HEADER CỦA HÓA ĐƠN (GIỐNG BILL IN) ---
        JPanel pnlBillHeader = new JPanel(new BorderLayout());
        pnlBillHeader.setBackground(bgPanel);
        pnlBillHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 70)),
            new EmptyBorder(10, 20, 15, 20)
        ));

        JLabel lblBillTitle = new JLabel("HÓA ĐƠN THANH TOÁN CINEMA", JLabel.CENTER);
        lblBillTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBillTitle.setForeground(colorGold);
        pnlBillHeader.add(lblBillTitle, BorderLayout.NORTH);

        JPanel pnlBillInfo = new JPanel(new GridLayout(3, 2, 15, 8));
        pnlBillInfo.setBackground(bgPanel);
        pnlBillInfo.setBorder(new EmptyBorder(10, 0, 0, 0));

        lblBillMaHD = new JLabel("Mã Hóa Đơn: ---");
        lblBillMaHD.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBillMaHD.setForeground(Color.WHITE);

        lblBillNgay = new JLabel("Thời gian in: ---");
        lblBillNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBillNgay.setForeground(Color.WHITE);

        lblBillNV = new JLabel("Thu Ngân: ---");
        lblBillNV.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBillNV.setForeground(Color.WHITE);

        lblBillKH = new JLabel("Khách Hàng: ---");
        lblBillKH.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBillKH.setForeground(Color.WHITE);

        lblBillTong = new JLabel("TỔNG CỘNG: 0 đ");
        lblBillTong.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBillTong.setForeground(themeRed);

        pnlBillInfo.add(lblBillMaHD);
        pnlBillInfo.add(lblBillNgay);
        pnlBillInfo.add(lblBillNV);
        pnlBillInfo.add(lblBillKH);
        pnlBillInfo.add(new JLabel("")); 
        pnlBillInfo.add(lblBillTong);

        pnlBillHeader.add(pnlBillInfo, BorderLayout.CENTER);
        
        // Gắn Header Bill vào phía trên bảng chi tiết
        pnlBottom.add(pnlBillHeader, BorderLayout.NORTH);

        // --- BẢNG CHI TIẾT ---
        String[] colsCT = {"Mã Suất Chiếu", "Số Lượng", "Vị Trí Ghế", "Đơn Giá", "Thành Tiền"};
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
        lblBillMaHD.setText("Mã Hóa Đơn: ---");
        lblBillNgay.setText("Thời gian in: ---");
        lblBillNV.setText("Thu Ngân: ---");
        lblBillKH.setText("Khách Hàng: ---");
        lblBillTong.setText("TỔNG CỘNG: 0 đ");
    }

    public void loadDataHoaDon() {
        modelHoaDon.setRowCount(0);
        listHoaDonCache = hoaDonDAO.layDanhSachHoaDon(); // Quét từ DB lên Cache
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

        for (HoaDon hd : listHoaDonCache) {
            String ngayLap = hd.getNgayLap();
            String ngayHienThi = ngayLap;
            try {
                if (ngayLap != null && ngayLap.length() >= 19) {
                    Date date = sdfIn.parse(ngayLap.substring(0, 19));
                    ngayHienThi = sdfOut.format(date); // Định dạng đẹp DD/MM/YYYY
                }
            } catch (Exception e) {}
            
            modelHoaDon.addRow(new Object[]{
                hd.getMaHD(), hd.getMaNV(), ngayHienThi, nf.format(hd.getTongTien()) + " đ"
            });
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tblHoaDon.getSelectedRow();
        if (row != -1) {
            // Lấy hóa đơn từ Cache để hiển thị lên Header Bill
            HoaDon hdChon = listHoaDonCache.get(row);
            String maHD = hdChon.getMaHD();
            
            // Ép lại định dạng ngày giờ cho Header
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

            // Cập nhật Thông tin Bill
            lblBillMaHD.setText("Mã Hóa Đơn: " + maHD);
            lblBillNgay.setText("Thời gian in: " + ngayHienThi);
            lblBillNV.setText("Thu Ngân: " + hdChon.getMaNV());
            
            String kh = (hdChon.getMaKH() == null || hdChon.getMaKH().trim().isEmpty()) ? "Khách Vãng Lai (Không Thẻ)" : hdChon.getMaKH();
            lblBillKH.setText("Thẻ Khách: " + kh);
            lblBillTong.setText("TỔNG CỘNG: " + nf.format(hdChon.getTongTien()) + " đ");

            modelChiTiet.setRowCount(0); 
            
            // Lấy Thông tin tổng quan từ Chi Tiết Hóa Đơn
            ArrayList<ChiTietHoaDon> dsCTHD = cthdDAO.layChiTietHoaDon(maHD);
            
            // Lấy Thông tin ghế từ Vé Phim
            ArrayList<VePhim> dsVe = hoaDonDAO.layChiTietVe(maHD);

            // --- THUẬT TOÁN GHÉP CHUỖI GHẾ ---
            for (ChiTietHoaDon cthd : dsCTHD) {
                StringBuilder danhSachGhe = new StringBuilder();
                
                for (VePhim ve : dsVe) {
                    // Nếu mã vé thuộc về đúng mã suất này thì ghép ghế vào
                    if (ve.getMaSuat().equals(cthd.getMaSuat())) {
                        if (danhSachGhe.length() > 0) {
                            danhSachGhe.append(", ");
                        }
                        danhSachGhe.append(ve.getMaGhe());
                    }
                }

                modelChiTiet.addRow(new Object[]{
                    cthd.getMaSuat(), 
                    cthd.getSoLuong() + " Vé", 
                    danhSachGhe.toString(), 
                    nf.format(cthd.getGiaVe()) + " đ",
                    nf.format(cthd.getThanhTien()) + " đ"
                });
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}