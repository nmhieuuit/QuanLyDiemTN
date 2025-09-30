-- Database Schema for QuanLyDiemTN
-- SQL Server Database

-- Tạo database nếu chưa có
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'QuanLyDiemTN')
BEGIN
    CREATE DATABASE QuanLyDiemTN;
END
GO

USE QuanLyDiemTN;
GO

-- Bảng Danh mục Học sinh (DM_HocSinh) - Bỏ Users vì không cần đăng nhập
CREATE TABLE DM_HocSinh (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(255) NOT NULL,
    NgaySinh DATE NOT NULL,
    GioiTinh BIT NOT NULL, -- 1: Nam, 0: Nữ
    DiaChi NVARCHAR(255),
    created_at DATETIME DEFAULT GETDATE()
);

-- Bảng Danh mục Khối (DM_Khoi)
CREATE TABLE DM_Khoi (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    TenKhoi NVARCHAR(10) NOT NULL UNIQUE, -- '10', '11', '12'
    MoTa NVARCHAR(255),
    created_at DATETIME DEFAULT GETDATE()
);

-- Thêm dữ liệu khối mặc định
INSERT INTO DM_Khoi (TenKhoi, MoTa) VALUES 
('10', N'Khối 10'),
('11', N'Khối 11'),
('12', N'Khối 12');

-- Bảng Danh mục Lớp (DM_Lop) - Cập nhật để có liên kết với khối
CREATE TABLE DM_Lop (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    TenLop NVARCHAR(100) NOT NULL,
    NamHoc INT NOT NULL,
    KhoiID INT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (KhoiID) REFERENCES DM_Khoi(ID)
);

-- Bảng Lớp - Học sinh (có thể chuyển lớp)
CREATE TABLE Lop_HocSinh (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    HS_ID INT NOT NULL,
    LOP_ID INT NOT NULL,
    NgayVaoLop DATE DEFAULT GETDATE(),
    NgayRaLop DATE NULL,
    TrangThai BIT DEFAULT 1, -- 1: Đang học, 0: Đã chuyển lớp
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (HS_ID) REFERENCES DM_HocSinh(ID),
    FOREIGN KEY (LOP_ID) REFERENCES DM_Lop(ID)
);

-- Bảng Điểm các học kỳ
CREATE TABLE HocSinh_DiemHocKy (
    HS_ID INT PRIMARY KEY,
    HK1_10 FLOAT CHECK (HK1_10 >= 0 AND HK1_10 <= 10),
    HK2_10 FLOAT CHECK (HK2_10 >= 0 AND HK2_10 <= 10),
    HK1_11 FLOAT CHECK (HK1_11 >= 0 AND HK1_11 <= 10),
    HK2_11 FLOAT CHECK (HK2_11 >= 0 AND HK2_11 <= 10),
    HK1_12 FLOAT CHECK (HK1_12 >= 0 AND HK1_12 <= 10),
    HK2_12 FLOAT CHECK (HK2_12 >= 0 AND HK2_12 <= 10),
    DiemTBCacNam AS (
        ((ISNULL(HK1_10,0) + ISNULL(HK2_10,0)*2)/3.0 + 
         (ISNULL(HK1_11,0) + ISNULL(HK2_11,0)*2)*2/3.0 + 
         (ISNULL(HK1_12,0) + ISNULL(HK2_12,0)*2)*3/3.0)/6.0
    ),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (HS_ID) REFERENCES DM_HocSinh(ID)
);

-- Bảng Môn học tốt nghiệp
CREATE TABLE MonHoc (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    TenMon NVARCHAR(255) NOT NULL,
    LoaiMon NVARCHAR(50) NOT NULL, -- 'BatBuoc' hoặc 'TuChon'
    created_at DATETIME DEFAULT GETDATE()
);

-- Thêm dữ liệu môn học mặc định
INSERT INTO MonHoc (TenMon, LoaiMon) VALUES 
(N'Toán', 'BatBuoc'),
(N'Ngữ văn', 'BatBuoc'),
(N'Ngoại ngữ', 'TuChon'),
(N'Lịch sử', 'TuChon'),
(N'Vật lý', 'TuChon'),
(N'Hóa học', 'TuChon'),
(N'Sinh học', 'TuChon'),
(N'Địa lý', 'TuChon'),
(N'Giáo dục kinh tế và pháp luật', 'TuChon'),
(N'Tin học', 'TuChon'),
(N'Công nghệ', 'TuChon');

-- Bảng Điểm thi tốt nghiệp
CREATE TABLE HocSinh_DiemThi (
    HS_ID INT PRIMARY KEY,
    DiemToan FLOAT NOT NULL CHECK (DiemToan >= 0 AND DiemToan <= 10),
    DiemVan FLOAT NOT NULL CHECK (DiemVan >= 0 AND DiemVan <= 10),
    TuChon1MH INT NOT NULL,
    TuChon1Diem FLOAT NOT NULL CHECK (TuChon1Diem >= 0 AND TuChon1Diem <= 10),
    TuChon2MH INT NOT NULL,
    TuChon2Diem FLOAT NOT NULL CHECK (TuChon2Diem >= 0 AND TuChon2Diem <= 10),
    DiemUT FLOAT DEFAULT 0 CHECK (DiemUT >= 0 AND DiemUT <= 2),
    TongDiemThi AS ((DiemToan + DiemVan + TuChon1Diem + TuChon2Diem)/4.0),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (HS_ID) REFERENCES DM_HocSinh(ID),
    FOREIGN KEY (TuChon1MH) REFERENCES MonHoc(ID),
    FOREIGN KEY (TuChon2MH) REFERENCES MonHoc(ID)
);

-- Bảng Kết quả xét tốt nghiệp
CREATE TABLE HocSinh_KetQuaTotNghiep (
    HS_ID INT PRIMARY KEY,
    DiemXetTotNghiep FLOAT,
    KetQua NVARCHAR(50), -- 'Đạt' hoặc 'Không đạt'
    GhiChu NVARCHAR(255),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (HS_ID) REFERENCES DM_HocSinh(ID)
);

-- Tạo indexes cho hiệu suất
CREATE INDEX IX_DM_HocSinh_HoTen ON DM_HocSinh(HoTen);
CREATE INDEX IX_DM_Khoi_TenKhoi ON DM_Khoi(TenKhoi);
CREATE INDEX IX_DM_Lop_TenLop ON DM_Lop(TenLop);
CREATE INDEX IX_DM_Lop_NamHoc ON DM_Lop(NamHoc);
CREATE INDEX IX_DM_Lop_KhoiID ON DM_Lop(KhoiID);
CREATE INDEX IX_Lop_HocSinh_HS_ID ON Lop_HocSinh(HS_ID);
CREATE INDEX IX_Lop_HocSinh_LOP_ID ON Lop_HocSinh(LOP_ID);

-- View để xem kết quả tổng hợp
CREATE VIEW VW_KetQuaTongHop AS
SELECT 
    hs.ID,
    hs.HoTen,
    hs.NgaySinh,
    hs.GioiTinh,
    k.TenKhoi,
    l.TenLop,
    l.NamHoc,
    dhk.DiemTBCacNam,
    dt.TongDiemThi,
    dt.DiemUT,
    (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) AS DiemXetTotNghiep,
    CASE 
        WHEN dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
             (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0 
        THEN N'Đạt' 
        ELSE N'Không đạt' 
    END AS KetQua
FROM DM_HocSinh hs
LEFT JOIN Lop_HocSinh lhs ON hs.ID = lhs.HS_ID AND lhs.TrangThai = 1
LEFT JOIN DM_Lop l ON lhs.LOP_ID = l.ID
LEFT JOIN DM_Khoi k ON l.KhoiID = k.ID
LEFT JOIN HocSinh_DiemHocKy dhk ON hs.ID = dhk.HS_ID
LEFT JOIN HocSinh_DiemThi dt ON hs.ID = dt.HS_ID;