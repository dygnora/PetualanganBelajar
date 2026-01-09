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
    public int getTotalScoreByUserId(int userId) {
        String sql = "SELECT SUM(score) as total FROM game_results WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
