/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.UserRepository;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author DD
 */
public class SettingsScreen extends JPanel {
    
    private JCheckBox chkMute; // [BARU] Checkbox Mute
    private JSlider sliderBGM;
    private JSlider sliderSFX;
    private final UserRepository userRepo;

    public SettingsScreen() {
        this.userRepo = new UserRepository();
        
        setLayout(new GridBagLayout());
        setBackground(GameConfig.COLOR_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15); // Jarak diperkecil sedikit biar muat
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 1. JUDUL
        JLabel title = new JLabel("PENGATURAN", SwingConstants.CENTER);
        title.setFont(GameConfig.FONT_TITLE);
        title.setForeground(GameConfig.COLOR_PRIMARY);
        gbc.gridy = 0;
        add(title, gbc);
        
        // 2. CHECKBOX MUTE [BARU]
        chkMute = new JCheckBox("Senyapkan Semua Suara (Mute)");
        chkMute.setFont(GameConfig.FONT_SUBTITLE);
        chkMute.setBackground(GameConfig.COLOR_BG);
        chkMute.setFocusPainted(false);
        
        // Logika: Kalau dicentang, matikan slider
        chkMute.addActionListener(e -> {
            boolean isMuted = chkMute.isSelected();
            toggleSliders(!isMuted); // Kalau Mute=True, maka Enable=False
        });
        
        gbc.gridy = 1;
        add(chkMute, gbc);
        
        // 3. SLIDER BGM (Musik Latar)
        JLabel lblBGM = new JLabel("Volume Musik (BGM)");
        lblBGM.setFont(GameConfig.FONT_SUBTITLE);
        gbc.gridy = 2;
        add(lblBGM, gbc);
        
        sliderBGM = createSlider();
        gbc.gridy = 3;
        add(sliderBGM, gbc);
        
        // 4. SLIDER SFX (Efek Suara)
        JLabel lblSFX = new JLabel("Volume Efek (SFX)");
        lblSFX.setFont(GameConfig.FONT_SUBTITLE);
        gbc.gridy = 4;
        add(lblSFX, gbc);
        
        sliderSFX = createSlider();
        gbc.gridy = 5;
        add(sliderSFX, gbc);
        
        // 5. TOMBOL SIMPAN
        JButton btnSave = new JButton("SIMPAN & KEMBALI");
        btnSave.setPreferredSize(new Dimension(300, 60));
        btnSave.setFont(GameConfig.FONT_SUBTITLE);
        btnSave.setBackground(GameConfig.COLOR_ACCENT);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        
        btnSave.addActionListener(e -> saveSettings());
        
        gbc.gridy = 6;
        gbc.insets = new Insets(30, 15, 15, 15);
        add(btnSave, gbc);
    }
    
    private JSlider createSlider() {
        JSlider slider = new JSlider(0, 100, 50); // Min 0, Max 100, Default 50
        slider.setMajorTickSpacing(20);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(GameConfig.COLOR_BG);
        return slider;
    }
    
    // Helper untuk mematikan/menghidupkan slider
    private void toggleSliders(boolean enabled) {
        sliderBGM.setEnabled(enabled);
        sliderSFX.setEnabled(enabled);
    }
    
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            // TODO: Nanti ambil state mute dari database/config
            // Contoh: chkMute.setSelected(GameConfig.IS_MUTED);
            // toggleSliders(!chkMute.isSelected());
        }
    }
    
    private void saveSettings() {
        boolean isMuted = chkMute.isSelected();
        int bgmVol = sliderBGM.getValue();
        int sfxVol = sliderSFX.getValue();
        
        // Logika Simpan
        // GameConfig.IS_MUTED = isMuted;
        
        String status = isMuted ? "Suara: MATI" : "Suara: HIDUP (" + bgmVol + "%)";
        System.out.println("Pengaturan Disimpan: " + status);
        
        // Update DB User jika login
        UserModel user = GameState.getCurrentUser();
        if (user != null) {
            // userRepo.updateSettings(user.getId(), isMuted, bgmVol, sfxVol);
        }
        
        JOptionPane.showMessageDialog(this, "Pengaturan Disimpan!\n" + status);
        ScreenManager.getInstance().showScreen("MAIN_MENU");
    }
}