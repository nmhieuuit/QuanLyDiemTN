package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConnection;
import model.Khoi;

public class KhoiDAO {
    
    public List<Khoi> getAllKhoi() {
        List<Khoi> list = new ArrayList<>();
        String sql = "SELECT * FROM DM_Khoi ORDER BY TenKhoi";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Khoi khoi = new Khoi();
                khoi.setId(rs.getInt("ID"));
                khoi.setTenKhoi(rs.getString("TenKhoi"));
                khoi.setMoTa(rs.getString("MoTa"));
                list.add(khoi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public Khoi getKhoiById(int id) {
        String sql = "SELECT * FROM DM_Khoi WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Khoi khoi = new Khoi();
                khoi.setId(rs.getInt("ID"));
                khoi.setTenKhoi(rs.getString("TenKhoi"));
                khoi.setMoTa(rs.getString("MoTa"));
                return khoi;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Khoi getKhoiByTenKhoi(String tenKhoi) {
        String sql = "SELECT * FROM DM_Khoi WHERE TenKhoi = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tenKhoi);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Khoi khoi = new Khoi();
                khoi.setId(rs.getInt("ID"));
                khoi.setTenKhoi(rs.getString("TenKhoi"));
                khoi.setMoTa(rs.getString("MoTa"));
                return khoi;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean insertKhoi(Khoi khoi) {
        String sql = "INSERT INTO DM_Khoi (TenKhoi, MoTa) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, khoi.getTenKhoi());
            stmt.setString(2, khoi.getMoTa());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateKhoi(Khoi khoi) {
        String sql = "UPDATE DM_Khoi SET TenKhoi = ?, MoTa = ? WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, khoi.getTenKhoi());
            stmt.setString(2, khoi.getMoTa());
            stmt.setInt(3, khoi.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteKhoi(int id) {
        String sql = "DELETE FROM DM_Khoi WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}