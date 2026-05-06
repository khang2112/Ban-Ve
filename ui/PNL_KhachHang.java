package ui;

import dao.KhachHangDAO;
import entity.KhachHang;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PNL_KhachHang extends JPanel implements ActionListener, MouseListener {
    private JTextField txtMa, txtTen, txtSDT, txtDiem;
    private JButton btnThem, btnSua, btnXoa, btnXoaRong;
    private DefaultTableModel model;
    private JTable table;
    
    private KhachHangDAO khachHangDAO;

    private Color bgDark = new Color(18, 18, 18);
    private Color bgPanel = new Color(30, 30, 30);
    private Color textWhite = new Color(240, 240, 240);
    private Color themeRed = new Color(229, 9, 20); 
    private Color colorGold = new Color(212, 175, 55); 

    class PosButton extends JButton {
        private Color bgColor;
        public PosButton(String text, Color bg, Color fg) {
            super(text);
            this.bgColor = bg;
            setBackground(bg); // FIX: HIỂN THỊ MÀU NGAY KHI LOAD
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(fg);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(120, 40));

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

    public PNL_KhachHang() {
        khachHangDAO = new KhachHangDAO(); 
        
        setLayout(new BorderLayout(15, 15));
        setBackground(bgDark);
        setBorder(new EmptyBorder(15, 20, 20, 20));

        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(2, 4, 20, 20));
        pnlInput.setBackground(bgPanel);
        pnlInput.setBorder(new EmptyBorder(15, 20, 15, 20)); 

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), "THÔNG TIN KHÁCH HÀNG",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), themeRed
        );
        
        JPanel pnlInputWrapper = new JPanel(new BorderLayout());
        pnlInputWrapper.setBackground(bgPanel);
        pnlInputWrapper.setBorder(border);
        pnlInputWrapper.add(pnlInput, BorderLayout.CENTER);

        pnlInput.add(createLabel("Mã Khách Hàng:"));
        txtMa = createTextField();
        pnlInput.add(txtMa);

        pnlInput.add(createLabel("Tên Khách Hàng:"));
        txtTen = createTextField();
        pnlInput.add(txtTen);

        pnlInput.add(createLabel("Số Điện Thoại:"));
        txtSDT = createTextField();
        pnlInput.add(txtSDT);

        pnlInput.add(createLabel("Điểm Tích Lũy:"));
        txtDiem = createTextField();
        txtDiem.setText("0"); 
        txtDiem.setEditable(false); 
        txtDiem.setForeground(colorGold); 
        txtDiem.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnlInput.add(txtDiem);

        pnlTop.add(pnlInputWrapper, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBtns.setOpaque(false);
        
        btnThem = new PosButton("THÊM", new Color(46, 204, 113), Color.WHITE);
        btnSua = new PosButton("CẬP NHẬT", new Color(52, 152, 219), Color.WHITE);
        btnXoa = new PosButton("XÓA", themeRed, Color.WHITE);
        btnXoaRong = new PosButton("LÀM MỚI", new Color(100, 100, 100), Color.WHITE);

        pnlBtns.add(btnThem);
        pnlBtns.add(btnSua);
        pnlBtns.add(btnXoa);
        pnlBtns.add(btnXoaRong);

        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        JPanel pnlTableWrapper = new JPanel(new BorderLayout());
        pnlTableWrapper.setBackground(bgPanel);
        pnlTableWrapper.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 60, 60), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));

        String[] cols = {"Mã KH", "Tên Khách Hàng", "Số Điện Thoại", "Điểm Tích Lũy"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        DefaultTableCellRenderer goldRenderer = new DefaultTableCellRenderer();
        goldRenderer.setHorizontalAlignment(JLabel.CENTER);
        goldRenderer.setForeground(colorGold);
        goldRenderer.setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getColumnModel().getColumn(3).setCellRenderer(goldRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(bgPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        pnlTableWrapper.add(scrollPane, BorderLayout.CENTER);
        add(pnlTableWrapper, BorderLayout.CENTER);

        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        table.addMouseListener(this);
        
        loadDataToTable();
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setBackground(new Color(40, 40, 40)); 
        txt.setForeground(Color.WHITE); 
        txt.setCaretColor(themeRed); 
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)), 
            new EmptyBorder(5, 10, 5, 10) 
        ));
        return txt;
    }

    private boolean validateData() {
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã, Tên và Số điện thoại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (ma.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Mã khách hàng không được chứa khoảng trắng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtMa.requestFocus();
            return false;
        }
        if (ten.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không hợp lệ (Không được chứa chữ số)!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return false;
        }
        if (!sdt.matches("\\d{3}-\\d{6}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải nhập đúng định dạng XXX-YYYYYY\n(Ví dụ: 090-123456)", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            txtSDT.requestFocus();
            return false;
        }
        return true; 
    }

    public void loadDataToTable() {
        model.setRowCount(0);
        ArrayList<KhachHang> ds = khachHangDAO.docTuBang();
        for (KhachHang kh : ds) {
            model.addRow(new Object[]{
                kh.getMaKH(), kh.getTenKH(), kh.getSoDienThoai(), kh.getDiemTichLuy()
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnXoaRong) {
            txtMa.setText(""); txtTen.setText(""); txtSDT.setText(""); txtDiem.setText("0");
            txtMa.requestFocus();
            txtMa.setEditable(true); 
            txtMa.setBackground(new Color(40, 40, 40));
            table.clearSelection();
        } 
        else if (o == btnThem) {
            if (!validateData()) return; 
            
            String ma = txtMa.getText().trim();
            String ten = txtTen.getText().trim();
            String sdt = txtSDT.getText().trim();

            KhachHang kh = new KhachHang(ma, ten, sdt, 0);
            if (khachHangDAO.themKhachHang(kh)) {
                loadDataToTable(); 
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                btnXoaRong.doClick();
            } else {
                JOptionPane.showMessageDialog(this, "Mã khách hàng '" + ma + "' đã tồn tại!", "Lỗi trùng mã", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (o == btnXoa) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa từ bảng!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String ma = model.getValueAt(row, 0).toString();
                if (khachHangDAO.xoaKhachHang(ma)) {
                    loadDataToTable();
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    btnXoaRong.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Khách hàng này có thể đang tồn tại trong hóa đơn.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (o == btnSua) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần cập nhật!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!validateData()) return;

            String ma = txtMa.getText().trim();
            String ten = txtTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            int diem = Integer.parseInt(txtDiem.getText().trim());

            KhachHang kh = new KhachHang(ma, ten, sdt, diem);
            if (khachHangDAO.suaKhachHang(kh)) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Sửa thất bại!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMa.setText(model.getValueAt(row, 0).toString());
            txtTen.setText(model.getValueAt(row, 1).toString());
            txtSDT.setText(model.getValueAt(row, 2).toString());
            txtDiem.setText(model.getValueAt(row, 3).toString());
            
            txtMa.setEditable(false); 
            txtMa.setBackground(new Color(60, 60, 60)); 
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}