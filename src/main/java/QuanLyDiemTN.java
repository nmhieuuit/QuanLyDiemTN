import java.awt.Font;
import java.sql.Connection;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import config.DatabaseConnection;
import gui.MainFrame;

public class QuanLyDiemTN {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Không thể thiết lập Look and Feel: " + e.getMessage());
        }
        
        setupVietnameseFont();
        
        SwingUtilities.invokeLater(() -> {
            try {
                if (checkDatabaseConnection()) {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                } else {
                    showConnectionError();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                showErrorDialog("Lỗi khởi chạy ứng dụng", e.getMessage());
            }
        });
    }
    
    private static void setupVietnameseFont() {
        Font vietnameseFont = new Font("Arial Unicode MS", Font.PLAIN, 12);
        
        UIManager.put("Label.font", vietnameseFont);
        UIManager.put("Button.font", vietnameseFont);
        UIManager.put("TextField.font", vietnameseFont);
        UIManager.put("TextArea.font", vietnameseFont);
        UIManager.put("Table.font", vietnameseFont);
        UIManager.put("TableHeader.font", new Font("Arial Unicode MS", Font.BOLD, 12));
        UIManager.put("Menu.font", vietnameseFont);
        UIManager.put("MenuItem.font", vietnameseFont);
        UIManager.put("TabbedPane.font", new Font("Arial Unicode MS", Font.BOLD, 12));
        UIManager.put("TitledBorder.font", new Font("Arial Unicode MS", Font.BOLD, 11));
        UIManager.put("OptionPane.messageFont", vietnameseFont);
        UIManager.put("OptionPane.buttonFont", vietnameseFont);
    }
    

    private static boolean checkDatabaseConnection() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra kết nối database: " + e.getMessage());
            return false;
        }
    }
    
    private static void showConnectionError() {
        String message = 
            "Không thể kết nối đến database!\n" +
            "\n" +
            "Vui lòng kiểm tra:\n" +
            "• SQL Server đã được khởi động\n" +
            "• Thông tin kết nối trong file database.properties\n" +
            "• Database QuanLyDiemTN đã được tạo\n" +
            "• Username và password đúng\n" +
            "\n" +
            "Ứng dụng sẽ chạy ở chế độ demo.\n";
        
        int choice = JOptionPane.showOptionDialog(
            null,
            message,
            "Lỗi kết nối Database",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            new String[]{"Tiếp tục (Demo)", "Thoát"},
            "Tiếp tục (Demo)"
        );
        
        if (choice == 0) {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        } else {
            System.exit(0);
        }
    }
    
    private static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }
}