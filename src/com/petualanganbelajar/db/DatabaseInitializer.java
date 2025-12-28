package com.petualanganbelajar.db;

import java.sql.*;

/**
 * Inisialisasi Database & Seeding Data Awal.
 * Updated: 
 * 1. Clean Asset (Aset Tunggal & Logic Math).
 * 2. Modul Huruf Level 1: Choice (Besar vs Kecil).
 * 3. Modul Huruf Level 2: Keypad (Teka-teki posisi huruf).
 * 4. Modul Huruf Level 3: Typing (Mengetik Kata Penuh dengan Clue).
 */
public class DatabaseInitializer {

    public static void createTables() {
        // 1. Tabel Users
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "avatar TEXT NOT NULL, "
                + "bgm_volume INTEGER DEFAULT 80, "
                + "sfx_volume INTEGER DEFAULT 100, "
                + "is_active INTEGER DEFAULT 1);";

        // 2. Tabel Modules
        String sqlModules = "CREATE TABLE IF NOT EXISTS modules ("
                + "id INTEGER PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "description TEXT);";

        // 3. Tabel Questions
        String sqlQuestions = "CREATE TABLE IF NOT EXISTS questions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "module_id INTEGER, "
                + "level INTEGER, "
                + "question_type TEXT, " // CHOICE, KEYPAD, CLICK, SEQUENCE, TYPING, COUNTING, MATH
                + "question_text TEXT, "
                + "question_image TEXT, "
                + "question_audio TEXT, "
                + "option_a TEXT, "
                + "option_b TEXT, "
                + "option_c TEXT, "
                + "correct_answer TEXT);";

        // 4. Tabel Progress
        String sqlProgress = "CREATE TABLE IF NOT EXISTS user_progress ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, "
                + "module_id INTEGER, "
                + "highest_level_unlocked INTEGER DEFAULT 1);";

        // 5. Tabel Hasil Game
        String sqlResults = "CREATE TABLE IF NOT EXISTS game_results ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_name TEXT, "
                + "avatar TEXT, "
                + "module_id INTEGER, "
                + "level INTEGER, "
                + "score INTEGER, "
                + "created_at TEXT);";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            // Buat Tabel
            stmt.execute(sqlUsers);
            stmt.execute(sqlModules);
            stmt.execute(sqlQuestions);
            stmt.execute(sqlProgress);
            stmt.execute(sqlResults);

            // Isi Data
            insertDefaultModules(conn);
            insertDefaultQuestions(conn);

            System.out.println("LOG: Database siap & Data Soal Lengkap.");

        } catch (SQLException e) {
            System.err.println("ERROR: Gagal inisialisasi database!");
            e.printStackTrace();
        }
    }

    private static void insertDefaultModules(Connection conn) {
        String countSql = "SELECT COUNT(*) AS total FROM modules";
        String insertSql = "INSERT INTO modules (id, name, description) VALUES (?, ?, ?)";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next() && rs.getInt("total") == 0) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, 1); pstmt.setString(2, "ANGKA"); pstmt.setString(3, "Bantu Bobo berhitung untuk pesta!"); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setString(2, "HURUF"); pstmt.setString(3, "Bantu Cici menulis undangan!"); pstmt.addBatch();
                    pstmt.setInt(1, 3); pstmt.setString(2, "WARNA"); pstmt.setString(3, "Bantu Moli menghias panggung!"); pstmt.addBatch();
                    pstmt.setInt(1, 4); pstmt.setString(2, "BENTUK"); pstmt.setString(3, "Bantu Tobi membangun gerbang!"); pstmt.addBatch();
                    pstmt.executeBatch();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertDefaultQuestions(Connection conn) {
        String countSql = "SELECT COUNT(*) AS total FROM questions";
        String insertSql = "INSERT INTO questions (module_id, level, question_type, question_text, "
                + "question_image, question_audio, option_a, option_b, option_c, correct_answer) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next() && rs.getInt("total") == 0) {
                System.out.println("LOG: Mengisi 60 Soal (Full Content)...");
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

                    // ========================================================
                    // MODUL 1: ANGKA (Bobo)
                    // ========================================================
                    
                    // --- Level 1: COUNTING (Gambar Dinamis dari aset tunggal) ---
                    addQuestion(pstmt, 1, 1, "COUNTING", "Ada berapa apel di meja?", "apel.png", null, "2", "3", "5", "3");
                    addQuestion(pstmt, 1, 1, "COUNTING", "Hitung jumlah pisang ini!", "pisang.png", null, "2", "4", "1", "2");
                    addQuestion(pstmt, 1, 1, "COUNTING", "Berapa jari yang diangkat?", "jari.png", null, "3", "4", "5", "5");
                    addQuestion(pstmt, 1, 1, "COUNTING", "Ada berapa bola?", "bola.png", null, "1", "3", "2", "1");
                    addQuestion(pstmt, 1, 1, "COUNTING", "Hitung jumlah kucing!", "kucing.png", null, "2", "4", "5", "4");

                    // --- Level 2: SEQUENCE (Urutan Angka - Tanpa Gambar) ---
                    addQuestion(pstmt, 1, 2, "SEQUENCE", "Angka berapa setelah 2?", null, null, "1", "3", "4", "3");
                    addQuestion(pstmt, 1, 2, "SEQUENCE", "Lengkapi urutannya: 4, 5, ...", null, null, "6", "3", "7", "6");
                    addQuestion(pstmt, 1, 2, "SEQUENCE", "Mana angka yang LEBIH BESAR dari 5?", null, null, "3", "4", "8", "8");
                    addQuestion(pstmt, 1, 2, "SEQUENCE", "Mana angka yang LEBIH KECIL dari 3?", null, null, "5", "2", "4", "2");
                    addQuestion(pstmt, 1, 2, "SEQUENCE", "Urutkan: 1, 2, 3, ...", null, null, "5", "4", "0", "4");

                    // --- Level 3: MATH (Penjumlahan Visual Dinamis) ---
                    // option_a menyimpan RUMUS: "jumlah_kiri|jumlah_kanan"
                    addQuestion(pstmt, 1, 3, "MATH", "Berapa hasil penjumlahan ini?", "apel.png", null, "2|1", null, null, "3");
                    addQuestion(pstmt, 1, 3, "MATH", "1 + 1 sama dengan berapa?", "pisang.png", null, "1|1", null, null, "2");
                    addQuestion(pstmt, 1, 3, "MATH", "Berapa jumlah semua bola?", "bola.png", null, "3|2", null, null, "5");
                    addQuestion(pstmt, 1, 3, "MATH", "2 Kucing datang lagi 2 Kucing?", "kucing.png", null, "2|2", null, null, "4");
                    addQuestion(pstmt, 1, 3, "MATH", "Ada 4 Jari, tambah 1 Jari?", "jari.png", null, "4|1", null, null, "5");


                    // ========================================================
                    // MODUL 2: HURUF (Cici) -> REVISI KEYPAD & TYPING FULL
                    // ========================================================

                    // --- Level 1: CHOICE (Pasangan Huruf Besar & Kecil) ---
                    addQuestion(pstmt, 2, 1, "CHOICE", "Mana huruf kecil untuk 'A'?", null, null, "b", "d", "a", "a");
                    addQuestion(pstmt, 2, 1, "CHOICE", "Mana HURUF BESAR untuk 'b'?", null, null, "B", "P", "D", "B");
                    addQuestion(pstmt, 2, 1, "CHOICE", "Cari pasangan kecil huruf 'E'!", null, null, "f", "e", "c", "e");
                    addQuestion(pstmt, 2, 1, "CHOICE", "Mana huruf kecil 'M' (Kaki Tiga)?", null, null, "w", "n", "m", "m");
                    addQuestion(pstmt, 2, 1, "CHOICE", "Huruf besar 'r' yang mana?", null, null, "P", "R", "K", "R");

                    // --- Level 2: KEYPAD (Teka-Teki Huruf Hilang - Variasi Posisi) ---
                    addQuestion(pstmt, 2, 2, "KEYPAD", "_ - Y - A - M. Huruf depannya?", "ayam.png", null, null, null, null, "A");
                    addQuestion(pstmt, 2, 2, "KEYPAD", "Lengkapi: B - ... - L - A", "bola.png", null, null, null, null, "O");
                    addQuestion(pstmt, 2, 2, "KEYPAD", "Huruf terakhirnya? I - K - A - ...", "ikan.png", null, null, null, null, "N");
                    addQuestion(pstmt, 2, 2, "KEYPAD", "U - ... - A - R. Hewan apa ini?", "ular.png", null, null, null, null, "L");
                    addQuestion(pstmt, 2, 2, "KEYPAD", "Huruf awal hewan ini adalah?", "rusa.png", null, null, null, null, "R");

                    // --- Level 3: TYPING (Mengetik Kata Penuh dengan Clue) ---
                    // Konsep: Anak melihat gambar dan clue ejaan, lalu mengetik ulang kata tersebut.
                    // Correct Answer sekarang adalah KATA LENGKAP.
                    addQuestion(pstmt, 2, 3, "TYPING", "Lihat Gambar! Ketik kata: BUKU", "buku.png", null, null, null, null, "BUKU");
                    addQuestion(pstmt, 2, 3, "TYPING", "Tempat menulis. Ketik: MEJA", "meja.png", null, null, null, null, "MEJA");
                    addQuestion(pstmt, 2, 3, "TYPING", "Hasilkan susu. Ketik: SAPI", "sapi.png", null, null, null, null, "SAPI");
                    addQuestion(pstmt, 2, 3, "TYPING", "Untuk ditendang. Ketik: BOLA", "bola.png", null, null, null, null, "BOLA"); 
                    addQuestion(pstmt, 2, 3, "TYPING", "Hewan air. Ketik: IKAN", "ikan.png", null, null, null, null, "IKAN"); 


                    // ========================================================
                    // MODUL 3: WARNA (Moli)
                    // ========================================================

                    // --- Level 1: CHOICE (Warna Dasar) ---
                    addQuestion(pstmt, 3, 1, "CHOICE", "Mana balon warna MERAH?", "balon_merah.png", null, "Biru", "Merah", "Kuning", "Merah");
                    addQuestion(pstmt, 3, 1, "CHOICE", "Apa warna Pisang?", "pisang.png", null, "Kuning", "Hijau", "Ungu", "Kuning");
                    addQuestion(pstmt, 3, 1, "CHOICE", "Langit cerah warnanya...", "langit.png", null, "Biru", "Merah", "Coklat", "Biru");
                    addQuestion(pstmt, 3, 1, "CHOICE", "Daun sehat warnanya apa?", "daun.png", null, "Hitam", "Putih", "Hijau", "Hijau");
                    addQuestion(pstmt, 3, 1, "CHOICE", "Mana cat warna UNGU?", "cat_ungu.png", null, "Merah", "Jingga", "Ungu", "Ungu");

                    // --- Level 2: CLICK (Identifikasi Objek) ---
                    addQuestion(pstmt, 3, 2, "CLICK", "Klik benda berwarna MERAH!", null, null, null, null, null, "Apel");
                    addQuestion(pstmt, 3, 2, "CLICK", "Klik benda berwarna KUNING!", null, null, null, null, null, "Lemon");
                    addQuestion(pstmt, 3, 2, "CLICK", "Mana yang warnanya BIRU?", null, null, null, null, null, "Laut");
                    addQuestion(pstmt, 3, 2, "CLICK", "Klik warna HITAM!", null, null, null, null, null, "Aspal");
                    addQuestion(pstmt, 3, 2, "CLICK", "Cari warna PUTIH!", null, null, null, null, null, "Awan");

                    // --- Level 3: SEQUENCE (Pola Warna) ---
                    addQuestion(pstmt, 3, 3, "SEQUENCE", "Merah - Biru - Merah - ...?", null, null, "Biru", "Hijau", "Kuning", "Biru");
                    addQuestion(pstmt, 3, 3, "SEQUENCE", "Hijau - Hijau - Kuning - ...?", null, null, "Merah", "Kuning", "Biru", "Kuning");
                    addQuestion(pstmt, 3, 3, "SEQUENCE", "Putih - Hitam - Putih - ...?", null, null, "Merah", "Hitam", "Biru", "Hitam");
                    addQuestion(pstmt, 3, 3, "SEQUENCE", "Biru - Merah - Biru - ...?", null, null, "Merah", "Kuning", "Hijau", "Merah");
                    addQuestion(pstmt, 3, 3, "SEQUENCE", "Campur Merah & Kuning jadi?", null, null, "Biru", "Ungu", "Jingga", "Jingga");


                    // ========================================================
                    // MODUL 4: BENTUK (Tobi)
                    // ========================================================

                    // --- Level 1: CHOICE (Mengenal Bentuk) ---
                    addQuestion(pstmt, 4, 1, "CHOICE", "Mana benda berbentuk LINGKARAN?", "roda.png", null, "Kotak", "Pintu", "Roda", "Roda");
                    addQuestion(pstmt, 4, 1, "CHOICE", "Pintu rumah bentuknya...", "pintu.png", null, "Persegi Panjang", "Bulat", "Segitiga", "Persegi Panjang");
                    addQuestion(pstmt, 4, 1, "CHOICE", "Atap rumah biasanya berbentuk...", "atap.png", null, "Segitiga", "Lingkaran", "Kotak", "Segitiga");
                    addQuestion(pstmt, 4, 1, "CHOICE", "Telur bentuknya...", "telur.png", null, "Lonjong", "Kotak", "Segitiga", "Lonjong");
                    addQuestion(pstmt, 4, 1, "CHOICE", "Mana yang berbentuk KOTAK?", "kardus.png", null, "Bola", "Kardus", "Topi", "Kardus");

                    // --- Level 2: CLICK (Identifikasi Objek) ---
                    addQuestion(pstmt, 4, 2, "CLICK", "Klik gambar SEGITIGA!", null, null, null, null, null, "Segitiga");
                    addQuestion(pstmt, 4, 2, "CLICK", "Klik gambar KOTAK!", null, null, null, null, null, "Kotak");
                    addQuestion(pstmt, 4, 2, "CLICK", "Klik gambar LINGKARAN!", null, null, null, null, null, "Lingkaran");
                    addQuestion(pstmt, 4, 2, "CLICK", "Mana bentuk BINTANG?", null, null, null, null, null, "Bintang");
                    addQuestion(pstmt, 4, 2, "CLICK", "Klik bentuk HATI (Love)!", null, null, null, null, null, "Hati");

                    // --- Level 3: CLICK (Logika Bentuk) ---
                    addQuestion(pstmt, 4, 3, "CLICK", "Dua Segitiga digabung jadi?", "tangram_kotak.png", null, null, null, null, "Kotak");
                    addQuestion(pstmt, 4, 3, "CLICK", "Dua Kotak digabung jadi?", "tangram_panjang.png", null, null, null, null, "Persegi Panjang");
                    addQuestion(pstmt, 4, 3, "CLICK", "Benda apa yang bulat?", null, null, null, null, null, "Bola");
                    addQuestion(pstmt, 4, 3, "CLICK", "Benda apa yang kotak?", null, null, null, null, null, "Dadu");
                    addQuestion(pstmt, 4, 3, "CLICK", "Roda bentuknya apa?", null, null, null, null, null, "Lingkaran");

                    pstmt.executeBatch();
                    System.out.println("LOG: 60 Soal Berhasil Dimasukkan!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addQuestion(PreparedStatement pstmt, int modId, int lvl, String type, String text, String img, String audio, String opA, String opB, String opC, String ans) throws SQLException {
        pstmt.setInt(1, modId);
        pstmt.setInt(2, lvl);
        pstmt.setString(3, type);
        pstmt.setString(4, text);
        pstmt.setString(5, img);
        pstmt.setString(6, audio);
        pstmt.setString(7, opA);
        pstmt.setString(8, opB);
        pstmt.setString(9, opC);
        pstmt.setString(10, ans);
        pstmt.addBatch();
    }
}