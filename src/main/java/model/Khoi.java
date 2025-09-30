package model;

import java.time.LocalDateTime;

/**
 * Model class cho Khối học - khớp với bảng DM_Khoi trong schema
 */
public class Khoi {
    private int id;
    private String tenKhoi; // '10', '11', '12'
    private String moTa;
    private LocalDateTime createdAt;
    
    // Constructor mặc định
    public Khoi() {}
    
    // Constructor với tham số
    public Khoi(String tenKhoi, String moTa) {
        this.tenKhoi = tenKhoi;
        this.moTa = moTa;
    }
    
    public Khoi(int id, String tenKhoi, String moTa) {
        this.id = id;
        this.tenKhoi = tenKhoi;
        this.moTa = moTa;
    }
    
    // Getter và Setter methods
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTenKhoi() {
        return tenKhoi;
    }
    
    public void setTenKhoi(String tenKhoi) {
        this.tenKhoi = tenKhoi;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Lấy số khối dưới dạng integer
     */
    public int getKhoiNumber() {
        try {
            return Integer.parseInt(tenKhoi);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return moTa != null ? moTa : "Khối " + tenKhoi;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Khoi khoi = (Khoi) obj;
        return id == khoi.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}