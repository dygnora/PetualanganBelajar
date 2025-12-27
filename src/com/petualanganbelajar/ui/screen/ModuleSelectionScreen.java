package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.ModuleRepository;
import com.petualanganbelajar.repository.ProgressRepository; // [PENTING] Import
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ModuleSelectionScreen extends JPanel {
    
    private JPanel modulesPanel;
    private JLabel lblUserInfo; // Label untuk Nama & Avatar
    private JLabel lblTotalScore; // [BARU] Label Total Skor

    public ModuleSelectionScreen() {
        setLayout(new BorderLayout());
        setBackground(GameConfig.COLOR_BG);
        
        // 1. Header User Info (Kiri Atas & Kanan Atas)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(GameConfig.COLOR_ACCENT); 
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // KIRI: Avatar + Nama
        lblUserInfo = new JLabel("Player: -");
        lblUserInfo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        lblUserInfo.setForeground(Color.WHITE);
        topBar.add(lblUserInfo, BorderLayout.WEST);
        
        // KANAN: Total Skor [BARU]
        lblTotalScore = new JLabel("🏆 Skor: 0");
        lblTotalScore.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        lblTotalScore.setForeground(Color.YELLOW);
        topBar.add(lblTotalScore, BorderLayout.EAST);
        
        add(topBar, BorderLayout.NORTH);
        
        // 2. Grid Modul
        modulesPanel = new JPanel(new GridLayout(0, 2, 20, 20)); // 2 Kolom
        modulesPanel.setBackground(GameConfig.COLOR_BG);
        modulesPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        add(modulesPanel, BorderLayout.CENTER);
        
        // 3. Tombol Logout/Kembali
        JButton btnBack = new JButton("GANTI PROFIL / KELUAR");
        btnBack.setFont(GameConfig.FONT_BODY);
        btnBack.addActionListener(e -> ScreenManager.getInstance().showScreen("MAIN_MENU"));
        
        JPanel footer = new JPanel();
        footer.setBackground(GameConfig.COLOR_BG);
        footer.add(btnBack);
        add(footer, BorderLayout.SOUTH);
    }
    
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            refreshUserInfo(); // Update nama & skor saat layar muncul
            loadModules();
        }
    }
    
    private void refreshUserInfo() {
        UserModel u = GameState.getCurrentUser();
        if (u != null) {
            // 1. Update Nama & Avatar
            String avatarVisual = "👤"; 
            if ("avatar_1.png".equals(u.getAvatar())) avatarVisual = "👦";
            if ("avatar_2.png".equals(u.getAvatar())) avatarVisual = "👧";
            if ("avatar_3.png".equals(u.getAvatar())) avatarVisual = "🐱";
            
            lblUserInfo.setText(avatarVisual + " " + u.getName());
            
            // 2. [BARU] Update Total Skor Real-time
            ProgressRepository progressRepo = new ProgressRepository();
            int totalScore = progressRepo.calculateTotalScore(u.getName());
            lblTotalScore.setText("🏆 Total Skor: " + totalScore);
        }
    }
    
    private void loadModules() {
        modulesPanel.removeAll();
        ModuleRepository repo = new ModuleRepository();
        List<ModuleModel> modules = repo.getAllModules();
        
        for (ModuleModel mod : modules) {
            JButton btnModule = new JButton(mod.getName());
            btnModule.setFont(new Font("Arial", Font.BOLD, 24));
            btnModule.setBackground(Color.WHITE);
            btnModule.setForeground(GameConfig.COLOR_PRIMARY);
            btnModule.setFocusPainted(false);
            btnModule.setBorder(BorderFactory.createLineBorder(GameConfig.COLOR_PRIMARY, 2));
            
            // Aksi: Pilih Level
            btnModule.addActionListener(e -> {
                 ScreenManager.getInstance().showLevelSelect(mod);
            });
            
            modulesPanel.add(btnModule);
        }
        modulesPanel.revalidate();
        modulesPanel.repaint();
    }
}