package com.petualanganbelajar.repository;

import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.model.LeaderboardEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardRepository {

    // Method 1: Mengambil 10 Besar Peringkat (Sudah ada sebelumnya)
    public List<LeaderboardEntry> getTopScores() {
        List<LeaderboardEntry> list = new ArrayList<>();
        
        String sql = "SELECT u.name, u.avatar, u.level, SUM(g.score) as total_score " +
                     "FROM game_results g " +
                     "JOIN users u ON g.user_id = u.id " +
                     "WHERE u.is_active = 1 " + 
                     "GROUP BY u.id, u.name, u.avatar, u.level " + 
                     "ORDER BY total_score DESC " +
                     "LIMIT 10";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int totalScore = rs.getInt("total_score");
                if (rs.wasNull()) totalScore = 0;

                list.add(new LeaderboardEntry(
                    rs.getString("name"),
                    rs.getString("avatar"),
                    rs.getInt("level"),
                    totalScore
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================================================================
    // [BARU] Method 2: Menghitung Total Skor Spesifik User (Untuk Level Up)
    // ==================================================================
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
        return 0; // Kembalikan 0 jika error atau belum ada skor
    }
}