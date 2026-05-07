CREATE DATABASE QuanLyBanVePhim;
GO
USE QuanLyBanVePhim;
GO

CREATE TABLE TaiKhoan (
    TenDangNhap VARCHAR(50) PRIMARY KEY, MatKhau VARCHAR(50) NOT NULL,
    HoTen NVARCHAR(100), VaiTro VARCHAR(50)
); 

CREATE TABLE NhanVien (
    MaNV VARCHAR(10) PRIMARY KEY, HoNV NVARCHAR(50) NOT NULL,
    TenNV NVARCHAR(50) NOT NULL, Tuoi INT,
    PhongBan NVARCHAR(50), TienLuong FLOAT
); 

CREATE TABLE KhachHang (
    MaKH VARCHAR(15) PRIMARY KEY, TenKH NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(20), DiemTichLuy INT DEFAULT 0
);

CREATE TABLE Phim (
    MaPhim VARCHAR(10) PRIMARY KEY, TenPhim NVARCHAR(100) NOT NULL,
    TheLoai NVARCHAR(50), GiaVe FLOAT NOT NULL, HinhAnh NVARCHAR(255) 
); 

CREATE TABLE SuatChieu (
    MaSuat VARCHAR(10) PRIMARY KEY, MaPhim VARCHAR(10) FOREIGN KEY REFERENCES Phim(MaPhim),
    PhongChieu NVARCHAR(50), NgayChieu DATE, GioChieu TIME
); 

CREATE TABLE HoaDon (
    MaHD VARCHAR(20) PRIMARY KEY, MaNV VARCHAR(10), 
    MaKH VARCHAR(15) FOREIGN KEY REFERENCES KhachHang(MaKH), 
    NgayLap DATETIME, TongTien FLOAT
); 

CREATE TABLE ChiTietHoaDon (
    MaHD VARCHAR(20) FOREIGN KEY REFERENCES HoaDon(MaHD),
    MaSuat VARCHAR(10) FOREIGN KEY REFERENCES SuatChieu(MaSuat),
    SoLuong INT,
    GiaVe FLOAT,
    ThanhTien FLOAT,
    PRIMARY KEY (MaHD, MaSuat)
);

CREATE TABLE VePhim (
    MaVe INT IDENTITY(1,1) PRIMARY KEY, MaHD VARCHAR(20) FOREIGN KEY REFERENCES HoaDon(MaHD),
    MaSuat VARCHAR(10) FOREIGN KEY REFERENCES SuatChieu(MaSuat),
    MaGhe VARCHAR(5), GiaVe FLOAT
); 
GO

INSERT INTO TaiKhoan VALUES ('admin', '123456', N'Nguyễn Dương Khang', N'Quản lý');
INSERT INTO TaiKhoan VALUES ('nhanvien', '123456', N'Nhân Viên Quầy', N'Nhân viên');
INSERT INTO NhanVien VALUES ('NV001', N'Nguyễn Dương', N'Khang', 20, N'Phòng kỹ thuật', 15000000);

-- BA BỘ PHIM NÀY SẼ TỰ ĐỘNG HIỆN LÊN TRANG CHỦ MÀ KHÔNG CẦN CODE CỨNG NỮA
INSERT INTO Phim VALUES ('P001', N'Mai', N'Tâm lý, Tình cảm', 90000, ''); 
INSERT INTO Phim VALUES ('P002', N'Đất Rừng Phương Nam', N'Lịch sử', 85000, ''); 
INSERT INTO Phim VALUES ('P003', N'Phí phông: Quỷ máu rừng thiên', N'Kinh dị', 120000, ''); 

INSERT INTO SuatChieu VALUES ('S001', 'P001', N'Phòng 1 (2D)', CAST(GETDATE() AS DATE), '19:00:00');
INSERT INTO SuatChieu VALUES ('S002', 'P002', N'Phòng 2 (3D)', CAST(GETDATE() AS DATE), '20:30:00');
GO

USE QuanLyBanVePhim;
GO

-- 1. Cập nhật Poster cho 3 phim đang có sẵn
UPDATE Phim SET HinhAnh = 'images/mai.jpg' WHERE MaPhim = 'P001';
UPDATE Phim SET HinhAnh = 'images/datrungphuongnam.jpg' WHERE MaPhim = 'P002';
UPDATE Phim SET HinhAnh = 'images/phiphong.jpg' WHERE MaPhim = 'P003';

-- 2. Thêm mới 3 phim để xài hết dàn Poster trong thư mục của bạn
INSERT INTO Phim (MaPhim, TenPhim, TheLoai, GiaVe, HinhAnh) 
VALUES ('P004', N'Bố Già', N'Tâm lý, Gia đình', 80000, 'images/springjourney.jpg');

INSERT INTO Phim (MaPhim, TenPhim, TheLoai, GiaVe, HinhAnh) 
VALUES ('P005', N'Avatar 2: Dòng Chảy', N'Khoa học viễn tưởng', 150000, 'images/thejunglebook.jpg');

INSERT INTO Phim (MaPhim, TenPhim, TheLoai, GiaVe, HinhAnh) 
VALUES ('P006', N'Trùm Sò', N'Hài hước', 75000, 'images/trumso.jpg');
GO

USE QuanLyBanVePhim;
GO

CREATE TABLE DichVu (
    MaDV VARCHAR(10) PRIMARY KEY,
    TenDV NVARCHAR(100),
    GiaDV FLOAT
);
INSERT INTO DichVu VALUES ('DV01', N'Combo 1 (1 Bắp + 1 Nước)', 65000);
INSERT INTO DichVu VALUES ('DV02', N'Combo 2 (1 Bắp + 2 Nước)', 85000);
INSERT INTO DichVu VALUES ('DV03', N'Bắp Phô Mai', 55000);
INSERT INTO DichVu VALUES ('DV04', N'Coca Cola Lớn', 30000);

CREATE TABLE ChiTietDichVu (
    MaHD VARCHAR(20) FOREIGN KEY REFERENCES HoaDon(MaHD),
    MaDV VARCHAR(10) FOREIGN KEY REFERENCES DichVu(MaDV),
    SoLuong INT,
    GiaBan FLOAT,
    ThanhTien FLOAT,
    PRIMARY KEY (MaHD, MaDV)
);
GO

