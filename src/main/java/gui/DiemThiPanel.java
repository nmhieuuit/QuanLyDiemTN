package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import dao.DiemDAO;
import dao.HocSinhDAO;
import dao.KhoiDAO;
import model.DiemThi;
import model.HocSinh;
import model.Khoi;
import model.MonHoc;
import util.ExcelExporter;

public class DiemThiPanel extends JPanel {
    private JComboBox<HocSinh> cboHocSinh;
    private JComboBox<Khoi> cboKhoi;
    private JTextField txtDiemToan, txtDiemVan, txtTuChon1Diem, txtTuChon2Diem, txtDiemUT;
    private JComboBox<MonHoc> cboTuChon1, cboTuChon2;
    private JTextField txtTongDiemThi, txtDiemXetTN;
    private JTable tableDiemThi;
    private DefaultTableModel tableModel;
    private JButton btnLuu, btnXoa, btnTimKiem, btnCapNhat, btnTinhDiem, btnReset, btnExportExcel;
    private JTextField txtTimKiem;
    
    private HocSinhDAO hocSinhDAO;
    private DiemDAO diemDAO;
    private KhoiDAO khoiDAO;
    private DecimalFormat df = new DecimalFormat("#.##");
    
    public DiemThiPanel() {
        hocSinhDAO = new HocSinhDAO();
        diemDAO = new DiemDAO();
        khoiDAO = new KhoiDAO();
        initComponents();
        setupLayout();
        setupEvents();
        loadData();
    }
    
    private void initComponents() {
        cboKhoi = new JComboBox<>();
        cboKhoi.setPreferredSize(new Dimension(120, 25));
        loadKhoiData();
        
        cboHocSinh = new JComboBox<>();
        cboHocSinh.setPreferredSize(new Dimension(200, 25));
        
        txtDiemToan = new JTextField(8);
        txtDiemVan = new JTextField(8);
        txtTuChon1Diem = new JTextField(8);
        txtTuChon2Diem = new JTextField(8);
        txtDiemUT = new JTextField(8);
        txtDiemUT.setText("0"); 
        
        txtTongDiemThi = new JTextField(8);
        txtTongDiemThi.setEditable(false);
        txtTongDiemThi.setBackground(Color.LIGHT_GRAY);
        
        txtDiemXetTN = new JTextField(10);
        txtDiemXetTN.setEditable(false);
        txtDiemXetTN.setBackground(Color.LIGHT_GRAY);
        
        cboTuChon1 = new JComboBox<>();
        cboTuChon2 = new JComboBox<>();
        
        txtTimKiem = new JTextField(15);
        
        btnLuu = new JButton("Lưu điểm");
        btnXoa = new JButton("Xóa điểm");
        btnReset = new JButton("Reset");
        btnTimKiem = new JButton("Tìm kiếm");
        btnCapNhat = new JButton("Cập nhật");
        btnTinhDiem = new JButton("Tính điểm xét TN");
        btnExportExcel = new JButton("Xuất Excel");
        
        String[] columns = {"Mã HS", "Họ tên", "Toán", "Văn", "Môn Tự chọn 1", "Điểm TC1", "Môn Tự chọn 2", "Điểm TC2", "Điểm ưu tiên", "Tổng điểm thi", "Điểm xét TN", "Kết quả"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableDiemThi = new JTable(tableModel);
        tableDiemThi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Nhập điểm thi tốt nghiệp"));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Khối:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cboKhoi, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Học sinh:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 2; 
        formPanel.add(cboHocSinh, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Toán:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDiemToan, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Văn:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtDiemVan, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tự chọn 1:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cboTuChon1, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Điểm:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtTuChon1Diem, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Tự chọn 2:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cboTuChon2, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Điểm:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtTuChon2Diem, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Điểm ưu tiên:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDiemUT, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("(≤ 2.0)"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Tổng điểm thi:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTongDiemThi, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Điểm xét TN:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtDiemXetTN, gbc);
        
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnTinhDiem);
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnExportExcel);
        
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Danh sách điểm thi tốt nghiệp"));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm học sinh:"));
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(tableDiemThi), BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        cboKhoi.addActionListener(e -> filterHocSinhByKhoi());
        cboHocSinh.addActionListener(e -> loadDiemThi());
        
        btnLuu.addActionListener(e -> luuDiemThi());
        btnXoa.addActionListener(e -> xoaDiemThi());
        btnReset.addActionListener(e -> clearDiemThiForm());
        btnTimKiem.addActionListener(e -> timKiemHocSinh());
        btnCapNhat.addActionListener(e -> loadData());
        btnTinhDiem.addActionListener(e -> tinhDiemXetTN());
        btnExportExcel.addActionListener(e -> exportToExcel());
        
        txtTimKiem.addActionListener(e -> timKiemHocSinh());
        
        JTextField[] diemFields = {txtDiemToan, txtDiemVan, txtTuChon1Diem, txtTuChon2Diem, txtDiemUT};
        for (JTextField field : diemFields) {
            field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { tinhTongDiem(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { tinhTongDiem(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { tinhTongDiem(); }
            });
        }
        
        tableDiemThi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectHocSinhFromTable();
            }
        });
    }
    
    private void loadData() {
        loadGrade12Students();
        
        List<MonHoc> monHocTuChon = diemDAO.getMonHocTuChon();
        cboTuChon1.removeAllItems();
        cboTuChon2.removeAllItems();
        
        cboTuChon1.addItem(null);
        cboTuChon2.addItem(null);
        
        for (MonHoc mh : monHocTuChon) {
            cboTuChon1.addItem(mh);
            cboTuChon2.addItem(mh);
        }
        
        DefaultListCellRenderer monHocRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Chọn môn --");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        };
        
        cboTuChon1.setRenderer(monHocRenderer);
        cboTuChon2.setRenderer(monHocRenderer);
        cboTuChon1.setSelectedIndex(0);
        cboTuChon2.setSelectedIndex(0);
        
        loadTableData();
    }
    
    private void loadGrade12Students() {
        cboHocSinh.removeAllItems();
        cboHocSinh.addItem(null); 
        
        try {
            List<HocSinh> allHocSinh = hocSinhDAO.getAllHocSinh();
            
            for (HocSinh hs : allHocSinh) {
                if (hs.getTenLop() != null && hs.getTenLop().contains("12")) {
                    cboHocSinh.addItem(hs);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải học sinh lớp 12: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        cboHocSinh.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Chọn học sinh --");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
        
        cboHocSinh.setSelectedIndex(0); 
    }
    
    private void loadTableData() {
        tableModel.setRowCount(0);
        
        try {
            List<HocSinh> allHocSinh = hocSinhDAO.getAllHocSinh();
            List<HocSinh> listHocSinh = new ArrayList<>();
            
            for (HocSinh hs : allHocSinh) {
                if (hs.getTenLop() != null && hs.getTenLop().contains("12")) {
                    listHocSinh.add(hs);
                }
            }
            
            List<MonHoc> allMonHoc = diemDAO.getAllMonHoc();
            
            for (HocSinh hs : listHocSinh) {
                DiemThi diemThi = diemDAO.getDiemThi(hs.getId());
                
                if (diemThi != null) {
                    String tenTC1 = "", tenTC2 = "";
                    for (MonHoc mh : allMonHoc) {
                        if (mh.getId() == diemThi.getTuChon1MH()) tenTC1 = mh.getTenMon();
                        if (mh.getId() == diemThi.getTuChon2MH()) tenTC2 = mh.getTenMon();
                    }
                    
                    double diemXetTN = tinhDiemXetTotNghiep(hs.getId(), diemThi);
                    String ketQua = (diemThi.getDiemToan() >= 5 && diemThi.getDiemVan() >= 5 && diemXetTN >= 5) ? "Đạt" : "Không đạt";
                    
                    Object[] row = {
                        hs.getId(),
                        hs.getHoTen(),
                        df.format(diemThi.getDiemToan()),
                        df.format(diemThi.getDiemVan()),
                        tenTC1,
                        df.format(diemThi.getTuChon1Diem()),
                        tenTC2,
                        df.format(diemThi.getTuChon2Diem()),
                        df.format(diemThi.getDiemUT()),
                        df.format(diemThi.getTongDiemThi()),
                        df.format(diemXetTN),
                        ketQua
                    };
                    tableModel.addRow(row);
                } else {
                    Object[] row = {hs.getId(), hs.getHoTen(), "", "", "", "", "", "", "", "", "", ""};
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu bảng: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadDiemThi() {
        HocSinh selectedHS = (HocSinh) cboHocSinh.getSelectedItem();
        if (selectedHS != null) {
            DiemThi diemThi = diemDAO.getDiemThi(selectedHS.getId());
            
            if (diemThi != null) {
                txtDiemToan.setText(String.valueOf(diemThi.getDiemToan()));
                txtDiemVan.setText(String.valueOf(diemThi.getDiemVan()));
                txtTuChon1Diem.setText(String.valueOf(diemThi.getTuChon1Diem()));
                txtTuChon2Diem.setText(String.valueOf(diemThi.getTuChon2Diem()));
                txtDiemUT.setText(String.valueOf(diemThi.getDiemUT()));
                
                for (int i = 0; i < cboTuChon1.getItemCount(); i++) {
                    MonHoc mh = cboTuChon1.getItemAt(i);
                    if (mh != null && mh.getId() == diemThi.getTuChon1MH()) {
                        cboTuChon1.setSelectedIndex(i);
                        break;
                    }
                }
                for (int i = 0; i < cboTuChon2.getItemCount(); i++) {
                    MonHoc mh = cboTuChon2.getItemAt(i);
                    if (mh != null && mh.getId() == diemThi.getTuChon2MH()) {
                        cboTuChon2.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                clearForm();
            }
            tinhTongDiem();
            tinhDiemXetTN();
        }
    }
    
    private void luuDiemThi() {
        HocSinh selectedHS = (HocSinh) cboHocSinh.getSelectedItem();
        if (selectedHS == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học sinh!");
            return;
        }
        
        if (!validateInput()) return;
        
        try {
            DiemThi diemThi = new DiemThi();
            diemThi.setHsId(selectedHS.getId());
            diemThi.setDiemToan(Double.parseDouble(txtDiemToan.getText().trim()));
            diemThi.setDiemVan(Double.parseDouble(txtDiemVan.getText().trim()));
            diemThi.setTuChon1MH(((MonHoc) cboTuChon1.getSelectedItem()).getId());
            diemThi.setTuChon1Diem(Double.parseDouble(txtTuChon1Diem.getText().trim()));
            diemThi.setTuChon2MH(((MonHoc) cboTuChon2.getSelectedItem()).getId());
            diemThi.setTuChon2Diem(Double.parseDouble(txtTuChon2Diem.getText().trim()));
            
            // Điểm ưu tiên có thể để trống, mặc định là 0
            double diemUT = txtDiemUT.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtDiemUT.getText().trim());
            diemThi.setDiemUT(diemUT);
            
            if (diemDAO.insertOrUpdateDiemThi(diemThi)) {
                JOptionPane.showMessageDialog(this, "Lưu điểm thi thành công!");
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu điểm thi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập điểm hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaDiemThi() {
        HocSinh selectedHS = (HocSinh) cboHocSinh.getSelectedItem();
        if (selectedHS == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học sinh!");
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa điểm thi của học sinh này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            clearForm();
            loadTableData();
        }
    }
    
    private void timKiemHocSinh() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            loadTableData();
            return;
        }
        
        tableModel.setRowCount(0);
        
        try {
            List<HocSinh> searchResults = hocSinhDAO.searchHocSinh(keyword);
            
            // Lọc chỉ học sinh lớp 12
            List<HocSinh> grade12Results = new ArrayList<>();
            for (HocSinh hs : searchResults) {
                if (hs.getTenLop() != null && hs.getTenLop().contains("12")) {
                    grade12Results.add(hs);
                }
            }
            
            List<MonHoc> allMonHoc = diemDAO.getAllMonHoc();
            
            for (HocSinh hs : grade12Results) {
                DiemThi diemThi = diemDAO.getDiemThi(hs.getId());
                
                if (diemThi != null) {
                    String tenTC1 = "", tenTC2 = "";
                    for (MonHoc mh : allMonHoc) {
                        if (mh.getId() == diemThi.getTuChon1MH()) tenTC1 = mh.getTenMon();
                        if (mh.getId() == diemThi.getTuChon2MH()) tenTC2 = mh.getTenMon();
                    }
                    
                    double diemXetTN = tinhDiemXetTotNghiep(hs.getId(), diemThi);
                    String ketQua = (diemThi.getDiemToan() >= 5 && diemThi.getDiemVan() >= 5 && diemXetTN >= 5) ? "Đạt" : "Không đạt";
                    
                    Object[] row = {
                        hs.getId(),
                        hs.getHoTen(),
                        df.format(diemThi.getDiemToan()),
                        df.format(diemThi.getDiemVan()),
                        tenTC1,
                        df.format(diemThi.getTuChon1Diem()),
                        tenTC2,
                        df.format(diemThi.getTuChon2Diem()),
                        df.format(diemThi.getDiemUT()),
                        df.format(diemThi.getTongDiemThi()),
                        df.format(diemXetTN),
                        ketQua
                    };
                    tableModel.addRow(row);
                } else {
                    Object[] row = {hs.getId(), hs.getHoTen(), "", "", "", "", "", "", "", "", "", ""};
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tìm kiếm: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void selectHocSinhFromTable() {
        int selectedRow = tableDiemThi.getSelectedRow();
        if (selectedRow >= 0) {
            int hsId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            for (int i = 0; i < cboHocSinh.getItemCount(); i++) {
                HocSinh hs = cboHocSinh.getItemAt(i);
                if (hs != null && hs.getId() == hsId) {
                    cboHocSinh.setSelectedIndex(i);
                    loadDiemThi();
                    break;
                }
            }
        }
    }
    
    private void tinhTongDiem() {
        try {
            double toan = txtDiemToan.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtDiemToan.getText().trim());
            double van = txtDiemVan.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtDiemVan.getText().trim());
            double tc1 = txtTuChon1Diem.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtTuChon1Diem.getText().trim());
            double tc2 = txtTuChon2Diem.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtTuChon2Diem.getText().trim());
            
            double tongDiem = (toan + van + tc1 + tc2) / 4.0;
            txtTongDiemThi.setText(df.format(tongDiem));
        } catch (NumberFormatException e) {
            txtTongDiemThi.setText("");
        }
    }
    
    private void tinhDiemXetTN() {
        HocSinh selectedHS = (HocSinh) cboHocSinh.getSelectedItem();
        if (selectedHS != null) {
            try {
                double tongDiemThi = txtTongDiemThi.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtTongDiemThi.getText().trim());
                double diemUT = txtDiemUT.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtDiemUT.getText().trim());
                
                var diemHocKy = diemDAO.getDiemHocKy(selectedHS.getId());
                double diemTBCacNam = 0;
                if (diemHocKy != null && diemHocKy.getDiemTBCacNam() != null) {
                    diemTBCacNam = diemHocKy.getDiemTBCacNam();
                }
                
                // Công thức: ((Tổng điểm thi + Điểm TB các năm)/2) + Điểm ưu tiên
                double diemXetTN = ((tongDiemThi + diemTBCacNam) / 2.0) + diemUT;
                txtDiemXetTN.setText(df.format(diemXetTN));
            } catch (NumberFormatException e) {
                txtDiemXetTN.setText("");
            }
        }
    }
    
    private double tinhDiemXetTotNghiep(int hsId, DiemThi diemThi) {
        var diemHocKy = diemDAO.getDiemHocKy(hsId);
        double diemTBCacNam = 0;
        if (diemHocKy != null && diemHocKy.getDiemTBCacNam() != null) {
            diemTBCacNam = diemHocKy.getDiemTBCacNam();
        }
        
        return ((diemThi.getTongDiemThi() + diemTBCacNam) / 2.0) + diemThi.getDiemUT();
    }
    
    private boolean validateInput() {
        try {
            String toanText = txtDiemToan.getText().trim();
            if (toanText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập điểm Toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtDiemToan.requestFocus();
                return false;
            }
            double toan = Double.parseDouble(toanText);
            if (toan < 0 || toan > 10) {
                JOptionPane.showMessageDialog(this, "Điểm Toán phải từ 0 đến 10!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtDiemToan.requestFocus();
                return false;
            }
            
            String vanText = txtDiemVan.getText().trim();
            if (vanText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập điểm Văn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtDiemVan.requestFocus();
                return false;
            }
            double van = Double.parseDouble(vanText);
            if (van < 0 || van > 10) {
                JOptionPane.showMessageDialog(this, "Điểm Văn phải từ 0 đến 10!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtDiemVan.requestFocus();
                return false;
            }
            
            String tc1Text = txtTuChon1Diem.getText().trim();
            if (tc1Text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập điểm Tự chọn 1!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtTuChon1Diem.requestFocus();
                return false;
            }
            double tc1 = Double.parseDouble(tc1Text);
            if (tc1 < 0 || tc1 > 10) {
                JOptionPane.showMessageDialog(this, "Điểm Tự chọn 1 phải từ 0 đến 10!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtTuChon1Diem.requestFocus();
                return false;
            }
            
            String tc2Text = txtTuChon2Diem.getText().trim();
            if (tc2Text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập điểm Tự chọn 2!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtTuChon2Diem.requestFocus();
                return false;
            }
            double tc2 = Double.parseDouble(tc2Text);
            if (tc2 < 0 || tc2 > 10) {
                JOptionPane.showMessageDialog(this, "Điểm Tự chọn 2 phải từ 0 đến 10!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtTuChon2Diem.requestFocus();
                return false;
            }
            
            String utText = txtDiemUT.getText().trim();
            if (!utText.isEmpty()) {
                double ut = Double.parseDouble(utText);
                if (ut < 0 || ut > 2) {
                    JOptionPane.showMessageDialog(this, "Điểm ưu tiên phải từ 0 đến 2!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    txtDiemUT.requestFocus();
                    return false;
                }
            }
            
            if (cboTuChon1.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn môn tự chọn 1!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                cboTuChon1.requestFocus();
                return false;
            }
            
            if (cboTuChon2.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn môn tự chọn 2!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                cboTuChon2.requestFocus();
                return false;
            }
            
            if (((MonHoc) cboTuChon1.getSelectedItem()).getId() == ((MonHoc) cboTuChon2.getSelectedItem()).getId()) {
                JOptionPane.showMessageDialog(this, "Hai môn tự chọn phải khác nhau!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                cboTuChon1.requestFocus();
                return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập điểm hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void clearForm() {
        txtDiemToan.setText("");
        txtDiemVan.setText("");
        txtTuChon1Diem.setText("");
        txtTuChon2Diem.setText("");
        txtDiemUT.setText("");
        txtTongDiemThi.setText("");
        txtDiemXetTN.setText("");
        if (cboTuChon1.getItemCount() > 0) cboTuChon1.setSelectedIndex(0);
        if (cboTuChon2.getItemCount() > 0) cboTuChon2.setSelectedIndex(0);
    }
    
    private void loadKhoiData() {
        try {
            List<Khoi> khoiList = khoiDAO.getAllKhoi();
            cboKhoi.removeAllItems();
            
            for (Khoi khoi : khoiList) {
                if ("12".equals(khoi.getTenKhoi())) {
                    cboKhoi.addItem(khoi);
                    break; 
                }
            }
            
            cboKhoi.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Khoi khoi) {
                        setText("Khối " + khoi.getTenKhoi());
                    }
                    return this;
                }
            });
            
            if (cboKhoi.getItemCount() > 0) {
                cboKhoi.setSelectedIndex(0);
            }
            
            cboKhoi.setEnabled(false);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu khối: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterHocSinhByKhoi() {
        loadGrade12Students();
    }
    
    private void clearDiemThiForm() {
        cboHocSinh.setSelectedIndex(0);
        cboTuChon1.setSelectedIndex(0);
        cboTuChon2.setSelectedIndex(0);
        txtDiemToan.setText("");
        txtDiemVan.setText("");
        txtTuChon1Diem.setText("");
        txtTuChon2Diem.setText("");
        txtDiemUT.setText("0"); // Reset to default
        txtTongDiemThi.setText("");
        txtDiemXetTN.setText("");
    }

    private void exportToExcel() {
        ExcelExporter.exportTable(this, tableModel, "DanhSachDiemThi", "DanhSachDiemThi");
    }
}
