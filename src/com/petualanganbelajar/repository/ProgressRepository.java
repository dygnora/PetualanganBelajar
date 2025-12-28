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

    // 2. Simpan Skor
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Buka Level Berikutnya [UPDATED LOGIC]
    public void unlockNextLevel(int userId, int moduleId, int currentLevel) {
        // Jika currentLevel 3 lulus, nextLevel jadi 4. 
        // Angka 4 ini kita anggap sebagai penanda "Modul Selesai" di database.
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

    // 4. Hitung Total Skor
    public int calculateTotalScore(String userName) {
        String sql = "SELECT SUM(score) FROM game_results WHERE user_name = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 5. [BARU] Cek Apakah Game Tamat (Semua Modul mencapai Level 4)
    public boolean isGameCompleted(int userId) {
        // Kita anggap game tamat jika user punya 4 record di user_progress (untuk 4 modul)
        // DAN semua record itu memiliki highest_level_unlocked >= 4.
        
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
}