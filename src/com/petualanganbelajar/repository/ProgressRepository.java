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

    // 2. Simpan Skor (LOGIC: HANYA SIMPAN JIKA REKOR BARU)
    // Digunakan untuk Leaderboard (High Score)
    public void saveScore(int userId, int moduleId, int level, int newScore) {
        // Cek skor lama
        String checkSql = "SELECT score FROM game_results WHERE user_id = ? AND module_id = ? AND level = ?";
        int oldScore = -1;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, moduleId);
            checkStmt.setInt(3, level);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                oldScore = rs.getInt("score");
            }
            
            // Simpan HANYA JIKA (Belum ada data) ATAU (Skor Baru > Skor Lama)
            if (oldScore == -1 || newScore > oldScore) {
                // INSERT OR REPLACE akan bekerja karena ada UNIQUE constraint
                String sql = "INSERT OR REPLACE INTO game_results (user_id, module_id, level, score, created_at) VALUES (?, ?, ?, ?, datetime('now'))";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, moduleId);
                    pstmt.setInt(3, level);
                    pstmt.setInt(4, newScore);
                    pstmt.executeUpdate();
                    // System.out.println("LOG: Highscore Updated!");
                }
            } else {
                // System.out.println("LOG: Skor tidak disimpan (Lebih rendah dari rekor).");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Tambah XP ke User (LOGIC: SELALU NAMBAH / GRINDING)
    // Digunakan untuk Level User (Accumulative)
    public void addPlayerXP(int userId, int xpEarned) {
        String sql = "UPDATE users SET total_xp = total_xp + ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, xpEarned);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            
            // System.out.println("LOG: Player XP Increased by " + xpEarned);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. Buka Level Berikutnya
    public void unlockNextLevel(int userId, int moduleId, int currentLevel) {
        int nextLevel = currentLevel + 1;
        
        if (nextLevel > 4) return;

        int currentUnlocked = getHighestLevelUnlocked(userId, moduleId);
        
        if (nextLevel > currentUnlocked) {
            String checkSql = "SELECT id FROM user_progress WHERE user_id = ? AND module_id = ?";
            String updateSql;
            
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, moduleId);
                
                boolean exists = false;
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) exists = true;
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
                    System.out.println("LOG: Level Unlocked -> " + nextLevel);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 5. Hitung Total Skor (Untuk Leaderboard)
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

    // 6. Cek Game Tamat
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
    
    // 7. Cek Tamat Semua Modul (Epilog)
    public boolean isGameFullyCompleted(int userId) {
        String sql = "SELECT COUNT(DISTINCT module_id) FROM user_progress WHERE user_id = ? AND highest_level_unlocked > 3 AND module_id IN (1, 2, 3, 4)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int completedModules = rs.getInt(1);
                    return completedModules == 4; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}