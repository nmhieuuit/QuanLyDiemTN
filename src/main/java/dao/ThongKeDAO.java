package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import config.DatabaseConnection;

public class ThongKeDAO {
    
    public Object[] thongKeTheoLop(int lopId) {
        String sql = """
            SELECT 
                COUNT(DISTINCT hs.ID) as TongHS,
                COUNT(DISTINCT CASE 
                    WHEN dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                         (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0 
                    THEN hs.ID END) as SoDat,
                COUNT(DISTINCT CASE 
                    WHEN NOT (dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                              (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0)
                    THEN hs.ID END) as SoKhongDat,
                CASE WHEN COUNT(DISTINCT hs.ID) > 0 
                     THEN CAST(COUNT(DISTINCT CASE 
                         WHEN dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                              (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0 
                         THEN hs.ID END) * 100.0 / COUNT(DISTINCT hs.ID) AS DECIMAL(5,2))
                     ELSE 0 END as TyLeDat,
                AVG((((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0))) as DiemTB
            FROM DM_HocSinh hs
            INNER JOIN Lop_HocSinh lhs ON hs.ID = lhs.HS_ID AND lhs.TrangThai = 1 AND lhs.LOP_ID = ?
            LEFT JOIN HocSinh_DiemHocKy dhk ON hs.ID = dhk.HS_ID
            LEFT JOIN HocSinh_DiemThi dt ON hs.ID = dt.HS_ID
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, lopId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Object[]{
                    rs.getInt("TongHS"),
                    rs.getInt("SoDat"),
                    rs.getInt("SoKhongDat"),
                    rs.getDouble("TyLeDat"),
                    rs.getObject("DiemTB") != null ? rs.getDouble("DiemTB") : 0.0
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new Object[]{0, 0, 0, 0.0, 0.0};
    }
    
    public Map<String, Object[]> thongKeTheoTatCaLop() {
        Map<String, Object[]> result = new HashMap<>();
        String sql = """
            SELECT 
                l.TenLop,
                l.NamHoc,
                COUNT(DISTINCT hs.ID) as TongHS,
                COUNT(DISTINCT CASE 
                    WHEN dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                         (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0 
                    THEN hs.ID END) as SoDat,
                COUNT(DISTINCT CASE 
                    WHEN NOT (dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                              (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0)
                    THEN hs.ID END) as SoKhongDat,
                CASE WHEN COUNT(DISTINCT hs.ID) > 0 
                     THEN CAST(COUNT(DISTINCT CASE 
                         WHEN dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                              (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0 
                         THEN hs.ID END) * 100.0 / COUNT(DISTINCT hs.ID) AS DECIMAL(5,2))
                     ELSE 0 END as TyLeDat,
                AVG((((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0))) as DiemTB
            FROM DM_Lop l
            JOIN DM_Khoi k ON l.KhoiID = k.ID
            LEFT JOIN Lop_HocSinh lhs ON l.ID = lhs.LOP_ID AND lhs.TrangThai = 1
            LEFT JOIN DM_HocSinh hs ON lhs.HS_ID = hs.ID
            LEFT JOIN HocSinh_DiemHocKy dhk ON hs.ID = dhk.HS_ID
            LEFT JOIN HocSinh_DiemThi dt ON hs.ID = dt.HS_ID
            WHERE k.TenKhoi = '12'
            GROUP BY l.ID, l.TenLop, l.NamHoc
            ORDER BY l.NamHoc DESC, l.TenLop
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String tenLop = rs.getString("TenLop");
                int namHoc = rs.getInt("NamHoc");
                String key = tenLop + " (" + namHoc + "-" + (namHoc + 1) + ")";
                Object[] stats = {
                    rs.getInt("TongHS"),
                    rs.getInt("SoDat"),
                    rs.getInt("SoKhongDat"),
                    rs.getDouble("TyLeDat"),
                    rs.getObject("DiemTB") != null ? rs.getDouble("DiemTB") : 0.0
                };
                result.put(key, stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public Object[] thongKeTheoNamHoc(int namHoc) {
        String sql = """
            SELECT 
                COUNT(DISTINCT hs.ID) as TongHS,
                COUNT(DISTINCT CASE 
                    WHEN dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                         (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0 
                    THEN hs.ID END) as SoDat,
                COUNT(DISTINCT CASE 
                    WHEN NOT (dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                              (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0)
                    THEN hs.ID END) as SoKhongDat,
                CASE WHEN COUNT(DISTINCT hs.ID) > 0 
                     THEN CAST(COUNT(DISTINCT CASE 
                         WHEN dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                              (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0 
                         THEN hs.ID END) * 100.0 / COUNT(DISTINCT hs.ID) AS DECIMAL(5,2))
                     ELSE 0 END as TyLeDat,
                AVG((((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0))) as DiemTB
            FROM DM_HocSinh hs
            INNER JOIN Lop_HocSinh lhs ON hs.ID = lhs.HS_ID AND lhs.TrangThai = 1
            INNER JOIN DM_Lop l ON lhs.LOP_ID = l.ID AND l.NamHoc = ?
            INNER JOIN DM_Khoi k ON l.KhoiID = k.ID AND k.TenKhoi = '12'
            LEFT JOIN HocSinh_DiemHocKy dhk ON hs.ID = dhk.HS_ID
            LEFT JOIN HocSinh_DiemThi dt ON hs.ID = dt.HS_ID
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, namHoc);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Object[]{
                    rs.getInt("TongHS"),
                    rs.getInt("SoDat"),
                    rs.getInt("SoKhongDat"),
                    rs.getDouble("TyLeDat"),
                    rs.getObject("DiemTB") != null ? rs.getDouble("DiemTB") : 0.0
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new Object[]{0, 0, 0, 0.0, 0.0};
    }
    
    public Map<Integer, Object[]> thongKeTheoTatCaNamHoc() {
        Map<Integer, Object[]> result = new HashMap<>();
        String sql = """
            SELECT 
                l.NamHoc,
                COUNT(DISTINCT hs.ID) as TongHS,
                COUNT(DISTINCT CASE 
                    WHEN dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                         (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0 
                    THEN hs.ID END) as SoDat,
                COUNT(DISTINCT CASE 
                    WHEN NOT (dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                              (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0)
                    THEN hs.ID END) as SoKhongDat,
                CASE WHEN COUNT(DISTINCT hs.ID) > 0 
                     THEN CAST(COUNT(DISTINCT CASE 
                         WHEN dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                              (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0 
                         THEN hs.ID END) * 100.0 / COUNT(DISTINCT hs.ID) AS DECIMAL(5,2))
                     ELSE 0 END as TyLeDat,
                AVG((((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0))) as DiemTB
            FROM DM_Lop l
            JOIN DM_Khoi k ON l.KhoiID = k.ID
            LEFT JOIN Lop_HocSinh lhs ON l.ID = lhs.LOP_ID AND lhs.TrangThai = 1
            LEFT JOIN DM_HocSinh hs ON lhs.HS_ID = hs.ID
            LEFT JOIN HocSinh_DiemHocKy dhk ON hs.ID = dhk.HS_ID
            LEFT JOIN HocSinh_DiemThi dt ON hs.ID = dt.HS_ID
            WHERE k.TenKhoi = '12'
            GROUP BY l.NamHoc
            ORDER BY l.NamHoc DESC
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Integer namHoc = rs.getInt("NamHoc");
                Object[] stats = {
                    rs.getInt("TongHS"),
                    rs.getInt("SoDat"),
                    rs.getInt("SoKhongDat"),
                    rs.getDouble("TyLeDat"),
                    rs.getObject("DiemTB") != null ? rs.getDouble("DiemTB") : 0.0
                };
                result.put(namHoc, stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public Object[] thongKeTongQuat() {
        String sql = """
            SELECT 
                COUNT(DISTINCT hs.ID) as TongHS,
                COUNT(DISTINCT CASE 
                    WHEN dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                         (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0 
                    THEN hs.ID END) as SoDat,
                COUNT(DISTINCT CASE 
                    WHEN NOT (dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                              (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0)
                    THEN hs.ID END) as SoKhongDat,
                CASE WHEN COUNT(DISTINCT hs.ID) > 0 
                     THEN CAST(COUNT(DISTINCT CASE 
                         WHEN dt.DiemToan >= 1.0 AND dt.DiemVan >= 1.0 AND dt.TuChon1Diem >= 1.0 AND dt.TuChon2Diem >= 1.0 AND
                              (((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0)) >= 5.0 
                         THEN hs.ID END) * 100.0 / COUNT(DISTINCT hs.ID) AS DECIMAL(5,2))
                     ELSE 0 END as TyLeDat,
                AVG((((dt.TongDiemThi + ISNULL(dhk.DiemTBCacNam,0))/2.0) + ISNULL(dt.DiemUT,0))) as DiemTB
            FROM DM_HocSinh hs
            INNER JOIN Lop_HocSinh lhs ON hs.ID = lhs.HS_ID AND lhs.TrangThai = 1
            INNER JOIN DM_Lop l ON lhs.LOP_ID = l.ID
            INNER JOIN DM_Khoi k ON l.KhoiID = k.ID AND k.TenKhoi = '12'
            LEFT JOIN HocSinh_DiemHocKy dhk ON hs.ID = dhk.HS_ID
            LEFT JOIN HocSinh_DiemThi dt ON hs.ID = dt.HS_ID
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return new Object[]{
                    rs.getInt("TongHS"),
                    rs.getInt("SoDat"),
                    rs.getInt("SoKhongDat"),
                    rs.getDouble("TyLeDat"),
                    rs.getObject("DiemTB") != null ? rs.getDouble("DiemTB") : 0.0
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new Object[]{0, 0, 0, 0.0, 0.0};
    }
    
    public Map<String, Object[]> thongKeTheoMon() {
        Map<String, Object[]> result = new HashMap<>();
        
        // Thống kê điểm Toán
        result.put("Toán", thongKeTheoMonCuThe("DiemToan"));
        
        // Thống kê điểm Văn  
        result.put("Văn", thongKeTheoMonCuThe("DiemVan"));
        
        return result;
    }
    
    private Object[] thongKeTheoMonCuThe(String columnName) {
        String sql = "SELECT " +
                    "COUNT(*) as TongHS, " +
                    "COUNT(CASE WHEN " + columnName + " >= 1.0 THEN 1 END) as SoDat, " +
                    "COUNT(CASE WHEN " + columnName + " < 1.0 THEN 1 END) as SoKhongDat, " +
                    "CASE WHEN COUNT(*) > 0 " +
                    "     THEN CAST(COUNT(CASE WHEN " + columnName + " >= 1.0 THEN 1 END) * 100.0 / COUNT(*) AS DECIMAL(5,2)) " +
                    "     ELSE 0 END as TyLeDat, " +
                    "AVG(" + columnName + ") as DiemTB " +
                    "FROM HocSinh_DiemThi " +
                    "WHERE " + columnName + " IS NOT NULL";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return new Object[]{
                    rs.getInt("TongHS"),
                    rs.getInt("SoDat"),
                    rs.getInt("SoKhongDat"),
                    rs.getDouble("TyLeDat"),
                    rs.getDouble("DiemTB")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new Object[]{0, 0, 0, 0.0, 0.0};
    }
}
