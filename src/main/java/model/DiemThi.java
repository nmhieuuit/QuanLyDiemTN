package model;

import java.time.LocalDateTime;

public class DiemThi {
    private int hsId;
    private double diemToan;
    private double diemVan;
    private int tuChon1MH;
    private double tuChon1Diem;
    private int tuChon2MH;
    private double tuChon2Diem;
    private double diemUT;
    private double tongDiemThi;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public DiemThi() {}
    
    public DiemThi(int hsId) {
        this.hsId = hsId;
    }
    
    public int getHsId() { return hsId; }
    public void setHsId(int hsId) { this.hsId = hsId; }
    
    public double getDiemToan() { return diemToan; }
    public void setDiemToan(double diemToan) { this.diemToan = diemToan; }
    
    public double getDiemVan() { return diemVan; }
    public void setDiemVan(double diemVan) { this.diemVan = diemVan; }
    
    public int getTuChon1MH() { return tuChon1MH; }
    public void setTuChon1MH(int tuChon1MH) { this.tuChon1MH = tuChon1MH; }
    
    public double getTuChon1Diem() { return tuChon1Diem; }
    public void setTuChon1Diem(double tuChon1Diem) { this.tuChon1Diem = tuChon1Diem; }
    
    public int getTuChon2MH() { return tuChon2MH; }
    public void setTuChon2MH(int tuChon2MH) { this.tuChon2MH = tuChon2MH; }
    
    public double getTuChon2Diem() { return tuChon2Diem; }
    public void setTuChon2Diem(double tuChon2Diem) { this.tuChon2Diem = tuChon2Diem; }
    
    public double getDiemUT() { return diemUT; }
    public void setDiemUT(double diemUT) { this.diemUT = diemUT; }
    
    public double getTongDiemThi() { return tongDiemThi; }
    public void setTongDiemThi(double tongDiemThi) { this.tongDiemThi = tongDiemThi; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public double tinhTongDiemThi() {
        return (diemToan + diemVan + tuChon1Diem + tuChon2Diem) / 4.0;
    }
}
