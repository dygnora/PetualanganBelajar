/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.repository;

import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.model.UserModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository untuk mengelola data User (CRUD + XP Logic).
 * @author DD
 */
public class UserRepository {
    
    // 1. AMBIL SEMUA USER AKTIF
    public List<UserModel> getAllActiveUsers() {
        List<UserModel> users = new ArrayList<>();
        // [UPDATE SQL] Kita ambil kolom 'level' dan 'total_xp'
        String sql = "SELECT * FROM users WHERE is_active = 1 ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Pastikan Constructor UserModel Anda sesuai dengan urutan ini
                UserModel user = new UserModel(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("avatar"),
                    rs.getInt("level"),       // Level Karakter
                    rs.getInt("bgm_volume"),
                    rs.getInt("sfx_volume"),
                    rs.getInt("is_active") == 1
                );
                
                // [PENTING] Set Total XP secara manual (karena mungkin tidak ada di constructor lama)
                // Pastikan Anda menambahkan method 'setTotalXP' di class UserModel!
                // Jika belum ada, tambahkan: public void setTotalXP(int xp) { this.totalXP = xp; }
                try {
                    user.setTotalXP(rs.getInt("total_xp"));
                } catch (Exception e) {
                    // Abaikan jika method belum ada di Model, tapi sebaiknya ditambahkan.
                    System.err.println("Warning: UserModel belum memiliki method setTotalXP.");
                }
                
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error ambil data user: " + e.getMessage());
        }
        return users;
    }

    // 2. TAMBAH USER BARU
    public boolean createUser(String name, String avatar) {
        // Cek Slot (Maks 3)
        if (getAllActiveUsers().size() >= 3) {
            System.out.println("Slot Penuh! Tidak bisa tambah user.");
            return false;
        }

        // Level default 1 & total_xp 0 sudah diatur default di database
        String sql = "INSERT INTO users(name, avatar) VALUES(?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, avatar);
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Gagal buat user: " + e.getMessage());
            return false;
        }
    }

    // 3. HAPUS USER (Soft Delete)
    public boolean deleteUser(int userId) {
        String sql = "UPDATE users SET is_active = 0 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 4. UPDATE VOLUME SETTINGS
    public void updateVolume(int userId, int bgmVol, int sfxVol) {
        String sql = "UPDATE users SET bgm_volume = ?, sfx_volume = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bgmVol);
            pstmt.setInt(2, sfxVol);
            pstmt.setInt(3, userId);
            
            pstmt.executeUpdate();
            // System.out.println("LOG: Volume updated.");
            
        } catch (SQLException e) {
            System.err.println("Gagal update volume: " + e.getMessage());
        }
    }
    
    // 5. UPDATE LEVEL USER (Naik Level)
    public void updateUserLevel(int userId, int newLevel) {
        String sql = "UPDATE users SET level = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newLevel);
            pstmt.setInt(2, userId);
            
            pstmt.executeUpdate();
            System.out.println("LOG: Level user " + userId + " naik ke level " + newLevel);
            
        } catch (SQLException e) {
            System.err.println("Gagal update level: " + e.getMessage());
        }
    }
    
    // 6. [BARU] TAMBAH XP (Grinding Logic)
    // Ini dipanggil setiap kali selesai game untuk menambah XP bar
    public void addXP(int userId, int xpAmount) {
        String sql = "UPDATE users SET total_xp = total_xp + ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, xpAmount);
            pstmt.setInt(2, userId);
            
            pstmt.executeUpdate();
            System.out.println("LOG: XP User " + userId + " bertambah " + xpAmount);
            
        } catch (SQLException e) {
            System.err.println("Gagal tambah XP: " + e.getMessage());
        }
    }
    
    // 7. GET USER XP
    public int getUserXP(int userId) {
        String sql = "SELECT total_xp FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_xp");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}