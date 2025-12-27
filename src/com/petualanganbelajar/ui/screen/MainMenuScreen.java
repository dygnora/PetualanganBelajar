/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.ui.screen;
import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.repository.UserRepository;
import com.petualanganbelajar.model.UserModel;
import javax.swing.*;
import java.awt.*;
import java.util.List;
/**
 *
 * @author DD
 */
public class MainMenuScreen extends JPanel {
    // VARIABEL GLOBAL DI CLASS INI
    private final UserRepository userRepo;
    private JButton btnContinue;
    private JButton btnNewGame;

    // --- BAGIAN INI YANG TADI HILANG (CONSTRUCTOR) ---
    public MainMenuScreen() {
        this.userRepo = new UserRepository();
        
        setLayout(new GridBagLayout());
        setBackground(GameConfig.COLOR_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0); 
        gbc.gridx = 0;
        
        // Baris 0: JUDUL
        JLabel title = new JLabel("MENU UTAMA");
        title.setFont(GameConfig.FONT_TITLE);
        title.setForeground(GameConfig.COLOR_PRIMARY);
        gbc.gridy = 0;
        add(title, gbc);
        
        // Baris 1: LANJUTKAN
        btnContinue = createMenuButton("LANJUTKAN PETUALANGAN");
        btnContinue.addActionListener(e -> {
            ScreenManager.getInstance().showScreen("PROFILE_SELECT");
        });
        gbc.gridy = 1;
        add(btnContinue, gbc);
        
        // Baris 2: MULAI BARU
        btnNewGame = createMenuButton("MULAI BARU");
        btnNewGame.addActionListener(e -> {
            List<UserModel> users = userRepo.getAllActiveUsers();
            if (users.size() >= 3) {
                JOptionPane.showMessageDialog(this, 
                    "Slot profil penuh (Maksimal 3).\nSilakan 'Lanjutkan' dan hapus salah satu profil lama.",
                    "Slot Penuh", JOptionPane.WARNING_MESSAGE);
            } else {
                ScreenManager.getInstance().showScreen("PROFILE_CREATE");
            }
        });
        gbc.gridy = 2;
        add(btnNewGame, gbc);
        
        // Baris 3: PAPAN JUARA (Perbaikan: gridy = 3)
        JButton btnLeaderboard = createMenuButton("PAPAN JUARA");
        btnLeaderboard.addActionListener(e -> {
            ScreenManager.getInstance().showScreen("LEADERBOARD");
        });
        gbc.gridy = 3; 
        add(btnLeaderboard, gbc);

        // Baris 4: PENGATURAN (Perbaikan: gridy = 4)
        JButton btnSettings = createMenuButton("PENGATURAN");
        btnSettings.addActionListener(e -> {
            ScreenManager.getInstance().showScreen("SETTINGS");
        });
        
        gbc.gridy = 4;
        add(btnSettings, gbc);
        gbc.gridy = 4;
        add(btnSettings, gbc);
        
        // Baris 5: KELUAR (Perbaikan: gridy = 5)
        JButton btnExit = createMenuButton("KELUAR");
        btnExit.setBackground(Color.RED);
        btnExit.addActionListener(e -> System.exit(0));
        gbc.gridy = 5;
        add(btnExit, gbc);
    }
    // --- AKHIR DARI CONSTRUCTOR ---
    
    // --- HELPER BIKIN TOMBOL BIAR RAPI ---
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(300, 60));
        btn.setFont(GameConfig.FONT_SUBTITLE);
        btn.setBackground(GameConfig.COLOR_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    // --- LOGIKA VALIDASI (Master Context 4.1) ---
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            // Setiap menu dibuka, cek database
            // Pastikan userRepo sudah di-init di constructor
            if (userRepo != null) {
                List<UserModel> users = userRepo.getAllActiveUsers();
                
                // Jika tidak ada user (0), tombol Lanjutkan dimatikan
                if (users.isEmpty()) {
                    btnContinue.setEnabled(false);
                    btnContinue.setText("BELUM ADA DATA");
                    btnContinue.setBackground(Color.GRAY);
                } else {
                    btnContinue.setEnabled(true);
                    btnContinue.setText("LANJUTKAN PETUALANGAN");
                    btnContinue.setBackground(GameConfig.COLOR_ACCENT);
                }
            }
        }
    }
}
