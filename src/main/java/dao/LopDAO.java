package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConnection;
import model.HocSinh;
import model.Lop;

public class LopDAO {
    
    public List<Lop> getAllLop() {
        List<Lop> list = new ArrayList<>();
        String sql = "SELECT l.*, k.TenKhoi FROM DM_Lop l " +
                    "LEFT JOIN DM_Khoi k ON l.KhoiID = k.ID " +
                    "ORDER BY l.NamHoc DESC, k.TenKhoi, l.TenLop";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Lop lop = new Lop();
                lop.setId(rs.getInt("ID"));
                lop.setTenLop(rs.getString("TenLop"));
                lop.setNamHoc(rs.getInt("NamHoc"));
                lop.setKhoiId(rs.getInt("KhoiID"));
                lop.setTenKhoi(rs.getString("TenKhoi"));
                list.add(lop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Lấy danh sách lớp theo khối
     */
    public List<Lop> getLopByKhoi(int khoiId) {
        List<Lop> list = new ArrayList<>();
        String sql = "SELECT l.*, k.TenKhoi FROM DM_Lop l " +
                    "LEFT JOIN DM_Khoi k ON l.KhoiID = k.ID " +
                    "WHERE l.KhoiID = ? " +
                    "ORDER BY l.NamHoc DESC, l.TenLop";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, khoiId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Lop lop = new Lop();
                lop.setId(rs.getInt("ID"));
                lop.setTenLop(rs.getString("TenLop"));
                lop.setNamHoc(rs.getInt("NamHoc"));
                lop.setKhoiId(rs.getInt("KhoiID"));
                lop.setTenKhoi(rs.getString("TenKhoi"));
                list.add(lop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean insertLop(Lop lop) {
        String sql = "INSERT INTO DM_Lop (TenLop, NamHoc, KhoiID) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, lop.getTenLop());
            stmt.setInt(2, lop.getNamHoc());
            stmt.setInt(3, lop.getKhoiId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateLop(Lop lop) {
        String sql = "UPDATE DM_Lop SET TenLop=?, NamHoc=?, KhoiID=? WHERE ID=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, lop.getTenLop());
            stmt.setInt(2, lop.getNamHoc());
            stmt.setInt(3, lop.getKhoiId());
            stmt.setInt(4, lop.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteLop(int id) {
        String sql = "DELETE FROM DM_Lop WHERE ID=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<HocSinh> getHocSinhInLop(int lopId) {
        List<HocSinh> list = new ArrayList<>();
        String sql = """
            SELECT hs.* FROM DM_HocSinh hs 
            INNER JOIN Lop_HocSinh lhs ON hs.ID = lhs.HS_ID 
            WHERE lhs.LOP_ID = ? AND lhs.TrangThai = 1
            ORDER BY hs.HoTen
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, lopId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                HocSinh hs = new HocSinh();
                hs.setId(rs.getInt("ID"));
                hs.setHoTen(rs.getString("HoTen"));
                hs.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                hs.setGioiTinh(rs.getBoolean("GioiTinh"));
                hs.setDiaChi(rs.getString("DiaChi"));
                list.add(hs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean addHocSinhToLop(int hsId, int lopId) {
        // Kiểm tra business rule: Một năm học, học sinh chỉ được học 1 lớp
        String checkSql = "SELECT COUNT(*) FROM Lop_HocSinh lhs " +
                         "JOIN DM_Lop l ON lhs.LOP_ID = l.ID " +
                         "WHERE lhs.HS_ID = ? AND l.NamHoc = (SELECT NamHoc FROM DM_Lop WHERE ID = ?) " +
                         "AND lhs.TrangThai = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, hsId);
            checkStmt.setInt(2, lopId);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Học sinh đã có lớp trong năm học này
                    return false;
                }
            }
            
            // Thêm học sinh vào lớp
            String insertSql = "INSERT INTO Lop_HocSinh (HS_ID, LOP_ID) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, hsId);
                insertStmt.setInt(2, lopId);
                
                return insertStmt.executeUpdate() > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean removeHocSinhFromLop(int hsId, int lopId) {
        String sql = "UPDATE Lop_HocSinh SET TrangThai = 0, NgayRaLop = GETDATE() WHERE HS_ID = ? AND LOP_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, hsId);
            stmt.setInt(2, lopId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy danh sách học sinh chưa có lớp trong một năm học cụ thể
     */
    public List<HocSinh> getHocSinhChuaCoLop(int namHoc) {
        List<HocSinh> list = new ArrayList<>();
        String sql = "SELECT hs.* FROM DM_HocSinh hs " +
                    "WHERE hs.ID NOT IN (" +
                    "    SELECT DISTINCT lhs.HS_ID " +
                    "    FROM Lop_HocSinh lhs " +
                    "    JOIN DM_Lop l ON lhs.LOP_ID = l.ID " +
                    "    WHERE l.NamHoc = ? AND lhs.TrangThai = 1" +
                    ") " +
                    "ORDER BY hs.HoTen";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, namHoc);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HocSinh hs = new HocSinh();
                    hs.setId(rs.getInt("ID"));
                    hs.setHoTen(rs.getString("HoTen"));
                    hs.setGioiTinh(rs.getBoolean("GioiTinh"));
                    hs.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                    hs.setDiaChi(rs.getString("DiaChi"));
                    list.add(hs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
