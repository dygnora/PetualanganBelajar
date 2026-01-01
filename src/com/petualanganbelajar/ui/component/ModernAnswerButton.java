package com.petualanganbelajar.ui.component;

import com.petualanganbelajar.util.StyleConstants;

import javax.swing.*;
import java.awt.*;

/**
 * MODERN ANSWER BUTTON (Refactored)
 * Tombol jawaban dengan gaya modern: Sudut sangat bulat (Radius 40) dan support gambar.
 * Mewarisi AbstractGameButton untuk logika interaksi standar.
 */
public class ModernAnswerButton extends AbstractGameButton {

    public ModernAnswerButton(String text, Color color) {
        super(text, color);
        
        // Konfigurasi spesifik tombol ini
        setPreferredSize(new Dimension(220, 70));
        setMaximumSize(new Dimension(250, 70)); // Agar tidak melar di BoxLayout
        setFont(new Font("Comic Sans MS", Font.BOLD, 26)); 
    }

    @Override
    protected void drawShape(Graphics2D g2, int w, int h, Color c, int yOffset) {
        // Radius sudut yang lebih besar untuk gaya modern
        int arcSize = 40;
        
        // Logika Warna Khusus:
        // Jika tombol ini berisi Icon (Gambar), background-nya Putih bersih
        Color paintColor = (getIcon() != null) ? Color.WHITE : c;

        // 1. Shadow (Bayangan)
        // Hanya gambar shadow jika tombol sedang tidak ditekan dalam
        if (yOffset == 0) { 
            g2.setColor(StyleConstants.COL_SHADOW);
            // Bayangan sedikit lebih turun (+4) dan masuk ke dalam (+2)
            g2.fillRoundRect(2, 4, w - 4, h - 4, arcSize, arcSize);
        }

        // 2. Body Tombol Utama
        g2.setColor(paintColor);
        g2.fillRoundRect(0, yOffset, w, h - 4, arcSize, arcSize);

        // 3. Border / Stroke
        // Jika ini tombol gambar, beri border abu-abu agar batasnya jelas
        if (getIcon() != null) {
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(0, yOffset, w, h - 4, arcSize, arcSize);
            
            // Render Icon di sini (Manual)
            // Karena AbstractGameButton hanya menangani Teks
            renderIcon(g2, w, h, yOffset);
        }
    }

    /**
     * Helper khusus untuk menggambar Icon di tengah tombol
     */
    private void renderIcon(Graphics2D g2, int w, int h, int yOffset) {
        Icon icon = getIcon();
        if (icon != null) {
            int ix = (w - icon.getIconWidth()) / 2;
            int iy = ((h - icon.getIconHeight()) / 2) + yOffset; // Ikuti animasi tekan
            icon.paintIcon(this, g2, ix, iy);
        }
    }
}