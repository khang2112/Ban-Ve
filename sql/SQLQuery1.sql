CREATE DATABASE QuanLyBanVePhim;
GO
USE QuanLyBanVePhim;
GO

-- Bảng Tài Khoản (Dành cho nhân viên)
CREATE TABLE TaiKhoan (
    TenDangNhap VARCHAR(50) PRIMARY KEY,
    MatKhau VARCHAR(50) NOT NULL,
    HoTen NVARCHAR(100)
);

-- Bảng Phim
CREATE TABLE Phim (
    MaPhim VARCHAR(10) PRIMARY KEY,
    TenPhim NVARCHAR(100) NOT NULL,
    TheLoai NVARCHAR(50),
    GiaVe FLOAT NOT NULL
);

-- Thêm dữ liệu mẫu
INSERT INTO TaiKhoan VALUES ('admin', '123456', N'Nguyễn Dương Khang');
INSERT INTO Phim VALUES ('P001', N'Mai', N'Tình cảm', 90000);
INSERT INTO Phim VALUES ('P002', N'Đào, Phở và Piano', N'Lịch sử', 50000);
INSERT INTO Phim VALUES ('P003', N'Godzilla x Kong', N'Hành động', 120000);
INSERT INTO Phim VALUES ('P004', N'Phí phông', N'Kinh dị', 120000);
INSERT INTO Phim VALUES ('P005', N'Trùm sò', N'Hài hước', 220000);

ALTER TABLE TaiKhoan ADD VaiTro NVARCHAR(20);

-- Cập nhật admin hiện tại
UPDATE TaiKhoan SET VaiTro = 'Manager' WHERE TenDangNhap = 'admin';

-- Thêm một nhân viên mẫu
INSERT INTO TaiKhoan (TenDangNhap, MatKhau, HoTen, VaiTro) 
VALUES ('staff1', '123456', N'Lê Ngọc Quỳnh Hương', 'Staff');
