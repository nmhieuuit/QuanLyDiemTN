package model;

import java.time.LocalDateTime;

/**
 * Model class cho Môn học - khớp với bảng MonHoc trong schema
 */
public class MonHoc {
    private int id;
    private String tenMon;
    private String loaiMon; // 'BatBuoc' hoặc 'TuChon'
    private LocalDateTime createdAt;
    
    // Constructor mặc định
    public MonHoc() {}
    
    // Constructor với tham số
    public MonHoc(String tenMon, String loaiMon) {
        this.tenMon = tenMon;
        this.loaiMon = loaiMon;
    }
    
    public MonHoc(int id, String tenMon, String loaiMon) {
        this.id = id;
        this.tenMon = tenMon;
        this.loaiMon = loaiMon;
    }
    
    // Getter và Setter methods
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTenMon() {
        return tenMon;
    }
    
    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }
    
    public String getLoaiMon() {
        return loaiMon;
    }
    
    public void setLoaiMon(String loaiMon) {
        this.loaiMon = loaiMon;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Kiểm tra môn học bắt buộc
    public boolean isBatBuoc() {
        return "BatBuoc".equals(loaiMon);
    }
    
    // Kiểm tra môn học tự chọn
    public boolean isTuChon() {
        return "TuChon".equals(loaiMon);
    }
    
    @Override
    public String toString() {
        return tenMon;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MonHoc monHoc = (MonHoc) obj;
        return id == monHoc.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}