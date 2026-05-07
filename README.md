```markdown
# 🎬 CINEMA POS - HỆ THỐNG QUẢN LÝ BÁN VÉ RẠP CHIẾU PHIM 🍿

![Java](https://img.shields.io/badge/Java-Swing-orange?style=flat-square&logo=java)
![SQL Server](https://img.shields.io/badge/Database-SQL_Server-blue?style=flat-square&logo=microsoft-sql-server)
![Role](https://img.shields.io/badge/Role-Admin%20%7C%20Employee-success?style=flat-square)

**Cinema POS** là phần mềm quản lý phòng vé rạp chiếu phim trên nền tảng Desktop (Java Swing). Dự án được thiết kế với giao diện Dark Mode hiện đại (chuẩn rạp chiếu phim), luồng nghiệp vụ khép kín và tự động hóa cao, mang lại trải nghiệm thao tác bán vé (Point of Sale) mượt mà, chuyên nghiệp.

---

## 🌟 TÍNH NĂNG NỔI BẬT

### 🎟️ 1. Hệ thống Bán Vé POS (Point of Sale)
- **Sơ đồ ghế trực quan:** Hiển thị sơ đồ ghế theo thời gian thực. Phân biệt rõ ràng Ghế Trống, Đang Chọn, Đã Bán. Hỗ trợ nhiều loại ghế (Ghế Thường, Ghế VIP).
- **Dịch vụ đi kèm (F&B):** Tích hợp bán Bắp & Nước trực tiếp vào giỏ hàng ngay trên cùng một màn hình bán vé.
- **Mã giảm giá (Voucher):** Hỗ trợ nhập mã giảm giá linh hoạt (giảm theo % hoặc số tiền) và tự động tính toán lại tổng bill.
- **In Hóa Đơn (Bill):** Tự động xuất hóa đơn điện tử chi tiết bao gồm thông tin vé, số ghế, combo bắp nước, tiền giảm giá và tổng thu.

### 📊 2. Quản Lý & Thống Kê
- **Quản lý Phim & Suất Chiếu:** Thêm, sửa, xóa danh mục phim. Lên lịch suất chiếu thông minh (kiểm tra chống trùng lặp thời gian ở quá khứ).
- **Bảng điều khiển (Dashboard):** Hiển thị doanh thu, số lượng vé bán ra trong ngày. Biểu đồ cột phân tích doanh thu và bảng xếp hạng TOP phim hot.
- **Quản lý Khách Hàng:** Tự động lưu trữ lịch sử khách hàng, tính toán và cộng điểm tích lũy sau mỗi lần giao dịch thành công.

### 🛡️ 3. Phân Quyền Bảo Mật
- **Quản lý (Admin):** Toàn quyền truy cập, chỉnh sửa CSDL (Phim, Suất Chiếu, Thống kê doanh thu, Quản lý nhân viên).
- **Nhân viên (Employee):** Giới hạn quyền truy cập. Chỉ được phép thực hiện nghiệp vụ Bán vé, xem Lịch chiếu và xem Lịch sử hóa đơn do mình lập.

---

## 🛠️ CÔNG NGHỆ SỬ DỤNG

- **Ngôn ngữ lập trình:** Java (JDK 17+)
- **Giao diện (GUI):** Java Swing (AWT, CardLayout, JTable Custom Rendering)
- **Cơ sở dữ liệu:** Microsoft SQL Server
- **Kết nối CSDL:** JDBC (Java Database Connectivity)
- **Thư viện mở rộng:** `jcalendar` (JDateChooser cho chọn ngày)
- **Kiến trúc:** DAO (Data Access Object) Pattern

---

## 🚀 HƯỚNG DẪN CÀI ĐẶT & CHẠY DỰ ÁN

### Yêu cầu hệ thống:
- Đã cài đặt [Java JDK](https://www.oracle.com/java/technologies/downloads/).
- Đã cài đặt Hệ quản trị CSDL [SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server-downloads).
- IDE khuyên dùng: Visual Studio Code, Eclipse, hoặc IntelliJ IDEA.

### Các bước triển khai:
1. **Clone dự án:**
   ```bash
   git clone [https://github.com/your-username/Cinema-POS.git](https://github.com/your-username/Cinema-POS.git)
   ```
2. **Khởi tạo Database:**
   - Mở SQL Server Management Studio (SSMS).
   - Mở file script `Database/QuanLyBanVePhim.sql` có trong thư mục dự án.
   - Bấm `Execute` để tạo toàn bộ CSDL, các bảng và nạp dữ liệu mẫu (Poster phim, Bắp Nước,...).
3. **Cấu hình kết nối JDBC:**
   - Mở file `connect/Database.java`.
   - Đổi lại `url`, `username`, `password` sao cho khớp với cấu hình SQL Server trên máy của bạn.
4. **Chạy ứng dụng:**
   - Compile và Run file `UI_DangNhap.java` để khởi động phần mềm.

---

## 👤 TÀI KHOẢN TRẢI NGHIỆM
Hệ thống có sẵn các tài khoản mẫu với phân quyền khác nhau:
- **Quản lý:** Tài khoản: `admin` | Mật khẩu: `123456`
- **Nhân viên:** Tài khoản: `nhanvien` | Mật khẩu: `123456`

---

## 📸 ẢNH GIAO DIỆN CHƯƠNG TRÌNH
*(Thay thế các link dưới đây bằng đường dẫn ảnh chụp màn hình thực tế của bạn)*

- **Trang chủ & Bảng điều khiển:**
  <img src="link_anh_trang_chu" width="800">
- **Giao diện Bán vé POS (Chọn ghế, bắp nước, áp mã giảm giá):**
  <img src="link_anh_ban_ve" width="800">
- **Giao diện Quản lý Lịch chiếu:**
  <img src="link_anh_lich_chieu" width="800">

---

## 👨‍💻 THÔNG TIN NHÓM PHÁT TRIỂN

Dự án được thực hiện phục vụ cho đồ án môn học chuyên ngành Kỹ thuật Phần mềm.

**Nhóm 4 - Lớp DHKTPM20A**
- Nguyễn Dương Khang *(Trưởng nhóm)*
- *(Điền tên các thành viên khác vào đây)*
- *(Điền tên các thành viên khác vào đây)*

**Trường Đại học Công nghiệp Thành phố Hồ Chí Minh (IUH)**
```

**Mẹo nhỏ cho bạn:** Để file README này "ăn tiền" nhất khi đẩy lên GitHub, bạn hãy dùng công cụ Snipping Tool chụp lại 3 tấm ảnh giao diện ưng ý nhất (Trang chủ, Màn hình bán vé có đủ ghế và giỏ hàng, Màn hình thống kê biểu đồ), lưu vào thư mục `images` rồi sửa lại đường dẫn ở phần **📸 ẢNH GIAO DIỆN CHƯƠNG TRÌNH** nhé. 

Code xong dự án lớn thế này, dùng Git đẩy lên repo là quá tuyệt vời luôn!
