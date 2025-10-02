package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConnection;
import model.DiemTotNghiep;


public class DiemTotNghiepDAO {
    
    public DiemTotNghiepDAO() {
    }

    public boolean themDiemTotNghiep(DiemTotNghiep diemTN) {
        String sql = "INSERT INTO DiemTotNghiep (HS_ID, TenMon, Diem, KetQua) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, diemTN.getHsId());
            pstmt.setString(2, diemTN.getTenMon());
            pstmt.setDouble(3, diemTN.getDiem());
            pstmt.setString(4, diemTN.getKetQua());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi thêm điểm tốt nghiệp: " + e.getMessage());
        }
        return false;
    }
    

    public boolean suaDiemTotNghiep(DiemTotNghiep diemTN) {
        String sql = "UPDATE DiemTotNghiep SET Diem = ?, KetQua = ? WHERE HS_ID = ? AND TenMon = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, diemTN.getDiem());
            pstmt.setString(2, diemTN.getKetQua());
            pstmt.setInt(3, diemTN.getHsId());
            pstmt.setString(4, diemTN.getTenMon());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi sửa điểm tốt nghiệp: " + e.getMessage());
        }
        return false;
    }

    public boolean xoaDiemTotNghiep(int hsId, String tenMon) {
        String sql = "DELETE FROM DiemTotNghiep WHERE HS_ID = ? AND TenMon = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, hsId);
            pstmt.setString(2, tenMon);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi xóa điểm tốt nghiệp: " + e.getMessage());
        }
        return false;
    }

    public DiemTotNghiep getDiemTotNghiep(int hsId, String tenMon) {
        String sql = "SELECT * FROM DiemTotNghiep WHERE HS_ID = ? AND TenMon = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, hsId);
            pstmt.setString(2, tenMon);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDiemTotNghiep(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi lấy điểm tốt nghiệp: " + e.getMessage());
        }
        return null;
    }
    
    public List<DiemTotNghiep> getDiemTotNghiepBySinhVien(int hsId) {
        List<DiemTotNghiep> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM DiemTotNghiep WHERE HS_ID = ? ORDER BY TenMon";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, hsId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                danhSach.add(mapResultSetToDiemTotNghiep(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi lấy điểm tốt nghiệp theo sinh viên: " + e.getMessage());
        }
        return danhSach;
    }
    

    public List<DiemTotNghiep> getDiemTotNghiepByMon(String tenMon) {
        List<DiemTotNghiep> danhSach = new ArrayList<>();
        String sql = "SELECT dt.*, sv.TenSinhVien FROM DiemTotNghiep dt " +
                     "JOIN SinhVien sv ON dt.HS_ID = sv.HS_ID " +
                     "WHERE dt.TenMon = ? ORDER BY sv.TenSinhVien";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tenMon);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                danhSach.add(mapResultSetToDiemTotNghiep(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi lấy điểm tốt nghiệp theo môn: " + e.getMessage());
        }
        return danhSach;
    }
    
    public ResultSet thongKeDauRotTheoMon(String tenMon) {
        String sql = "SELECT KetQua, COUNT(*) as SoLuong FROM DiemTotNghiep " +
                     "WHERE TenMon = ? GROUP BY KetQua";
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tenMon);
            return pstmt.executeQuery();
            
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê đậu/rớt theo môn: " + e.getMessage());
        }
        return null;
    }
    

    public List<Integer> getSinhVienDatTotNghiep() {
        List<Integer> danhSach = new ArrayList<>();
        String sql = "SELECT HS_ID FROM DiemTotNghiep " +
                     "WHERE TenMon IN ('Toán', 'Văn') AND Diem >= 5 " +
                     "GROUP BY HS_ID HAVING COUNT(*) = 2";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                danhSach.add(rs.getInt("HS_ID"));
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi lấy sinh viên đạt tốt nghiệp: " + e.getMessage());
        }
        return danhSach;
    }

    private DiemTotNghiep mapResultSetToDiemTotNghiep(ResultSet rs) throws SQLException {
        DiemTotNghiep diemTN = new DiemTotNghiep();
        diemTN.setHsId(rs.getInt("HS_ID"));
        diemTN.setTenMon(rs.getString("TenMon"));
        diemTN.setDiem(rs.getDouble("Diem"));
        diemTN.setKetQua(rs.getString("KetQua"));
        return diemTN;
    }
}