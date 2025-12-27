package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.ModuleRepository;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ModuleSelectionScreen extends JPanel {
    
    private JPanel modulesPanel;
    private JLabel lblUserInfo; // Label untuk Nama & Avatar

    public ModuleSelectionScreen() {
        setLayout(new BorderLayout());
        setBackground(GameConfig.COLOR_BG);
        
        // 1. Header User Info (Kiri Atas)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(GameConfig.COLOR_ACCENT); // Warna beda dikit biar jelas
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        lblUserInfo = new JLabel("Player: -");
        lblUserInfo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        lblUserInfo.setForeground(Color.WHITE);
        topBar.add(lblUserInfo, BorderLayout.WEST);
        
        JLabel lblTitle = new JLabel("PILIH PELAJARAN");
        lblTitle.setFont(GameConfig.FONT_SUBTITLE);
        lblTitle.setForeground(Color.WHITE);
        topBar.add(lblTitle, BorderLayout.EAST);
        
        add(topBar, BorderLayout.NORTH);
        
        // 2. Grid Modul
        modulesPanel = new JPanel(new GridLayout(0, 2, 20, 20)); // 2 Kolom
        modulesPanel.setBackground(GameConfig.COLOR_BG);
        modulesPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        add(modulesPanel, BorderLayout.CENTER);
        
        // 3. Tombol Logout/Kembali
        JButton btnBack = new JButton("GANTI PROFIL / KELUAR");
        btnBack.addActionListener(e -> ScreenManager.getInstance().showScreen("MAIN_MENU"));
        add(btnBack, BorderLayout.SOUTH);
    }
    
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            refreshUserInfo(); // [PENTING] Update nama user saat layar muncul
            loadModules();
        }
    }
    
    private void refreshUserInfo() {
        UserModel u = GameState.getCurrentUser();
        if (u != null) {
            // Mapping Emoji lagi
            String avatarVisual = "👤"; 
            if ("avatar_1.png".equals(u.getAvatar())) avatarVisual = "👦";
            if ("avatar_2.png".equals(u.getAvatar())) avatarVisual = "👧";
            if ("avatar_3.png".equals(u.getAvatar())) avatarVisual = "🐱";
            
            lblUserInfo.setText(avatarVisual + " " + u.getName());
        }
    }
    
    private void loadModules() {
        modulesPanel.removeAll();
        ModuleRepository repo = new ModuleRepository();
        List<ModuleModel> modules = repo.getAllModules();
        
        for (ModuleModel mod : modules) {
            JButton btnModule = new JButton(mod.getName());
            btnModule.setFont(GameConfig.FONT_SUBTITLE);
            btnModule.setBackground(Color.WHITE);
            btnModule.setPreferredSize(new Dimension(150, 100));
            
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