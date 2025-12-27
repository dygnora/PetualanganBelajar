/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.model.ModuleModel;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author DD
 */
public class ResultScreen extends JPanel {
    
    private JLabel lblTitle;
    private JLabel lblScore;
    private JLabel lblStar;
    private JButton btnRetry;
    private JButton btnMenu;
    
    // Simpan info terakhir buat fitur Retry
    private ModuleModel lastModule;
    private int lastLevel;

    public ResultScreen() {
        setLayout(new GridBagLayout());
        setBackground(GameConfig.COLOR_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        
        // 1. Judul
        lblTitle = new JLabel("HASIL BELAJAR");
        lblTitle.setFont(GameConfig.FONT_TITLE);
        lblTitle.setForeground(GameConfig.COLOR_PRIMARY);
        gbc.gridy = 0;
        add(lblTitle, gbc);
        
        // 2. Bintang Visual (Emoji Besar)
        lblStar = new JLabel("⭐⭐⭐");
        lblStar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        gbc.gridy = 1;
        add(lblStar, gbc);
        
        // 3. Skor Angka
        lblScore = new JLabel("SKOR: 0 / 0");
        lblScore.setFont(GameConfig.FONT_SUBTITLE);
        gbc.gridy = 2;
        add(lblScore, gbc);
        
        // 4. Tombol Main Lagi
        btnRetry = new JButton("MAIN LAGI");
        btnRetry.setPreferredSize(new Dimension(200, 50));
        btnRetry.setFont(GameConfig.FONT_BODY);
        btnRetry.setBackground(GameConfig.COLOR_ACCENT);
        btnRetry.setForeground(Color.WHITE);
        
        // [PERBAIKAN] Ganti startGame menjadi showGame
        btnRetry.addActionListener(e -> {
            ScreenManager.getInstance().showGame(lastModule, lastLevel);
        });
        
        gbc.gridy = 3;
        gbc.insets = new Insets(30, 10, 10, 10);
        add(btnRetry, gbc);
        
        // 5. Tombol Kembali Menu
        btnMenu = new JButton("MENU UTAMA");
        btnMenu.setPreferredSize(new Dimension(200, 50));
        btnMenu.setFont(GameConfig.FONT_BODY);
        
        btnMenu.addActionListener(e -> {
            ScreenManager.getInstance().showScreen("MAIN_MENU");
        });
        
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(btnMenu, gbc);
    }
    
    public void showResult(ModuleModel module, int level, int score, int maxScore) {
        this.lastModule = module;
        this.lastLevel = level;
        
        lblScore.setText("SKOR: " + score + " / " + maxScore);
        
        // Logika Bintang Sederhana
        double percentage = ((double) score / maxScore) * 100;
        
        if (percentage == 100) {
            lblTitle.setText("SEMPURNA!");
            lblStar.setText("⭐⭐⭐");
        } else if (percentage >= 70) {
            lblTitle.setText("HEBAT!");
            lblStar.setText("⭐⭐");
        } else if (percentage >= 40) {
            lblTitle.setText("BAGUS!");
            lblStar.setText("⭐");
        } else {
            lblTitle.setText("COBA LAGI YA!");
            lblStar.setText("✊");
        }
    }
}