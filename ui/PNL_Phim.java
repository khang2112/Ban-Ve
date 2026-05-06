package ui;

import dao.PhimDAO;
import entity.Phim;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PNL_Phim extends JPanel implements ActionListener, MouseListener {
    private JTextField txtMaPhim, txtTenPhim, txtGiaVe;
    private JComboBox<String> cboTheLoai; 
    private JButton btnThem, btnSua, btnXoa, btnXoaRong;
    private DefaultTableModel model;
    private JTable table;
    private PhimDAO phimDAO;

    // --- BẢNG MÀU CHỦ ĐỀ ĐỎ ---
    private Color bgDark = new Color(18, 18, 18);          
    private Color bgPanel = new Color(30, 30, 30);         
    private Color textWhite = new Color(240, 240, 240);    
    private Color themeRed = new Color(229, 9, 20);        

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
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public PNL_Phim() {
        phimDAO = new PhimDAO(); 
        
        setLayout(new BorderLayout(20, 20)); 
        setBackground(bgDark);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // ==========================================
        // 1. FORM NHẬP LIỆU (ĐÃ CHỈNH LẠI BỐ CỤC CÂN ĐỐI)
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout(20, 20));
        pnlTop.setOpaque(false);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 1, true), "THÔNG TIN PHIM",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 15), themeRed
        );
        
        JPanel pnlInputWrapper = new JPanel(new BorderLayout(15, 0));
        pnlInputWrapper.setBackground(bgPanel);
        pnlInputWrapper.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(20, 30, 20, 30)));

        // Chia lưới 2 hàng, 4 cột để phân bố đều các Textfield
        JPanel pnlInput = new JPanel(new GridLayout(2, 4, 25, 25)); 
        pnlInput.setOpaque(false);

        pnlInput.add(createLabel("Mã Phim:"));
        txtMaPhim = createTextField();
        txtMaPhim.setEditable(false); 
        txtMaPhim.setForeground(new Color(212, 175, 55)); 
        pnlInput.add(txtMaPhim);

        pnlInput.add(createLabel("Tên Phim:"));
        txtTenPhim = createTextField();
        pnlInput.add(txtTenPhim);

        pnlInput.add(createLabel("Thể Loại:"));
        cboTheLoai = createComboBox(); 
        pnlInput.add(cboTheLoai);

        pnlInput.add(createLabel("Giá Vé (VNĐ):"));
        txtGiaVe = createTextField();
        pnlInput.add(txtGiaVe);

        pnlInputWrapper.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlInputWrapper, BorderLayout.CENTER);

        // ==========================================
        // 2. NÚT CHỨC NĂNG
        // ==========================================
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        pnlBtns.setOpaque(false);
        
        btnThem = new PosButton("THÊM MỚI", new Color(46, 204, 113), Color.WHITE);
        btnThem.setPreferredSize(new Dimension(130, 42));
        btnSua = new PosButton("CẬP NHẬT", new Color(52, 152, 219), Color.WHITE);
        btnSua.setPreferredSize(new Dimension(130, 42));
        btnXoa = new PosButton("XÓA PHIM", themeRed, Color.WHITE); 
        btnXoa.setPreferredSize(new Dimension(130, 42));
        btnXoaRong = new PosButton("LÀM MỚI", new Color(100, 100, 100), Color.WHITE);
        btnXoaRong.setPreferredSize(new Dimension(130, 42));

        pnlBtns.add(btnThem);
        pnlBtns.add(btnSua);
        pnlBtns.add(btnXoa);
        pnlBtns.add(btnXoaRong);

        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // ==========================================
        // 3. BẢNG DỮ LIỆU
        // ==========================================
        JPanel pnlTableWrapper = new JPanel(new BorderLayout());
        pnlTableWrapper.setBackground(bgPanel);
        pnlTableWrapper.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 60, 60), 1, true), 
            new EmptyBorder(5, 5, 5, 5)
        ));

        // Đã xóa cột hiển thị đường dẫn ảnh
        String[] cols = {"Mã Phim", "Tên Phim", "Thể Loại", "Giá Vé"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        table.setRowHeight(40); 
        table.setBackground(bgPanel); 
        table.setForeground(textWhite);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setSelectionBackground(themeRed); 
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false); 
        table.setIntercellSpacing(new Dimension(0, 0));
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(20, 20, 20));
        headerRenderer.setForeground(themeRed); 
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 15));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, themeRed)); 
        
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

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
        
        loadComboTheLoai();
        loadDataToTable();
        txtMaPhim.setText(phimDAO.phatSinhMaPhim()); 
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.BOLD, 15)); 
        txt.setBackground(new Color(45, 45, 45)); 
        txt.setForeground(Color.WHITE); 
        txt.setCaretColor(themeRed); 
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1, true), 
            new EmptyBorder(8, 12, 8, 12) 
        ));
        return txt;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> cbo = new JComboBox<>();
        cbo.setUI(new BasicComboBoxUI()); 
        cbo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cbo.setBackground(new Color(45, 45, 45)); 
        cbo.setForeground(Color.WHITE);
        cbo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1, true), 
            new EmptyBorder(4, 8, 4, 8) 
        ));
        cbo.setEditable(true); 
        cbo.getEditor().getEditorComponent().setBackground(new Color(45, 45, 45));
        cbo.getEditor().getEditorComponent().setForeground(Color.WHITE);
        return cbo;
    }

    private boolean validateData() {
        String ma = txtMaPhim.getText().trim();
        String ten = txtTenPhim.getText().trim();
        String giaStr = txtGiaVe.getText().trim();
        String theLoai = "";
        if (cboTheLoai.getSelectedItem() != null) {
            theLoai = cboTheLoai.getSelectedItem().toString().trim();
        }

        if (ma.isEmpty() || ten.isEmpty() || theLoai.isEmpty() || giaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin phim!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            double gia = Double.parseDouble(giaStr);
            if (gia < 0) {
                JOptionPane.showMessageDialog(this, "Giá vé phải lớn hơn hoặc bằng 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá vé phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void loadComboTheLoai() {
        cboTheLoai.removeAllItems();
        ArrayList<String> dsTL = phimDAO.layDanhSachTheLoai();
        for (String tl : dsTL) {
            cboTheLoai.addItem(tl);
        }
    }

    public void loadDataToTable() {
        model.setRowCount(0);
        ArrayList<Phim> ds = phimDAO.docTuBang();
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        for (Phim p : ds) {
            model.addRow(new Object[]{
                p.getMaPhim(), p.getTenPhim(), p.getTheLoai(), nf.format(p.getGiaVe()) + " đ"
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnXoaRong) {
            txtTenPhim.setText(""); txtGiaVe.setText("");
            if (cboTheLoai.getItemCount() > 0) cboTheLoai.setSelectedIndex(0);
            
            txtMaPhim.setText(phimDAO.phatSinhMaPhim()); 
            txtTenPhim.requestFocus();
            table.clearSelection();
            
            loadComboTheLoai();
        } 
        else if (o == btnThem) {
            if (!validateData()) return;
            
            String ma = txtMaPhim.getText().trim();
            String ten = txtTenPhim.getText().trim();
            String tl = cboTheLoai.getSelectedItem().toString().trim(); 
            double gia = Double.parseDouble(txtGiaVe.getText().trim());

            // Tạo đối tượng Phim, tham số hình ảnh để chuỗi rỗng ""
            Phim p = new Phim(ma, ten, tl, gia, "");
            
            if (phimDAO.themPhim(p)) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Thêm phim thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                btnXoaRong.doClick(); 
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại (Lỗi CSDL)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (o == btnXoa) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phim cần xóa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phim này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String ma = model.getValueAt(row, 0).toString();
                if (phimDAO.xoaPhim(ma)) {
                    loadDataToTable();
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    btnXoaRong.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Phim này có thể đang có suất chiếu.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (o == btnSua) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phim cần sửa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!validateData()) return;

            String ma = txtMaPhim.getText().trim();
            String ten = txtTenPhim.getText().trim();
            String tl = cboTheLoai.getSelectedItem().toString().trim();
            double gia = Double.parseDouble(txtGiaVe.getText().trim());

            Phim p = new Phim(ma, ten, tl, gia, "");
            
            if (phimDAO.suaPhim(p)) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Sửa thất bại!", "Lỗi CSDL", ERROR);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMaPhim.setText(model.getValueAt(row, 0).toString());
            txtTenPhim.setText(model.getValueAt(row, 1).toString());
            cboTheLoai.setSelectedItem(model.getValueAt(row, 2).toString());
            
            String giaStr = model.getValueAt(row, 3).toString().replaceAll("[. đ]", "");
            txtGiaVe.setText(giaStr);
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}