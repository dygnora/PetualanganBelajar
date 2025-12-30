package com.petualanganbelajar.repository;

import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.model.LeaderboardEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository untuk Papan Peringkat.
 * Updated: Menampilkan User dengan Total Skor tertinggi (Join Table).
 */
public class LeaderboardRepository {

    public List<LeaderboardEntry> getTopScores() {
        List<LeaderboardEntry> list = new ArrayList<>();
        
        // QUERY BARU: Menggabungkan (JOIN) tabel users dan game_results
        String sql = "SELECT u.name, u.avatar, SUM(g.score) as total_score " +
                     "FROM game_results g " +
                     "JOIN users u ON g.user_id = u.id " +
                     "GROUP BY u.id, u.name, u.avatar " + 
                     "ORDER BY total_score DESC " +
                     "LIMIT 10";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new LeaderboardEntry(
                    rs.getString("name"),       // Ambil dari tabel users
                    rs.getString("avatar"),     // Ambil dari tabel users
                    "Total XP", 
                    rs.getInt("total_score")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}