USE QuanLyBanVePhim;
GO

CREATE TABLE SuatChieu (
    MaSuat VARCHAR(10) PRIMARY KEY,
    MaPhim VARCHAR(10) FOREIGN KEY REFERENCES Phim(MaPhim),
    PhongChieu NVARCHAR(50),
    NgayChieu DATE,
    GioChieu TIME
);
GO

-- Thêm thử 1 dòng dữ liệu mẫu (đảm bảo 'P001' đã có trong bảng Phim của bạn)
INSERT INTO SuatChieu VALUES ('S001', 'P001', N'Phòng 1 (2D)', '2026-04-10', '19:00:00');