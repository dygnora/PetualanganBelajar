package com.petualanganbelajar.repository;

import com.petualanganbelajar.db.DatabaseConnection;
import java.sql.*;

public class ProgressRepository {

    // 1. Cek level tertinggi
    public int getHighestLevelUnlocked(int userId, int moduleId) {
        String sql = "SELECT highest_level_unlocked FROM user_progress WHERE user_id = ? AND module_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, moduleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("highest_level_unlocked");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Default Level 1
    }

    // 2. Simpan Skor (DIPERBAIKI: Menggunakan ID)
    public void saveScore(int userId, int moduleId, int level, int score) {
        // Query menggunakan user_id, bukan user_name
        String sql = "INSERT INTO game_results (user_id, module_id, level, score, created_at) VALUES (?, ?, ?, ?, datetime('now'))";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);   // Set user_id
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
        if (nextLevel > currentUnlocked) {
            String checkSql = "SELECT id FROM user_progress WHERE user_id = ? AND module_id = ?";
            String updateSql;
            
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, moduleId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
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

    // 4. Hitung Total Skor (DIPERBAIKI: Query by user_id)
    public int calculateTotalScore(int userId) {
        String sql = "SELECT SUM(score) FROM game_results WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 5. Cek Apakah Game Tamat
    public boolean isGameCompleted(int userId) {
        String sql = "SELECT COUNT(*) FROM user_progress WHERE user_id = ? AND highest_level_unlocked >= 4";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Ada 4 modul total. Jika count == 4, berarti tamat.
                return rs.getInt(1) >= 4;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Cek apakah user sudah menamatkan seluruh game (Modul 1-4, Level 3 Selesai)
    public boolean isGameFullyCompleted(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM user_progress " +
                     "WHERE user_id = ? AND highest_level_unlocked > 3"; 
        // Logic: Jika user punya progress > 3 di 4 modul berbeda, berarti tamat.
        // Atau cara lebih sederhana: Cek apakah dia punya entry level > 3 untuk modul 1,2,3,4
        
        // Kita pakai cara aman: Cek satu per satu
        for (int modId = 1; modId <= 4; modId++) {
            if (getHighestLevelUnlocked(userId, modId) <= 3) {
                return false; // Ada modul yang belum level 4 (tamat level 3)
            }
        }
        return true;
    }
}