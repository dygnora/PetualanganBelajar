package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.model.ModuleModel;

import javax.swing.*;
import java.awt.*;

/**
 * Layar Hasil (Result Screen)
 * Updated: Menambahkan tombol "Lanjut Level" dan navigasi yang lebih baik.
 */
public class ResultScreen extends JPanel {

    private JLabel lblTitle;
    private JLabel lblScore;
    private JLabel lblStar;
    
    // Tombol-tombol Navigasi
    private JButton btnNextLevel; // [BARU]
    private JButton btnRetry;
    private JButton btnModuleMenu; // [UPDATE] Dulu ke Main Menu, sekarang ke Modul

    // Simpan data terakhir untuk logika navigasi
    private ModuleModel lastModule;
    private int lastLevel;

    public ResultScreen() {
        setLayout(new GridBagLayout());
        setBackground(GameConfig.COLOR_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Agar tombol sama lebar

        // 1. Judul
        lblTitle = new JLabel("HASIL BELAJAR", SwingConstants.CENTER);
        lblTitle.setFont(GameConfig.FONT_TITLE);
        lblTitle.setForeground(GameConfig.COLOR_PRIMARY);
        gbc.gridy = 0;
        add(lblTitle, gbc);

        // 2. Bintang Visual
        lblStar = new JLabel("⭐⭐⭐", SwingConstants.CENTER);
        lblStar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        gbc.gridy = 1;
        add(lblStar, gbc);

        // 3. Skor Angka
        lblScore = new JLabel("SKOR: 0/0", SwingConstants.CENTER);
        lblScore.setFont(GameConfig.FONT_SUBTITLE);
        gbc.gridy = 2;
        add(lblScore, gbc);

        // --- TOMBOL NAVIGASI ---

        // 4. Tombol LANJUT KE LEVEL BERIKUTNYA (Awalnya disembunyikan)
        btnNextLevel = new JButton("LANJUT LEVEL >>");
        styleButton(btnNextLevel, new Color(50, 205, 50)); // Warna Hijau
        btnNextLevel.addActionListener(e -> {
            // Masuk ke Story Level selanjutnya (Level + 1)
            ScreenManager.getInstance().showStory(lastModule, lastLevel + 1);
        });
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 10, 5, 10); // Jarak agak jauh dari skor
        add(btnNextLevel, gbc);

        // 5. Tombol MAIN LAGI (Retry)
        btnRetry = new JButton("ULANGI LEVEL");
        styleButton(btnRetry, GameConfig.COLOR_ACCENT); // Warna Biru
        btnRetry.addActionListener(e -> {
            // Main lagi di level yang sama
            ScreenManager.getInstance().showGame(lastModule, lastLevel);
        });
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 5, 10);
        add(btnRetry, gbc);

        // 6. Tombol KEMBALI KE MODUL
        btnModuleMenu = new JButton("PILIH MODUL LAIN");
        styleButton(btnModuleMenu, Color.ORANGE); // Warna Oranye
        btnModuleMenu.addActionListener(e -> {
            // Kembali ke menu pemilihan modul
            ScreenManager.getInstance().showScreen("MODULE_SELECT");
        });
        gbc.gridy = 5;
        add(btnModuleMenu, gbc);
    }

    // Helper untuk styling tombol biar rapi
    private void styleButton(JButton btn, Color bgColor) {
        btn.setPreferredSize(new Dimension(300, 55));
        btn.setFont(GameConfig.FONT_SUBTITLE);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
    }

    /**
     * Dipanggil setiap kali game selesai untuk menampilkan hasil.
     */
    public void showResult(ModuleModel module, int level, int score, int maxScore) {
        this.lastModule = module;
        this.lastLevel = level;

        // Update Teks Skor
        lblScore.setText("SKOR: " + score + " / " + maxScore);

        // Hitung Persentase
        double percentage = maxScore > 0 ? ((double) score / maxScore) * 100 : 0;
        boolean isPassed = percentage >= 60; // KKM 60%

        // Update Visual Bintang & Judul
        if (percentage == 100) {
            lblTitle.setText("SEMPURNA!");
            lblStar.setText("⭐⭐⭐");
        } else if (percentage >= 80) {
            lblTitle.setText("HEBAT!");
            lblStar.setText("⭐⭐");
        } else if (percentage >= 60) {
            lblTitle.setText("BAGUS!");
            lblStar.setText("⭐");
        } else {
            lblTitle.setText("AYO COBA LAGI!");
            lblStar.setText("❌"); // Atau icon sedih
        }

        // --- LOGIKA TOMBOL LANJUT ---
        // Tombol Lanjut HANYA muncul jika:
        // 1. User Lulus (Nilai >= 60%)
        // 2. Bukan Level Terakhir (Level < 3)
        // 3. Bukan Modul Epilog
        if (isPassed && level < 3 && !module.getName().equalsIgnoreCase("EPILOGUE")) {
            btnNextLevel.setVisible(true);
            btnNextLevel.setText("LANJUT KE LEVEL " + (level + 1) + " >>");
        } else {
            btnNextLevel.setVisible(false); // Sembunyikan jika tidak memenuhi syarat
        }
        
        // Refresh layout agar posisi tombol rapi
        revalidate();
        repaint();
    }
}