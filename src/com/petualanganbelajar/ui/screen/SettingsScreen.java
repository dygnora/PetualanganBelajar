/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.UserRepository;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author DD
 */
public class SettingsScreen extends JPanel {
    
    private JCheckBox chkMute; 
    private JSlider sliderBGM;
    private JSlider sliderSFX;
    private final UserRepository userRepo;

    public SettingsScreen() {
        this.userRepo = new UserRepository();
        
        setLayout(new GridBagLayout());
        setBackground(GameConfig.COLOR_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15); 
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 1. JUDUL
        JLabel title = new JLabel("PENGATURAN", SwingConstants.CENTER);
        title.setFont(GameConfig.FONT_TITLE);
        title.setForeground(GameConfig.COLOR_PRIMARY);
        gbc.gridy = 0;
        add(title, gbc);
        
        // 2. CHECKBOX MUTE
        chkMute = new JCheckBox("Senyapkan Semua Suara (Mute)");
        chkMute.setFont(GameConfig.FONT_SUBTITLE);
        chkMute.setBackground(GameConfig.COLOR_BG);
        chkMute.setFocusPainted(false);
        
        chkMute.addActionListener(e -> {
            boolean isMuted = chkMute.isSelected();
            toggleSliders(!isMuted); 
        });
        
        gbc.gridy = 1;
        add(chkMute, gbc);
        
        // 3. SLIDER BGM
        JLabel lblBGM = new JLabel("Volume Musik (BGM)");
        lblBGM.setFont(GameConfig.FONT_SUBTITLE);
        gbc.gridy = 2;
        add(lblBGM, gbc);
        
        sliderBGM = createSlider();
        gbc.gridy = 3;
        add(sliderBGM, gbc);
        
        // 4. SLIDER SFX
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
        JSlider slider = new JSlider(0, 100, 50); 
        slider.setMajorTickSpacing(20);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(GameConfig.COLOR_BG);
        return slider;
    }
    
    private void toggleSliders(boolean enabled) {
        sliderBGM.setEnabled(enabled);
        sliderSFX.setEnabled(enabled);
    }
    
    // [UPDATE] Saat layar muncul, load settingan user dari database/memory
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            UserModel user = GameState.getCurrentUser();
            if (user != null) {
                // Jika sudah login, set slider sesuai preferensi user
                sliderBGM.setValue(user.getBgmVolume());
                sliderSFX.setValue(user.getSfxVolume());
                // (Optional: Jika volume 0, bisa auto check mute)
            } else {
                // Jika belum login (di menu utama), pakai default 80/100
                sliderBGM.setValue(80);
                sliderSFX.setValue(100);
            }
        }
    }
    
    private void saveSettings() {
        boolean isMuted = chkMute.isSelected();
        int bgmVol = sliderBGM.getValue();
        int sfxVol = sliderSFX.getValue();
        
        // 1. Terapkan ke Audio Engine (Langsung terasa efeknya)
        SoundPlayer.getInstance().setMute(isMuted); 
        // Nanti SoundPlayer bisa ditambah setVolume(bgmVol) jika mau lebih canggih
        
        // 2. Simpan ke Database User (Jika sedang Login)
        UserModel user = GameState.getCurrentUser();
        if (user != null) {
            // Update Database
            userRepo.updateVolume(user.getId(), bgmVol, sfxVol);
            
            // Update juga object user yang sedang aktif di memori (biar sinkron)
            user.setBgmVolume(bgmVol);
            user.setSfxVolume(sfxVol);
        }
        
        String status = isMuted ? "Suara: MATI" : "Suara: HIDUP (" + bgmVol + "%)";
        JOptionPane.showMessageDialog(this, "Pengaturan Disimpan!\n" + status);
        ScreenManager.getInstance().showScreen("MAIN_MENU");
    }
}