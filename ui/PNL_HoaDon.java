package ui;

import dao.HoaDonDAO;
import entity.HoaDon;
import entity.VePhim;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.ArrayList;
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

    // --- BẢNG MÀU CHỦ ĐỀ ĐỎ (NETFLIX / CGV THEME) ---
    private Color bgDark = new Color(18, 18, 18);
    private Color bgPanel = new Color(30, 30, 30);
    private Color textWhite = new Color(240, 240, 240);
    private Color themeRed = new Color(229, 9, 20); 

    public PNL_HoaDon() {
        hoaDonDAO = new HoaDonDAO();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(bgDark);
        setBorder(new EmptyBorder(15, 20, 20, 20));

        // ==========================================
        // 1. PHẦN TRÊN: DANH SÁCH HÓA ĐƠN
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(bgPanel);
        TitledBorder borderHD = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), "DANH SÁCH HÓA ĐƠN",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), themeRed
        );
        pnlTop.setBorder(BorderFactory.createCompoundBorder(borderHD, new EmptyBorder(5, 5, 5, 5)));

        String[] colsHD = {"Mã Hóa Đơn", "Nhân Viên Lập", "Ngày Lập", "Tổng Tiền"};
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
        // 2. PHẦN DƯỚI: CHI TIẾT VÉ PHIM
        // ==========================================
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(bgPanel);
        TitledBorder borderCT = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), "CHI TIẾT VÉ PHIM (Click chọn Hóa Đơn ở trên để xem)",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), themeRed
        );
        pnlBottom.setBorder(BorderFactory.createCompoundBorder(borderCT, new EmptyBorder(5, 5, 5, 5)));

        String[] colsCT = {"Mã Vé", "Mã Suất Chiếu", "Ghế", "Giá Vé"};
        modelChiTiet = new DefaultTableModel(colsCT, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblChiTiet = new JTable(modelChiTiet);
        setupTableStyle(tblChiTiet);

        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); 

        JScrollPane scrollCT = new JScrollPane(tblChiTiet);
        scrollCT.getViewport().setBackground(bgPanel);
        scrollCT.setBorder(BorderFactory.createEmptyBorder());
        
        pnlBottom.add(scrollCT, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.CENTER);

        tblHoaDon.addMouseListener(this);
        
        // --- FIX: GẮN CẢM BIẾN TỰ ĐỘNG CẬP NHẬT KHI MỞ TAB ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadDataHoaDon(); // Quét Hóa đơn mới nhất
                modelChiTiet.setRowCount(0); // Làm sạch bảng chi tiết cũ
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

    public void loadDataHoaDon() {
        modelHoaDon.setRowCount(0);
        ArrayList<HoaDon> dsHD = hoaDonDAO.layDanhSachHoaDon();
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        for (HoaDon hd : dsHD) {
            String ngayLap = hd.getNgayLap();
            if (ngayLap != null && ngayLap.length() >= 19) {
                ngayLap = ngayLap.substring(0, 19);
            }
            
            modelHoaDon.addRow(new Object[]{
                hd.getMaHD(), 
                hd.getMaNV(), 
                ngayLap, 
                nf.format(hd.getTongTien()) + " đ"
            });
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tblHoaDon.getSelectedRow();
        if (row != -1) {
            String maHD = modelHoaDon.getValueAt(row, 0).toString();
            modelChiTiet.setRowCount(0); 
            
            ArrayList<VePhim> dsVe = hoaDonDAO.layChiTietVe(maHD);
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

            for (VePhim ve : dsVe) {
                modelChiTiet.addRow(new Object[]{
                    ve.getMaVe(), 
                    ve.getMaSuat(), 
                    ve.getMaGhe(), 
                    nf.format(ve.getGiaVe()) + " đ"
                });
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}