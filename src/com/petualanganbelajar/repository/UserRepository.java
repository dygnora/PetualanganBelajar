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
 *
 * @author DD
 */
public class UserRepository {
    // 1. AMBIL SEMUA USER AKTIF (Maksimal 3 slot)
    public List<UserModel> getAllActiveUsers() {
        List<UserModel> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = 1 ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UserModel user = new UserModel(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("avatar"),
                    rs.getInt("bgm_volume"),
                    rs.getInt("sfx_volume"),
                    rs.getInt("is_active") == 1
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error ambil data user: " + e.getMessage());
        }
        return users;
    }

    // 2. TAMBAH USER BARU
    public boolean createUser(String name, String avatar) {
        // Cek dulu apakah slot penuh (< 3)
        if (getAllActiveUsers().size() >= 3) {
            System.out.println("Slot Penuh! Tidak bisa tambah user.");
            return false;
        }

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
    // Kita tidak benar-benar menghapus data, cuma set is_active = 0
    // Agar histori skor di Leaderboard tidak hilang.
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
            System.out.println("LOG: Volume user " + userId + " diupdate (BGM:" + bgmVol + ", SFX:" + sfxVol + ")");
            
        } catch (SQLException e) {
            System.err.println("Gagal update volume: " + e.getMessage());
        }
    }
    
}
