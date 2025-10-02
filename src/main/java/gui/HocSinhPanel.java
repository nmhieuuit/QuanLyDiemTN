package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

import dao.HocSinhDAO;
import model.HocSinh;
import util.ExcelExporter;

public class HocSinhPanel extends JPanel {
    private JTextField txtHoTen, txtDiaChi, txtTimKiem;
    private JComboBox<String> cboGioiTinh;
    private JSpinner spnNgaySinh;
    private JTable tableHocSinh;
    private DefaultTableModel tableModel;
    private JButton btnThem, btnSua, btnXoa, btnTimKiem, btnCapNhat, btnReset, btnExportExcel;
    
    private HocSinhDAO hocSinhDAO;
    private int selectedHocSinhId = -1;
    
    public HocSinhPanel() {
        hocSinhDAO = new HocSinhDAO();
        initComponents();
        setupLayout();
        setupEvents();
        loadData();
    }
    
    private void initComponents() {
        txtHoTen = new JTextField(20);
        txtDiaChi = new JTextField(20);
        txtTimKiem = new JTextField(15);
        
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        
        spnNgaySinh = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgaySinh, "dd/MM/yyyy");
        spnNgaySinh.setEditor(dateEditor);
        
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnReset = new JButton("Reset");
        btnTimKiem = new JButton("Tìm kiếm");
        btnCapNhat = new JButton("Cập nhật");
        btnExportExcel = new JButton("Xuất Excel");
        
        String[] columns = {"Mã HS", "Họ tên", "Ngày sinh", "Giới tính", "Địa chỉ", "Lớp Hiện tại"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableHocSinh = new JTable(tableModel);
        tableHocSinh.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableHocSinh.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectHocSinh();
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel - Form nhập liệu
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Học sinh"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(txtHoTen, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        topPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(cboGioiTinh, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        topPanel.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(spnNgaySinh, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        topPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(txtDiaChi, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnExportExcel);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        topPanel.add(buttonPanel, gbc);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(tableHocSinh), BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        btnThem.addActionListener(e -> themHocSinh());
        btnSua.addActionListener(e -> suaHocSinh());
        btnXoa.addActionListener(e -> xoaHocSinh());
        btnReset.addActionListener(e -> clearForm());
        btnTimKiem.addActionListener(e -> timKiemHocSinh());
        btnCapNhat.addActionListener(e -> loadData());
        btnExportExcel.addActionListener(e -> exportToExcel());
        
        txtTimKiem.addActionListener(e -> timKiemHocSinh());
    }
    
    private void themHocSinh() {
        if (validateInput()) {
            HocSinh hs = createHocSinhFromInput();
            if (hocSinhDAO.insertHocSinh(hs)) {
                JOptionPane.showMessageDialog(this, "Thêm học sinh thành công!");
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm học sinh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void suaHocSinh() {
        if (selectedHocSinhId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học sinh cần sửa!");
            return;
        }
        
        if (validateInput()) {
            HocSinh hs = createHocSinhFromInput();
            hs.setId(selectedHocSinhId);
            
            if (hocSinhDAO.updateHocSinh(hs)) {
                JOptionPane.showMessageDialog(this, "Cập nhật học sinh thành công!");
                clearForm();
                loadData();
                selectedHocSinhId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật học sinh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void xoaHocSinh() {
        if (selectedHocSinhId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học sinh cần xóa!");
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa học sinh này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (hocSinhDAO.deleteHocSinh(selectedHocSinhId)) {
                JOptionPane.showMessageDialog(this, "Xóa học sinh thành công!");
                clearForm();
                loadData();
                selectedHocSinhId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa học sinh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void timKiemHocSinh() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            List<HocSinh> list = hocSinhDAO.searchHocSinh(keyword);
            updateTable(list);
        }
    }
    
    private void selectHocSinh() {
        int selectedRow = tableHocSinh.getSelectedRow();
        if (selectedRow >= 0) {
            selectedHocSinhId = (Integer) tableModel.getValueAt(selectedRow, 0);
            txtHoTen.setText((String) tableModel.getValueAt(selectedRow, 1));
            
            String ngaySinhStr = (String) tableModel.getValueAt(selectedRow, 2);
            try {
                LocalDate ngaySinh = LocalDate.parse(ngaySinhStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                spnNgaySinh.setValue(java.sql.Date.valueOf(ngaySinh));
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            String gioiTinh = (String) tableModel.getValueAt(selectedRow, 3);
            cboGioiTinh.setSelectedItem(gioiTinh);
            txtDiaChi.setText((String) tableModel.getValueAt(selectedRow, 4));
        }
    }
    
    private void loadData() {
        List<HocSinh> list = hocSinhDAO.getAllHocSinhWithLop();
        updateTable(list);
    }
    
    private void updateTable(List<HocSinh> list) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (HocSinh hs : list) {
            Object[] row = {
                hs.getId(),
                hs.getHoTen(),
                hs.getNgaySinh().format(formatter),
                hs.getGioiTinhText(),
                hs.getDiaChi(),
                hs.getTenLop() != null ? hs.getTenLop() : "Chưa có lớp"
            };
            tableModel.addRow(row);
        }
    }
    
    private boolean validateInput() {
        if (txtHoTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!");
            txtHoTen.requestFocus();
            return false;
        }
        return true;
    }
    
    private HocSinh createHocSinhFromInput() {
        HocSinh hs = new HocSinh();
        hs.setHoTen(txtHoTen.getText().trim());
        hs.setGioiTinh("Nam".equals(cboGioiTinh.getSelectedItem()));
        hs.setDiaChi(txtDiaChi.getText().trim());
        
        java.util.Date date = (java.util.Date) spnNgaySinh.getValue();
        hs.setNgaySinh(new java.sql.Date(date.getTime()).toLocalDate());
        
        return hs;
    }
    
    private void clearForm() {
        txtHoTen.setText("");
        txtDiaChi.setText("");
        cboGioiTinh.setSelectedIndex(0);
        spnNgaySinh.setValue(new java.util.Date());
        selectedHocSinhId = -1;
        tableHocSinh.clearSelection();
    }
    
    private void exportToExcel() {
        ExcelExporter.exportTable(this, tableModel, "Danh sách học sinh", "DanhSachHocSinh");
    }
}
