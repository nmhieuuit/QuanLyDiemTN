package model;

import java.util.Date;

/**
 * Model class cho Điểm học kỳ
 */
public class Diem {
    private int hsId;
    private String maLop;
    private String tenMon;
    private Date ngayThi;
    private double diemToan;
    private double diemVan;
    private String hocKy; // HK1, HK2
    private String namHoc;
    private int userCreated;
    private Date createdAt;
    
    // Constructor mặc định
    public Diem() {}
    
    // Constructor với tham số
    public Diem(int hsId, String maLop, String tenMon, String hocKy, String namHoc) {
        this.hsId = hsId;
        this.maLop = maLop;
        this.tenMon = tenMon;
        this.hocKy = hocKy;
        this.namHoc = namHoc;
        this.createdAt = new Date();
    }
    
    public Diem(int hsId, String maLop, String tenMon, double diemToan, 
               double diemVan, String hocKy, String namHoc) {
        this.hsId = hsId;
        this.maLop = maLop;
        this.tenMon = tenMon;
        this.diemToan = diemToan;
        this.diemVan = diemVan;
        this.hocKy = hocKy;
        this.namHoc = namHoc;
        this.createdAt = new Date();
    }
    
    // Getter và Setter methods
    public int getHsId() {
        return hsId;
    }
    
    public void setHsId(int hsId) {
        this.hsId = hsId;
    }
    
    public String getMaLop() {
        return maLop;
    }
    
    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }
    
    public String getTenMon() {
        return tenMon;
    }
    
    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }
    
    public Date getNgayThi() {
        return ngayThi;
    }
    
    public void setNgayThi(Date ngayThi) {
        this.ngayThi = ngayThi;
    }
    
    public double getDiemToan() {
        return diemToan;
    }
    
    public void setDiemToan(double diemToan) {
        this.diemToan = diemToan;
    }
    
    public double getDiemVan() {
        return diemVan;
    }
    
    public void setDiemVan(double diemVan) {
        this.diemVan = diemVan;
    }
    
    public String getHocKy() {
        return hocKy;
    }
    
    public void setHocKy(String hocKy) {
        this.hocKy = hocKy;
    }
    
    public String getNamHoc() {
        return namHoc;
    }
    
    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }
    
    public int getUserCreated() {
        return userCreated;
    }
    
    public void setUserCreated(int userCreated) {
        this.userCreated = userCreated;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Tính điểm trung bình của môn học
     */
    public double getDiemTrungBinh() {
        return (diemToan + diemVan) / 2.0;
    }
    
    /**
     * Kiểm tra điểm có đạt yêu cầu không (>= 5.0)
     */
    public boolean isDat() {
        return getDiemTrungBinh() >= 5.0;
    }
    
    @Override
    public String toString() {
        return tenMon + " - " + hocKy + " (" + namHoc + "): " + getDiemTrungBinh();
    }
}