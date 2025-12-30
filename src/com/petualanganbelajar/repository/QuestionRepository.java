package com.petualanganbelajar.repository;

import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.model.QuestionModel;
import com.petualanganbelajar.model.QuestionType; // Import Enum baru
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionRepository {

    // [UBAH] Nama method dikembalikan ke getQuestionsByModule sesuai kode asli Anda
    public List<QuestionModel> getQuestionsByModule(int moduleId, int level) {
        List<QuestionModel> questions = new ArrayList<>();
        
        // Ambil 5 soal acak
        String sql = "SELECT * FROM questions WHERE module_id = ? AND level = ? ORDER BY RANDOM() LIMIT 5";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, moduleId);
            pstmt.setInt(2, level);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // --- KONVERSI STRING DB KE ENUM (SAFETY) ---
                String typeStr = rs.getString("question_type");
                QuestionType typeEnum = QuestionType.CHOICE; // Default
                try {
                    if (typeStr != null) {
                        typeEnum = QuestionType.valueOf(typeStr.toUpperCase());
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Tipe soal tidak dikenal di DB: " + typeStr);
                }
                // -------------------------------------------

                questions.add(new QuestionModel(
                    rs.getInt("id"),
                    rs.getInt("module_id"),
                    rs.getInt("level"),
                    typeEnum, // Masukkan Enum
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
    
    // Method Helper (Opsional, jika ada bagian lain yang butuh getQuestionsByLevel)
    public List<QuestionModel> getQuestionsByLevel(int moduleId, int level) {
        return getQuestionsByModule(moduleId, level);
    }
}