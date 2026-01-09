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

    // 2. Simpan Skor (LOGIC BARU: INSERT OR UPDATE HIGHSCORE)
    public void saveScore(int userId, int moduleId, int level, int newScore) {
        System.out.println("\n--- [DEBUG] START SAVE SCORE ---");
        System.out.println("Input -> User: " + userId + ", Modul: " + moduleId + ", Level: " + level + ", Score: " + newScore);
        
        // Cek Stack Trace (Siapa yang memanggil method ini?)
        // Ini akan memberitahu kita jika saveScore dipanggil 2x dari tempat berbeda
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length > 2) {
            System.out.println("Caller: " + stack[2].getClassName() + "." + stack[2].getMethodName());
        }

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
                System.out.println("[DEBUG] Data Lama Ditemukan: Score = " + oldScore);
            } else {
                System.out.println("[DEBUG] Data Lama TIDAK Ditemukan (Insert Baru)");
            }
            
            if (oldScore == -1 || newScore > oldScore) {
                String sql = "INSERT OR REPLACE INTO game_results (user_id, module_id, level, score, created_at) VALUES (?, ?, ?, ?, datetime('now'))";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, moduleId);
                    pstmt.setInt(3, level);
                    pstmt.setInt(4, newScore);
                    int row = pstmt.executeUpdate();
                    System.out.println("[DEBUG] Execute Update: " + row + " baris terpengaruh.");
                }
            } else {
                System.out.println("[DEBUG] Skor tidak disimpan karena (" + newScore + " <= " + oldScore + ")");
            }

            // --- CCTV EXTRA: CEK TOTAL DATA ---
            // Kita hitung ada berapa baris untuk user ini di level ini. Harusnya CUMA 1.
            String countSql = "SELECT COUNT(*) as total, SUM(score) as total_score FROM game_results WHERE user_id = ? AND module_id = ? AND level = ?";
            try(PreparedStatement cntStmt = conn.prepareStatement(countSql)) {
                cntStmt.setInt(1, userId);
                cntStmt.setInt(2, moduleId);
                cntStmt.setInt(3, level);
                ResultSet rsCnt = cntStmt.executeQuery();
                if(rsCnt.next()) {
                    int jumlahBaris = rsCnt.getInt("total");
                    int totalSkor = rsCnt.getInt("total_score");
                    System.out.println("[DEBUG] VERIFIKASI DB -> Jumlah Baris: " + jumlahBaris + " (Harusnya 1), Total Skor: " + totalSkor);
                    
                    if (jumlahBaris > 1) {
                        System.err.println("!!! BAHAYA: ADA DUPLIKAT DATA DI DATABASE !!!");
                        System.err.println("Solusi: Hapus file .db dan jalankan ulang agar UNIQUE constraint aktif.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("--- [DEBUG] END SAVE SCORE ---\n");
    }

    // 3. Buka Level Berikutnya
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
                    System.out.println("LOG: Level Unlocked -> " + nextLevel);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 4. Hitung Total Skor (Hanya untuk keperluan legacy, bisa dihapus jika tidak dipakai)
    public int calculateTotalScore(int userId) {
        // Karena sekarang saveScore memastikan hanya ada 1 baris per level, SUM aman digunakan.
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

    // 5. Cek Apakah Game Tamat
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
    
    // 6. Cek Tamat Semua Modul
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