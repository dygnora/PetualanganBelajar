package com.petualanganbelajar.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Database Initializer (SINGLE MODULE VERSION)
 * Status: Fokus Modul 1 (Angka) - Production Ready.
 * Modul Lain: Dinonaktifkan sementara (bisa diaktifkan kembali nanti).
 */
public class DatabaseInitializer {

    private static final Random rand = new Random();
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void createTables() {
        // --- STRUKTUR TABEL (SAMA) ---
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, avatar TEXT NOT NULL, bgm_volume INTEGER DEFAULT 80, sfx_volume INTEGER DEFAULT 100, is_active INTEGER DEFAULT 1);";
        String sqlModules = "CREATE TABLE IF NOT EXISTS modules (id INTEGER PRIMARY KEY, name TEXT NOT NULL, description TEXT);";
        String sqlQuestions = "CREATE TABLE IF NOT EXISTS questions (id INTEGER PRIMARY KEY AUTOINCREMENT, module_id INTEGER, level INTEGER, question_type TEXT, question_text TEXT, question_image TEXT, question_audio TEXT, option_a TEXT, option_b TEXT, option_c TEXT, correct_answer TEXT);";
        String sqlProgress = "CREATE TABLE IF NOT EXISTS user_progress (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, module_id INTEGER, highest_level_unlocked INTEGER DEFAULT 1);";
        String sqlResults = "CREATE TABLE IF NOT EXISTS game_results (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, module_id INTEGER, level INTEGER, score INTEGER, created_at TEXT, FOREIGN KEY(user_id) REFERENCES users(id));";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsers); stmt.execute(sqlModules);
            stmt.execute(sqlQuestions); stmt.execute(sqlProgress);
            stmt.execute(sqlResults);

            insertDefaultModules(conn);
            insertFinalQuestions(conn);

            System.out.println("LOG: Database siap. Hanya Modul 1 (Angka) yang aktif.");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void insertDefaultModules(Connection conn) {
        String countSql = "SELECT COUNT(*) AS total FROM modules";
        String insertSql = "INSERT INTO modules (id, name, description) VALUES (?, ?, ?)";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt("total") == 0) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    // --- HANYA MODUL 1 YANG DIAKTIFKAN ---
                    pstmt.setInt(1, 1); pstmt.setString(2, "ANGKA"); pstmt.setString(3, "Bantu Bobo Menyiapkan Makanan!"); pstmt.addBatch();
                    
                    // Modul lain dinonaktifkan sementara
                    /*
                    pstmt.setInt(1, 2); pstmt.setString(2, "HURUF"); pstmt.setString(3, "Bantu Cici Mengirim Undangan!"); pstmt.addBatch();
                    pstmt.setInt(1, 3); pstmt.setString(2, "WARNA"); pstmt.setString(3, "Bantu Moli Dekorasi Pesta!"); pstmt.addBatch();
                    pstmt.setInt(1, 4); pstmt.setString(2, "BENTUK"); pstmt.setString(3, "Bantu Tobi Bangun Panggung!"); pstmt.addBatch();
                    */
                    
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
                System.out.println("LOG: Generating Questions for Module 1 Only...");
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    // MODUL 1: ANGKA (Bobo) - AKTIF
                    genMathL1(pstmt); genMathL2(pstmt); genMathL3(pstmt);
                    
                    // Modul lain dinonaktifkan sementara (Code tetap disimpan di bawah)
                    /*
                    genLetterL1(pstmt); genLetterL2(pstmt); genLetterL3(pstmt);
                    genColor(pstmt);
                    genShape(pstmt);
                    */
                    
                    pstmt.executeBatch();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- HELPER OPSI ---
    private static int[] getWrongInts(int ans, int min, int max) {
        int w1, w2;
        do { w1 = rand.nextInt(max - min + 1) + min; } while (w1 == ans);
        do { w2 = rand.nextInt(max - min + 1) + min; } while (w2 == ans || w2 == w1);
        return new int[]{w1, w2};
    }

    // Helper Char (Disimpan untuk nanti)
    private static String[] getWrongChars(String ansStr) {
        char ans = ansStr.toUpperCase().charAt(0);
        char w1, w2;
        do { w1 = ALPHABET.charAt(rand.nextInt(ALPHABET.length())); } while (w1 == ans);
        do { w2 = ALPHABET.charAt(rand.nextInt(ALPHABET.length())); } while (w2 == ans || w2 == w1);
        return new String[]{String.valueOf(w1), String.valueOf(w2)};
    }

    // ==========================================
    // MODUL 1: ANGKA (Bobo - Food Theme) - AKTIF
    // ==========================================

    private static void genMathL1(PreparedStatement pstmt) throws SQLException {
        // LEVEL 1: Menghitung Stok (1-10)
        String[] foods = {"Apel", "Pisang", "Jeruk", "Donat", "Kue"};
        
        String[] templates = {
            "Perut Bobo lapar! Bantu hitung %s ini.",
            "Wah, ada %s lezat! Berapa jumlahnya?",
            "Teman-teman minta %s. Kita harus siapkan berapa?",
            "Nyam nyam! Ada berapa %s di meja?",
            "Bantu Bobo mengecek stok %s untuk pesta!"
        };

        List<String> deck = new ArrayList<>();
        for (String f : foods) {
            for (int n = 1; n <= 10; n++) { 
                deck.add(f + ":" + n); 
            }
        }
        Collections.shuffle(deck);

        for (int i = 0; i < 20; i++) {
            String[] parts = deck.get(i).split(":");
            String item = parts[0];
            int ans = Integer.parseInt(parts[1]);
            
            String template = templates[rand.nextInt(templates.length)];
            String text = String.format(template, item);

            int[] wrongs = getWrongInts(ans, 1, 10); 
            addQ(pstmt, 1, 1, "CHOICE", text, item.toLowerCase()+".png", null, 
                 ""+ans, ""+wrongs[0], ""+wrongs[1], ""+ans);
        }
    }

    private static void genMathL2(PreparedStatement pstmt) throws SQLException {
        // LEVEL 2: Menata Pesta (Urutan & Perbandingan)
        List<String[]> qPool = new ArrayList<>();

        // TIPE A: Urutan - 12 Soal
        String[] seqTemplates = {
            "Susun kursi tamu: %d, %d, ... Kursi selanjutnya nomor?",
            "Tiket masuk nomor %d, %d, ... Lalu nomor berapa?",
            "Toples madu diurutkan: %d, %d, ... Angka selanjutnya?"
        };
        List<Integer> starts = new ArrayList<>();
        for(int i=1; i<=18; i++) starts.add(i);
        Collections.shuffle(starts);

        for (int i = 0; i < 12; i++) {
            int s = starts.get(i % starts.size());
            String tpl = seqTemplates[rand.nextInt(seqTemplates.length)];
            String text = String.format(tpl, s, s+1);
            int ans = s + 2;
            qPool.add(new String[]{text, String.valueOf(ans), "SEQUENCE"});
        }

        // TIPE B: Perbandingan - 8 Soal
        for (int i = 0; i < 8; i++) {
            boolean askBigger = rand.nextBoolean();
            int target = rand.nextInt(9) + 2; 
            
            String text; int ansVal;
            if (askBigger) {
                text = "Wadah ini kurang! Cari angka LEBIH BESAR dari " + target + "!";
                ansVal = target + (rand.nextInt(3) + 1); 
            } else {
                text = "Piringnya kekecilan. Cari angka LEBIH KECIL dari " + target + "!";
                ansVal = target - (rand.nextInt(target - 1) + 1);
                if (ansVal < 1) ansVal = 1;
            }
            qPool.add(new String[]{text, String.valueOf(ansVal), "CHOICE"});
        }
        Collections.shuffle(qPool);

        for (String[] q : qPool) {
            String text = q[0];
            int ans = Integer.parseInt(q[1]);
            String type = q[2];
            
            int w1, w2;
            if (type.equals("SEQUENCE")) {
                w1 = ans - 3; w2 = ans + 2;
            } else {
                if (text.contains("BESAR")) { w1 = ans - 5; w2 = ans - 3; }
                else { w1 = ans + 3; w2 = ans + 5; }
            }
            if (w1 <= 0) w1 = ans + 4; if (w2 <= 0) w2 = ans + 5;
            if (w1 == ans) w1 = ans + 1;
            if (w2 == ans || w2 == w1) w2 = (w1 == ans + 1) ? ans + 2 : ans + 1;

            addQ(pstmt, 1, 2, "CHOICE", text, null, null, ""+ans, ""+w1, ""+w2, ""+ans);
        }
    }

    private static void genMathL3(PreparedStatement pstmt) throws SQLException {
        // LEVEL 3: Operasi Hitung (Cerita Makanan)
        List<String[]> questionPool = new ArrayList<>();

        // A. Penjumlahan (10 Soal)
        String[] addItems = {"Madu", "Donat", "Ikan", "Roti"};
        for (int i = 0; i < 10; i++) {
            int a = rand.nextInt(5) + 1; int b = rand.nextInt(5) + 1;
            int sum = a + b;
            String item = addItems[rand.nextInt(addItems.length)];
            
            String text;
            if (rand.nextBoolean()) text = "Bobo punya " + a + " " + item + ", dapat " + b + " lagi. Totalnya? (" + a + "+" + b + ")";
            else text = "Ada " + a + " " + item + " ditambah " + b + " " + item + " jadi berapa?";
            
            questionPool.add(new String[]{text, String.valueOf(sum)});
        }

        // B. Pengurangan (7 Soal)
        String[] subItems = {"Kue", "Pisang", "Permen", "Apel"};
        int countSub = 0;
        while (countSub < 7) {
            int start = rand.nextInt(8) + 2; 
            int take = rand.nextInt(start - 1) + 1; 
            int sisa = start - take;
            String item = subItems[rand.nextInt(subItems.length)];
            
            if (sisa <= 5) {
                String text;
                if (rand.nextBoolean()) text = "Ada " + start + " " + item + ", dimakan " + take + ". Sisa berapa? (" + start + "-" + take + ")";
                else text = "Bobo bawa " + start + " " + item + ", jatuh " + take + ". Tinggal berapa?";
                
                questionPool.add(new String[]{text, String.valueOf(sisa)});
                countSub++;
            }
        }

        // C. Konsep Nol (3 Soal)
        questionPool.add(new String[]{"5 Apel ditambah 0 (tidak ada). Jadi berapa?", "5"});
        questionPool.add(new String[]{"3 Ikan dikurangi 0 (tidak diambil). Sisa?", "3"});
        questionPool.add(new String[]{"Punya 2 Kue, dimakan semua (dikurang 2). Sisa?", "0"});

        Collections.shuffle(questionPool);
        for (String[] qData : questionPool) {
            int ans = Integer.parseInt(qData[1]);
            int[] wrongs = getWrongInts(ans, 0, 10);
            addQ(pstmt, 1, 3, "CHOICE", qData[0], null, null, ""+ans, ""+wrongs[0], ""+wrongs[1], ""+ans);
        }
    }

    // ==========================================
    // MODUL LAIN (NONAKTIF / CADANGAN)
    // ==========================================
    // Kode di bawah ini disimpan untuk pengembangan masa depan.
    
    private static void genLetterL1(PreparedStatement pstmt) throws SQLException { /* Logic Disimpan */ }
    private static void genLetterL2(PreparedStatement pstmt) throws SQLException { /* Logic Disimpan */ }
    private static void genLetterL3(PreparedStatement pstmt) throws SQLException { /* Logic Disimpan */ }
    private static void genColor(PreparedStatement pstmt) throws SQLException { /* Logic Disimpan */ }
    private static void genShape(PreparedStatement pstmt) throws SQLException { /* Logic Disimpan */ }

    // --- HELPER INSERT ---
    private static void addQ(PreparedStatement pstmt, int modId, int lvl, String type, String text,
                             String img, String audio, String op1, String op2, String op3, String ans) throws SQLException {
        List<String> ops = new ArrayList<>();
        ops.add(op1); ops.add(op2); ops.add(op3);
        Collections.shuffle(ops);

        pstmt.setInt(1, modId);
        pstmt.setInt(2, lvl);
        pstmt.setString(3, type);
        pstmt.setString(4, text);
        pstmt.setString(5, img);
        pstmt.setString(6, audio);
        pstmt.setString(7, ops.get(0));
        pstmt.setString(8, ops.get(1));
        pstmt.setString(9, ops.get(2));
        pstmt.setString(10, ans);
        pstmt.addBatch();
    }
}