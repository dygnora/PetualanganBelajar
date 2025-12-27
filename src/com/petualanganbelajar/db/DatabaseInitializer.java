/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.db;
import java.sql.*;
/**
 *
 * @author DD
 */
public class DatabaseInitializer {
    public static void createTables() {
        // SQL dari Master Context v1.5
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "avatar TEXT NOT NULL, "
                + "bgm_volume INTEGER DEFAULT 80, "
                + "sfx_volume INTEGER DEFAULT 100, "
                + "is_active INTEGER DEFAULT 1);";

        String sqlModules = "CREATE TABLE IF NOT EXISTS modules ("
                + "id INTEGER PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "description TEXT);";

        String sqlQuestions = "CREATE TABLE IF NOT EXISTS questions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "module_id INTEGER, "
                + "level INTEGER, "
                + "question_text TEXT, "
                + "question_image TEXT, "
                + "question_audio TEXT, "
                + "option_a TEXT, "
                + "option_b TEXT, "
                + "option_c TEXT, "
                + "correct_answer TEXT);";

        String sqlProgress = "CREATE TABLE IF NOT EXISTS user_progress ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, "
                + "module_id INTEGER, "
                + "highest_level_unlocked INTEGER DEFAULT 1);";

        String sqlResults = "CREATE TABLE IF NOT EXISTS game_results ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_name TEXT, "
                + "avatar TEXT, "
                + "module_id INTEGER, "
                + "level INTEGER, "
                + "score INTEGER, "
                + "created_at TEXT);";

        // Eksekusi SQL
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sqlUsers);
            stmt.execute(sqlModules);
            stmt.execute(sqlQuestions);
            stmt.execute(sqlProgress);
            stmt.execute(sqlResults);
            
            // 2. ISI DATA DEFAULT (Seeding)
            insertDefaultModules(conn);
            insertDefaultQuestions(conn);
            
            System.out.println("LOG: Tabel Database & Data Awal Siap.");
            
        } catch (SQLException e) {
            System.err.println("ERROR: Gagal membuat tabel!");
            e.printStackTrace();
        }
    }
    
    // --- METHOD BARU UNTUK MENGISI DATA ---
    private static void insertDefaultModules(Connection conn) {
        String countSql = "SELECT COUNT(*) AS total FROM modules";
        String insertSql = "INSERT INTO modules (id, name, description) VALUES (?, ?, ?)";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next() && rs.getInt("total") == 0) {
                System.out.println("LOG: Tabel Modules kosong. Mengisi data default...");
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    // Modul 1: Angka
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "ANGKA");
                    pstmt.setString(3, "Belajar berhitung 1 sampai 10");
                    pstmt.addBatch();

                    // Modul 2: Huruf
                    pstmt.setInt(1, 2);
                    pstmt.setString(2, "HURUF");
                    pstmt.setString(3, "Mengenal abjad A sampai Z");
                    pstmt.addBatch();

                    // Modul 3: Warna
                    pstmt.setInt(1, 3);
                    pstmt.setString(2, "WARNA");
                    pstmt.setString(3, "Mengenal warna-warni ceria");
                    pstmt.addBatch();
                    
                    // Modul 4: Hewan
                    pstmt.setInt(1, 4);
                    pstmt.setString(2, "HEWAN");
                    pstmt.setString(3, "Mengenal nama-nama hewan");
                    pstmt.addBatch();

                    pstmt.executeBatch();
                    System.out.println("LOG: Data default modul berhasil dimasukkan!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void insertDefaultQuestions(Connection conn) {
        String countSql = "SELECT COUNT(*) AS total FROM questions";
        // Query Insert (ModuleID, Level, Text, Image, Audio, OptA, OptB, OptC, Correct)
        String insertSql = "INSERT INTO questions (module_id, level, question_text, question_image, question_audio, option_a, option_b, option_c, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next() && rs.getInt("total") == 0) {
                System.out.println("LOG: Tabel Questions kosong. Mengisi data soal...");
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    // --- MODUL 1: ANGKA (Level 1) ---
                    
                    // Soal 1
                    pstmt.setInt(1, 1); // Module 1 (Angka)
                    pstmt.setInt(2, 1); // Level 1
                    pstmt.setString(3, "Berapakah jumlah apel ini? (Bayangkan ada 2 apel)"); 
                    pstmt.setString(4, "apel_2.png"); // Nanti kita pakai gambar
                    pstmt.setString(5, null);
                    pstmt.setString(6, "1");
                    pstmt.setString(7, "2");
                    pstmt.setString(8, "3");
                    pstmt.setString(9, "2"); // Jawaban Benar
                    pstmt.addBatch();

                    // Soal 2
                    pstmt.setInt(1, 1); 
                    pstmt.setInt(2, 1);
                    pstmt.setString(3, "Angka berapakah ini? [ 5 ]");
                    pstmt.setString(4, null);
                    pstmt.setString(5, null);
                    pstmt.setString(6, "Lima");
                    pstmt.setString(7, "Dua");
                    pstmt.setString(8, "Tiga");
                    pstmt.setString(9, "Lima");
                    pstmt.addBatch();
                    
                    // Soal 3
                    pstmt.setInt(1, 1);
                    pstmt.setInt(2, 1);
                    pstmt.setString(3, "Mana yang merupakan angka SEPULUH?");
                    pstmt.setString(4, null);
                    pstmt.setString(5, null);
                    pstmt.setString(6, "10");
                    pstmt.setString(7, "01");
                    pstmt.setString(8, "100");
                    pstmt.setString(9, "10");
                    pstmt.addBatch();

                    pstmt.executeBatch();
                    System.out.println("LOG: Data soal default berhasil dimasukkan!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
