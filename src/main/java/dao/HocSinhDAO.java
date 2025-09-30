package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConnection;
import model.HocSinh;

public class HocSinhDAO {
    
    public List<HocSinh> getAllHocSinh() {
        List<HocSinh> list = new ArrayList<>();
        // Truy vấn lấy học sinh không trùng lặp với thông tin lớp gần nhất (năm học cao nhất)
        String sql = "WITH LatestClass AS (" +
                    "    SELECT hs.ID, hs.HoTen, hs.NgaySinh, hs.GioiTinh, hs.DiaChi, hs.created_at, " +
                    "           l.TenLop + ' - ' + CAST(l.NamHoc AS NVARCHAR) AS TenLop, " +
                    "           ROW_NUMBER() OVER (PARTITION BY hs.ID ORDER BY l.NamHoc DESC) as rn " +
                    "    FROM DM_HocSinh hs " +
                    "    LEFT JOIN Lop_HocSinh lhs ON hs.ID = lhs.HS_ID AND lhs.TrangThai = 1 " +
                    "    LEFT JOIN DM_Lop l ON lhs.LOP_ID = l.ID " +
                    ") " +
                    "SELECT ID, HoTen, NgaySinh, GioiTinh, DiaChi, created_at, TenLop " +
                    "FROM LatestClass " +
                    "WHERE rn = 1 " +
                    "ORDER BY HoTen ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                HocSinh hs = new HocSinh();
                hs.setId(rs.getInt("ID"));
                hs.setHoTen(rs.getString("HoTen"));
                hs.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                hs.setGioiTinh(rs.getBoolean("GioiTinh"));
                hs.setDiaChi(rs.getString("DiaChi"));
                hs.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                hs.setTenLop(rs.getString("TenLop")); // Tên lớp gần nhất hoặc null
                list.add(hs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean insertHocSinh(HocSinh hs) {
        String sql = "INSERT INTO DM_HocSinh (HoTen, NgaySinh, GioiTinh, DiaChi) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, hs.getHoTen());
            stmt.setDate(2, Date.valueOf(hs.getNgaySinh()));
            stmt.setBoolean(3, hs.isGioiTinh());
            stmt.setString(4, hs.getDiaChi());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateHocSinh(HocSinh hs) {
        String sql = "UPDATE DM_HocSinh SET HoTen=?, NgaySinh=?, GioiTinh=?, DiaChi=? WHERE ID=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, hs.getHoTen());
            stmt.setDate(2, Date.valueOf(hs.getNgaySinh()));
            stmt.setBoolean(3, hs.isGioiTinh());
            stmt.setString(4, hs.getDiaChi());
            stmt.setInt(5, hs.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteHocSinh(int id) {
        String sql = "DELETE FROM DM_HocSinh WHERE ID=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<HocSinh> searchHocSinh(String keyword) {
        List<HocSinh> list = new ArrayList<>();
        // Truy vấn tìm kiếm học sinh không trùng lặp với thông tin lớp gần nhất
        String sql = "WITH LatestClass AS (" +
                    "    SELECT hs.ID, hs.HoTen, hs.NgaySinh, hs.GioiTinh, hs.DiaChi, hs.created_at, " +
                    "           l.TenLop + ' - ' + CAST(l.NamHoc AS NVARCHAR) AS TenLop, " +
                    "           ROW_NUMBER() OVER (PARTITION BY hs.ID ORDER BY l.NamHoc DESC) as rn " +
                    "    FROM DM_HocSinh hs " +
                    "    LEFT JOIN Lop_HocSinh lhs ON hs.ID = lhs.HS_ID AND lhs.TrangThai = 1 " +
                    "    LEFT JOIN DM_Lop l ON lhs.LOP_ID = l.ID " +
                    "    WHERE hs.HoTen LIKE ? OR hs.ID = ? OR hs.DiaChi LIKE ? " +
                    ") " +
                    "SELECT ID, HoTen, NgaySinh, GioiTinh, DiaChi, created_at, TenLop " +
                    "FROM LatestClass " +
                    "WHERE rn = 1 " +
                    "ORDER BY HoTen ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + keyword + "%");
            stmt.setInt(2, keyword.matches("\\d+") ? Integer.parseInt(keyword) : -1);
            stmt.setString(3, "%" + keyword + "%"); 
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                HocSinh hs = new HocSinh();
                hs.setId(rs.getInt("ID"));
                hs.setHoTen(rs.getString("HoTen"));
                hs.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                hs.setGioiTinh(rs.getBoolean("GioiTinh"));
                hs.setDiaChi(rs.getString("DiaChi"));
                hs.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                hs.setTenLop(rs.getString("TenLop")); // Tên lớp gần nhất hoặc null
                list.add(hs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<HocSinh> getHocSinhByLop(int lopId) {
        List<HocSinh> list = new ArrayList<>();
        String sql = "SELECT hs.* FROM DM_HocSinh hs " +
                    "INNER JOIN Lop_HocSinh hsl ON hs.ID = hsl.HS_ID " +
                    "WHERE hsl.Lop_ID = ? ORDER BY hs.HoTen ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, lopId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HocSinh hs = new HocSinh();
                    hs.setId(rs.getInt("ID"));
                    hs.setHoTen(rs.getString("HoTen"));
                    hs.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                    hs.setGioiTinh(rs.getBoolean("GioiTinh"));
                    hs.setDiaChi(rs.getString("DiaChi"));
                    hs.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    list.add(hs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<HocSinh> getAllHocSinhWithLop() {
        List<HocSinh> list = new ArrayList<>();
        String sql = "WITH LatestClass AS (" +
                    "    SELECT hs.ID, hs.HoTen, hs.NgaySinh, hs.GioiTinh, hs.DiaChi, hs.created_at, " +
                    "           l.TenLop," +
                    "           ROW_NUMBER() OVER (PARTITION BY hs.ID ORDER BY l.NamHoc DESC) as rn " +
                    "    FROM DM_HocSinh hs " +
                    "    LEFT JOIN Lop_HocSinh lhs ON hs.ID = lhs.HS_ID AND lhs.TrangThai = 1 " +
                    "    LEFT JOIN DM_Lop l ON lhs.LOP_ID = l.ID " +
                    ") " +
                    "SELECT ID, HoTen, NgaySinh, GioiTinh, DiaChi, created_at, TenLop " +
                    "FROM LatestClass " +
                    "WHERE rn = 1 " +
                    "ORDER BY HoTen ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                HocSinh hs = new HocSinh();
                hs.setId(rs.getInt("ID"));
                hs.setHoTen(rs.getString("HoTen"));
                hs.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                hs.setGioiTinh(rs.getBoolean("GioiTinh"));
                hs.setDiaChi(rs.getString("DiaChi"));
                hs.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                // Set tên lớp (có thể null nếu học sinh chưa có lớp)
                String tenLop = rs.getString("TenLop");
                hs.setTenLop(tenLop);
                
                list.add(hs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
