package ui;

import dao.TaiKhoanDAO;
import entity.TaiKhoan;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class UI_DangNhap extends JFrame implements ActionListener {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnThoat;
    private TaiKhoanDAO taiKhoanDAO;

    // --- LỚP VẼ ẢNH NỀN ---
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String fileName) {
            // Đặt màu nền dự phòng màu tối (để lỡ mất ảnh thì chữ trắng vẫn hiện rõ)
            setBackground(new Color(25, 25, 25)); 
            try {
                backgroundImage = new ImageIcon(fileName).getImage();
            } catch (Exception e) {
                System.out.println("Không tìm thấy ảnh nền!");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // --- LỚP TẠO HIỆU ỨNG KÍNH MỜ ---
    class GlassPanel extends JPanel {
        public GlassPanel() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(15, 15, 15, 200)); // Đen tuyền, mờ 80% cho cảm giác deep/sang trọng
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40); // Bo góc lớn hơn (40px)
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // --- LỚP NÚT BẤM CAO CẤP (BO GÓC MỀM MẠI) ---
    class PremiumButton extends JButton {
        private Color bgColor, hoverColor, borderColor;

        public PremiumButton(String text, Color bg, Color hover, Color fg, Color border) {
            super(text);
            this.bgColor = bg;
            this.hoverColor = hover;
            this.borderColor = border;
            
            setForeground(fg);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 14));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(hoverColor); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(bgColor); repaint(); }
            });
            setBackground(bgColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Vẽ nền nút
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            
            // Vẽ viền (nếu có)
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 25, 25);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public UI_DangNhap() {
        taiKhoanDAO = new TaiKhoanDAO();
        setTitle("Hệ Thống Đặt Vé - Đăng Nhập");
        setSize(850, 550); 
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Đã sửa lại đường dẫn ảnh thành "images/bg_login.jpg" cho khớp cấu trúc thư mục của bạn
        BackgroundPanel bgPanel = new BackgroundPanel("images/login.png"); 
        bgPanel.setLayout(new GridBagLayout()); 
        setContentPane(bgPanel);

        GlassPanel pnlLogin = new GlassPanel();
        pnlLogin.setPreferredSize(new Dimension(420, 440)); 
        pnlLogin.setLayout(null);
        bgPanel.add(pnlLogin);

        // --- TIÊU ĐỀ ---
        JLabel lblTitle = new JLabel("RẠP CHIẾU PHIM", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(220, 20, 60)); 
        lblTitle.setBounds(0, 50, 420, 40);
        pnlLogin.add(lblTitle);

        // --- TÀI KHOẢN ---
        JLabel lblUser = new JLabel("TÊN TÀI KHOẢN");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(150, 150, 150));
        lblUser.setBounds(60, 130, 300, 20);
        pnlLogin.add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(60, 155, 300, 35);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtUser.setForeground(Color.WHITE);
        txtUser.setCaretColor(new Color(212, 175, 55)); // Con trỏ chuột màu Vàng Gold
        txtUser.setOpaque(false); 
        txtUser.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(100, 100, 100)));
        pnlLogin.add(txtUser);

        // --- MẬT KHẨU ---
        JLabel lblPass = new JLabel("MẬT KHẨU");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(150, 150, 150));
        lblPass.setBounds(60, 215, 300, 20);
        pnlLogin.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(60, 240, 300, 35);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtPass.setForeground(Color.WHITE);
        txtPass.setCaretColor(new Color(212, 175, 55));
        txtPass.setOpaque(false);
        txtPass.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(100, 100, 100)));
        pnlLogin.add(txtPass);

        // --- NÚT ĐĂNG NHẬP (Solid Gold Button) ---
        // Nền vàng kim loại, chữ đen đậm -> Rất quyền lực và nổi bật
        btnLogin = new PremiumButton(
        	    "ĐĂNG NHẬP", 
        	    new Color(220, 20, 60),   // đỏ chính
        	    new Color(255, 80, 100),  // hover đỏ sáng hơn
        	    Color.WHITE,              // chữ trắng cho dễ nhìn
        	    null
        	);
        btnLogin.setBounds(60, 320, 145, 45);
        pnlLogin.add(btnLogin);

        // --- NÚT THOÁT (Ghost Button) ---
        // Nền trong suốt, viền xám sáng, chữ xám -> Tinh tế, không tranh giành sự chú ý với nút Đăng Nhập
        btnThoat = new PremiumButton(
            "THOÁT", 
            new Color(0, 0, 0, 0),      // Màu nền trong suốt
            new Color(255, 255, 255, 30),// Màu khi Hover (Phủ 1 lớp sương trắng)
            new Color(200, 200, 200),   // Màu chữ (Trắng xám)
            new Color(100, 100, 100)    // Có viền mảnh màu xám
        );
        btnThoat.setBounds(215, 320, 145, 45);
        pnlLogin.add(btnThoat);

        // Đăng ký sự kiện click
        btnLogin.addActionListener(this);
        btnThoat.addActionListener(this);
        
        // Nhấn Enter ở ô mật khẩu cũng tự đăng nhập
        txtPass.addActionListener(e -> btnLogin.doClick());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource(); 
        
        if (o == btnThoat) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc chắn muốn thoát hệ thống?", 
                    "Xác nhận thoát", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0); 
            }
            
        } else if (o == btnLogin) {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            TaiKhoan tk = taiKhoanDAO.kiemTraDangNhap(user, pass);
            
            if (tk != null) {
                // Đã chốt hàm khởi tạo chuẩn
                new UI_TrangChu(tk).setVisible(true); 
                this.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> {
            new UI_DangNhap().setVisible(true);
        });
    }
}