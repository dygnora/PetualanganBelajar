package com.petualanganbelajar.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class DatabaseConnection {

    private static Connection connection = null;

    // Method untuk mendapatkan lokasi database yang AMAN (Writeable)
    private static String getDatabasePath() {
        // Ambil folder "AppData" user (C:\Users\NamaUser\AppData\Roaming\)
        String appData = System.getenv("APPDATA");
        
        // Jika bukan Windows (Mac/Linux), pakai User Home
        if (appData == null) {
            appData = System.getProperty("user.home");
        }

        // Buat folder khusus untuk game kamu
        File gameFolder = new File(appData, "PetualanganBelajar");
        if (!gameFolder.exists()) {
            gameFolder.mkdirs(); // Buat folder jika belum ada
        }

        // Return path lengkap ke file database
        // Hasil: C:\Users\Budi\AppData\Roaming\PetualanganBelajar\game.db
        return "jdbc:sqlite:" + gameFolder.getAbsolutePath() + File.separator + "game.db";
    }

    public static Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load Driver
                Class.forName("org.sqlite.JDBC");
                
                // Koneksi ke path dinamis
                connection = DriverManager.getConnection(getDatabasePath());
                
                // System.out.println("LOG: DB Connected -> " + getDatabasePath());
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