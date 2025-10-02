package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import dao.HocSinhDAO;
import dao.KhoiDAO;
import dao.LopDAO;
import model.HocSinh;
import model.Khoi;
import model.Lop;
import util.ExcelExporter;

public class LopPanel extends JPanel {
    private JTextField txtTenLop;
    private JSpinner spnNamHoc;
    private JComboBox<Khoi> cboKhoi;
    private JComboBox<Lop> cboLop;
    private JTable tableLop, tableHocSinhTrongLop, tableHocSinhChuaCoLop;
    private DefaultTableModel tableModelLop, tableModelHSTrongLop, tableModelHSChuaCoLop;
    private JButton btnThemLop, btnSuaLop, btnXoaLop, btnCapNhatLop, btnResetLop;
    private JButton btnThemVaoLop, btnBoKhoiLop, btnCapNhatHS, btnExportExcel;
    
    private LopDAO lopDAO;
    private HocSinhDAO hocSinhDAO;
    private KhoiDAO khoiDAO;
    private int selectedLopId = -1;
    
    public LopPanel() {
        lopDAO = new LopDAO();
        hocSinhDAO = new HocSinhDAO();
        khoiDAO = new KhoiDAO();
        initComponents();
        setupLayout();
        setupEvents();
        loadData();
    }
    
    private void initComponents() {
        txtTenLop = new JTextField(20);
        txtTenLop.setPreferredSize(new Dimension(200, 25));
        spnNamHoc = new JSpinner(new SpinnerNumberModel(2025, 2023, 2030, 1));
        spnNamHoc.setPreferredSize(new Dimension(200, 25));
        
        cboKhoi = new JComboBox<>();
        cboKhoi.setPreferredSize(new Dimension(200, 25));
        loadKhoiData();
        
        cboLop = new JComboBox<>();
        
        btnThemLop = new JButton("Thêm");
        btnSuaLop = new JButton("Sửa");
        btnXoaLop = new JButton("Xóa");
        btnResetLop = new JButton("Reset");
        btnCapNhatLop = new JButton("Cập nhật");
        
        btnThemVaoLop = new JButton("Thêm vào lớp");
        btnBoKhoiLop = new JButton("Bỏ khỏi lớp");
        btnCapNhatHS = new JButton("Cập nhật danh sách");
        btnExportExcel = new JButton("Xuất Excel");
        
        String[] columnsLop = {"ID", "Tên lớp", "Năm học", "Khối"};
        tableModelLop = new DefaultTableModel(columnsLop, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableLop = new JTable(tableModelLop);
        
        String[] columnsHS = {"Mã HS", "Họ tên", "Giới tính", "Ngày sinh"};
        tableModelHSTrongLop = new DefaultTableModel(columnsHS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableHocSinhTrongLop = new JTable(tableModelHSTrongLop);
        
        tableModelHSChuaCoLop = new DefaultTableModel(columnsHS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableHocSinhChuaCoLop = new JTable(tableModelHSChuaCoLop);
        
        tableLop.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectLop();
            }
        });
        
        cboLop.addActionListener(e -> loadHocSinhInLop());
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Quản lý Lớp"));
        leftPanel.setPreferredSize(new Dimension(450, 0));
        
        // Form lớp
        JPanel formLopPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 20;
        JLabel lblTenLop = new JLabel("Tên lớp:");
        lblTenLop.setPreferredSize(new Dimension(80, 25));
        formLopPanel.add(lblTenLop, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 
        gbc.ipadx = 0; 
        formLopPanel.add(txtTenLop, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; gbc.ipadx = 20;
        JLabel lblNamHoc = new JLabel("Năm học:");
        lblNamHoc.setPreferredSize(new Dimension(80, 25));
        formLopPanel.add(lblNamHoc, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.ipadx = 0;
        formLopPanel.add(spnNamHoc, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; gbc.ipadx = 20;
        JLabel lblKhoi = new JLabel("Khối:");
        lblKhoi.setPreferredSize(new Dimension(80, 25));
        formLopPanel.add(lblKhoi, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.ipadx = 0;
        formLopPanel.add(cboKhoi, gbc);
        
        JPanel buttonLopPanel = new JPanel(new FlowLayout());
        buttonLopPanel.add(btnThemLop);
        buttonLopPanel.add(btnSuaLop);
        buttonLopPanel.add(btnXoaLop);
        buttonLopPanel.add(btnResetLop);
        buttonLopPanel.add(btnCapNhatLop);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formLopPanel.add(buttonLopPanel, gbc);
        
        leftPanel.add(formLopPanel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(tableLop), BorderLayout.CENTER);
        
        add(leftPanel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Phân công Học sinh"));
        
        JPanel selectLopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectLopPanel.add(new JLabel("Chọn lớp:"));
        selectLopPanel.add(cboLop);
        selectLopPanel.add(btnCapNhatHS);
        selectLopPanel.add(btnExportExcel);
        
        rightPanel.add(selectLopPanel, BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        JPanel hsTrongLopPanel = new JPanel(new BorderLayout());
        hsTrongLopPanel.setBorder(BorderFactory.createTitledBorder("Học sinh trong lớp"));
        hsTrongLopPanel.add(new JScrollPane(tableHocSinhTrongLop), BorderLayout.CENTER);
        
        JPanel buttonTrongLopPanel = new JPanel(new FlowLayout());
        buttonTrongLopPanel.add(btnBoKhoiLop);
        hsTrongLopPanel.add(buttonTrongLopPanel, BorderLayout.SOUTH);
        
        JPanel hsChuaCoLopPanel = new JPanel(new BorderLayout());
        hsChuaCoLopPanel.setBorder(BorderFactory.createTitledBorder("Học sinh chưa có lớp"));
        hsChuaCoLopPanel.add(new JScrollPane(tableHocSinhChuaCoLop), BorderLayout.CENTER);
        
        JPanel buttonChuaCoLopPanel = new JPanel(new FlowLayout());
        buttonChuaCoLopPanel.add(btnThemVaoLop);
        hsChuaCoLopPanel.add(buttonChuaCoLopPanel, BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(hsTrongLopPanel);
        splitPane.setRightComponent(hsChuaCoLopPanel);
        splitPane.setResizeWeight(0.5);
        
        rightPanel.add(splitPane, BorderLayout.CENTER);
        
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        btnThemLop.addActionListener(e -> themLop());
        btnSuaLop.addActionListener(e -> suaLop());
        btnXoaLop.addActionListener(e -> xoaLop());
        btnResetLop.addActionListener(e -> clearLopForm());
        btnCapNhatLop.addActionListener(e -> loadData());
        
        btnThemVaoLop.addActionListener(e -> themHocSinhVaoLop());
        btnBoKhoiLop.addActionListener(e -> boHocSinhKhoiLop());
        btnCapNhatHS.addActionListener(e -> loadHocSinhInLop());
        btnExportExcel.addActionListener(e -> exportToExcel());
    }
    
    private void themLop() {
        if (validateLopInput()) {
            Lop lop = new Lop();
            lop.setTenLop(txtTenLop.getText().trim());
            lop.setNamHoc((Integer) spnNamHoc.getValue());
            
            Khoi selectedKhoi = (Khoi) cboKhoi.getSelectedItem();
            if (selectedKhoi != null) {
                lop.setKhoiId(selectedKhoi.getId());
            }
            
            if (lopDAO.insertLop(lop)) {
                JOptionPane.showMessageDialog(this, "Thêm lớp thành công!");
                clearLopForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm lớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void suaLop() {
        if (selectedLopId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp cần sửa!");
            return;
        }
        
        if (validateLopInput()) {
            Lop lop = new Lop();
            lop.setId(selectedLopId);
            lop.setTenLop(txtTenLop.getText().trim());
            lop.setNamHoc((Integer) spnNamHoc.getValue());
            
            if (lopDAO.updateLop(lop)) {
                JOptionPane.showMessageDialog(this, "Cập nhật lớp thành công!");
                clearLopForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật lớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void xoaLop() {
        if (selectedLopId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp cần xóa!");
            return;
        }
        
        List<HocSinh> hocSinhTrongLop = lopDAO.getHocSinhInLop(selectedLopId);
        if (!hocSinhTrongLop.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không thể xóa lớp vì vẫn còn " + hocSinhTrongLop.size() + " học sinh đang học trong lớp này!\n" +
                "Vui lòng chuyển tất cả học sinh ra khỏi lớp trước khi xóa.", 
                "Không thể xóa lớp", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa lớp này?\n" +
            "Hành động này sẽ xóa toàn bộ lịch sử học sinh đã từng học trong lớp.", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (lopDAO.deleteLop(selectedLopId)) {
                JOptionPane.showMessageDialog(this, "Xóa lớp thành công!");
                clearLopForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa lớp!\n" +
                    "Có thể do lớp vẫn còn học sinh đang học hoặc có lỗi database.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void themHocSinhVaoLop() {
        Lop selectedLop = (Lop) cboLop.getSelectedItem();
        if (selectedLop == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp!");
            return;
        }
        
        int selectedRow = tableHocSinhChuaCoLop.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học sinh cần thêm vào lớp!");
            return;
        }
        
        int hsId = (Integer) tableModelHSChuaCoLop.getValueAt(selectedRow, 0);
        String hsName = (String) tableModelHSChuaCoLop.getValueAt(selectedRow, 1);
        
        if (lopDAO.addHocSinhToLop(hsId, selectedLop.getId())) {
            JOptionPane.showMessageDialog(this, "Thêm học sinh vào lớp thành công!");
            loadHocSinhInLop();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Không thể thêm học sinh " + hsName + " vào lớp!\n" +
                "Học sinh đã có lớp trong năm học " + selectedLop.getNamHoc() + " này.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void boHocSinhKhoiLop() {
        Lop selectedLop = (Lop) cboLop.getSelectedItem();
        if (selectedLop == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp!");
            return;
        }
        
        int selectedRow = tableHocSinhTrongLop.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học sinh cần bỏ khỏi lớp!");
            return;
        }
        
        int hsId = (Integer) tableModelHSTrongLop.getValueAt(selectedRow, 0);
        
        if (lopDAO.removeHocSinhFromLop(hsId, selectedLop.getId())) {
            JOptionPane.showMessageDialog(this, "Bỏ học sinh khỏi lớp thành công!");
            loadHocSinhInLop();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi bỏ học sinh khỏi lớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void selectLop() {
        int selectedRow = tableLop.getSelectedRow();
        if (selectedRow >= 0) {
            selectedLopId = (Integer) tableModelLop.getValueAt(selectedRow, 0);
            txtTenLop.setText((String) tableModelLop.getValueAt(selectedRow, 1));
            spnNamHoc.setValue((Integer) tableModelLop.getValueAt(selectedRow, 2));
            
            String tenKhoi = (String) tableModelLop.getValueAt(selectedRow, 3);
            if (tenKhoi != null && !tenKhoi.trim().isEmpty()) {
                for (int i = 0; i < cboKhoi.getItemCount(); i++) {
                    Khoi khoi = cboKhoi.getItemAt(i);
                    if (khoi != null && khoi.getTenKhoi().equals(tenKhoi)) {
                        cboKhoi.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                if (cboKhoi.getItemCount() > 0) {
                    cboKhoi.setSelectedIndex(0);
                }
            }
        }
    }
    
    private void loadData() {
        List<Lop> listLop = lopDAO.getAllLop();
        
        tableModelLop.setRowCount(0);
        for (Lop lop : listLop) {
            String tenKhoi = lop.getTenKhoi() != null ? lop.getTenKhoi() : "";
            Object[] row = {lop.getId(), lop.getTenLop(), lop.getNamHoc(), tenKhoi};
            tableModelLop.addRow(row);
        }
        
        cboLop.removeAllItems();
        cboLop.addItem(null); 
        for (Lop lop : listLop) {
            cboLop.addItem(lop);
        }
        
        cboLop.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Chọn lớp --");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
        
        cboLop.setSelectedIndex(0); 
        
        loadHocSinhInLop();
    }
    
    private void loadHocSinhInLop() {
        Lop selectedLop = (Lop) cboLop.getSelectedItem();
        
        tableModelHSTrongLop.setRowCount(0);
        tableModelHSChuaCoLop.setRowCount(0);
        
        if (selectedLop != null) {
            List<HocSinh> hsTrongLop = lopDAO.getHocSinhInLop(selectedLop.getId());
            for (HocSinh hs : hsTrongLop) {
                Object[] row = {hs.getId(), hs.getHoTen(), hs.getGioiTinhText(), 
                               hs.getNgaySinh().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))};
                tableModelHSTrongLop.addRow(row);
            }
            
            List<HocSinh> hsChuaCoLop = lopDAO.getHocSinhChuaCoLop(selectedLop.getNamHoc());
            for (HocSinh hs : hsChuaCoLop) {
                Object[] row = {hs.getId(), hs.getHoTen(), hs.getGioiTinhText(),
                               hs.getNgaySinh().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))};
                tableModelHSChuaCoLop.addRow(row);
            }
        }
    }
    
    private boolean validateLopInput() {
        if (txtTenLop.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên lớp!");
            txtTenLop.requestFocus();
            return false;
        }
        
        if (cboKhoi.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khối!");
            cboKhoi.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void loadKhoiData() {
        try {
            List<Khoi> khoiList = khoiDAO.getAllKhoi();
            cboKhoi.removeAllItems();
            for (Khoi khoi : khoiList) {
                cboKhoi.addItem(khoi);
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu khối: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearLopForm() {
        txtTenLop.setText("");
        spnNamHoc.setValue(2024);
        if (cboKhoi.getItemCount() > 0) {
            cboKhoi.setSelectedIndex(0);
        }
        selectedLopId = -1;
        tableLop.clearSelection();
    }
    
    private void exportToExcel() {
        ExcelExporter.exportTable(this, tableModelLop, "Danh sách lớp", "DanhSachLop");
    }
}
