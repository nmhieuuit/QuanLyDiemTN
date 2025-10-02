package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import dao.KhoiDAO;
import dao.LopDAO;
import dao.ThongKeDAO;
import model.Khoi;
import model.Lop;

public class ThongKePanel extends JPanel {
    private JComboBox<String> cboThongKeType;
    private JComboBox<Lop> cboLop;
    private JComboBox<Integer> cboNamHoc;
    private JTable tableThongKe;
    private DefaultTableModel tableModel;
    private JButton btnThongKe, btnExportExcel, btnShowChart;
    private JTextArea txtSummary;
    
    private ThongKeDAO thongKeDAO;
    private LopDAO lopDAO;
    private KhoiDAO khoiDAO;
    private DecimalFormat df = new DecimalFormat("#.##");
    
    public ThongKePanel() {
        thongKeDAO = new ThongKeDAO();
        lopDAO = new LopDAO();
        khoiDAO = new KhoiDAO();
        initComponents();
        setupLayout();
        setupEvents();
        loadData();
    }
    
    private void initComponents() {
        cboThongKeType = new JComboBox<>(new String[]{
            "Thống kê theo lớp", 
            "Thống kê theo năm học"
        });
        
        cboLop = new JComboBox<>();
        cboNamHoc = new JComboBox<>();
        
        btnThongKe = new JButton("Thống kê");
        btnExportExcel = new JButton("Xuất Excel");
        btnShowChart = new JButton("Xem biểu đồ");
        
        String[] columns = {"STT", "Thông tin", "Tổng HS", "Đạt", "Không đạt", "Tỷ lệ đạt (%)", "Điểm TB"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableThongKe = new JTable(tableModel);
        
        txtSummary = new JTextArea(8, 30);
        txtSummary.setEditable(false);
        txtSummary.setBackground(java.awt.Color.LIGHT_GRAY);
        txtSummary.setBorder(BorderFactory.createTitledBorder("Tóm tắt"));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel - Controls
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Tùy chọn thống kê"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(new JLabel("Loại thống kê:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(cboThongKeType, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        topPanel.add(new JLabel("Lớp:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(cboLop, gbc);
        
        gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE;
        topPanel.add(new JLabel("Năm học:"), gbc);
        gbc.gridx = 5; gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(cboNamHoc, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnThongKe);
        buttonPanel.add(btnExportExcel);
        buttonPanel.add(btnShowChart);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 6;
        topPanel.add(buttonPanel, gbc);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(tableThongKe), BorderLayout.CENTER);
        centerPanel.add(new JScrollPane(txtSummary), BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        cboThongKeType.addActionListener(e -> updateControlsVisibility());
        btnThongKe.addActionListener(e -> performThongKe());
        btnExportExcel.addActionListener(e -> exportToExcel());
        btnShowChart.addActionListener(e -> showChart());
    }
    
    private void loadData() {
        cboLop.removeAllItems();
        cboLop.addItem(null); 
        
        try {
            List<Khoi> khoiList = khoiDAO.getAllKhoi();
            Khoi khoi12 = null;
            for (Khoi khoi : khoiList) {
                if ("12".equals(khoi.getTenKhoi())) {
                    khoi12 = khoi;
                    break;
                }
            }
            
            if (khoi12 != null) {
                List<Lop> lopKhoi12 = lopDAO.getLopByKhoi(khoi12.getId());
                for (Lop lop : lopKhoi12) {
                    cboLop.addItem(lop);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu lớp 12: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        cboLop.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Tất cả lớp 12 --");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
        
        cboNamHoc.removeAllItems();
        cboNamHoc.addItem(null); 
        for (int year = 2020; year <= 2030; year++) {
            cboNamHoc.addItem(year);
        }
        
        cboNamHoc.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Tất cả năm học --");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
        
        updateControlsVisibility();
    }
    
    private void updateControlsVisibility() {
        String selectedType = (String) cboThongKeType.getSelectedItem();
        
        switch (selectedType) {
            case "Thống kê theo lớp":
                cboLop.setEnabled(true);
                cboNamHoc.setEnabled(false);
                break;
            case "Thống kê theo năm học":
                cboLop.setEnabled(false);
                cboNamHoc.setEnabled(true);
                break;
            case "Thống kê tổng quát":
                cboLop.setEnabled(false);
                cboNamHoc.setEnabled(false);
                break;
        }
    }
    
    private void performThongKe() {
        String type = (String) cboThongKeType.getSelectedItem();
        tableModel.setRowCount(0);
        
        try {
            switch (type) {
                case "Thống kê theo lớp":
                    thongKeTheoLop();
                    break;
                case "Thống kê theo năm học":
                    thongKeTheoNamHoc();
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thống kê: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void thongKeTheoLop() {
        Lop selectedLop = (Lop) cboLop.getSelectedItem();
        if (selectedLop == null) {
            Map<String, Object[]> stats = thongKeDAO.thongKeTheoTatCaLop();
            int stt = 1;
            for (Map.Entry<String, Object[]> entry : stats.entrySet()) {
                Object[] data = entry.getValue();
                Object[] row = {stt++, entry.getKey(), data[0], data[1], data[2], data[3], data[4]};
                tableModel.addRow(row);
            }
        } else {
            Object[] stats = thongKeDAO.thongKeTheoLop(selectedLop.getId());
            Object[] row = {1, selectedLop.getTenLop(), stats[0], stats[1], stats[2], stats[3], stats[4]};
            tableModel.addRow(row);
        }
        
        updateSummary();
    }
    
    private void thongKeTheoNamHoc() {
        Integer selectedYear = (Integer) cboNamHoc.getSelectedItem();
        if (selectedYear == null) {
            Map<Integer, Object[]> stats = thongKeDAO.thongKeTheoTatCaNamHoc();
            int stt = 1;
            for (Map.Entry<Integer, Object[]> entry : stats.entrySet()) {
                Object[] data = entry.getValue();
                Object[] row = {stt++, entry.getKey() + "-" + (entry.getKey() + 1), 
                               data[0], data[1], data[2], data[3], data[4]};
                tableModel.addRow(row);
            }
        } else {
            Object[] stats = thongKeDAO.thongKeTheoNamHoc(selectedYear);
            Object[] row = {1, selectedYear + "-" + (selectedYear + 1), 
                           stats[0], stats[1], stats[2], stats[3], stats[4]};
            tableModel.addRow(row);
        }
        
        updateSummary();
    }
    
    private void updateSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("TỔNG KẾT THỐNG KÊ\n");
        summary.append("==================\n");
        
        int totalStudents = 0, totalPass = 0, totalFail = 0;
        double totalAvg = 0;
        int rows = tableModel.getRowCount();
        
        for (int i = 0; i < rows; i++) {
            totalStudents += (Integer) tableModel.getValueAt(i, 2);
            totalPass += (Integer) tableModel.getValueAt(i, 3);
            totalFail += (Integer) tableModel.getValueAt(i, 4);
        }
        
        if (rows > 0) {
            double passRate = totalStudents > 0 ? (double) totalPass / totalStudents * 100 : 0;
            
            summary.append("Tổng số học sinh: ").append(totalStudents).append("\n");
            summary.append("Số học sinh đạt: ").append(totalPass).append("\n");
            summary.append("Số học sinh không đạt: ").append(totalFail).append("\n");
            summary.append("Tỷ lệ đạt: ").append(df.format(passRate)).append("%\n");
        }
        
        txtSummary.setText(summary.toString());
    }
    
    private void exportToExcel() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("ThongKe_" + 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Thống kê");
                
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(tableModel.getColumnName(i));
                    
                    CellStyle headerStyle = workbook.createCellStyle();
                    org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerStyle.setFont(headerFont);
                    cell.setCellStyle(headerStyle);
                }
                
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Cell cell = row.createCell(j);
                        Object value = tableModel.getValueAt(i, j);
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
                
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }
                
                try (FileOutputStream outputStream = new FileOutputStream(fileChooser.getSelectedFile())) {
                    workbook.write(outputStream);
                }
                
                JOptionPane.showMessageDialog(this, "Xuất Excel thành công!");
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất Excel: " + e.getMessage(), 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showChart() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng thực hiện thống kê trước khi xem biểu đồ!", 
                                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String type = (String) cboThongKeType.getSelectedItem();
        
        javax.swing.JFrame chartFrame = new javax.swing.JFrame("Biểu đồ thống kê");
        chartFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setSize(800, 600);
        chartFrame.setLocationRelativeTo(this);
        
        javax.swing.JTabbedPane tabbedPane = new javax.swing.JTabbedPane();
        
        JFreeChart barChart = createBarChart(type);
        ChartPanel barChartPanel = new ChartPanel(barChart);
        tabbedPane.addTab("Biểu đồ cột", barChartPanel);
        
        JFreeChart pieChart = createPieChart(type);
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        tabbedPane.addTab("Biểu đồ tròn", pieChartPanel);
        
        chartFrame.add(tabbedPane);
        chartFrame.setVisible(true);
    }
    
    private JFreeChart createBarChart(String type) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String label = tableModel.getValueAt(i, 1).toString(); 
            Number dat = (Number) tableModel.getValueAt(i, 3); 
            Number khongDat = (Number) tableModel.getValueAt(i, 4);
            
            dataset.addValue(dat, "Đạt", label);
            dataset.addValue(khongDat, "Không đạt", label);
        }
        
        return ChartFactory.createBarChart(
            "Thống kê kết quả học tập - " + type,
            "Đối tượng thống kê",
            "Số lượng học sinh", 
            dataset
        );
    }
    
    private JFreeChart createPieChart(String type) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        
        // Calculate total pass and fail from all rows
        int totalPass = 0;
        int totalFail = 0;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Number dat = (Number) tableModel.getValueAt(i, 3);
            Number khongDat = (Number) tableModel.getValueAt(i, 4);
            
            totalPass += dat.intValue();
            totalFail += khongDat.intValue();
        }
        
        dataset.setValue("Đạt (" + totalPass + ")", totalPass);
        dataset.setValue("Không đạt (" + totalFail + ")", totalFail);
        
        return ChartFactory.createPieChart(
            "Tỷ lệ đạt/không đạt tổng thể - " + type,
            dataset,
            true, 
            true,
            false 
        );
    }
}
