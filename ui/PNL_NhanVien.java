package ui;

import dao.NhanVienDAO;
import entity.NhanVien;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PNL_NhanVien extends JPanel implements ActionListener, MouseListener {
    private JTextField txtMa, txtHo, txtTen, txtTuoi, txtLuong, txtTim;
    private JComboBox<String> cboPhongBan;
    private JButton btnThem, btnXoa, btnSua, btnXoaRong, btnTim;
    private DefaultTableModel model;
    private JTable table;
    
    private NhanVienDAO nhanVienDAO;

    // --- BẢNG MÀU CHỦ ĐỀ ĐỎ (NETFLIX / CGV THEME) ---
    private Color bgDark = new Color(18, 18, 18);
    private Color bgPanel = new Color(30, 30, 30);
    private Color textWhite = new Color(240, 240, 240);
    private Color themeRed = new Color(229, 9, 20); 

    // --- LỚP NÚT BẤM CAO CẤP CHỐNG LỖI WINDOWS UI ---
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

    public PNL_NhanVien() {
        nhanVienDAO = new NhanVienDAO();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(bgDark);
        setBorder(new EmptyBorder(15, 20, 20, 20));

        // ==========================================
        // 1. FORM NHẬP LIỆU
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(3, 4, 20, 20));
        pnlInput.setBackground(bgPanel);
        pnlInput.setBorder(new EmptyBorder(15, 20, 15, 20)); 

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), "THÔNG TIN NHÂN VIÊN",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), themeRed
        );
        
        JPanel pnlInputWrapper = new JPanel(new BorderLayout());
        pnlInputWrapper.setBackground(bgPanel);
        pnlInputWrapper.setBorder(border);
        pnlInputWrapper.add(pnlInput, BorderLayout.CENTER);

        pnlInput.add(createLabel("Mã NV:"));
        txtMa = createTextField();
        pnlInput.add(txtMa);

        pnlInput.add(createLabel("Phòng Ban:"));
        String[] phongBan = {"Phòng tổ chức", "Phòng kỹ thuật", "Phòng nhân sự"};
        cboPhongBan = new JComboBox<>(phongBan);
        cboPhongBan.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cboPhongBan.setBackground(new Color(40, 40, 40));
        cboPhongBan.setForeground(Color.WHITE);
        cboPhongBan.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        pnlInput.add(cboPhongBan);

        pnlInput.add(createLabel("Họ lót:"));
        txtHo = createTextField();
        pnlInput.add(txtHo);

        pnlInput.add(createLabel("Tên:"));
        txtTen = createTextField();
        pnlInput.add(txtTen);

        pnlInput.add(createLabel("Tuổi:"));
        txtTuoi = createTextField();
        pnlInput.add(txtTuoi);

        pnlInput.add(createLabel("Tiền Lương:"));
        txtLuong = createTextField();
        pnlInput.add(txtLuong);

        pnlTop.add(pnlInputWrapper, BorderLayout.CENTER);

        // ==========================================
        // 2. NÚT CHỨC NĂNG
        // ==========================================
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

        // ==========================================
        // 3. KHU VỰC TÌM KIẾM & BẢNG DỮ LIỆU
        // ==========================================
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setOpaque(false);

        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlTimKiem.setOpaque(false);
        
        JLabel lblTimKiem = createLabel("Tìm kiếm (Mã/Tên):");
        lblTimKiem.setForeground(new Color(212, 175, 55)); 
        txtTim = createTextField();
        txtTim.setPreferredSize(new Dimension(250, 35));
        
        btnTim = new PosButton("TÌM", new Color(41, 128, 185), Color.WHITE); 
        btnTim.setPreferredSize(new Dimension(80, 35));
        
        pnlTimKiem.add(lblTimKiem);
        pnlTimKiem.add(txtTim);
        pnlTimKiem.add(btnTim);

        pnlCenter.add(pnlTimKiem, BorderLayout.NORTH);

        JPanel pnlTableWrapper = new JPanel(new BorderLayout());
        pnlTableWrapper.setBackground(bgPanel);
        pnlTableWrapper.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 60, 60), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));

        String[] cols = {"Mã NV", "Họ Lót", "Tên", "Tuổi", "Phòng Ban", "Tiền Lương"};
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

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(bgPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        pnlTableWrapper.add(scrollPane, BorderLayout.CENTER);
        pnlCenter.add(pnlTableWrapper, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        btnTim.addActionListener(this); 
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
        String ho = txtHo.getText().trim();
        String ten = txtTen.getText().trim();
        String tuoiStr = txtTuoi.getText().trim();
        String luongStr = txtLuong.getText().replace(",", "").replace(".", "").trim();

        if (ma.isEmpty() || ho.isEmpty() || ten.isEmpty() || tuoiStr.isEmpty() || luongStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (ho.matches(".*\\d.*") || ten.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "Họ và Tên không được chứa chữ số.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            int tuoi = Integer.parseInt(tuoiStr);
            if (tuoi < 18 || tuoi > 62) {
                JOptionPane.showMessageDialog(this, "Tuổi nhân viên phải từ 18 đến 62.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtTuoi.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tuổi phải là định dạng số nguyên.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtTuoi.requestFocus();
            return false;
        }
        try {
            double luong = Double.parseDouble(luongStr);
            if (luong <= 0) {
                JOptionPane.showMessageDialog(this, "Tiền lương phải lớn hơn 0.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtLuong.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tiền lương phải là định dạng số.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtLuong.requestFocus();
            return false;
        }
        return true; 
    }

    private void xoaDuLieuBang() {
        model.setRowCount(0);
    }
    
    private void hienThiDanhSach(ArrayList<NhanVien> ds) {
        xoaDuLieuBang();
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN")); 
        for (NhanVien nv : ds) {
            model.addRow(new Object[]{
                nv.getMaNV(), nv.getHoNV(), nv.getTenNV(), nv.getTuoi(), 
                nv.getPhongBan(), nf.format(nv.getTienLuong()) + " đ"
            });
        }
    }

    private void loadDataToTable() {
        ArrayList<NhanVien> ds = nhanVienDAO.docTuBang();
        hienThiDanhSach(ds);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnXoaRong) {
            txtMa.setText(""); txtHo.setText(""); txtTen.setText("");
            txtTuoi.setText(""); txtLuong.setText(""); cboPhongBan.setSelectedIndex(0);
            txtMa.requestFocus();
            txtMa.setEditable(true);
            txtMa.setBackground(new Color(40, 40, 40));
            table.clearSelection();
            
            txtTim.setText("");
            loadDataToTable();
        } 
        else if (o == btnTim) {
            String tuKhoa = txtTim.getText().trim();
            if (tuKhoa.isEmpty()) {
                loadDataToTable(); 
            } else {
                ArrayList<NhanVien> dsTimKiem = nhanVienDAO.timNhanVien(tuKhoa);
                hienThiDanhSach(dsTimKiem);
                if (dsTimKiem.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên nào phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        else if (o == btnThem) {
            if (!validateData()) return; 

            String ma = txtMa.getText().trim();
            String ho = txtHo.getText().trim();
            String ten = txtTen.getText().trim();
            int tuoi = Integer.parseInt(txtTuoi.getText().trim());
            String phong = cboPhongBan.getSelectedItem().toString();
            double luong = Double.parseDouble(txtLuong.getText().replace(",", "").replace(".", "").trim());

            NhanVien nv = new NhanVien(ma, ho, ten, tuoi, phong, luong);
            if (nhanVienDAO.themNhanVien(nv)) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                btnXoaRong.doClick();
            } else {
                JOptionPane.showMessageDialog(this, "Mã nhân viên đã tồn tại trong hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (o == btnXoa) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                String ma = model.getValueAt(row, 0).toString();
                if (nhanVienDAO.xoaNhanVien(ma)) {
                    loadDataToTable(); 
                    JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    btnXoaRong.doClick();
                }
            }
        }
        else if (o == btnSua) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần cập nhật từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!validateData()) return; 

            String ma = txtMa.getText().trim();
            String ho = txtHo.getText().trim();
            String ten = txtTen.getText().trim();
            int tuoi = Integer.parseInt(txtTuoi.getText().trim());
            String phong = cboPhongBan.getSelectedItem().toString();
            double luong = Double.parseDouble(txtLuong.getText().replace(",", "").replace(".", "").trim());

            NhanVien nv = new NhanVien(ma, ho, ten, tuoi, phong, luong);
            if (nhanVienDAO.suaNhanVien(nv)) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMa.setText(model.getValueAt(row, 0).toString());
            txtHo.setText(model.getValueAt(row, 1).toString());
            txtTen.setText(model.getValueAt(row, 2).toString());
            txtTuoi.setText(model.getValueAt(row, 3).toString());
            cboPhongBan.setSelectedItem(model.getValueAt(row, 4).toString());
            
            String luongStr = model.getValueAt(row, 5).toString().replaceAll("[^0-9]", ""); 
            txtLuong.setText(luongStr);
            
            txtMa.setEditable(false);
            txtMa.setBackground(new Color(60, 60, 60)); 
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}