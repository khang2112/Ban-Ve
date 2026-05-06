USE QuanLyBanVePhim;
GO

-- 1. Bảng Khách Hàng (Tích điểm)
CREATE TABLE KhachHang (
    MaKH VARCHAR(10) PRIMARY KEY,
    TenKH NVARCHAR(50) NOT NULL,
    SoDienThoai VARCHAR(15) NOT NULL,
    DiemTichLuy INT DEFAULT 0
);
GO

-- 2. Bảng Dịch Vụ (Bắp, Nước)
CREATE TABLE DichVu (
    MaDV VARCHAR(10) PRIMARY KEY,
    TenDV NVARCHAR(50),
    Gia FLOAT
);
GO

-- Thêm một khách hàng mẫu
INSERT INTO KhachHang VALUES ('KH001', N'Nguyễn Văn A', '090-123456', 100);

USE QuanLyBanVePhim;
GO
-- Thêm cột MaKH vào bảng HoaDon, cho phép NULL (vì có khách không có thẻ thành viên)
ALTER TABLE HoaDon ADD MaKH VARCHAR(10) NULL;