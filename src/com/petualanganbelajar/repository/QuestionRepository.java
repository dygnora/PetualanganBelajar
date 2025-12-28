package com.petualanganbelajar.repository;

import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.model.QuestionModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository untuk mengambil data soal dari database.
 * Updated: Mengambil maksimal 5 soal acak per sesi.
 */
public class QuestionRepository {

    public List<QuestionModel> getQuestionsByModule(int moduleId, int level) {
        List<QuestionModel> questions = new ArrayList<>();
        
        // [UPDATE] LIMIT diubah jadi 5
        String sql = "SELECT * FROM questions WHERE module_id = ? AND level = ? ORDER BY RANDOM() LIMIT 5";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, moduleId);
            pstmt.setInt(2, level);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                questions.add(new QuestionModel(
                    rs.getInt("id"),
                    rs.getInt("module_id"),
                    rs.getInt("level"),
                    rs.getString("question_type"),
                    rs.getString("question_text"),
                    rs.getString("question_image"),
                    rs.getString("question_audio"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("correct_answer")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
}