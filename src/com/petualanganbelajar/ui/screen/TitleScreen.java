/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.ui.screen;
import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.ScreenManager;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author DD
 */
public class TitleScreen extends JPanel {
    public TitleScreen() {
        // 1. Setup Layout & Warna
        setLayout(new GridBagLayout()); // Agar komponen ada di tengah
        setBackground(GameConfig.COLOR_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Jarak antar elemen
        gbc.gridx = 0;
        
        // 2. Judul Game
        JLabel titleLabel = new JLabel(GameConfig.GAME_TITLE);
        titleLabel.setFont(GameConfig.FONT_TITLE);
        titleLabel.setForeground(GameConfig.COLOR_PRIMARY);
        
        gbc.gridy = 0;
        add(titleLabel, gbc);
        
        // 3. Sub-Judul
        JLabel subLabel = new JLabel("Belajar Angka, Huruf & Warna");
        subLabel.setFont(GameConfig.FONT_SUBTITLE);
        subLabel.setForeground(GameConfig.COLOR_ACCENT);
        
        gbc.gridy = 1;
        add(subLabel, gbc);
        
        // 4. Spacer (Jarak Kosong)
        gbc.gridy = 2;
        add(Box.createVerticalStrut(50), gbc);
        
        // 5. Tombol MULAI
        JButton startButton = new JButton("MULAI PETUALANGAN");
        startButton.setFont(GameConfig.FONT_SUBTITLE);
        startButton.setBackground(GameConfig.COLOR_PRIMARY);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(300, 60));
        
        // Aksi Tombol (Nanti kita arahkan ke Pilih Profil)
        startButton.addActionListener(e -> {
            ScreenManager.getInstance().showScreen("MAIN_MENU");
        });
        
        gbc.gridy = 3;
        add(startButton, gbc);
        
        // 6. Footer (Credit)
        JLabel footerLabel = new JLabel("Versi 1.0 - (c) 2025 Petualangan Belajar");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        gbc.gridy = 4;
        gbc.insets = new Insets(50, 10, 10, 10);
        add(footerLabel, gbc);
    }
}
