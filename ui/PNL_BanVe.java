package ui;

import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.PhimDAO;
import dao.SuatChieuDAO;
import entity.Phim;
import entity.SuatChieu;
import entity.TaiKhoan;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PNL_BanVe extends JPanel implements ActionListener {
	private UI_TrangChu ui_TrangChu;
    
    private JComboBox<String> cboPhim, cboNgay, cboSuatChieu;
    private JPanel pnlSeatMap;
    private DefaultTableModel cartModel;
    private JTable tblCart;
    private JButton btnThanhToan, btnHuy;
    private JLabel lblTongTien, lblSoLuong;
    private JTextField txtMaKH;
    
    private ArrayList<SeatButton> listGhế = new ArrayList<>();
    private ArrayList<String> gheDangChon = new ArrayList<>();
    private double giaVeHienTai = 80000; 
    
    // --- FIX: CỜ CHẶN RESET DỮ LIỆU ---
    private boolean isTuDongChon = false;
    
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private KhachHangDAO khachHangDAO = new KhachHangDAO(); 
    private SuatChieuDAO suatChieuDAO = new SuatChieuDAO();
    private PhimDAO phimDAO = new PhimDAO();

    private ArrayList<Phim> dsPhimAll = new ArrayList<>();
    private ArrayList<SuatChieu> dsSuatChieuAll = new ArrayList<>();

    class SeatButton extends JToggleButton {
        public SeatButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false); 
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (!isEnabled()) {
                g2.setColor(new Color(229, 9, 20)); 
            } else if (isSelected()) {
                g2.setColor(new Color(46, 204, 113)); 
            } else {
                g2.setColor(new Color(60, 60, 60)); 
            }

            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
            g2.dispose();
            super.paintComponent(g); 
        }
    }

    class PosButton extends JButton {
        private Color bgColor;
        public PosButton(String text, Color bg, Color fg) {
            super(text);
            this.bgColor = bg;
            setBackground(bg); 
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(fg);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(bgColor.brighter()); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(bgColor); repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground() == null ? bgColor : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public PNL_BanVe(UI_TrangChu ui_TrangChu) {
    	this.ui_TrangChu = ui_TrangChu;
        setLayout(new BorderLayout(20, 20)); 
        setBackground(new Color(18, 18, 18)); 
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlTop = new JPanel(new BorderLayout(10, 0));
        pnlTop.setOpaque(false);
        pnlTop.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlFilters.setOpaque(false);
        
        cboPhim = createStyledComboBox(220);
        cboNgay = createStyledComboBox(140);
        cboSuatChieu = createStyledComboBox(140);
        
        pnlFilters.add(taoWrapCombo("1. CHỌN PHIM:", cboPhim));
        pnlFilters.add(taoWrapCombo("2. CHỌN NGÀY:", cboNgay));
        pnlFilters.add(taoWrapCombo("3. CHỌN SUẤT:", cboSuatChieu));

        JPanel pnlTopRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlTopRight.setOpaque(false);
        
        txtMaKH = new JTextField();
        txtMaKH.setPreferredSize(new Dimension(180, 40));
        txtMaKH.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtMaKH.setBackground(new Color(40, 40, 40));
        txtMaKH.setForeground(new Color(212, 175, 55)); 
        txtMaKH.setCaretColor(Color.WHITE);
        txtMaKH.setHorizontalAlignment(JTextField.CENTER);
        txtMaKH.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(70, 70, 70), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));

        pnlTopRight.add(taoWrapCombo("MÃ KHÁCH HÀNG:", txtMaKH));

        pnlTop.add(pnlFilters, BorderLayout.WEST);
        pnlTop.add(pnlTopRight, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 20));
        pnlCenter.setOpaque(false);
        
        JLabel lblScreen = new JLabel("M À N   H Ì N H", JLabel.CENTER);
        lblScreen.setOpaque(true);
        lblScreen.setBackground(new Color(52, 152, 219)); 
        lblScreen.setForeground(Color.WHITE);
        lblScreen.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblScreen.setPreferredSize(new Dimension(0, 40));
        lblScreen.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(41, 128, 185))); 
        pnlCenter.add(lblScreen, BorderLayout.NORTH);

        pnlSeatMap = new JPanel(new GridLayout(6, 10, 12, 12));
        pnlSeatMap.setOpaque(false);
        pnlSeatMap.setBorder(new EmptyBorder(10, 40, 20, 40));
        
        taoSoDoGheChuyenNghiep(); 
        
        pnlCenter.add(pnlSeatMap, BorderLayout.CENTER);
        
        JPanel pnlLegend = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        pnlLegend.setOpaque(false);
        pnlLegend.add(createLegend(new Color(60, 60, 60), "Ghế Trống"));
        pnlLegend.add(createLegend(new Color(46, 204, 113), "Đang Chọn"));
        pnlLegend.add(createLegend(new Color(229, 9, 20), "Đã Bán"));
        pnlCenter.add(pnlLegend, BorderLayout.SOUTH);

        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlRight = new JPanel(new BorderLayout(0, 15));
        pnlRight.setOpaque(false);
        pnlRight.setPreferredSize(new Dimension(400, 0)); 
        pnlRight.setBorder(new EmptyBorder(0, 20, 0, 0)); 

        String[] cartCols = {"Loại", "Ghế", "Thành tiền"};
        cartModel = new DefaultTableModel(cartCols, 0);
        tblCart = new JTable(cartModel);
        tblCart.setRowHeight(35);
        tblCart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblCart.setBackground(new Color(30, 30, 30));
        tblCart.setForeground(Color.WHITE);
        tblCart.setShowGrid(false); 
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(40, 40, 40));
        headerRenderer.setForeground(new Color(212, 175, 55)); 
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        for (int i = 0; i < tblCart.getModel().getColumnCount(); i++) {
            tblCart.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblCart.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        JScrollPane scrollCart = new JScrollPane(tblCart);
        scrollCart.getViewport().setBackground(new Color(30, 30, 30));
        scrollCart.setBorder(new LineBorder(new Color(60, 60, 60), 1));
        
        JPanel pnlCartWrapper = new JPanel(new BorderLayout());
        pnlCartWrapper.setOpaque(false);
        JLabel lblCartTitle = new JLabel("CHI TIẾT VÉ CHỌN");
        lblCartTitle.setForeground(Color.WHITE);
        lblCartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCartTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnlCartWrapper.add(lblCartTitle, BorderLayout.NORTH);
        pnlCartWrapper.add(scrollCart, BorderLayout.CENTER);
        
        pnlRight.add(pnlCartWrapper, BorderLayout.CENTER);

        JPanel pnlCheckout = new JPanel(new BorderLayout(0, 15));
        pnlCheckout.setBackground(new Color(35, 35, 35));
        pnlCheckout.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 60, 60), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel pnlTotalInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlTotalInfo.setOpaque(false);
        
        lblSoLuong = new JLabel("Số lượng vé: 0", JLabel.LEFT);
        lblSoLuong.setForeground(new Color(200, 200, 200));
        lblSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        lblTongTien = new JLabel("0 đ", JLabel.RIGHT);
        lblTongTien.setForeground(new Color(46, 204, 113)); 
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 36));
        
        pnlTotalInfo.add(lblSoLuong);
        pnlTotalInfo.add(lblTongTien);
        
        pnlCheckout.add(pnlTotalInfo, BorderLayout.NORTH);

        JPanel pnlActionBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlActionBtns.setOpaque(false);
        
        btnHuy = new PosButton("HỦY BỎ", new Color(100, 100, 100), Color.WHITE);
        btnThanhToan = new PosButton("THANH TOÁN", new Color(229, 9, 20), Color.WHITE); 
        
        pnlActionBtns.add(btnHuy);
        pnlActionBtns.add(btnThanhToan);
        pnlCheckout.add(pnlActionBtns, BorderLayout.SOUTH);

        pnlRight.add(pnlCheckout, BorderLayout.SOUTH);
        add(pnlRight, BorderLayout.EAST);

        btnThanhToan.addActionListener(this);
        btnHuy.addActionListener(this);
        
        // --- FIX: CẢM BIẾN TẢI LẠI TRANG CHỈ KHI KHÔNG TỰ ĐỘNG CHỌN ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (isTuDongChon) {
                    isTuDongChon = false; // Tắt cờ, không reset data
                } else {
                    loadDuLieuVaoBoNho(); // Nếu bấm thủ công vào tab Bán Vé thì nạp mới
                }
            }
        });
    }

    private JComboBox<String> createStyledComboBox(int width) {
        JComboBox<String> cbo = new JComboBox<>();
        cbo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cbo.setPreferredSize(new Dimension(width, 40));
        cbo.setUI(new BasicComboBoxUI());
        cbo.setBackground(new Color(40, 40, 40));
        cbo.setForeground(Color.WHITE);
        return cbo;
    }

    private JPanel taoWrapCombo(String title, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 5));
        pnl.setOpaque(false);
        JLabel lbl = new JLabel(title);
        lbl.setForeground(new Color(150, 150, 150));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createLegend(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        JLabel lblColor = new JLabel();
        lblColor.setPreferredSize(new Dimension(18, 18));
        lblColor.setOpaque(true);
        lblColor.setBackground(c);
        lblColor.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        JLabel lblText = new JLabel(text);
        lblText.setForeground(Color.LIGHT_GRAY);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(lblColor);
        p.add(lblText);
        return p;
    }

    private void taoSoDoGheChuyenNghiep() {
        pnlSeatMap.removeAll();
        listGhế.clear();
        String[] hang = {"A", "B", "C", "D", "E", "F"};
        for (int i = 0; i < hang.length; i++) {
            int gheSo = 1;
            for (int col = 0; col < 10; col++) {
                if (col == 2 || col == 7) {
                    pnlSeatMap.add(new JLabel("")); 
                } else {
                    String tenGhe = hang[i] + gheSo;
                    SeatButton btnGhe = new SeatButton(tenGhe); 
                    
                    btnGhe.addActionListener(e -> {
                        if (btnGhe.isSelected()) gheDangChon.add(tenGhe);
                        else gheDangChon.remove(tenGhe);
                        capNhatGioHang();
                    });
                    
                    listGhế.add(btnGhe);
                    pnlSeatMap.add(btnGhe);
                    gheSo++;
                }
            }
        }
        pnlSeatMap.revalidate();
        pnlSeatMap.repaint();
    }

    private void xoaSuKienCombo() {
        for(ActionListener al : cboPhim.getActionListeners()) cboPhim.removeActionListener(al);
        for(ActionListener al : cboNgay.getActionListeners()) cboNgay.removeActionListener(al);
        for(ActionListener al : cboSuatChieu.getActionListeners()) cboSuatChieu.removeActionListener(al);
    }
    
    private void themSuKienCombo() {
        cboPhim.addActionListener(e -> loadNgayTheoPhim());
        cboNgay.addActionListener(e -> loadSuatTheoNgay());
        cboSuatChieu.addActionListener(e -> loadGheTheoSuat());
    }

    private void loadDuLieuVaoBoNho() {
        try {
            dsPhimAll = phimDAO.docTuBang();
            dsSuatChieuAll = suatChieuDAO.docTuBang();
            
            xoaSuKienCombo(); 
            
            cboPhim.removeAllItems();
            for (Phim p : dsPhimAll) {
                cboPhim.addItem(p.getTenPhim());
            }
            
            themSuKienCombo();
            
            if(cboPhim.getItemCount() > 0) {
                cboPhim.setSelectedIndex(0);
                loadNgayTheoPhim(); 
            }
        } catch (Exception e) {
            System.out.println("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void loadNgayTheoPhim() {
        xoaSuKienCombo();
        cboNgay.removeAllItems();
        cboSuatChieu.removeAllItems();
        
        if (cboPhim.getSelectedIndex() != -1) {
            String tenPhim = cboPhim.getSelectedItem().toString();
            String maPhimChon = "";
            
            for(Phim p : dsPhimAll) {
                if(p.getTenPhim().equals(tenPhim)) {
                    maPhimChon = p.getMaPhim();
                    giaVeHienTai = p.getGiaVe(); 
                    break;
                }
            }
            
            ArrayList<String> uniqueDates = new ArrayList<>();
            DateTimeFormatter dtfNgay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for(SuatChieu sc : dsSuatChieuAll) {
                if(sc.getMaPhim().equals(maPhimChon)) {
                    String ngayStr = sc.getNgayChieu().format(dtfNgay);
                    if(!uniqueDates.contains(ngayStr)) {
                        uniqueDates.add(ngayStr);
                        cboNgay.addItem(ngayStr);
                    }
                }
            }
        }
        themSuKienCombo();
        
        if(cboNgay.getItemCount() > 0) {
            cboNgay.setSelectedIndex(0);
            loadSuatTheoNgay();
        } else {
            resetSoDoGhe();
        }
    }

    private void loadSuatTheoNgay() {
        xoaSuKienCombo();
        cboSuatChieu.removeAllItems();
        
        if (cboPhim.getSelectedIndex() != -1 && cboNgay.getSelectedIndex() != -1) {
            String tenPhim = cboPhim.getSelectedItem().toString();
            String maPhimChon = "";
            for(Phim p : dsPhimAll) if(p.getTenPhim().equals(tenPhim)) maPhimChon = p.getMaPhim();
            
            String ngayChon = cboNgay.getSelectedItem().toString();
            DateTimeFormatter dtfNgay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter dtfGio = DateTimeFormatter.ofPattern("HH:mm");
            
            for(SuatChieu sc : dsSuatChieuAll) {
                if(sc.getMaPhim().equals(maPhimChon) && sc.getNgayChieu().format(dtfNgay).equals(ngayChon)) {
                    String hienThi = sc.getMaSuat() + " - " + sc.getGioChieu().format(dtfGio) + " (" + sc.getPhongChieu() + ")";
                    cboSuatChieu.addItem(hienThi);
                }
            }
        }
        themSuKienCombo();
        
        if(cboSuatChieu.getItemCount() > 0) {
            cboSuatChieu.setSelectedIndex(0);
            loadGheTheoSuat();
        } else {
            resetSoDoGhe();
        }
    }

    private void loadGheTheoSuat() {
        resetSoDoGhe();
        if (cboSuatChieu.getSelectedIndex() == -1) return;
        
        String suatDangChon = cboSuatChieu.getSelectedItem().toString();
        String maSuat = suatDangChon.split(" - ")[0]; 

        ArrayList<String> gheDaBan = hoaDonDAO.layDanhSachGheDaBan(maSuat);
        for (SeatButton btn : listGhế) {
            if (gheDaBan.contains(btn.getText())) {
                btn.setEnabled(false); 
            }
        }
    }

    private void resetSoDoGhe() {
        for (SeatButton btn : listGhế) {
            btn.setSelected(false);
            btn.setEnabled(true);
        }
        gheDangChon.clear();
        capNhatGioHang();
    }

    private void capNhatGioHang() {
        cartModel.setRowCount(0); 
        double tongTien = 0;
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        for (String ghe : gheDangChon) {
            String loaiGhe = ghe.startsWith("F") ? "VIP" : "Thường";
            double giaGheNay = ghe.startsWith("F") ? giaVeHienTai + 20000 : giaVeHienTai; 
            
            cartModel.addRow(new Object[]{loaiGhe, ghe, nf.format(giaGheNay)});
            tongTien += giaGheNay;
        }
        
        lblSoLuong.setText("Số lượng vé: " + gheDangChon.size());
        lblTongTien.setText(nf.format(tongTien) + " đ");
    }

    private void inHoaDon(String maHD, String suatInfo, String maKH, String tongTienStr) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("                CINEMA POS                \n");
        sb.append("            HÓA ĐƠN THANH TOÁN            \n");
        sb.append("==========================================\n");
        sb.append("Mã HĐ: ").append(maHD).append("\n");
        sb.append("Ngày in: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
        if (!maKH.isEmpty()) {
            sb.append("Thẻ Khách hàng: ").append(maKH).append("\n");
        }
        sb.append("------------------------------------------\n");
        sb.append("Chi tiết: \n").append(suatInfo).append("\n"); 
        sb.append("Vị trí ghế: ").append(String.join(", ", gheDangChon)).append("\n");
        sb.append("Số lượng:   ").append(gheDangChon.size()).append(" vé\n");
        sb.append("------------------------------------------\n");
        sb.append("TỔNG TIỀN:                  ").append(tongTienStr).append("\n");
        sb.append("==========================================\n");
        sb.append("     CẢM ƠN QUÝ KHÁCH VÀ HẸN GẶP LẠI!     \n");
        sb.append("        Wifi: Cinema_Free (Pass: 8888)    \n");

        JTextArea txtBill = new JTextArea(sb.toString());
        txtBill.setFont(new Font("Monospaced", Font.BOLD, 13)); 
        txtBill.setEditable(false);
        txtBill.setBackground(Color.WHITE);
        txtBill.setForeground(Color.BLACK);
        txtBill.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(txtBill);
        scroll.setPreferredSize(new Dimension(380, 450));

        Object[] options = {"In Hóa Đơn (Print)", "Đóng lại"};
        int choice = JOptionPane.showOptionDialog(this, scroll, "Chi tiết Hóa Đơn: " + maHD,
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean complete = txtBill.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Đã gửi lệnh in thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Đã hủy lệnh in.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối máy in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnHuy) {
            resetSoDoGhe(); 
            txtMaKH.setText(""); 
        } else if (e.getSource() == btnThanhToan) {
            if (gheDangChon.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 ghế!");
                return;
            }
            if (cboSuatChieu.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn suất chiếu hợp lệ!");
                return;
            }
            
            String maHD = "HD" + new SimpleDateFormat("HHmmss").format(new Date());
            
            String suatInfo = cboPhim.getSelectedItem().toString() + "\n" 
                            + "Ngày: " + cboNgay.getSelectedItem().toString() + "\n"
                            + "Suất: " + cboSuatChieu.getSelectedItem().toString();
                            
            String maSuat = cboSuatChieu.getSelectedItem().toString().split(" - ")[0]; 
            String maKH = txtMaKH.getText().trim();
            
            if (!maKH.isEmpty()) {
                if (!khachHangDAO.kiemTraTonTai(maKH)) {
                    JOptionPane.showMessageDialog(this, 
                        "Mã khách hàng '" + maKH + "' không tồn tại trong hệ thống!\n" +
                        "Vui lòng sang mục Khách Hàng để tạo thẻ mới.", 
                        "Lỗi Khách Hàng", JOptionPane.ERROR_MESSAGE);
                    txtMaKH.requestFocus();
                    txtMaKH.selectAll(); 
                    return; 
                }
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận thanh toán cho " + gheDangChon.size() + " vé?\nTổng tiền: " + lblTongTien.getText(), 
                "XÁC NHẬN THANH TOÁN", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                String maNV = (ui_TrangChu != null && ui_TrangChu.getTaiKhoanDangNhap() != null) 
                              ? ui_TrangChu.getTaiKhoanDangNhap().getTenDangNhap() 
                              : "admin";

                boolean isSuccess = hoaDonDAO.thanhToanHoaDon(maHD, maNV, maSuat, gheDangChon, giaVeHienTai, maKH);
                
                if (isSuccess) {
                    String loiChuc = "Thanh toán thành công!\nMã Hóa Đơn: " + maHD;
                    if (!maKH.isEmpty()) {
                        int diemThuong = (int) ((gheDangChon.size() * giaVeHienTai) / 10000);
                        loiChuc += "\n🎉 Đã tích lũy " + diemThuong + " điểm cho khách hàng " + maKH;
                    }
                    JOptionPane.showMessageDialog(this, loiChuc, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    String tongTienStr = lblTongTien.getText();
                    inHoaDon(maHD, suatInfo, maKH, tongTienStr);
                    
                    txtMaKH.setText("");
                    loadGheTheoSuat(); 
                    if (ui_TrangChu != null) {
                        ui_TrangChu.capNhatSoLuongSuatChieu();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL, thanh toán thất bại!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    // --- FIX: THIẾT LẬP CỜ CHẶN RESET KHI TỰ ĐỘNG CHỌN TỪ SUẤT CHIẾU ---
    public void tuDongChonSuat(String tenPhim, String ngayChieuYYYYMMDD, String maSuat) {
        isTuDongChon = true; // Bật cờ chặn ComponentShown
        loadDuLieuVaoBoNho(); // Đổ Data mới lên trước
        
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(ngayChieuYYYYMMDD);
            String ngayChieuDDMMYYYY = date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            cboPhim.setSelectedItem(tenPhim);
            cboNgay.setSelectedItem(ngayChieuDDMMYYYY);
            
            for(int i = 0; i < cboSuatChieu.getItemCount(); i++) {
                if(cboSuatChieu.getItemAt(i).toString().startsWith(maSuat)) {
                    cboSuatChieu.setSelectedIndex(i);
                    break;
                }
            }
        } catch(Exception e) {
            System.out.println("Lỗi auto chọn: " + e.getMessage());
        }
    }
}