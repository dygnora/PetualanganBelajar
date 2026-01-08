package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class TitleScreen extends JPanel {

    // Aset Gambar
    private Image bgImage;
    private Image titleImage;
    
    // Variabel untuk efek kedip teks
    private boolean showText = true; 
    private Timer blinkTimer;

    // --- VARIABEL RESPONSIVE ---
    private final float BASE_W = 1920f;
    private final float BASE_H = 1080f;
    private float scaleFactor = 1.0f;

    public TitleScreen() {
        setLayout(null); 
        setFocusable(true); 
        loadAssets();

        // [FIX] Listener ComponentResized DIHAPUS.
        // Kita tidak membutuhkannya lagi karena perhitungan dipindah ke paintComponent
        // agar tidak ada delay (glitch) saat awal render.

        // 1. Timer untuk Efek Teks Berkedip
        blinkTimer = new Timer(800, e -> {
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

    // --- LOGIC RESPONSIVE ---
    private void calculateScaleFactor() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        float sW = (float) getWidth() / BASE_W;
        float sH = (float) getHeight() / BASE_H;
        this.scaleFactor = Math.min(sW, sH);
        // Mencegah UI terlalu kecil
        if (this.scaleFactor < 0.5f) this.scaleFactor = 0.5f;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow(); 
        playTitleMusic();
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            SwingUtilities.invokeLater(() -> {
                // Tidak perlu calculateScaleFactor disini lagi, 
                // karena akan otomatis terhitung saat repaint() dipanggil
                playTitleMusic();
                repaint();
            });
        }
    }
    
    private void playTitleMusic() {
        try {
            SoundPlayer.getInstance().playBGM("bgm_menu.wav");
        } catch (Exception e) {
            System.err.println("Gagal memutar BGM Title: " + e.getMessage());
        }
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
    protected void paintComponent(Graphics g) {
        // [FIX UTAMA] Hitung skala TEPAT SEBELUM menggambar.
        // Ini menjamin scaleFactor selalu benar sesuai ukuran layar detik ini juga
        // dan mencegah gambar terlihat "membesar" di frame pertama.
        calculateScaleFactor(); 

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
            g2.setPaint(new GradientPaint(0, 0, new Color(135, 206, 235), 0, h, new Color(25, 118, 210))); 
            g2.fillRect(0, 0, w, h);
        }

        // 2. GAMBAR JUDUL (LOGO) - RESPONSIVE
        if (titleImage != null) {
            // Ukuran Base SESUAI REQUEST (TIDAK DIUBAH)
            int logoBaseW = 1100; 
            int logoBaseH = 800; 
            
            int logoWidth = (int)(logoBaseW * scaleFactor);
            int logoHeight = (int)(logoBaseH * scaleFactor);
            
            // Posisi X tetap ditengah layar
            int x = (w - logoWidth) / 2;
            
            // Posisi Y dikalikan scale factor agar posisinya proporsional
            int yOffset = (int)(-20 * scaleFactor); 

            g2.drawImage(titleImage, x, yOffset, logoWidth, logoHeight, this);
        }

        // 3. TEKS "TEKAN APAPUN" - RESPONSIVE
        if (showText) {
            String text = "Tekan apapun untuk masuk ke dalam game";
            
            // Font size SESUAI REQUEST (TIDAK DIUBAH)
            int baseFontSize = 56;
            int scaledFontSize = (int)(baseFontSize * scaleFactor);
            
            g2.setFont(new Font("Comic Sans MS", Font.BOLD, scaledFontSize));
            FontMetrics fm = g2.getFontMetrics();
            
            int textX = (w - fm.stringWidth(text)) / 2;
            
            // Jarak dari bawah SESUAI REQUEST
            int marginBottom = (int)(80 * scaleFactor);
            int textY = h - marginBottom; 

            // Stroke/Outline Hitam
            g2.setColor(Color.BLACK);
            int shift = Math.max(1, (int)(2 * scaleFactor)); 
            g2.drawString(text, textX - shift, textY - shift);
            g2.drawString(text, textX - shift, textY + shift);
            g2.drawString(text, textX + shift, textY - shift);
            g2.drawString(text, textX + shift, textY + shift);

            // Teks Utama Putih
            g2.setColor(Color.WHITE);
            g2.drawString(text, textX, textY);
        }
        
        // 4. Versi Kecil - RESPONSIVE
        int smallFontSize = Math.max(10, (int)(12 * scaleFactor)); 
        g2.setFont(new Font("Arial", Font.PLAIN, smallFontSize));
        g2.setColor(new Color(255, 255, 255, 120));
        
        int verMargin = (int)(10 * scaleFactor);
        int verX = w - (int)(40 * scaleFactor);
        g2.drawString("v1.0", verX, h - verMargin);
    }
}