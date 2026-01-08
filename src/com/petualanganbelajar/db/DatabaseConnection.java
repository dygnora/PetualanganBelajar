package com.petualanganbelajar.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class DatabaseConnection {

    private static Connection connection = null;

    // Method untuk mendapatkan lokasi database yang AMAN (Writeable)
    private static String getDatabasePath() {
        // Ambil folder "AppData" user (Contoh: C:\Users\NamaUser\AppData\Roaming\)
        String appData = System.getenv("APPDATA");
        
        // Fallback untuk Mac/Linux (User Home)
        if (appData == null) {
            appData = System.getProperty("user.home");
        }

        // Buat folder khusus untuk game
        File gameFolder = new File(appData, "PetualanganBelajar");
        if (!gameFolder.exists()) {
            gameFolder.mkdirs(); // Buat folder jika belum ada
        }

        // Return connection string lengkap
        return "jdbc:sqlite:" + gameFolder.getAbsolutePath() + File.separator + "game.db";
    }

    public static Connection connect() {
        try {
            // [PENTING] Cek apakah connection null ATAU sudah tertutup (closed)
            // Ini mencegah error "SQLException: The database has been closed"
            if (connection == null || connection.isClosed()) {
                
                // Load Driver SQLite
                Class.forName("org.sqlite.JDBC");
                
                // Buat koneksi baru ke path dinamis
                connection = DriverManager.getConnection(getDatabasePath());
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("ERROR: Gagal koneksi database! " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}