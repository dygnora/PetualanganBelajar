/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author DD
 */
public class SplashScreen extends JPanel {
    
    private JProgressBar progressBar;
    private Timer timer;
    private int progress = 0;

    public SplashScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE); // Latar Putih Bersih
        
        // 1. LOGO / JUDUL (Tengah)
        JLabel lblLogo = new JLabel("PETUALANGAN BELAJAR", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Comic Sans MS", Font.BOLD, 48)); // Font Anak-anak
        lblLogo.setForeground(GameConfig.COLOR_PRIMARY);
        add(lblLogo, BorderLayout.CENTER);
        
        // 2. LOADING BAR (Bawah)
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(GameConfig.COLOR_ACCENT);
        progressBar.setPreferredSize(new Dimension(800, 30));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(Color.WHITE);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        bottomPanel.add(progressBar);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // 3. LOGIKA TIMER (Simulasi Loading 3 Detik)
        // Update setiap 30ms
        timer = new Timer(30, e -> {
            progress++;
            progressBar.setValue(progress);
            
            // Efek Loading Text
            if (progress < 50) progressBar.setString("Menyiapkan Buku Gambar...");
            else if (progress < 80) progressBar.setString("Mengasah Pensil Warna...");
            else progressBar.setString("Siap Belajar!");
            
            // SELESAI -> PINDAH KE TITLE
            if (progress >= 100) {
                timer.stop();
                ScreenManager.getInstance().showScreen("TITLE");
                
                // Coba putar lagu intro jika ada (Nanti di polishing)
                // SoundPlayer.getInstance().playBGM("intro.wav"); 
            }
        });
    }
    
    // Method ini dipanggil otomatis saat layar ditampilkan
    @Override
    public void addNotify() {
        super.addNotify();
        progress = 0;
        timer.start(); // Mulai timer saat layar muncul
    }
}