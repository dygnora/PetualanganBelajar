package com.petualanganbelajar.db;

import com.petualanganbelajar.content.*; // Import package baru
import java.sql.*;

public class DatabaseInitializer {

    public static void createTables() {
        // 1. Definisi Tabel Lama
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, avatar TEXT NOT NULL, bgm_volume INTEGER DEFAULT 80, sfx_volume INTEGER DEFAULT 100, is_active INTEGER DEFAULT 1);";
        String sqlModules = "CREATE TABLE IF NOT EXISTS modules (id INTEGER PRIMARY KEY, name TEXT NOT NULL, description TEXT);";
        String sqlQuestions = "CREATE TABLE IF NOT EXISTS questions (id INTEGER PRIMARY KEY AUTOINCREMENT, module_id INTEGER, level INTEGER, question_type TEXT, question_text TEXT, question_image TEXT, question_audio TEXT, option_a TEXT, option_b TEXT, option_c TEXT, correct_answer TEXT);";
        String sqlProgress = "CREATE TABLE IF NOT EXISTS user_progress (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, module_id INTEGER, highest_level_unlocked INTEGER DEFAULT 1);";
        String sqlResults = "CREATE TABLE IF NOT EXISTS game_results (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, module_id INTEGER, level INTEGER, score INTEGER, created_at TEXT, FOREIGN KEY(user_id) REFERENCES users(id));";

        // 2. [BARU] Definisi Tabel Story Progress
        // Kita tambahkan di sini agar terkumpul dengan teman-temannya
        String sqlStory = "CREATE TABLE IF NOT EXISTS story_progress (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "module_id INTEGER NOT NULL, " +
                "level INTEGER NOT NULL, " +
                "story_type TEXT NOT NULL, " +
                "seen_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE(user_id, module_id, level, story_type));";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            // 3. Eksekusi Pembuatan Tabel
            stmt.execute(sqlUsers); 
            stmt.execute(sqlModules);
            stmt.execute(sqlQuestions); 
            stmt.execute(sqlProgress);
            stmt.execute(sqlResults);
            stmt.execute(sqlStory); 

            insertDefaultModules(conn);
            insertFinalQuestions(conn);

            System.out.println("LOG: Database Initialization Complete.");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void insertDefaultModules(Connection conn) {
        String countSql = "SELECT COUNT(*) AS total FROM modules";
        String insertSql = "INSERT INTO modules (id, name, description) VALUES (?, ?, ?)";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt("total") == 0) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, 1); pstmt.setString(2, "ANGKA"); pstmt.setString(3, "Bantu Bobo Menyiapkan Makanan!"); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setString(2, "HURUF"); pstmt.setString(3, "Bantu Cici Mengantar Undangan!"); pstmt.addBatch();
                    pstmt.setInt(1, 3); pstmt.setString(2, "WARNA"); pstmt.setString(3, "Bantu Moli Menghias Pesta!"); pstmt.addBatch();
                    pstmt.setInt(1, 4); pstmt.setString(2, "BENTUK"); pstmt.setString(3, "Ayo Mengenal Bangun Datar!"); pstmt.addBatch();
                    pstmt.executeBatch();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void insertFinalQuestions(Connection conn) {
        String countSql = "SELECT COUNT(*) AS total FROM questions";
        String insertSql = "INSERT INTO questions (module_id, level, question_type, question_text, question_image, question_audio, option_a, option_b, option_c, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt("total") == 0) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    
                    // PANGGIL GENERATOR DARI FILE TERPISAH
                    MathContent.generate(pstmt);
                    LetterContent.generate(pstmt);
                    ColorContent.generate(pstmt);
                    ShapeContent.generate(pstmt);
                    
                    pstmt.executeBatch();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}