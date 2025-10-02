package gui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private HocSinhPanel hocSinhPanel;
    private LopPanel lopPanel;
    private DiemHocKyPanel diemHocKyPanel;
    private DiemThiPanel diemThiPanel;
    private ThongKePanel thongKePanel;
    
    public MainFrame() {
        initComponents();
        setupLayout();
        setDefaultProperties();
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        hocSinhPanel = new HocSinhPanel();
        lopPanel = new LopPanel();
        diemHocKyPanel = new DiemHocKyPanel();
        diemThiPanel = new DiemThiPanel();
        thongKePanel = new ThongKePanel();
    }
    
    private void setupLayout() {
        tabbedPane.addTab("Quản lý Học sinh", new ImageIcon(), hocSinhPanel, "Thêm, sửa, xóa học sinh");
        tabbedPane.addTab("Quản lý Lớp", new ImageIcon(), lopPanel, "Quản lý lớp và phân công học sinh");
        tabbedPane.addTab("Điểm Học kỳ", new ImageIcon(), diemHocKyPanel, "Nhập điểm các học kỳ");
        tabbedPane.addTab("Điểm Thi TN", new ImageIcon(), diemThiPanel, "Nhập điểm thi tốt nghiệp");
        tabbedPane.addTab("Thống kê", new ImageIcon(), thongKePanel, "Xem thống kê và báo cáo");
        
        add(tabbedPane, BorderLayout.CENTER);
        
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("Tệp");
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Trợ giúp");
        JMenuItem aboutItem = new JMenuItem("Giới thiệu");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setDefaultProperties() {
        setTitle("Quản lý Điểm Tốt nghiệp");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, 
            "Phần mềm Quản lý Điểm Tốt nghiệp\n" +
            "Phiên bản 1.0\n" +
            "Phát triển bằng Java Swing và SQL Server",
            "Giới thiệu",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
