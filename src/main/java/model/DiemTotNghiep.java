package model;

public class DiemTotNghiep {
    private int hsId;
    private String tenMon;
    private double diem;
    private String ketQua; 
    
    public DiemTotNghiep() {}

    public DiemTotNghiep(int hsId, String tenMon, double diem) {
        this.hsId = hsId;
        this.tenMon = tenMon;
        this.diem = diem;
        this.ketQua = diem >= 5.0 ? "Đậu" : "Rớt";
    }
    
    public DiemTotNghiep(int hsId, String tenMon, double diem, String ketQua) {
        this.hsId = hsId;
        this.tenMon = tenMon;
        this.diem = diem;
        this.ketQua = ketQua;
    }
    
    public int getHsId() {
        return hsId;
    }
    
    public void setHsId(int hsId) {
        this.hsId = hsId;
    }
    
    public String getTenMon() {
        return tenMon;
    }
    
    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }
    
    public double getDiem() {
        return diem;
    }
    
    public void setDiem(double diem) {
        this.diem = diem;
        // Tự động cập nhật kết quả khi thay đổi điểm
        this.ketQua = diem >= 5.0 ? "Đậu" : "Rớt";
    }
    
    public String getKetQua() {
        return ketQua;
    }
    
    public void setKetQua(String ketQua) {
        this.ketQua = ketQua;
    }
    
    public boolean isDau() {
        return diem >= 5.0 && "Đậu".equals(ketQua);
    }
    
    public boolean isMonBatBuoc() {
        return "Toán".equals(tenMon) || "Văn".equals(tenMon) || 
               "TOAN".equals(tenMon) || "VAN".equals(tenMon);
    }
    
    @Override
    public String toString() {
        return tenMon + ": " + diem + " (" + ketQua + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DiemTotNghiep that = (DiemTotNghiep) obj;
        return hsId == that.hsId && tenMon.equals(that.tenMon);
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(hsId) + tenMon.hashCode();
    }
}