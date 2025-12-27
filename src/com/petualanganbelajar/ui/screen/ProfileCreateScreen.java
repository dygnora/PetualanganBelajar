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
import java.awt.event.ActionListener;

/**
 *
 * @author DD
 */
public class ProfileCreateScreen extends JPanel {

    private final UserRepository userRepo;
    private final JTextField nameField;
    
    // Variabel untuk menyimpan avatar yang sedang dipilih
    private String selectedAvatar = "avatar_1.png"; // Default
    
    // Tombol-tombol avatar (biar bisa diatur visualnya saat diklik)
    private JToggleButton btnAv1, btnAv2, btnAv3;

    public ProfileCreateScreen() {
        this.userRepo = new UserRepository();

        setLayout(new GridBagLayout());
        setBackground(GameConfig.COLOR_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        // 1. Judul
        JLabel title = new JLabel("BUAT PROFIL BARU");
        title.setFont(GameConfig.FONT_TITLE);
        title.setForeground(GameConfig.COLOR_PRIMARY);
        gbc.gridy = 0;
        add(title, gbc);

        // 2. Label Tanya Nama
        JLabel lblName = new JLabel("Siapa Namamu?");
        lblName.setFont(GameConfig.FONT_SUBTITLE);
        gbc.gridy = 1;
        add(lblName, gbc);

        // 3. Input Text
        nameField = new JTextField(15);
        nameField.setFont(GameConfig.FONT_SUBTITLE);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        gbc.gridy = 2;
        add(nameField, gbc);
        
        // 4. PILIH AVATAR (LOGIKA BARU)
        JLabel lblAvatar = new JLabel("Pilih Kartunmu:");
        lblAvatar.setFont(GameConfig.FONT_SUBTITLE);
        gbc.gridy = 3;
        add(lblAvatar, gbc);
        
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        avatarPanel.setBackground(GameConfig.COLOR_BG);
        
        // Kita pakai Group agar cuma bisa pilih 1
        ButtonGroup bg = new ButtonGroup();
        
        // Buat 3 Tombol Avatar (Sementara pakai Teks/Emoji dulu biar ga error gambar)
        btnAv1 = createAvatarButton("👦", "avatar_1.png");
        btnAv2 = createAvatarButton("👧", "avatar_2.png");
        btnAv3 = createAvatarButton("🐱", "avatar_3.png");
        
        // Default Pilih yang pertama
        btnAv1.setSelected(true);
        updateAvatarStyle(); // Update visual seleksi
        
        bg.add(btnAv1);
        bg.add(btnAv2);
        bg.add(btnAv3);
        
        avatarPanel.add(btnAv1);
        avatarPanel.add(btnAv2);
        avatarPanel.add(btnAv3);
        
        gbc.gridy = 4;
        add(avatarPanel, gbc);

        // 5. Tombol Simpan & Main
        JButton btnSave = new JButton("MULAI PETUALANGAN!");
        btnSave.setFont(GameConfig.FONT_SUBTITLE);
        btnSave.setBackground(GameConfig.COLOR_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(300, 60));
        
        btnSave.addActionListener(e -> saveAndPlay());
        
        gbc.gridy = 5;
        gbc.insets = new Insets(30, 10, 10, 10);
        add(btnSave, gbc);

        // 6. Tombol Batal
        JButton btnCancel = new JButton("Batal");
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCancel.setForeground(Color.RED);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(e -> ScreenManager.getInstance().showScreen("MAIN_MENU"));
        
        gbc.gridy = 6;
        add(btnCancel, gbc);
    }
    
    // Helper membuat tombol avatar
    private JToggleButton createAvatarButton(String text, String value) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40)); // Ukuran Emoji Besar
        btn.setPreferredSize(new Dimension(80, 80));
        btn.setFocusPainted(false);
        
        btn.addActionListener(e -> {
            this.selectedAvatar = value;
            updateAvatarStyle();
        });
        
        return btn;
    }
    
    // Logika Visual: Tombol yang dipilih warnanya beda
    private void updateAvatarStyle() {
        styleButton(btnAv1);
        styleButton(btnAv2);
        styleButton(btnAv3);
    }
    
    private void styleButton(JToggleButton btn) {
        if (btn.isSelected()) {
            btn.setBackground(GameConfig.COLOR_ACCENT); // Biru/Terang kalau dipilih
            btn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
    }
    
    private void saveAndPlay() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi namamu dulu ya!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Simpan ke Database (User + Avatar yang dipilih)
        if (userRepo.createUser(name, selectedAvatar)) {
            
            // 2. Ambil data user yang baru dibuat untuk Login otomatis
            UserModel newUser = userRepo.getAllActiveUsers().stream()
                .filter(u -> u.getName().equals(name))
                .reduce((first, second) -> second) // Ambil yang terakhir
                .orElse(null);

            if (newUser != null) {
                // 3. Set Login & Masuk Game
                GameState.setCurrentUser(newUser);
                nameField.setText(""); // Reset field
                JOptionPane.showMessageDialog(this, "Halo " + newUser.getName() + ", Selamat Datang!");
                ScreenManager.getInstance().showScreen("MODULE_SELECT");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Gagal membuat profil. Coba lagi.");
        }
    }
}