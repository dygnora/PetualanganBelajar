package com.petualanganbelajar.repository;

import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.model.LeaderboardEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository untuk Papan Peringkat.
 * Updated: Menampilkan User dengan Total Skor tertinggi (Akumulasi).
 */
public class LeaderboardRepository {

    public List<LeaderboardEntry> getTopScores() {
        List<LeaderboardEntry> list = new ArrayList<>();
        
        // [UPDATE] Query menjumlahkan (SUM) semua skor user dari tabel history
        String sql = "SELECT user_name, avatar, SUM(score) as total_score " +
                     "FROM game_results " +
                     "GROUP BY user_name " +
                     "ORDER BY total_score DESC " +
                     "LIMIT 10";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new LeaderboardEntry(
                    rs.getString("user_name"),
                    rs.getString("avatar"),
                    "Total XP", // Label diganti agar umum
                    rs.getInt("total_score")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}