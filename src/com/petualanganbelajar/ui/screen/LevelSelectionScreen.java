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
import com.petualanganbelajar.repository.ProgressRepository;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author DD
 */
public class LevelSelectionScreen extends JPanel {
    
    private ModuleModel currentModule;
    private JLabel lblTitle;
    private JPanel levelsPanel;
    private ProgressRepository progressRepo;

    public LevelSelectionScreen() {
        this.progressRepo = new ProgressRepository();
        
        setLayout(new BorderLayout());
        setBackground(GameConfig.COLOR_BG);
        
        // 1. HEADER
        lblTitle = new JLabel("PILIH LEVEL", SwingConstants.CENTER);
        lblTitle.setFont(GameConfig.FONT_TITLE);
        lblTitle.setForeground(GameConfig.COLOR_PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        // 2. LEVEL BUTTONS CONTAINER
        levelsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        levelsPanel.setBackground(GameConfig.COLOR_BG);
        add(levelsPanel, BorderLayout.CENTER);
        
        // 3. BACK BUTTON
        JButton btnBack = new JButton("KEMBALI");
        btnBack.setFont(GameConfig.FONT_BODY);
        btnBack.addActionListener(e -> ScreenManager.getInstance().showScreen("MODULE_SELECT"));
        
        JPanel footer = new JPanel();
        footer.setBackground(GameConfig.COLOR_BG);
        footer.add(btnBack);
        add(footer, BorderLayout.SOUTH);
    }
    
    // Method ini dipanggil oleh ScreenManager sebelum layar ditampilkan
    public void setModule(ModuleModel module) {
        this.currentModule = module;
        lblTitle.setText("BELAJAR: " + module.getName().toUpperCase());
        refreshLevels();
    }
    
    private void refreshLevels() {
        levelsPanel.removeAll();
        
        UserModel user = GameState.getCurrentUser();
        int highestUnlocked = 1; // Default
        if (user != null) {
            highestUnlocked = progressRepo.getHighestLevelUnlocked(user.getId(), currentModule.getId());
        }
        
        // Buat Tombol Level 1, 2, 3
        for (int i = 1; i <= 3; i++) {
            int lvlNum = i;
            boolean isUnlocked = (lvlNum <= highestUnlocked);
            
            JButton btnLevel = new JButton("LEVEL " + lvlNum);
            btnLevel.setPreferredSize(new Dimension(150, 150));
            btnLevel.setFont(new Font("Arial", Font.BOLD, 24));
            
            if (isUnlocked) {
                btnLevel.setBackground(Color.WHITE);
                btnLevel.setForeground(GameConfig.COLOR_PRIMARY);
                btnLevel.setBorder(BorderFactory.createLineBorder(GameConfig.COLOR_PRIMARY, 3));
            } else {
                btnLevel.setBackground(Color.LIGHT_GRAY);
                btnLevel.setForeground(Color.GRAY);
                btnLevel.setText("🔒 LVL " + lvlNum);
                btnLevel.setEnabled(false); // Disable tombol
            }
            
            // AKSI TOMBOL LEVEL
            btnLevel.addActionListener(e -> {
                if (isUnlocked) {
                    // [PERBAIKAN] Panggil showStory, bukan startGame
                    ScreenManager.getInstance().showStory(currentModule, lvlNum);
                }
            });
            
            levelsPanel.add(btnLevel);
        }
        
        levelsPanel.revalidate();
        levelsPanel.repaint();
    }
}