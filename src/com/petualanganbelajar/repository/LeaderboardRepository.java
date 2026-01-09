package com.petualanganbelajar.repository;

import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.model.LeaderboardEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardRepository {

    // Method 1: Mengambil 10 Besar Peringkat berdasarkan TOTAL XP
    public List<LeaderboardEntry> getTopScores() {
        List<LeaderboardEntry> list = new ArrayList<>();

        // [FIX]
        // 1. Hapus "WHERE is_active = 1" agar user yang dihapus tetap muncul.
        // 2. Tambahkan "WHERE total_xp > 0" agar user baru (XP 0) tidak memenuhi leaderboard.
        String sql = 
            "SELECT name, avatar, level, total_xp AS total_score, is_active " +
            "FROM users " +
            "WHERE total_xp > 0 " + // Hanya tampilkan yang sudah pernah main
            "ORDER BY total_xp DESC " + 
            "LIMIT 10";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                boolean isActive = rs.getInt("is_active") == 1;
                
                // [OPSIONAL] Tandai user yang sudah dihapus
                if (!isActive) {
                    name = name + " (DIHAPUS)"; 
                }

                list.add(new LeaderboardEntry(
                    name,
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
    public int getTotalScoreByUserId(int userId) {
        String sql = "SELECT total_xp FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_xp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}