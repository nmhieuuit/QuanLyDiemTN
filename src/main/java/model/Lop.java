package model;

import java.time.LocalDateTime;

/**
 * Model class cho Lớp học - khớp với bảng DM_Lop trong schema
 */
public class Lop {
    private int id;
    private String tenLop;
    private int namHoc;  // Sửa từ String sang int để khớp với schema
    private int khoiId;  // Thêm khoiId để liên kết với bảng DM_Khoi
    private String tenKhoi; // Để hiển thị, sẽ được load từ JOIN
    private LocalDateTime createdAt;
    
    // Constructor mặc định
    public Lop() {}
    
    // Constructor với tham số
    public Lop(String tenLop, int namHoc, int khoiId) {
        this.tenLop = tenLop;
        this.namHoc = namHoc;
        this.khoiId = khoiId;
    }
    
    public Lop(int id, String tenLop, int namHoc, int khoiId) {
        this.id = id;
        this.tenLop = tenLop;
        this.namHoc = namHoc;
        this.khoiId = khoiId;
    }
    
    // Getter và Setter methods
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTenLop() {
        return tenLop;
    }
    
    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }
    
    public int getNamHoc() {
        return namHoc;
    }
    
    public void setNamHoc(int namHoc) {
        this.namHoc = namHoc;
    }
    
    public int getKhoiId() {
        return khoiId;
    }
    
    public void setKhoiId(int khoiId) {
        this.khoiId = khoiId;
    }
    
    public String getTenKhoi() {
        return tenKhoi;
    }
    
    public void setTenKhoi(String tenKhoi) {
        this.tenKhoi = tenKhoi;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Lấy khối lớp (10, 11, 12) từ tên lớp
     */
    public int getKhoi() {
        if (tenLop != null && tenLop.length() >= 2) {
            String khoi = tenLop.substring(0, 2);
            try {
                return Integer.parseInt(khoi);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return tenLop + " (" + namHoc + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Lop lop = (Lop) obj;
        return id == lop.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}