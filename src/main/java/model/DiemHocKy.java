package model;

import java.time.LocalDateTime;

public class DiemHocKy {
    private int hsId;
    private Double hk1_10;
    private Double hk2_10;
    private Double hk1_11;
    private Double hk2_11;
    private Double hk1_12;
    private Double hk2_12;
    private Double diemTBCacNam;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public DiemHocKy() {}
    
    public DiemHocKy(int hsId) {
        this.hsId = hsId;
    }
    
    public int getHsId() { return hsId; }
    public void setHsId(int hsId) { this.hsId = hsId; }
    
    public Double getHk1_10() { return hk1_10; }
    public void setHk1_10(Double hk1_10) { this.hk1_10 = hk1_10; }
    
    public Double getHk2_10() { return hk2_10; }
    public void setHk2_10(Double hk2_10) { this.hk2_10 = hk2_10; }
    
    public Double getHk1_11() { return hk1_11; }
    public void setHk1_11(Double hk1_11) { this.hk1_11 = hk1_11; }
    
    public Double getHk2_11() { return hk2_11; }
    public void setHk2_11(Double hk2_11) { this.hk2_11 = hk2_11; }
    
    public Double getHk1_12() { return hk1_12; }
    public void setHk1_12(Double hk1_12) { this.hk1_12 = hk1_12; }
    
    public Double getHk2_12() { return hk2_12; }
    public void setHk2_12(Double hk2_12) { this.hk2_12 = hk2_12; }
    
    public Double getDiemTBCacNam() { return diemTBCacNam; }
    public void setDiemTBCacNam(Double diemTBCacNam) { this.diemTBCacNam = diemTBCacNam; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Tính điểm trung bình các năm theo công thức
    public double tinhDiemTBCacNam() {
        double tb10 = 0, tb11 = 0, tb12 = 0;
        
        if (hk1_10 != null && hk2_10 != null) {
            tb10 = (hk1_10 + hk2_10 * 2) / 3.0;
        }
        if (hk1_11 != null && hk2_11 != null) {
            tb11 = (hk1_11 + hk2_11 * 2) / 3.0;
        }
        if (hk1_12 != null && hk2_12 != null) {
            tb12 = (hk1_12 + hk2_12 * 2) / 3.0;
        }
        
        return (tb10 + tb11 * 2 + tb12 * 3) / 6.0;
    }
    
    public boolean hasValidGrades() {
        return (hk1_10 != null || hk2_10 != null || 
                hk1_11 != null || hk2_11 != null ||
                hk1_12 != null || hk2_12 != null);
    }
    
    public String formatDiem(Double diem) {
        return diem != null ? String.format("%.2f", diem) : "";
    }
}
