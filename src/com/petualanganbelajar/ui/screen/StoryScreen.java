/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.util.StoryContent;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author DD
 */
public class StoryScreen extends JPanel {
    
    private JLabel lblStoryText;
    private JLabel lblTutorialText;
    private JLabel lblCharacter; // Nanti bisa diisi gambar Kancil/Guru
    private JButton btnStart;
    
    // Data sementara untuk dilempar ke GameScreen
    private ModuleModel pendingModule;
    private int pendingLevel;

    public StoryScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE); // Latar bersih seperti buku cerita
        
        // 1. HEADER (Judul Misi)
        JLabel lblTitle = new JLabel("MISI BARU!", SwingConstants.CENTER);
        lblTitle.setFont(GameConfig.FONT_TITLE);
        lblTitle.setForeground(GameConfig.COLOR_PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        // 2. CENTER (Visual & Cerita)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        
        // Gambar Karakter (Placeholder Emoji dulu)
        lblCharacter = new JLabel("🐰", SwingConstants.CENTER);
        lblCharacter.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        lblCharacter.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Teks Cerita (Story)
        lblStoryText = new JLabel("...", SwingConstants.CENTER);
        lblStoryText.setFont(new Font("Comic Sans MS", Font.PLAIN, 20)); // Font santai
        lblStoryText.setForeground(Color.BLACK);
        lblStoryText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Kotak Instruksi (Tutorial)
        JPanel tutorialPanel = new JPanel();
        tutorialPanel.setBackground(new Color(255, 250, 205)); // Lemon Chiffon (Kuning muda)
        tutorialPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2, true));
        tutorialPanel.setMaximumSize(new Dimension(600, 80));
        
        lblTutorialText = new JLabel("...", SwingConstants.CENTER);
        lblTutorialText.setFont(new Font("Arial", Font.BOLD, 16));
        lblTutorialText.setForeground(new Color(200, 100, 0)); // Coklat/Orange tua
        tutorialPanel.add(lblTutorialText);
        
        centerPanel.add(lblCharacter);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(lblStoryText);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(tutorialPanel); // Masukkan kotak tutorial
        
        add(centerPanel, BorderLayout.CENTER);
        
        // 3. FOOTER (Tombol Mulai)
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 40, 20));
        
        btnStart = new JButton("AYO MULAI!");
        btnStart.setFont(GameConfig.FONT_SUBTITLE);
        btnStart.setBackground(GameConfig.COLOR_ACCENT);
        btnStart.setForeground(Color.WHITE);
        btnStart.setPreferredSize(new Dimension(250, 60));
        btnStart.setFocusPainted(false);
        
        btnStart.addActionListener(e -> {
            // Lanjut ke Game Screen yang sebenarnya
            ScreenManager.getInstance().showGame(pendingModule, pendingLevel);
        });
        
        footerPanel.add(btnStart);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    // Method untuk setup konten sebelum layar ditampilkan
    public void setupStory(ModuleModel module, int level) {
        this.pendingModule = module;
        this.pendingLevel = level;
        
        // Ambil teks dari helper
        String story = StoryContent.getStory(module.getName(), level);
        String tutorial = StoryContent.getTutorial(module.getName(), level);
        
        // Format HTML agar bisa multi-line (enter)
        lblStoryText.setText("<html><center>" + story.replace("\n", "<br>") + "</center></html>");
        lblTutorialText.setText(tutorial);
        
        // Ganti Icon Karakter sesuai modul (Opsional)
        if (module.getName().equalsIgnoreCase("Angka")) lblCharacter.setText("🐰"); // Kancil
        else if (module.getName().equalsIgnoreCase("Huruf")) lblCharacter.setText("🦉"); // Burung Hantu
        else lblCharacter.setText("🐯"); 
    }
}