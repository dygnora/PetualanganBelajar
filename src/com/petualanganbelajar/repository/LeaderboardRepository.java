package com.petualanganbelajar.repository;

import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.model.LeaderboardEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardRepository {

    // Method 1: Mengambil 10 Besar Peringkat
    public List<LeaderboardEntry> getTopScores() {
        List<LeaderboardEntry> list = new ArrayList<>();

        String sql =
            "SELECT " +
            "COALESCE(u.name, 'Unknown') AS name, " +
            "COALESCE(u.avatar, 'default.png') AS avatar, " +
            "COALESCE(u.level, 1) AS level, " +
            "SUM(g.score) AS total_score " +
            "FROM game_results g " +
            "LEFT JOIN users u ON g.user_id = u.id " +   // 🔥 FIX UTAMA
            "GROUP BY g.user_id " +
            "ORDER BY total_score DESC " +
            "LIMIT 10";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new LeaderboardEntry(
                    rs.getString("name"),
                    rs.getString("avatar"),
                    rs.getInt("level"),
                    rs.getInt("total_score")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Method 2: Menghitung Total Skor Spesifik User
    // Method 2: Menghitung Total Skor Spesifik User (VERSI DEBUG)
    public int getTotalScoreByUserId(int userId) {
        System.out.println("\n--- [DEBUG] LEADERBOARD CHECK (User ID: " + userId + ") ---");
        
        // 1. Cek Detail Baris per Baris (Detektif)
        String detailSql = "SELECT id, module_id, level, score FROM game_results WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(detailSql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("Isi Tabel 'game_results' untuk user ini:");
            System.out.println("| ID | Modul | Level | Score |");
            System.out.println("|----|-------|-------|-------|");
            
            while (rs.next()) {
                int id = rs.getInt("id");
                int mod = rs.getInt("module_id");
                int lvl = rs.getInt("level");
                int scr = rs.getInt("score");
                System.out.println(String.format("| %-2d | %-5d | %-5d | %-5d |", id, mod, lvl, scr));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Hitung Total Sebenarnya
        String sql = "SELECT SUM(score) as total FROM game_results WHERE user_id = ?";
        int total = 0;
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total");
            }
            System.out.println(">> HASIL QUERY SUM(score): " + total);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("--- [DEBUG] END LEADERBOARD CHECK ---\n");
        return total;
    }
}
