package com.petualanganbelajar.repository;

import com.petualanganbelajar.db.DatabaseConnection;
import java.sql.*;

public class ProgressRepository {

    // 1. Cek level tertinggi
    public int getHighestLevelUnlocked(int userId, int moduleId) {
        String sql = "SELECT highest_level_unlocked FROM user_progress WHERE user_id = ? AND module_id = ?";
        // [FIX] Ensure ResultSet is also in try-with-resources or explicitly closed
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, moduleId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("highest_level_unlocked");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Default Level 1
    }

    // 2. Simpan Skor
    public void saveScore(int userId, int moduleId, int level, int score) {
        String sql = "INSERT INTO game_results (user_id, module_id, level, score, created_at) VALUES (?, ?, ?, ?, datetime('now'))";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, moduleId);
            pstmt.setInt(3, level);
            pstmt.setInt(4, score);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Buka Level Berikutnya
    public void unlockNextLevel(int userId, int moduleId, int currentLevel) {
        int nextLevel = currentLevel + 1;
        
        // Kita ijinkan sampai 4 (Tamat Modul), tapi tidak lebih.
        if (nextLevel > 4) return;

        int currentUnlocked = getHighestLevelUnlocked(userId, moduleId);
        
        // Only update if the new level is higher than what's currently unlocked
        if (nextLevel > currentUnlocked) {
            String checkSql = "SELECT id FROM user_progress WHERE user_id = ? AND module_id = ?";
            String updateSql;
            
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, moduleId);
                
                boolean exists = false;
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        exists = true;
                    }
                }

                if (exists) {
                    updateSql = "UPDATE user_progress SET highest_level_unlocked = ? WHERE user_id = ? AND module_id = ?";
                } else {
                    updateSql = "INSERT INTO user_progress (highest_level_unlocked, user_id, module_id) VALUES (?, ?, ?)";
                }

                try (PreparedStatement upStmt = conn.prepareStatement(updateSql)) {
                    upStmt.setInt(1, nextLevel);
                    upStmt.setInt(2, userId);
                    upStmt.setInt(3, moduleId);
                    upStmt.executeUpdate();
                    System.out.println("LOG: Progress Update -> User " + userId + " Modul " + moduleId + " ke Level " + nextLevel);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 4. Hitung Total Skor
    public int calculateTotalScore(int userId) {
        String sql = "SELECT SUM(score) FROM game_results WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 5. Cek Apakah Game Tamat (Logic lama)
    public boolean isGameCompleted(int userId) {
        String sql = "SELECT COUNT(*) FROM user_progress WHERE user_id = ? AND highest_level_unlocked >= 4";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) >= 4;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // [FIXED] Cek apakah user sudah menamatkan seluruh game (Modul 1-4, Level 3 Selesai)
    public boolean isGameFullyCompleted(int userId) {
        // Instead of calling getHighestLevelUnlocked 4 times (opening/closing 4 connections),
        // let's do it in one optimized query.
        
        // Logic: We need to ensure that for modules 1, 2, 3, and 4, the user has unlocked > 3 (meaning level 4 is available/completed).
        // This query counts how many distinct modules have reached level > 3 for the user.
        String sql = "SELECT COUNT(DISTINCT module_id) FROM user_progress WHERE user_id = ? AND highest_level_unlocked > 3 AND module_id IN (1, 2, 3, 4)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int completedModules = rs.getInt(1);
                    return completedModules == 4; // True if all 4 modules are completed
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}