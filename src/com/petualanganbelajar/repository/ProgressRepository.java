/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.repository;
import com.petualanganbelajar.db.DatabaseConnection;
import java.sql.*;
/**
 *
 * @author DD
 */
public class ProgressRepository {
    // Cek level tertinggi yang sudah dibuka user untuk modul tertentu
    public int getHighestLevelUnlocked(int userId, int moduleId) {
        String sql = "SELECT highest_level_unlocked FROM user_progress WHERE user_id = ? AND module_id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, moduleId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("highest_level_unlocked");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Default: Jika belum ada data, berarti baru Level 1 yang terbuka
        return 1;
    }
    
    
    // 1. SIMPAN HASIL PERMAINAN KE HISTORY
    public void saveScore(String userName, String avatar, int moduleId, int level, int score) {
        String sql = "INSERT INTO game_results (user_name, avatar, module_id, level, score, created_at) VALUES (?, ?, ?, ?, ?, datetime('now'))";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userName);
            pstmt.setString(2, avatar);
            pstmt.setInt(3, moduleId);
            pstmt.setInt(4, level);
            pstmt.setInt(5, score);
            
            pstmt.executeUpdate();
            System.out.println("LOG: Skor berhasil disimpan.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. BUKA LEVEL SELANJUTNYA (Jika lulus KKM)
    public void unlockNextLevel(int userId, int moduleId, int currentLevel) {
        // Logika: Jika baru saja menang Level 1, maka kita buka Level 2.
        int nextLevel = currentLevel + 1;
        if (nextLevel > 3) return; // Maksimal level 3

        // Cek dulu level yg sekarang terbuka
        int currentUnlocked = getHighestLevelUnlocked(userId, moduleId);
        
        // Hanya update jika level user naik (biar ga turun)
        if (nextLevel > currentUnlocked) {
            // Cek apakah data progress user sudah ada?
            String checkSql = "SELECT id FROM user_progress WHERE user_id = ? AND module_id = ?";
            String updateSql;
            
            // Logic: Insert or Update
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, moduleId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // Update
                    updateSql = "UPDATE user_progress SET highest_level_unlocked = ? WHERE user_id = ? AND module_id = ?";
                } else {
                    // Insert Baru
                    updateSql = "INSERT INTO user_progress (highest_level_unlocked, user_id, module_id) VALUES (?, ?, ?)";
                }
                
                try (PreparedStatement upStmt = conn.prepareStatement(updateSql)) {
                    upStmt.setInt(1, nextLevel);
                    upStmt.setInt(2, userId);
                    upStmt.setInt(3, moduleId);
                    upStmt.executeUpdate();
                    System.out.println("LOG: Level " + nextLevel + " Terbuka!");
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 3. HITUNG TOTAL SKOR KUMULATIF (Skor Terbaik per Level dijumlahkan)
    public int calculateTotalScore(String userName) {
        // Query ini menjumlahkan HANYA skor tertinggi dari setiap level yang pernah dimainkan
        String sql = "SELECT SUM(max_score) FROM (" +
                     "  SELECT MAX(score) as max_score " +
                     "  FROM game_results " +
                     "  WHERE user_name = ? " +
                     "  GROUP BY module_id, level" +
                     ")";
                     
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1); // Mengembalikan hasil SUM
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Jika belum pernah main
    }
    
    
}
