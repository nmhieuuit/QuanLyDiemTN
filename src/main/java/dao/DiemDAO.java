package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConnection;
import model.DiemHocKy;
import model.DiemThi;
import model.MonHoc;

public class DiemDAO {
    
    public DiemHocKy getDiemHocKy(int hsId) {
        String sql = "SELECT * FROM HocSinh_DiemHocKy WHERE HS_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, hsId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                DiemHocKy diem = new DiemHocKy();
                diem.setHsId(rs.getInt("HS_ID"));
                diem.setHk1_10(rs.getObject("HK1_10", Double.class));
                diem.setHk2_10(rs.getObject("HK2_10", Double.class));
                diem.setHk1_11(rs.getObject("HK1_11", Double.class));
                diem.setHk2_11(rs.getObject("HK2_11", Double.class));
                diem.setHk1_12(rs.getObject("HK1_12", Double.class));
                diem.setHk2_12(rs.getObject("HK2_12", Double.class));
                diem.setDiemTBCacNam(rs.getObject("DiemTBCacNam", Double.class));
                return diem;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean insertOrUpdateDiemHocKy(DiemHocKy diem) {
        String checkSql = "SELECT COUNT(*) FROM HocSinh_DiemHocKy WHERE HS_ID = ?";
        String insertSql = "INSERT INTO HocSinh_DiemHocKy (HS_ID, HK1_10, HK2_10, HK1_11, HK2_11, HK1_12, HK2_12) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE HocSinh_DiemHocKy SET HK1_10=?, HK2_10=?, HK1_11=?, HK2_11=?, HK1_12=?, HK2_12=?, updated_at=GETDATE() WHERE HS_ID=?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, diem.getHsId());
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                boolean exists = rs.getInt(1) > 0;
                
                PreparedStatement stmt = conn.prepareStatement(exists ? updateSql : insertSql);
                
                if (exists) {
                    stmt.setObject(1, diem.getHk1_10());
                    stmt.setObject(2, diem.getHk2_10());
                    stmt.setObject(3, diem.getHk1_11());
                    stmt.setObject(4, diem.getHk2_11());
                    stmt.setObject(5, diem.getHk1_12());
                    stmt.setObject(6, diem.getHk2_12());
                    stmt.setInt(7, diem.getHsId());
                } else {
                    stmt.setInt(1, diem.getHsId());
                    stmt.setObject(2, diem.getHk1_10());
                    stmt.setObject(3, diem.getHk2_10());
                    stmt.setObject(4, diem.getHk1_11());
                    stmt.setObject(5, diem.getHk2_11());
                    stmt.setObject(6, diem.getHk1_12());
                    stmt.setObject(7, diem.getHk2_12());
                }
                
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public DiemThi getDiemThi(int hsId) {
        String sql = "SELECT * FROM HocSinh_DiemThi WHERE HS_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, hsId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                DiemThi diem = new DiemThi();
                diem.setHsId(rs.getInt("HS_ID"));
                diem.setDiemToan(rs.getDouble("DiemToan"));
                diem.setDiemVan(rs.getDouble("DiemVan"));
                diem.setTuChon1MH(rs.getInt("TuChon1MH"));
                diem.setTuChon1Diem(rs.getDouble("TuChon1Diem"));
                diem.setTuChon2MH(rs.getInt("TuChon2MH"));
                diem.setTuChon2Diem(rs.getDouble("TuChon2Diem"));
                diem.setDiemUT(rs.getDouble("DiemUT"));
                diem.setTongDiemThi(rs.getDouble("TongDiemThi"));
                return diem;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean insertOrUpdateDiemThi(DiemThi diem) {
        String checkSql = "SELECT COUNT(*) FROM HocSinh_DiemThi WHERE HS_ID = ?";
        String insertSql = "INSERT INTO HocSinh_DiemThi (HS_ID, DiemToan, DiemVan, TuChon1MH, TuChon1Diem, TuChon2MH, TuChon2Diem, DiemUT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE HocSinh_DiemThi SET DiemToan=?, DiemVan=?, TuChon1MH=?, TuChon1Diem=?, TuChon2MH=?, TuChon2Diem=?, DiemUT=?, updated_at=GETDATE() WHERE HS_ID=?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, diem.getHsId());
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                boolean exists = rs.getInt(1) > 0;
                
                PreparedStatement stmt = conn.prepareStatement(exists ? updateSql : insertSql);
                
                if (exists) {
                    stmt.setDouble(1, diem.getDiemToan());
                    stmt.setDouble(2, diem.getDiemVan());
                    stmt.setInt(3, diem.getTuChon1MH());
                    stmt.setDouble(4, diem.getTuChon1Diem());
                    stmt.setInt(5, diem.getTuChon2MH());
                    stmt.setDouble(6, diem.getTuChon2Diem());
                    stmt.setDouble(7, diem.getDiemUT());
                    stmt.setInt(8, diem.getHsId());
                } else {
                    stmt.setInt(1, diem.getHsId());
                    stmt.setDouble(2, diem.getDiemToan());
                    stmt.setDouble(3, diem.getDiemVan());
                    stmt.setInt(4, diem.getTuChon1MH());
                    stmt.setDouble(5, diem.getTuChon1Diem());
                    stmt.setInt(6, diem.getTuChon2MH());
                    stmt.setDouble(7, diem.getTuChon2Diem());
                    stmt.setDouble(8, diem.getDiemUT());
                }
                
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<MonHoc> getAllMonHoc() {
        List<MonHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM MonHoc ORDER BY LoaiMon, TenMon";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                MonHoc mh = new MonHoc();
                mh.setId(rs.getInt("ID"));
                mh.setTenMon(rs.getString("TenMon"));
                mh.setLoaiMon(rs.getString("LoaiMon"));
                list.add(mh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<MonHoc> getMonHocTuChon() {
        List<MonHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM MonHoc WHERE LoaiMon = 'TuChon' ORDER BY TenMon";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                MonHoc mh = new MonHoc();
                mh.setId(rs.getInt("ID"));
                mh.setTenMon(rs.getString("TenMon"));
                mh.setLoaiMon(rs.getString("LoaiMon"));
                list.add(mh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
