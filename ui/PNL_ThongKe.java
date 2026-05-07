package ui;

import dao.HoaDonDAO;
import dao.ThongKeDAO;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PNL_ThongKe extends JPanel {
    private JLabel lblTongDoanhThu, lblTongSoVe, lblPhimTop1;
    private DefaultTableModel modelXepHang;
    private JTable tblXepHang;
    private ThongKeDAO thongKeDAO;
    private CustomBarChart barChart;
    private JLabel lblSoSanh;
    private JComboBox<String> cboNgay, cboThang, cboNam, cboTheLoai;
    private PosButton btnLoc;

    // --- LỚP NÚT BẤM CAO CẤP ---
    class PosButton extends JButton {
        private Color bgColor;
        public PosButton(String text, Color bg, Color fg) {
            super(text);
            this.bgColor = bg;
            setBackground(bg);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // --- LỚP VẼ BIỂU ĐỒ CỘT ---
    class CustomBarChart extends JPanel {
        private ArrayList<String> labels = new ArrayList<>();
        private ArrayList<Double> values = new ArrayList<>();
        private double maxValue = 0;

        public CustomBarChart() { setOpaque(false); }

        public void updateChart(ArrayList<String> labels, ArrayList<Double> values) {
            this.labels = labels;
            this.values = values;
            this.maxValue = 0;
            for (Double v : values) {
                if (v > maxValue) maxValue = v;
            }
            repaint(); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (values.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(150, 150, 150));
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                g2.drawString("Không có dữ liệu trong khoảng thời gian này...", getWidth()/2 - 140, getHeight()/2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 40;
            int labelPadding = 30;

            int chartWidth = width - padding * 2;
            int chartHeight = height - padding - labelPadding;

            g2.setColor(new Color(100, 100, 100));
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(padding, height - labelPadding, width - padding, height - labelPadding); 
            g2.drawLine(padding, padding, padding, height - labelPadding); 

            int barWidth = chartWidth / values.size() - 20; 
            if (barWidth > 80) barWidth = 80; 

            FontMetrics metrics = g2.getFontMetrics();

            for (int i = 0; i < values.size(); i++) {
                int x = padding + 10 + i * (chartWidth / values.size()) + ((chartWidth / values.size()) - barWidth) / 2;
                int barHeight = (int) ((values.get(i) / maxValue) * chartHeight);
                int y = height - labelPadding - barHeight;

                GradientPaint gradient = new GradientPaint(x, y, new Color(52, 152, 219), x, y + barHeight, new Color(41, 128, 185));
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, 10, 10)); 

                g2.setColor(new Color(241, 196, 15)); 
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String valStr = NumberFormat.getInstance(new Locale("vi", "VN")).format(values.get(i));
                int valWidth = metrics.stringWidth(valStr);
                g2.drawString(valStr, x + (barWidth - valWidth) / 2, y - 5);

                g2.setColor(new Color(200, 200, 200));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                String label = labels.get(i);
                if (label.length() > 12) label = label.substring(0, 10) + "..."; 
                int labelWidth = metrics.stringWidth(label);
                g2.drawString(label, x + (barWidth - labelWidth) / 2, height - 10);
            }
        }
    }

    public PNL_ThongKe() {
        thongKeDAO = new ThongKeDAO();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(18, 18, 18)); 
        setBorder(new EmptyBorder(15, 25, 20, 25));

        // ==========================================
        // 1. TOP BAR: BỘ LỌC (FILTER)
        // ==========================================
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlFilter.setOpaque(false);
        pnlFilter.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 50)));

        JLabel lblFilter = new JLabel("LỌC DỮ LIỆU:");
        lblFilter.setForeground(new Color(212, 175, 55));
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlFilter.add(lblFilter);

        String[] arrNgay = new String[32];
        arrNgay[0] = "Tất cả ngày";
        for (int i = 1; i <= 31; i++) {
            arrNgay[i] = "Ngày " + i;
        }
        cboNgay = createComboBox(arrNgay);

        cboThang = createComboBox(new String[]{"Tất cả tháng", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"});
        
        int currentYear = LocalDate.now().getYear();
        cboNam = createComboBox(new String[]{"Tất cả năm", String.valueOf(currentYear), String.valueOf(currentYear - 1), String.valueOf(currentYear - 2)});
        
        cboTheLoai = createComboBox(new String[]{"Tất cả thể loại", "Hành động", "Tâm lý", "Tình cảm", "Khoa học viễn tưởng", "Hài hước", "Kinh dị", "Hoạt hình"});

        btnLoc = new PosButton(" ÁP DỤNG BỘ LỌC ", new Color(229, 9, 20), Color.WHITE);
        btnLoc.setPreferredSize(new Dimension(180, 35));
        
        btnLoc.addActionListener(e -> loadData());

        pnlFilter.add(cboNgay);
        pnlFilter.add(cboThang);
        pnlFilter.add(cboNam);
        pnlFilter.add(cboTheLoai);
        pnlFilter.add(btnLoc);

        add(pnlFilter, BorderLayout.NORTH);

        // ==========================================
        // 2. PHẦN CENTER: THẺ TỔNG QUAN & BIỂU ĐỒ
        // ==========================================
        JPanel pnlCenterWrapper = new JPanel(new BorderLayout(0, 15));
        pnlCenterWrapper.setOpaque(false);

        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlCards.setOpaque(false);
        pnlCards.setPreferredSize(new Dimension(0, 100));

        // --- KHU VỰC SỬA LỖI LBLSOSANH ---
        lblTongDoanhThu = new JLabel("0 VNĐ", JLabel.LEFT);
        lblSoSanh = new JLabel("Đang tính toán...");
        lblSoSanh.setFont(new Font("Segoe UI", Font.ITALIC, 13)); 
        lblSoSanh.setForeground(Color.LIGHT_GRAY);
        
        JPanel cardDoanhThu = createSummaryCard("TỔNG DOANH THU", lblTongDoanhThu, new Color(46, 204, 113));
        cardDoanhThu.add(lblSoSanh, BorderLayout.SOUTH); // NHÉT lblSoSanh VÀO ĐÂY!
        pnlCards.add(cardDoanhThu);

        lblTongSoVe = new JLabel("0 Vé", JLabel.LEFT);
        pnlCards.add(createSummaryCard("SỐ VÉ ĐÃ BÁN", lblTongSoVe, new Color(52, 152, 219)));

        lblPhimTop1 = new JLabel("Đang tải...", JLabel.LEFT);
        pnlCards.add(createSummaryCard("PHIM HOT NHẤT", lblPhimTop1, new Color(212, 175, 55)));

        pnlCenterWrapper.add(pnlCards, BorderLayout.NORTH);

        JPanel pnlSplit = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlSplit.setOpaque(false);

        // Trái: Biểu đồ
        JPanel pnlChart = new JPanel(new BorderLayout());
        pnlChart.setBackground(new Color(30, 30, 30));
        pnlChart.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(60, 60, 60), 1), new EmptyBorder(10, 10, 10, 10)));
        JLabel lblChartTitle = new JLabel("BIỂU ĐỒ DOANH THU", JLabel.CENTER);
        lblChartTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblChartTitle.setForeground(new Color(212, 175, 55));
        pnlChart.add(lblChartTitle, BorderLayout.NORTH);
        
        barChart = new CustomBarChart();
        pnlChart.add(barChart, BorderLayout.CENTER);
        pnlSplit.add(pnlChart);

        // Phải: Bảng
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(new Color(30, 30, 30));
        pnlTable.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(60, 60, 60), 1), new EmptyBorder(10, 10, 10, 10)));
        JLabel lblTableTitle = new JLabel("CHI TIẾT XẾP HẠNG", JLabel.CENTER);
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTableTitle.setForeground(new Color(212, 175, 55));
        lblTableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnlTable.add(lblTableTitle, BorderLayout.NORTH);

        String[] cols = {"Hạng", "Tên Phim", "Số Vé", "Doanh Thu"};
        modelXepHang = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblXepHang = new JTable(modelXepHang);
        
        tblXepHang.setRowHeight(40);
        tblXepHang.setBackground(new Color(35, 35, 35));
        tblXepHang.setForeground(Color.WHITE);
        tblXepHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblXepHang.setSelectionBackground(new Color(229, 9, 20));
        tblXepHang.setSelectionForeground(Color.WHITE);
        tblXepHang.setShowGrid(false);
        tblXepHang.setIntercellSpacing(new Dimension(0, 0));
        
        // Header Bảng
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(20, 20, 20));
        headerRenderer.setForeground(new Color(229, 9, 20)); 
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 9, 20)));
        for (int i = 0; i < tblXepHang.getModel().getColumnCount(); i++) {
            tblXepHang.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        tblXepHang.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tblXepHang.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        
        DefaultTableCellRenderer rankRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 15));
                String val = value.toString();
                
                if (val.equals("TOP 1")) setForeground(new Color(255, 215, 0)); 
                else if (val.equals("TOP 2")) setForeground(new Color(192, 192, 192)); 
                else if (val.equals("TOP 3")) setForeground(new Color(205, 127, 50)); 
                else setForeground(new Color(150, 150, 150)); 

                if (isSelected) setForeground(Color.WHITE); 
                return c;
            }
        };
        tblXepHang.getColumnModel().getColumn(0).setCellRenderer(rankRenderer);

        JScrollPane scrollTable = new JScrollPane(tblXepHang);
        scrollTable.getViewport().setBackground(new Color(35, 35, 35));
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        pnlTable.add(scrollTable, BorderLayout.CENTER);
        pnlSplit.add(pnlTable);

        pnlCenterWrapper.add(pnlSplit, BorderLayout.CENTER);
        add(pnlCenterWrapper, BorderLayout.CENTER);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadData();
            }
        });

        loadData();
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cbo = new JComboBox<>(items);
        cbo.setUI(new BasicComboBoxUI());
        cbo.setBackground(new Color(40, 40, 40));
        cbo.setForeground(Color.WHITE);
        cbo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbo.setPreferredSize(new Dimension(135, 35)); 
        return cbo;
    }

    private JPanel createSummaryCard(String title, JLabel lblValue, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(new Color(30, 30, 30));
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor), new EmptyBorder(10, 20, 10, 20)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(150, 150, 150));

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    public void loadData() {
        String ngayStr = cboNgay.getSelectedItem().toString();
        int ngay = 0;

        if (!ngayStr.equals("Tất cả ngày")) {
            ngay = Integer.parseInt(ngayStr.replace("Ngày ", "").trim());
        }  
        String thangStr = cboThang.getSelectedItem().toString();
        int thang = 0;

        if (!thangStr.equals("Tất cả tháng")) {
            thang = Integer.parseInt(thangStr.replace("Tháng ", "").trim());
        }        
        String namStr = cboNam.getSelectedItem().toString();
        int nam = namStr.equals("Tất cả năm") ? 0 : Integer.parseInt(namStr);
        
        String theLoai = cboTheLoai.getSelectedItem().toString();
        if (theLoai.equals("Tất cả thể loại")) theLoai = ""; 

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        try {
            double[] tongQuan = thongKeDAO.layThongKeTongQuan(ngay, thang, nam, theLoai);
            lblTongSoVe.setText((int) tongQuan[0] + " Vé");
            lblTongDoanhThu.setText(nf.format(tongQuan[1]) + " đ");

            modelXepHang.setRowCount(0);
            ArrayList<Object[]> listPhim = thongKeDAO.thongKeDoanhThuTheoPhim(ngay, thang, nam, theLoai);
            
            ArrayList<String> chartLabels = new ArrayList<>();
            ArrayList<Double> chartValues = new ArrayList<>();
            
            int hang = 1;
            lblPhimTop1.setText("Chưa có dữ liệu"); 
            
            for (Object[] row : listPhim) {
                String tenPhim = (String) row[0];
                int soVe = (int) row[1];
                double tien = (double) row[2];
                
                if (hang == 1) lblPhimTop1.setText(tenPhim);

                modelXepHang.addRow(new Object[]{"TOP " + hang, tenPhim, soVe, nf.format(tien) + " đ"});
                
                if (hang <= 5) { 
                    chartLabels.add(tenPhim);
                    chartValues.add(tien);
                }
                hang++;
            }
            
            barChart.updateChart(chartLabels, chartValues);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu thống kê: " + e.getMessage());
        }
        
        // --- CHỖ NÀY ĐÃ THÊM LỆNH GỌI HÀM CẬP NHẬT ---
        capNhatSoSanhDoanhThu();
    }
    
    public void capNhatSoSanhDoanhThu() {
        HoaDonDAO hoaDonDAO = new HoaDonDAO();
        Calendar cal = Calendar.getInstance();
        
        // 1. Lấy dữ liệu tháng hiện tại
        int thangNay = cal.get(Calendar.MONTH) + 1;
        int namNay = cal.get(Calendar.YEAR);
        double doanhThuThangNay = hoaDonDAO.getTongDoanhThuTheoThang(thangNay, namNay);
        
        // 2. Tính toán tháng trước
        cal.add(Calendar.MONTH, -1);
        int thangTruoc = cal.get(Calendar.MONTH) + 1;
        int namTruoc = cal.get(Calendar.YEAR);
        double doanhThuThangTruoc = hoaDonDAO.getTongDoanhThuTheoThang(thangTruoc, namTruoc);
        
        // 3. Tính toán phần trăm tăng trưởng
        double chenhLech = doanhThuThangNay - doanhThuThangTruoc;
        double phanTram = 0;
        if (doanhThuThangTruoc > 0) {
            phanTram = (chenhLech / doanhThuThangTruoc) * 100;
        }

        // 4. Cập nhật giao diện
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        
        // Đảm bảo lblSoSanh đã được khởi tạo trước khi gọi setText
        if (lblSoSanh != null) {
            if (chenhLech >= 0) {
                lblSoSanh.setText(String.format("↑ %s (+%.1f%%) so với tháng trước", nf.format(chenhLech), phanTram));
                lblSoSanh.setForeground(new Color(46, 204, 113)); // Màu xanh lá - Lời
            } else {
                lblSoSanh.setText(String.format("↓ %s (%.1f%%) so với tháng trước", nf.format(Math.abs(chenhLech)), phanTram));
                lblSoSanh.setForeground(new Color(231, 76, 60)); // Màu đỏ - Lỗ
            }
        }
    }
}