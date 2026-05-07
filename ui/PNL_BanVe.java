package ui;

import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.PhimDAO;
import dao.SuatChieuDAO;
import entity.DichVu;
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
import java.util.HashMap;
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
    private JComboBox<String> cboDichVu;
    private JSpinner spnSoLuongDV;
    private PosButton btnThemDV, btnApDungMa;
    
    private JPanel pnlSeatMap;
    private DefaultTableModel cartModel;
    private JTable tblCart;
    private JButton btnThanhToan, btnHuy;
    private JTextField txtMaKH, txtMaGiamGia;
    
    private JLabel lblSoLuong, lblTamTinh, lblTienGiam, lblTongTien;
    
    private ArrayList<SeatButton> listGhế = new ArrayList<>();
    private ArrayList<String> gheDangChon = new ArrayList<>();
    private HashMap<String, Object[]> gioHangDV = new HashMap<>(); 
    
    private double giaVeHienTai = 80000; 
    private double phanTramGiam = 0; 
    private double tienGiamGia = 0;  
    
    private boolean isTuDongChon = false;
    
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private KhachHangDAO khachHangDAO = new KhachHangDAO(); 
    private SuatChieuDAO suatChieuDAO = new SuatChieuDAO();
    private PhimDAO phimDAO = new PhimDAO();

    private ArrayList<Phim> dsPhimAll = new ArrayList<>();
    private ArrayList<SuatChieu> dsSuatChieuAll = new ArrayList<>();
    private ArrayList<DichVu> dsDichVuAll = new ArrayList<>();

    // --- ĐÃ TĂNG ĐỘ BO TRÒN CHO GHẾ NGỒI ---
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
            if (!isEnabled()) g2.setColor(new Color(229, 9, 20)); 
            else if (isSelected()) g2.setColor(new Color(46, 204, 113)); 
            else g2.setColor(new Color(60, 60, 60)); 
            // Bo góc 20, 20 cho ghế mềm mại hơn
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);
            g2.dispose();
            super.paintComponent(g); 
        }
    }

    // --- ĐÃ TĂNG ĐỘ BO TRÒN CHO NÚT BẤM (GIAO DIỆN CAO CẤP) ---
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
            // Tăng bán kính bo góc lên 25 để nút thon, mượt mà hơn
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public PNL_BanVe(UI_TrangChu ui_TrangChu) {
    	this.ui_TrangChu = ui_TrangChu;
        setLayout(new BorderLayout(20, 20)); 
        setBackground(new Color(18, 18, 18)); 
        setBorder(new EmptyBorder(15, 20, 15, 20));

        // --- TOP FILTER ---
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
        txtMaKH.setPreferredSize(new Dimension(200, 40));
        txtMaKH.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtMaKH.setBackground(new Color(40, 40, 40));
        txtMaKH.setForeground(new Color(212, 175, 55)); 
        txtMaKH.setCaretColor(Color.WHITE);
        txtMaKH.setHorizontalAlignment(JTextField.CENTER);
        txtMaKH.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(70, 70, 70), 1), new EmptyBorder(5, 5, 5, 5)));
        pnlTopRight.add(taoWrapCombo("MÃ KHÁCH HÀNG (Tích điểm):", txtMaKH));

        pnlTop.add(pnlFilters, BorderLayout.WEST);
        pnlTop.add(pnlTopRight, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // --- MÀN HÌNH GHẾ ---
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

        // ==========================================
        // KHU VỰC BÊN PHẢI (FIX LỖI MẤT NÚT)
        // ==========================================
        JPanel pnlRight = new JPanel(new BorderLayout(0, 10));
        pnlRight.setOpaque(false);
        // Tăng chiều rộng cột bên phải lên 460 để đảm bảo không rớt chữ/nút
        pnlRight.setPreferredSize(new Dimension(460, 0)); 
        pnlRight.setBorder(new EmptyBorder(0, 15, 0, 0)); 

        JPanel pnlCartWrapper = new JPanel(new BorderLayout(0, 10));
        pnlCartWrapper.setOpaque(false);

        // THANH CÔNG CỤ BẮP NƯỚC (Dùng BorderLayout để cố định)
        JPanel pnlFNB = new JPanel(new BorderLayout(10, 0));
        pnlFNB.setOpaque(false);
        
        cboDichVu = createStyledComboBox(200);
        
        JPanel pnlFNBRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlFNBRight.setOpaque(false);
        
        spnSoLuongDV = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        spnSoLuongDV.setPreferredSize(new Dimension(60, 40));
        
        btnThemDV = new PosButton("THÊM VÀO GIỎ", new Color(41, 128, 185), Color.WHITE);
        btnThemDV.setPreferredSize(new Dimension(140, 40));
        
        pnlFNBRight.add(spnSoLuongDV);
        pnlFNBRight.add(btnThemDV);

        pnlFNB.add(cboDichVu, BorderLayout.CENTER);
        pnlFNB.add(pnlFNBRight, BorderLayout.EAST);

        pnlCartWrapper.add(taoWrapCombo("DỊCH VỤ ĐI KÈM (BẮP/NƯỚC):", pnlFNB), BorderLayout.NORTH);

        // BẢNG GIỎ HÀNG
        String[] cartCols = {"Loại", "Sản phẩm / Ghế", "Thành tiền"};
        cartModel = new DefaultTableModel(cartCols, 0);
        tblCart = new JTable(cartModel);
        tblCart.setRowHeight(35);
        tblCart.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
        pnlCartWrapper.add(scrollCart, BorderLayout.CENTER);
        
        pnlRight.add(pnlCartWrapper, BorderLayout.CENTER);

        // --- KHU VỰC THANH TOÁN ---
        JPanel pnlCheckout = new JPanel(new BorderLayout(0, 15));
        pnlCheckout.setBackground(new Color(35, 35, 35));
        pnlCheckout.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(60, 60, 60), 1), new EmptyBorder(15, 15, 15, 15)));
        
        // Nhập Mã giảm giá
        JPanel pnlDiscount = new JPanel(new BorderLayout(10, 0));
        pnlDiscount.setOpaque(false);
        JLabel lblCode = new JLabel("Mã giảm giá:");
        lblCode.setForeground(Color.WHITE);
        lblCode.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtMaGiamGia = new JTextField();
        txtMaGiamGia.setFont(new Font("Segoe UI", Font.BOLD, 15));
        txtMaGiamGia.setBackground(new Color(50, 50, 50));
        txtMaGiamGia.setForeground(Color.WHITE);
        txtMaGiamGia.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        btnApDungMa = new PosButton("ÁP DỤNG", new Color(212, 175, 55), Color.BLACK);
        btnApDungMa.setPreferredSize(new Dimension(110, 35)); // Tăng một chút cho tròn đẹp
        
        pnlDiscount.add(lblCode, BorderLayout.WEST);
        pnlDiscount.add(txtMaGiamGia, BorderLayout.CENTER);
        pnlDiscount.add(btnApDungMa, BorderLayout.EAST);
        
        pnlCheckout.add(pnlDiscount, BorderLayout.NORTH);

        // Thông số tiền
        JPanel pnlCalc = new JPanel(new GridLayout(4, 2, 0, 8));
        pnlCalc.setOpaque(false);
        
        lblSoLuong = new JLabel("0", SwingConstants.RIGHT);
        lblSoLuong.setForeground(Color.WHITE); lblSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        lblTamTinh = new JLabel("0 đ", SwingConstants.RIGHT);
        lblTamTinh.setForeground(Color.WHITE); lblTamTinh.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        lblTienGiam = new JLabel("0 đ", SwingConstants.RIGHT);
        lblTienGiam.setForeground(new Color(46, 204, 113)); lblTienGiam.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        lblTongTien = new JLabel("0 đ", SwingConstants.RIGHT);
        lblTongTien.setForeground(new Color(229, 9, 20)); lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 26));

        pnlCalc.add(createCalcLabel("Tổng SP / Vé:")); pnlCalc.add(lblSoLuong);
        pnlCalc.add(createCalcLabel("Tạm tính:")); pnlCalc.add(lblTamTinh);
        pnlCalc.add(createCalcLabel("Giảm giá voucher:")); pnlCalc.add(lblTienGiam);
        pnlCalc.add(createCalcLabel("TỔNG THANH TOÁN:")); pnlCalc.add(lblTongTien);
        
        pnlCheckout.add(pnlCalc, BorderLayout.CENTER);

        // Nút chốt đơn
        JPanel pnlActionBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlActionBtns.setOpaque(false);
        btnHuy = new PosButton("XÓA GIỎ", new Color(100, 100, 100), Color.WHITE);
        btnHuy.setPreferredSize(new Dimension(0, 45));
        btnThanhToan = new PosButton("THANH TOÁN LƯU HĐ", new Color(229, 9, 20), Color.WHITE); 
        pnlActionBtns.add(btnHuy); pnlActionBtns.add(btnThanhToan);
        pnlCheckout.add(pnlActionBtns, BorderLayout.SOUTH);

        pnlRight.add(pnlCheckout, BorderLayout.SOUTH);
        add(pnlRight, BorderLayout.EAST);

        // --- SỰ KIỆN NÚT BẤM ---
        btnThanhToan.addActionListener(this);
        btnHuy.addActionListener(this);
        
        btnThemDV.addActionListener(e -> {
            int idx = cboDichVu.getSelectedIndex();
            if (idx != -1) {
                DichVu dv = dsDichVuAll.get(idx);
                int sl = (int) spnSoLuongDV.getValue();
                if (gioHangDV.containsKey(dv.getMaDV())) {
                    Object[] data = gioHangDV.get(dv.getMaDV());
                    data[0] = (int)data[0] + sl;
                } else {
                    gioHangDV.put(dv.getMaDV(), new Object[]{sl, dv.getGiaDV(), dv.getTenDV()});
                }
                capNhatGioHang();
                spnSoLuongDV.setValue(1); 
            }
        });

        // SỰ KIỆN ÁP MÃ GIẢM GIÁ
        btnApDungMa.addActionListener(e -> {
            String code = txtMaGiamGia.getText().trim().toUpperCase();
            if (code.isEmpty()) {
                phanTramGiam = 0;
                capNhatGioHang();
                return;
            }
            if (code.equals("IUH20")) {
                phanTramGiam = 20;
                JOptionPane.showMessageDialog(this, "Áp dụng thành công mã giảm 20% (Sinh viên IUH)!");
            } else if (code.equals("GIAM10")) {
                phanTramGiam = 10;
                JOptionPane.showMessageDialog(this, "Áp dụng thành công mã giảm 10%!");
            } else if (code.equals("VIP50")) {
                phanTramGiam = 50;
                JOptionPane.showMessageDialog(this, "Áp dụng mã VIP thành công! Giảm 50%!");
            } else {
                JOptionPane.showMessageDialog(this, "Mã giảm giá không hợp lệ hoặc đã hết hạn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                phanTramGiam = 0;
                txtMaGiamGia.setText("");
            }
            capNhatGioHang();
        });
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (isTuDongChon) isTuDongChon = false; 
                else loadDuLieuVaoBoNho(); 
            }
        });
    }

    private JLabel createCalcLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JComboBox<String> createStyledComboBox(int width) {
        JComboBox<String> cbo = new JComboBox<>();
        cbo.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
                if (col == 2 || col == 7) { pnlSeatMap.add(new JLabel("")); } 
                else {
                    String tenGhe = hang[i] + gheSo;
                    SeatButton btnGhe = new SeatButton(tenGhe); 
                    btnGhe.addActionListener(e -> {
                        if (btnGhe.isSelected()) gheDangChon.add(tenGhe);
                        else gheDangChon.remove(tenGhe);
                        capNhatGioHang();
                    });
                    listGhế.add(btnGhe); pnlSeatMap.add(btnGhe);
                    gheSo++;
                }
            }
        }
        pnlSeatMap.revalidate(); pnlSeatMap.repaint();
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
            dsDichVuAll = new dao.DichVuDAO().docTuBang();
            cboDichVu.removeAllItems();
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            for (DichVu dv : dsDichVuAll) {
                cboDichVu.addItem(dv.getTenDV() + " (" + nf.format(dv.getGiaDV()) + ")");
            }
            
            xoaSuKienCombo(); 
            cboPhim.removeAllItems();
            for (Phim p : dsPhimAll) { cboPhim.addItem(p.getTenPhim()); }
            themSuKienCombo();
            if(cboPhim.getItemCount() > 0) {
                cboPhim.setSelectedIndex(0); loadNgayTheoPhim(); 
            }
        } catch (Exception e) {}
    }

    private void loadNgayTheoPhim() {
        xoaSuKienCombo();
        cboNgay.removeAllItems(); cboSuatChieu.removeAllItems();
        if (cboPhim.getSelectedIndex() != -1) {
            String tenPhim = cboPhim.getSelectedItem().toString();
            String maPhimChon = "";
            for(Phim p : dsPhimAll) {
                if(p.getTenPhim().equals(tenPhim)) { maPhimChon = p.getMaPhim(); giaVeHienTai = p.getGiaVe(); break; }
            }
            ArrayList<String> uniqueDates = new ArrayList<>();
            DateTimeFormatter dtfNgay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for(SuatChieu sc : dsSuatChieuAll) {
                if(sc.getMaPhim().equals(maPhimChon)) {
                    String ngayStr = sc.getNgayChieu().format(dtfNgay);
                    if(!uniqueDates.contains(ngayStr)) { uniqueDates.add(ngayStr); cboNgay.addItem(ngayStr); }
                }
            }
        }
        themSuKienCombo();
        if(cboNgay.getItemCount() > 0) { cboNgay.setSelectedIndex(0); loadSuatTheoNgay(); } 
        else { resetSoDoGhe(); }
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
                    cboSuatChieu.addItem(sc.getMaSuat() + " - " + sc.getGioChieu().format(dtfGio) + " (" + sc.getPhongChieu() + ")");
                }
            }
        }
        themSuKienCombo();
        if(cboSuatChieu.getItemCount() > 0) { cboSuatChieu.setSelectedIndex(0); loadGheTheoSuat(); } 
        else { resetSoDoGhe(); }
    }

    private void loadGheTheoSuat() {
        resetSoDoGhe();
        if (cboSuatChieu.getSelectedIndex() == -1) return;
        String maSuat = cboSuatChieu.getSelectedItem().toString().split(" - ")[0]; 
        ArrayList<String> gheDaBan = hoaDonDAO.layDanhSachGheDaBan(maSuat);
        for (SeatButton btn : listGhế) {
            if (gheDaBan.contains(btn.getText())) { btn.setEnabled(false); }
        }
    }

    private void resetSoDoGhe() {
        for (SeatButton btn : listGhế) { btn.setSelected(false); btn.setEnabled(true); }
        gheDangChon.clear();
        capNhatGioHang();
    }

    private void capNhatGioHang() {
        cartModel.setRowCount(0); 
        double tamTinh = 0;
        int tongSP = 0;
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        // 1. In vé
        for (String ghe : gheDangChon) {
            String loaiGhe = ghe.startsWith("F") ? "VIP" : "Thường";
            double giaGheNay = ghe.startsWith("F") ? giaVeHienTai + 20000 : giaVeHienTai; 
            cartModel.addRow(new Object[]{loaiGhe, "Ghế " + ghe, nf.format(giaGheNay)});
            tamTinh += giaGheNay;
            tongSP++;
        }
        
        // 2. In Dịch Vụ
        for (String maDV : gioHangDV.keySet()) {
            Object[] data = gioHangDV.get(maDV);
            int sl = (int) data[0];
            double gia = (double) data[1];
            String ten = (String) data[2];
            double tien = sl * gia;
            cartModel.addRow(new Object[]{"Bắp/Nước", ten + " (x" + sl + ")", nf.format(tien)});
            tamTinh += tien;
            tongSP += sl;
        }
        
        // 3. Tính mã giảm giá
        tienGiamGia = tamTinh * (phanTramGiam / 100.0);
        double tongThanhToan = tamTinh - tienGiamGia;
        if (tongThanhToan < 0) tongThanhToan = 0;
        
        lblSoLuong.setText(String.valueOf(tongSP));
        lblTamTinh.setText(nf.format(tamTinh) + " đ");
        
        if (phanTramGiam > 0) lblTienGiam.setText("- " + nf.format(tienGiamGia) + " đ (" + (int)phanTramGiam + "%)");
        else lblTienGiam.setText("0 đ");
        
        lblTongTien.setText(nf.format(tongThanhToan) + " đ");
    }

    private void inHoaDon(String maHD, String suatInfo, String maKH, String tongTienStr) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("                CINEMA POS                \n");
        sb.append("            HÓA ĐƠN THANH TOÁN            \n");
        sb.append("==========================================\n");
        sb.append("Mã HĐ: ").append(maHD).append("\n");
        sb.append("Ngày in: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
        if (!maKH.isEmpty()) sb.append("Thẻ Khách: ").append(maKH).append("\n");
        sb.append("------------------------------------------\n");
        
        if (!gheDangChon.isEmpty()) {
            sb.append("CHI TIẾT VÉ PHIM:\n").append(suatInfo).append("\n"); 
            sb.append("Vị trí ghế: ").append(String.join(", ", gheDangChon)).append("\n");
            sb.append("Số lượng:   ").append(gheDangChon.size()).append(" vé\n");
        }
        
        if (!gioHangDV.isEmpty()) {
            sb.append("\nCHI TIẾT DỊCH VỤ (BẮP/NƯỚC):\n");
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            for(Object[] data : gioHangDV.values()) {
                sb.append("- ").append(data[2]).append("\n  SL: ").append(data[0])
                  .append("  | Thành tiền: ").append(nf.format((double)data[1] * (int)data[0])).append("\n");
            }
        }
        
        sb.append("------------------------------------------\n");
        sb.append("Tạm tính:                   ").append(lblTamTinh.getText()).append("\n");
        if (phanTramGiam > 0) {
            sb.append("Giảm giá Voucher:          ").append(lblTienGiam.getText()).append("\n");
        }
        sb.append("TỔNG TIỀN:                  ").append(tongTienStr).append("\n");
        sb.append("==========================================\n");
        sb.append("     CẢM ƠN QUÝ KHÁCH VÀ HẸN GẶP LẠI!     \n");

        JTextArea txtBill = new JTextArea(sb.toString());
        txtBill.setFont(new Font("Monospaced", Font.BOLD, 13)); 
        txtBill.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtBill);
        scroll.setPreferredSize(new Dimension(380, 500));
        Object[] options = {"In Hóa Đơn (Print)", "Đóng lại"};
        int choice = JOptionPane.showOptionDialog(this, scroll, "Hóa Đơn: " + maHD, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            try { txtBill.print(); } catch (Exception ex) {}
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnHuy) {
            gioHangDV.clear(); 
            loadGheTheoSuat(); 
            txtMaKH.setText(""); 
            txtMaGiamGia.setText("");
            phanTramGiam = 0;
            capNhatGioHang();
            
        } else if (e.getSource() == btnThanhToan) {
            if (gheDangChon.isEmpty() && gioHangDV.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống! Vui lòng chọn ghế hoặc thêm Bắp/Nước.");
                return;
            }
            
            String maHD = "HD" + new SimpleDateFormat("HHmmss").format(new Date());
            String maSuat = "";
            String suatInfo = "";
            
            if (!gheDangChon.isEmpty()) {
                if (cboSuatChieu.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn suất chiếu hợp lệ cho vé!"); return;
                }
                maSuat = cboSuatChieu.getSelectedItem().toString().split(" - ")[0]; 
                suatInfo = cboPhim.getSelectedItem().toString() + "\nNgày: " + cboNgay.getSelectedItem().toString() + "\nSuất: " + cboSuatChieu.getSelectedItem().toString();
            }
            
            String maKH = txtMaKH.getText().trim();
            if (!maKH.isEmpty() && !khachHangDAO.kiemTraTonTai(maKH)) {
                JOptionPane.showMessageDialog(this, "Mã khách hàng không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; 
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán?\nTổng: " + lblTongTien.getText(), "XÁC NHẬN", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String maNV = (ui_TrangChu != null && ui_TrangChu.getTaiKhoanDangNhap() != null) ? ui_TrangChu.getTaiKhoanDangNhap().getTenDangNhap() : "admin";
                
                boolean isSuccess = hoaDonDAO.thanhToanHoaDon(maHD, maNV, maSuat, gheDangChon, giaVeHienTai, maKH, gioHangDV, tienGiamGia);
                
                if (isSuccess) {
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    inHoaDon(maHD, suatInfo, maKH, lblTongTien.getText());
                    txtMaKH.setText(""); txtMaGiamGia.setText(""); 
                    phanTramGiam = 0; gioHangDV.clear(); loadGheTheoSuat(); 
                    if (ui_TrangChu != null) ui_TrangChu.capNhatSoLuongSuatChieu();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    public void tuDongChonSuat(String tenPhim, String ngayChieuYYYYMMDD, String maSuat) {
        isTuDongChon = true; 
        loadDuLieuVaoBoNho(); 
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(ngayChieuYYYYMMDD);
            String ngayChieuDDMMYYYY = date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            cboPhim.setSelectedItem(tenPhim);
            cboNgay.setSelectedItem(ngayChieuDDMMYYYY);
            for(int i = 0; i < cboSuatChieu.getItemCount(); i++) {
                if(cboSuatChieu.getItemAt(i).toString().startsWith(maSuat)) {
                    cboSuatChieu.setSelectedIndex(i); break;
                }
            }
        } catch(Exception e) {}
    }
}