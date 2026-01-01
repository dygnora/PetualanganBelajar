package com.petualanganbelajar.ui.component;

import com.petualanganbelajar.util.StyleConstants;

import javax.swing.*;
import java.awt.*;

/**
 * ACTION ANSWER BUTTON (Refactored)
 * Tombol dengan gaya "Teal" default, border transparan halus, dan efek kedalaman.
 * Mewarisi AbstractGameButton untuk manajemen interaksi otomatis.
 */
public class ActionAnswerButton extends AbstractGameButton {

    public ActionAnswerButton(String text) {
        // Set warna default Teal (0, 150, 136) sesuai desain asli
        super(text, new Color(0, 150, 136));
        
        // Konfigurasi spesifik tombol ini
        setPreferredSize(new Dimension(150, 60));
        
        // Mempertahankan ukuran font 24 (sedikit lebih besar dari default tombol lain)
        setFont(new Font("Comic Sans MS", Font.BOLD, 24)); 
    }

    @Override
    protected void drawShape(Graphics2D g2, int w, int h, Color c, int yOffset) {
        // Logika Tampilan: Rounded Rectangle dengan Border Halus
        
        int arcSize = 20;

        // 1. Shadow (Bayangan Bawah)
        // Hanya gambar bayangan jika tombol tidak sedang ditekan penuh
        if (yOffset == 0) {
            g2.setColor(StyleConstants.COL_SHADOW); // atau new Color(0, 0, 0, 40)
            g2.fillRoundRect(2, 4, w - 4, h - 4, arcSize, arcSize);
        }

        // 2. Background Utama (Bergerak turun saat ditekan via yOffset)
        g2.setColor(c);
        g2.fillRoundRect(0, yOffset, w, h - 4, arcSize, arcSize);

        // 3. Border Halus (Putih Transparan)
        // Memberikan efek "glassy" atau highlight pinggiran
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(2));
        // Koordinat disesuaikan agar pas di dalam background (margin 1px)
        g2.drawRoundRect(1, 1 + yOffset, w - 2, h - 6, arcSize, arcSize);
    }
}