/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.repository;
import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.model.QuestionModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author DD
 */
public class QuestionRepository {
    // Ambil soal berdasarkan Modul & Level
    public List<QuestionModel> getQuestionsByModule(int moduleId, int level) {
        List<QuestionModel> questions = new ArrayList<>();
        // Ambil max 10 soal secara acak (RANDOM) agar tidak bosan
        String sql = "SELECT * FROM questions WHERE module_id = ? AND level = ? ORDER BY RANDOM() LIMIT 10";

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
