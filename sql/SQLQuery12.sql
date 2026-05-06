USE QuanLyBanVePhim;
GO

CREATE TABLE NhanVien (
    MaNV VARCHAR(10) PRIMARY KEY,
    HoNV NVARCHAR(50) NOT NULL,
    TenNV NVARCHAR(50) NOT NULL,
    Tuoi INT,
    PhongBan NVARCHAR(50),
    TienLuong FLOAT
);
GO

-- Thêm dữ liệu mẫu
INSERT INTO NhanVien VALUES ('NV001', N'Nguyễn Dương', N'Khang', 20, N'Phòng kỹ thuật', 15000000);
USE QuanLyBanVePhim;
GO

-- Bảng Hóa Đơn tổng
CREATE TABLE HoaDon (
    MaHD VARCHAR(15) PRIMARY KEY,
    MaNV VARCHAR(10), -- Nhân viên nào bán
    NgayLap DATETIME,
    TongTien FLOAT
);
GO
CREATE TABLE ChiTietHoaDon (
    MaHD VARCHAR(15),
    MaPhim VARCHAR(10),
    SoLuong INT,
    DonGia FLOAT,

    PRIMARY KEY (MaHD, MaPhim),

    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD),
    FOREIGN KEY (MaPhim) REFERENCES Phim(MaPhim)
);

-- Bảng Chi Tiết Vé (Lưu từng ghế đã bán)
CREATE TABLE VePhim (
    MaVe INT IDENTITY(1,1) PRIMARY KEY, -- Tự động tăng
    MaHD VARCHAR(15) FOREIGN KEY REFERENCES HoaDon(MaHD),
    MaSuat VARCHAR(10) FOREIGN KEY REFERENCES SuatChieu(MaSuat),
    MaGhe VARCHAR(5),
    GiaVe FLOAT
);
GO
INSERT INTO SuatChieu VALUES ('S002', 'P003', N'Phòng 2 (3D)', '2026-04-10', '20:30:00');
INSERT INTO HoaDon (MaHD, MaNV, NgayLap, TongTien)
VALUES ('HD01', 'NV01', GETDATE(), 300000);

INSERT INTO ChiTietHoaDon VALUES ('HD01', 'P001', 5, 90000);
INSERT INTO ChiTietHoaDon VALUES ('HD01', 'P002', 3, 50000);
INSERT INTO ChiTietHoaDon VALUES ('HD01', 'P003', 7, 120000);
INSERT INTO ChiTietHoaDon VALUES ('HD01', 'P004', 12, 120000);