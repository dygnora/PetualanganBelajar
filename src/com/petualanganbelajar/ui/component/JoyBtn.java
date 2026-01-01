package com.petualanganbelajar.ui.component;

import java.awt.*;

/**
 * JOY BUTTON
 * Implementasi tombol standar dengan gaya "Playful" (Rounded + 3D effect).
 * Tidak perlu memikirkan suara/mouse lagi, karena sudah diurus AbstractGameButton.
 */
public class JoyBtn extends AbstractGameButton {
    
    public JoyBtn(String text, Color color) {
        super(text, color);
        // Ukuran default yang pas untuk menu
        setPreferredSize(new Dimension(220, 55));
    }

    @Override
    protected void drawShape(Graphics2D g2, int w, int h, Color c, int yOffset) {
        // Shadow/Bagian Samping Tombol (3D effect)
        int shadowHeight = 4;
        
        // Gambar bagian bawah (lebih gelap)
        g2.setColor(c.darker());
        g2.fillRoundRect(0, yOffset + shadowHeight, w, h - shadowHeight - yOffset, 25, 25);
        
        // Gambar bagian atas (muka tombol)
        g2.setColor(c);
        g2.fillRoundRect(0, yOffset, w, h - shadowHeight - yOffset, 25, 25);
    }
}