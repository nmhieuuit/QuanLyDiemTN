package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import config.DatabaseConnection;

public class SchemaExecutor {
    
    public static void main(String[] args) {
        try {
            executeSchema();
            System.out.println("Schema đã được thực thi thành công!");
        } catch (Exception e) {
            System.err.println("Lỗi khi thực thi schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void executeSchema() throws IOException, SQLException {
        String schemaPath = "src/main/resources/database/schema.sql";
        String schemaContent = Files.readString(Paths.get(schemaPath));
        
        String[] sqlStatements = schemaContent.split("GO|;\\s*(?=\\r?\\n|$)");
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty() && !sql.startsWith("--")) {
                    try {
                        System.out.println("Executing: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        if (!e.getMessage().contains("already exists") && 
                            !e.getMessage().contains("There is already an object")) {
                            System.err.println("Error executing SQL: " + sql.substring(0, Math.min(100, sql.length())));
                            System.err.println("Error: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
}