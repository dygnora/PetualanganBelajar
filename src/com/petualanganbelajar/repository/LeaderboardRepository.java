/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.repository;
import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.model.LeaderboardEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author DD
 */
public class LeaderboardRepository {
    public List<LeaderboardEntry> getTopScores() {
        List<LeaderboardEntry> list = new ArrayList<>();
        
        // Ambil 10 Skor Tertinggi, urutkan dari yang terbesar (DESC)
        String sql = "SELECT gr.user_name, gr.avatar, gr.score, m.name as module_name " +
                     "FROM game_results gr " +
                     "LEFT JOIN modules m ON gr.module_id = m.id " +
                     "ORDER BY gr.score DESC " +
                     "LIMIT 10";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new LeaderboardEntry(
                    rs.getString("user_name"),
                    rs.getString("avatar"),
                    rs.getString("module_name"),
                    rs.getInt("score")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
