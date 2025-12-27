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
import java.util.List;

/**
 *
 * @author DD
 */
public class ProfileSelectionScreen extends JPanel {
    
    private final UserRepository userRepo;
    private final JPanel profileContainer;

    public ProfileSelectionScreen() {
        this.userRepo = new UserRepository();
        
        setLayout(new BorderLayout());
        setBackground(GameConfig.COLOR_BG);
        
        // 1. Header
        JLabel title = new JLabel("PILIH PROFILMU", SwingConstants.CENTER);
        title.setFont(GameConfig.FONT_TITLE);
        title.setForeground(GameConfig.COLOR_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(title, BorderLayout.NORTH);
        
        // 2. Container Profil (Tengah)
        profileContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        profileContainer.setBackground(GameConfig.COLOR_BG);
        add(profileContainer, BorderLayout.CENTER);
        
        // 3. Tombol Kembali
        JButton btnBack = new JButton("KEMBALI KE MENU");
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
            refreshProfiles();
        }
    }
    
    private void refreshProfiles() {
        profileContainer.removeAll();
        List<UserModel> users = userRepo.getAllActiveUsers();
        
        for (UserModel user : users) {
            profileContainer.add(createProfileCard(user));
        }
        
        if (users.isEmpty()) {
            JLabel emptyLabel = new JLabel("Belum ada profil. Klik 'Kembali' lalu 'Mulai Baru'.");
            emptyLabel.setFont(GameConfig.FONT_SUBTITLE);
            profileContainer.add(emptyLabel);
        }
        
        profileContainer.revalidate();
        profileContainer.repaint();
    }
    
    // [UPDATE] Sekarang return JPanel, bukan JButton langsung
    private JPanel createProfileCard(UserModel user) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(200, 300));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createLineBorder(GameConfig.COLOR_PRIMARY, 2));
        
        // --- BAGIAN ATAS: TOMBOL LOGIN (Avatar + Nama) ---
        String avatarVisual = "👤"; 
        if ("avatar_1.png".equals(user.getAvatar())) avatarVisual = "👦";
        if ("avatar_2.png".equals(user.getAvatar())) avatarVisual = "👧";
        if ("avatar_3.png".equals(user.getAvatar())) avatarVisual = "🐱";

        String htmlText = "<html><center><font size='7'>" + avatarVisual + "</font><br>" 
                        + "<font size='5'>" + user.getName() + "</font></center></html>";

        JButton btnLogin = new JButton(htmlText);
        btnLogin.setBackground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false); // Hilangkan border tombol biar menyatu
        
        // Aksi Login
        btnLogin.addActionListener(e -> {
            GameState.setCurrentUser(user);
            ScreenManager.getInstance().showScreen("MODULE_SELECT");
        });
        
        cardPanel.add(btnLogin, BorderLayout.CENTER);
        
        // --- BAGIAN BAWAH: TOMBOL HAPUS ---
        JButton btnDelete = new JButton("HAPUS PROFIL");
        btnDelete.setFont(new Font("Arial", Font.BOLD, 12));
        btnDelete.setBackground(Color.RED);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setPreferredSize(new Dimension(200, 40));
        
        // Aksi Hapus
        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Yakin mau menghapus profil " + user.getName() + "?\nData permainan akan hilang.",
                    "Hapus Profil",
                    JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (userRepo.deleteUser(user.getId())) {
                    refreshProfiles(); // Refresh layar otomatis
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus.");
                }
            }
        });
        
        cardPanel.add(btnDelete, BorderLayout.SOUTH);
        
        return cardPanel;
    }
}