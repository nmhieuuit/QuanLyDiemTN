package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HocSinh {
    private int id;
    private String hoTen;
    private LocalDate ngaySinh;
    private boolean gioiTinh; // true: Nam, false: Nữ
    private String diaChi;
    private LocalDateTime createdAt;
    private String tenLop; // Tên lớp của học sinh
    
    public HocSinh() {}
    
    public HocSinh(String hoTen, LocalDate ngaySinh, boolean gioiTinh, String diaChi) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    
    public boolean isGioiTinh() { return gioiTinh; }
    public void setGioiTinh(boolean gioiTinh) { this.gioiTinh = gioiTinh; }
    
    public String getGioiTinhText() { return gioiTinh ? "Nam" : "Nữ"; }
    
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }
    
    @Override
    public String toString() {
        return id + " - " + hoTen;
    }
}
