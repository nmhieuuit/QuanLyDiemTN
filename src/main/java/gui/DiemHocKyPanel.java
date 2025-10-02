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
import model.DiemHocKy;
import model.HocSinh;
import util.ExcelExporter;

public class DiemHocKyPanel extends JPanel {
    private JComboBox<HocSinh> cboHocSinh;
    private JTextField txtHK1_10, txtHK2_10, txtHK1_11, txtHK2_11, txtHK1_12, txtHK2_12;
    private JTextField txtDiemTB;
    private JTextField txtTBLop10, txtTBLop11, txtTBLop12; 
    private JTextField txtLopHienTai;
    private JTable tableDiem;
    private DefaultTableModel tableModel;
    private JButton btnLuu, btnXoa, btnTimKiem, btnCapNhat, btnReset, btnExportExcel;
    private JTextField txtTimKiem;
    
    private HocSinhDAO hocSinhDAO;
    private DiemDAO diemDAO;
    private DecimalFormat df = new DecimalFormat("#.##");
    
    public DiemHocKyPanel() {
        hocSinhDAO = new HocSinhDAO();
        diemDAO = new DiemDAO();
        initComponents();
        setupLayout();
        setupEvents();
        loadData();
    }
    
    private void initComponents() {
        cboHocSinh = new JComboBox<>();
        cboHocSinh.setPreferredSize(new Dimension(250, 25));
        
        txtHK1_10 = new JTextField(8);
        txtHK2_10 = new JTextField(8);
        txtHK1_11 = new JTextField(8);
        txtHK2_11 = new JTextField(8);
        txtHK1_12 = new JTextField(8);
        txtHK2_12 = new JTextField(8);
        txtDiemTB = new JTextField(8);
        txtDiemTB.setEditable(false);
        txtDiemTB.setBackground(Color.LIGHT_GRAY);
        
        txtTBLop10 = new JTextField(8);
        txtTBLop10.setEditable(false);
        txtTBLop10.setBackground(Color.LIGHT_GRAY);
        
        txtTBLop11 = new JTextField(8);
        txtTBLop11.setEditable(false);
        txtTBLop11.setBackground(Color.LIGHT_GRAY);
        
        txtTBLop12 = new JTextField(8);
        txtTBLop12.setEditable(false);
        txtTBLop12.setBackground(Color.LIGHT_GRAY);
        
        txtLopHienTai = new JTextField(15);
        txtLopHienTai.setEditable(false);
        txtLopHienTai.setBackground(Color.LIGHT_GRAY);
        
        txtTimKiem = new JTextField(15);
        
        btnLuu = new JButton("Lưu điểm");
        btnXoa = new JButton("Xóa điểm");
        btnReset = new JButton("Reset");
        btnTimKiem = new JButton("Tìm kiếm");
        btnCapNhat = new JButton("Cập nhật");
        btnExportExcel = new JButton("Xuất Excel");
        
        String[] columns = {"Mã HS", "Họ tên", "HK1-10", "HK2-10", "TB lớp 10", "HK1-11", "HK2-11", "TB lớp 11", "HK1-12", "HK2-12", "TB lớp 12", "TB các năm"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableDiem = new JTable(tableModel);
        tableDiem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Nhập điểm học kỳ"));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Học sinh:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cboHocSinh, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Lớp hiện tại:"), gbc);
        gbc.gridx = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtLopHienTai, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Lớp 10:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel("HK1:"), gbc);
        gbc.gridx = 2;
        formPanel.add(txtHK1_10, gbc);
        gbc.gridx = 3;
        gbc.insets = new Insets(5, 2, 5, 2);
        formPanel.add(new JLabel("HK2:"), gbc);
        gbc.gridx = 4;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(txtHK2_10, gbc);
        gbc.gridx = 5;
        formPanel.add(new JLabel("TB lớp 10:"), gbc);
        gbc.gridx = 6;
        formPanel.add(txtTBLop10, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Lớp 11:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel("HK1:"), gbc);
        gbc.gridx = 2;
        formPanel.add(txtHK1_11, gbc);
        gbc.gridx = 3;
        gbc.insets = new Insets(5, 2, 5, 2);
        formPanel.add(new JLabel("HK2:"), gbc);
        gbc.gridx = 4;
        gbc.insets = new Insets(5, 5, 5, 5); 
        formPanel.add(txtHK2_11, gbc);
        gbc.gridx = 5;
        formPanel.add(new JLabel("TB lớp 11:"), gbc);
        gbc.gridx = 6;
        formPanel.add(txtTBLop11, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Lớp 12:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel("HK1:"), gbc);
        gbc.gridx = 2;
        formPanel.add(txtHK1_12, gbc);
        gbc.gridx = 3;
        gbc.insets = new Insets(5, 2, 5, 2); 
        formPanel.add(new JLabel("HK2:"), gbc);
        gbc.gridx = 4;
        gbc.insets = new Insets(5, 5, 5, 5); 
        formPanel.add(txtHK2_12, gbc);
        gbc.gridx = 5;
        formPanel.add(new JLabel("TB lớp 12:"), gbc);
        gbc.gridx = 6;
        formPanel.add(txtTBLop12, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("TB các năm:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(txtDiemTB, gbc);
        
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnExportExcel);
        
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Danh sách điểm học kỳ"));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm học sinh:"));
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(tableDiem), BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        cboHocSinh.addActionListener(e -> loadDiemHocSinh());
        
        btnLuu.addActionListener(e -> luuDiem());
        btnXoa.addActionListener(e -> xoaDiem());
        btnReset.addActionListener(e -> clearForm());
        btnTimKiem.addActionListener(e -> timKiemHocSinh());
        btnCapNhat.addActionListener(e -> loadData());
        btnExportExcel.addActionListener(e -> exportToExcel());
        
        txtTimKiem.addActionListener(e -> timKiemHocSinh());
        
        JTextField[] diemFields = {txtHK1_10, txtHK2_10, txtHK1_11, txtHK2_11, txtHK1_12, txtHK2_12};
        for (JTextField field : diemFields) {
            field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { tinhDiemTB(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { tinhDiemTB(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { tinhDiemTB(); }
            });
        }
        
        tableDiem.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectHocSinhFromTable();
            }
        });
    }
    
    private void loadData() {
        List<HocSinh> listHocSinh = hocSinhDAO.getAllHocSinh();
        System.out.println("Loaded students: " + listHocSinh);
        cboHocSinh.removeAllItems();
        cboHocSinh.addItem(null);
        for (HocSinh hs : listHocSinh) {
            cboHocSinh.addItem(hs);
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
        
        loadTableData();
    }
    
    private void loadTableData() {
        tableModel.setRowCount(0);
        List<HocSinh> listHocSinh = hocSinhDAO.getAllHocSinh();
        
        for (HocSinh hs : listHocSinh) {
            DiemHocKy diem = diemDAO.getDiemHocKy(hs.getId());
            
            Double tbLop10 = null;
            Double tbLop11 = null;
            Double tbLop12 = null;
            
            if (diem != null) {
                if (diem.getHk1_10() != null && diem.getHk2_10() != null) {
                    tbLop10 = (diem.getHk1_10() + diem.getHk2_10() * 2) / 3.0;
                }
                if (diem.getHk1_11() != null && diem.getHk2_11() != null) {
                    tbLop11 = (diem.getHk1_11() + diem.getHk2_11() * 2) / 3.0;
                }
                if (diem.getHk1_12() != null && diem.getHk2_12() != null) {
                    tbLop12 = (diem.getHk1_12() + diem.getHk2_12() * 2) / 3.0;
                }
            }
            
            Object[] row = {
                hs.getId(),
                hs.getHoTen(),
                diem != null && diem.getHk1_10() != null ? df.format(diem.getHk1_10()) : "",
                diem != null && diem.getHk2_10() != null ? df.format(diem.getHk2_10()) : "",
                tbLop10 != null ? df.format(tbLop10) : "",
                diem != null && diem.getHk1_11() != null ? df.format(diem.getHk1_11()) : "",
                diem != null && diem.getHk2_11() != null ? df.format(diem.getHk2_11()) : "",
                tbLop11 != null ? df.format(tbLop11) : "",
                diem != null && diem.getHk1_12() != null ? df.format(diem.getHk1_12()) : "",
                diem != null && diem.getHk2_12() != null ? df.format(diem.getHk2_12()) : "",
                tbLop12 != null ? df.format(tbLop12) : "",
                diem != null && diem.getDiemTBCacNam() != null ? df.format(diem.getDiemTBCacNam()) : ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void loadDiemHocSinh() {
        HocSinh selectedHS = (HocSinh) cboHocSinh.getSelectedItem();
        if (selectedHS != null) {
            String lopHienTai = hocSinhDAO.getLopHienTai(selectedHS.getId());
            txtLopHienTai.setText(lopHienTai);
            
            DiemHocKy diem = diemDAO.getDiemHocKy(selectedHS.getId());
            
            if (diem != null) {
                txtHK1_10.setText(diem.getHk1_10() != null ? diem.getHk1_10().toString() : "");
                txtHK2_10.setText(diem.getHk2_10() != null ? diem.getHk2_10().toString() : "");
                txtHK1_11.setText(diem.getHk1_11() != null ? diem.getHk1_11().toString() : "");
                txtHK2_11.setText(diem.getHk2_11() != null ? diem.getHk2_11().toString() : "");
                txtHK1_12.setText(diem.getHk1_12() != null ? diem.getHk1_12().toString() : "");
                txtHK2_12.setText(diem.getHk2_12() != null ? diem.getHk2_12().toString() : "");
            } else {
                clearForm();
            }
            tinhDiemTB();
        } else {
            txtLopHienTai.setText("");
        }
    }
    
    private void luuDiem() {
        HocSinh selectedHS = (HocSinh) cboHocSinh.getSelectedItem();
        if (selectedHS == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học sinh!");
            return;
        }
        
        if (!validateDiemInput()) return;
        
        try {
            DiemHocKy diem = new DiemHocKy();
            diem.setHsId(selectedHS.getId());
            
            if (!txtHK1_10.getText().trim().isEmpty()) {
                diem.setHk1_10(Double.parseDouble(txtHK1_10.getText().trim()));
            }
            if (!txtHK2_10.getText().trim().isEmpty()) {
                diem.setHk2_10(Double.parseDouble(txtHK2_10.getText().trim()));
            }
            if (!txtHK1_11.getText().trim().isEmpty()) {
                diem.setHk1_11(Double.parseDouble(txtHK1_11.getText().trim()));
            }
            if (!txtHK2_11.getText().trim().isEmpty()) {
                diem.setHk2_11(Double.parseDouble(txtHK2_11.getText().trim()));
            }
            if (!txtHK1_12.getText().trim().isEmpty()) {
                diem.setHk1_12(Double.parseDouble(txtHK1_12.getText().trim()));
            }
            if (!txtHK2_12.getText().trim().isEmpty()) {
                diem.setHk2_12(Double.parseDouble(txtHK2_12.getText().trim()));
            }
            
            if (diemDAO.insertOrUpdateDiemHocKy(diem)) {
                JOptionPane.showMessageDialog(this, "Lưu điểm thành công!");
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu điểm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập điểm hợp lệ (0-10)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaDiem() {
        HocSinh selectedHS = (HocSinh) cboHocSinh.getSelectedItem();
        if (selectedHS == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học sinh!");
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa điểm của học sinh này?", 
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
        
        List<HocSinh> searchResults = hocSinhDAO.searchHocSinh(keyword);
        tableModel.setRowCount(0);
        
        for (HocSinh hs : searchResults) {
            DiemHocKy diem = diemDAO.getDiemHocKy(hs.getId());
            
            Double tbLop10 = null;
            Double tbLop11 = null;
            Double tbLop12 = null;
            
            if (diem != null) {
                if (diem.getHk1_10() != null && diem.getHk2_10() != null) {
                    tbLop10 = (diem.getHk1_10() + diem.getHk2_10() * 2) / 3.0;
                }
                if (diem.getHk1_11() != null && diem.getHk2_11() != null) {
                    tbLop11 = (diem.getHk1_11() + diem.getHk2_11() * 2) / 3.0;
                }
                if (diem.getHk1_12() != null && diem.getHk2_12() != null) {
                    tbLop12 = (diem.getHk1_12() + diem.getHk2_12() * 2) / 3.0;
                }
            }
            
            Object[] row = {
                hs.getId(),
                hs.getHoTen(),
                diem != null && diem.getHk1_10() != null ? df.format(diem.getHk1_10()) : "",
                diem != null && diem.getHk2_10() != null ? df.format(diem.getHk2_10()) : "",
                tbLop10 != null ? df.format(tbLop10) : "",
                diem != null && diem.getHk1_11() != null ? df.format(diem.getHk1_11()) : "",
                diem != null && diem.getHk2_11() != null ? df.format(diem.getHk2_11()) : "",
                tbLop11 != null ? df.format(tbLop11) : "",
                diem != null && diem.getHk1_12() != null ? df.format(diem.getHk1_12()) : "",
                diem != null && diem.getHk2_12() != null ? df.format(diem.getHk2_12()) : "",
                tbLop12 != null ? df.format(tbLop12) : "",
                diem != null && diem.getDiemTBCacNam() != null ? df.format(diem.getDiemTBCacNam()) : ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void selectHocSinhFromTable() {
        int selectedRow = tableDiem.getSelectedRow();
        if (selectedRow >= 0) {
            int hsId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            for (int i = 0; i < cboHocSinh.getItemCount(); i++) {
                HocSinh hs = cboHocSinh.getItemAt(i);
                if (hs != null && hs.getId() == hsId) {
                    cboHocSinh.setSelectedIndex(i);
                    loadDiemHocSinh();
                    break;
                }
            }
        }
    }
    
    private void tinhDiemTB() {
        try {
            Double hk1_10 = txtHK1_10.getText().trim().isEmpty() ? null : Double.parseDouble(txtHK1_10.getText().trim());
            Double hk2_10 = txtHK2_10.getText().trim().isEmpty() ? null : Double.parseDouble(txtHK2_10.getText().trim());
            Double hk1_11 = txtHK1_11.getText().trim().isEmpty() ? null : Double.parseDouble(txtHK1_11.getText().trim());
            Double hk2_11 = txtHK2_11.getText().trim().isEmpty() ? null : Double.parseDouble(txtHK2_11.getText().trim());
            Double hk1_12 = txtHK1_12.getText().trim().isEmpty() ? null : Double.parseDouble(txtHK1_12.getText().trim());
            Double hk2_12 = txtHK2_12.getText().trim().isEmpty() ? null : Double.parseDouble(txtHK2_12.getText().trim());
            
            double tb10 = 0, tb11 = 0, tb12 = 0;
            int count = 0;
            
            if (hk1_10 != null && hk2_10 != null) {
                tb10 = (hk1_10 + hk2_10 * 2) / 3.0;
                txtTBLop10.setText(df.format(tb10));
                count++;
            } else {
                txtTBLop10.setText("");
            }
            
            if (hk1_11 != null && hk2_11 != null) {
                tb11 = (hk1_11 + hk2_11 * 2) / 3.0;
                txtTBLop11.setText(df.format(tb11));
                count++;
            } else {
                txtTBLop11.setText("");
            }
            
            if (hk1_12 != null && hk2_12 != null) {
                tb12 = (hk1_12 + hk2_12 * 2) / 3.0;
                txtTBLop12.setText(df.format(tb12));
                count++;
            } else {
                txtTBLop12.setText("");
            }
            
            if (count > 0) {
                double tbCacNam = (tb10 + tb11 * 2 + tb12 * 3) / 6.0;
                txtDiemTB.setText(df.format(tbCacNam));
            } else {
                txtDiemTB.setText("");
            }
        } catch (NumberFormatException e) {
            txtDiemTB.setText("");
            txtTBLop10.setText("");
            txtTBLop11.setText("");
            txtTBLop12.setText("");
        }
    }
    
    private void clearForm() {
        txtHK1_10.setText("");
        txtHK2_10.setText("");
        txtHK1_11.setText("");
        txtHK2_11.setText("");
        txtHK1_12.setText("");
        txtHK2_12.setText("");
        txtDiemTB.setText("");
        txtTBLop10.setText("");
        txtTBLop11.setText("");
        txtTBLop12.setText("");
        txtLopHienTai.setText("");
    }
    
    private boolean validateDiemInput() {
        JTextField[] diemFields = {txtHK1_10, txtHK2_10, txtHK1_11, txtHK2_11, txtHK1_12, txtHK2_12};
        String[] fieldNames = {"HK1-10", "HK2-10", "HK1-11", "HK2-11", "HK1-12", "HK2-12"};
        
        for (int i = 0; i < diemFields.length; i++) {
            String text = diemFields[i].getText().trim();
            if (!text.isEmpty()) {
                try {
                    double diem = Double.parseDouble(text);
                    if (diem < 0 || diem > 10) {
                        JOptionPane.showMessageDialog(this, 
                            "Điểm " + fieldNames[i] + " phải từ 0 đến 10!", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        diemFields[i].requestFocus();
                        return false;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Điểm " + fieldNames[i] + " không hợp lệ!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    diemFields[i].requestFocus();
                    return false;
                }
            }
        }
        return true;
    }
    
    private void exportToExcel() {
        ExcelExporter.exportTable(this, tableModel, "Điểm học kỳ", "DiemHocKy");
    }
}
