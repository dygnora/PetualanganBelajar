package com.petualanganbelajar.repository;

import com.petualanganbelajar.db.DatabaseConnection;
import java.sql.*;

public class StoryRepository {

    // Cek: Apakah user ini sudah pernah lihat cerita ini?
    public boolean hasSeenStory(int userId, int moduleId, int level, String type) {
        String sql = "SELECT 1 FROM story_progress WHERE user_id=? AND module_id=? AND level=? AND story_type=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, moduleId);
            pstmt.setInt(3, level);
            pstmt.setString(4, type);
            
            return pstmt.executeQuery().next(); // Return true jika data ditemukan
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Simpan: Tandai cerita sudah dilihat
    public void markStoryAsSeen(int userId, int moduleId, int level, String type) {
        String sql = "INSERT OR IGNORE INTO story_progress (user_id, module_id, level, story_type) VALUES (?, ?, ?, ?)";
        // Gunakan Thread baru agar UI tidak macet saat simpan ke DB
        new Thread(() -> {
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, moduleId);
                pstmt.setInt(3, level);
                pstmt.setString(4, type);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }
}