package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class TitleScreen extends JPanel {

    // Aset Gambar
    private Image bgImage;
    private Image titleImage;
    
    // Variabel untuk efek kedip teks
    private boolean showText = true; 
    private Timer blinkTimer;

    public TitleScreen() {
        setLayout(null); 
        setFocusable(true); 
        loadAssets();

        // 1. Timer untuk Efek Teks Berkedip
        blinkTimer = new Timer(700, e -> {
            showText = !showText;
            repaint(); 
        });
        blinkTimer.start();

        // 2. Deteksi KLIK MOUSE
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                enterGame();
            }
        });

        // 3. Deteksi TOMBOL KEYBOARD
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                enterGame();
            }
        });
    }

    private void loadAssets() {
        try {
            // Load Background
            URL bgUrl = getClass().getResource("/images/bg_title.png");
            if (bgUrl != null) {
                bgImage = new ImageIcon(bgUrl).getImage();
            } else {
                URL altUrl = getClass().getResource("/images/bg_menu.png");
                if (altUrl != null) bgImage = new ImageIcon(altUrl).getImage();
            }

            // Load Title Logo
            URL titleUrl = getClass().getResource("/images/title_menu.png");
            if (titleUrl != null) {
                titleImage = new ImageIcon(titleUrl).getImage();
            } else {
                URL altTitle = getClass().getResource("/images/title_papan_juara.png");
                if (altTitle != null) titleImage = new ImageIcon(altTitle).getImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enterGame() {
        if (blinkTimer.isRunning()) blinkTimer.stop();
        try { SoundPlayer.getInstance().playSFX("click.wav"); } catch (Exception ignored) {}
        ScreenManager.getInstance().showScreen("MAIN_MENU");
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. GAMBAR BACKGROUND
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, w, h, this);
        } else {
            g2.setColor(new Color(135, 206, 235)); 
            g2.fillRect(0, 0, w, h);
        }

        // 2. GAMBAR JUDUL (LOGO) - POSISI DIPERBAIKI
        if (titleImage != null) {
            // Ukuran Logo
            int logoWidth = 800; 
            int logoHeight = 500; 
            
            // Posisi Tengah Horizontal
            int x = (w - logoWidth) / 2;
            
            // [PERUBAHAN DISINI]
            // Sebelumnya h / 6 (sekitar 128px), sekarang kita set fixed 40px dari atas
            // Agar logo naik ke atas dan tidak menutupi tengah layar
            int y = 40; 

            g2.drawImage(titleImage, x, y, logoWidth, logoHeight, this);
        }

        // 3. TEKS "TEKAN APAPUN"
        if (showText) {
            String text = "Tekan apapun untuk masuk ke dalam game";
            
            g2.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
            FontMetrics fm = g2.getFontMetrics();
            
            int textX = (w - fm.stringWidth(text)) / 2;
            int textY = h - 80; // Posisi di bawah

            // A. Stroke/Outline Hitam (Agar terbaca jelas di background warna-warni)
            // Kita gambar teks hitam bergeser sedikit ke segala arah untuk efek outline tebal
            g2.setColor(Color.BLACK);
            g2.drawString(text, textX - 2, textY - 2);
            g2.drawString(text, textX - 2, textY + 2);
            g2.drawString(text, textX + 2, textY - 2);
            g2.drawString(text, textX + 2, textY + 2);

            // B. Teks Utama Putih
            g2.setColor(Color.WHITE);
            g2.drawString(text, textX, textY);
        }
        
        // 4. Versi Kecil
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.setColor(new Color(255, 255, 255, 120));
        g2.drawString("v1.0", w - 40, h - 10);
    }
}