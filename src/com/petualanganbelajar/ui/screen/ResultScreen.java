/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.ui.screen;
import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.repository.ProgressRepository;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author DD
 */
public class ResultScreen extends JPanel {
    private final ProgressRepository progressRepo;
    private JLabel lblTitle;
    private JLabel lblScore;
    private JLabel lblStars;
    private JButton btnNext;
    private JButton btnRetry;
    
    // Data sementara
    private ModuleModel currentModule;
    private int currentLevel;

    public ResultScreen() {
        this.progressRepo = new ProgressRepository();
        
        setLayout(new GridBagLayout());
        setBackground(GameConfig.COLOR_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        
        // 1. Judul Hasil
        lblTitle = new JLabel("HASIL PERMAINAN");
        lblTitle.setFont(GameConfig.FONT_TITLE);
        lblTitle.setForeground(GameConfig.COLOR_PRIMARY);
        gbc.gridy = 0;
        add(lblTitle, gbc);
        
        // 2. Bintang (Visual Reward)
        lblStars = new JLabel("★★★"); // Pakai simbol bintang klasik (Black Star Unicode)
        lblStars.setFont(new Font("Serif", Font.BOLD, 80)); // Font standar yang besar
        lblStars.setForeground(new Color(255, 215, 0)); // WARNA EMAS (Gold)
        
        gbc.gridy = 1;
        add(lblStars, gbc);
        
        // 3. Skor Angka
        lblScore = new JLabel("Skor: 0");
        lblScore.setFont(GameConfig.FONT_SUBTITLE);
        gbc.gridy = 2;
        add(lblScore, gbc);
        
        // 4. Panel Tombol
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(GameConfig.COLOR_BG);
        
        btnRetry = new JButton("ULANGI");
        btnRetry.setFont(GameConfig.FONT_BODY);
        btnRetry.addActionListener(e -> ScreenManager.getInstance().startGame(currentModule, currentLevel));
        
        btnNext = new JButton("LEVEL SELANJUTNYA");
        btnNext.setFont(GameConfig.FONT_BODY);
        btnNext.setBackground(GameConfig.COLOR_ACCENT);
        btnNext.setForeground(Color.WHITE);
        btnNext.addActionListener(e -> ScreenManager.getInstance().showLevelSelect(currentModule));
        
        btnPanel.add(btnRetry);
        btnPanel.add(btnNext);
        
        gbc.gridy = 3;
        add(btnPanel, gbc);
    }
    
    public void showResult(ModuleModel module, int level, int score, int maxScore) {
        this.currentModule = module;
        this.currentLevel = level;
        
        // Hitung Persentase
        double percentage = (double) score / maxScore * 100;
        
        lblScore.setText("Skor Kamu: " + score + " / " + maxScore);
        
        // Logika Bintang & Unlock
        if (percentage >= 70) {
            // LULUS
            lblTitle.setText("LUAR BIASA!");
            lblTitle.setForeground(new Color(34, 139, 34)); // Hijau (ForestGreen) biar keren
            
            lblStars.setText("★★★"); // Tiga Bintang Penuh
            lblStars.setForeground(new Color(255, 215, 0)); // Warna Emas
            
            btnNext.setVisible(true);
            
            // Simpan ke DB & Buka Level Baru
            int userId = GameState.getCurrentUser().getId();
            String userName = GameState.getCurrentUser().getName();
            String avatar = GameState.getCurrentUser().getAvatar();
            
            progressRepo.saveScore(userName, avatar, module.getId(), level, score);
            progressRepo.unlockNextLevel(userId, module.getId(), level);
            
        } else {
            // GAGAL
            lblTitle.setText("JANGAN MENYERAH!");
            lblTitle.setForeground(Color.RED);
            
            lblStars.setText("★☆☆"); // Satu Bintang, Dua Kosong
            lblStars.setForeground(Color.GRAY); // Warna Abu-abu sedih
            
            btnNext.setVisible(false);
        }
    }
}
