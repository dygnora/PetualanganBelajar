package com.petualanganbelajar.ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GlassKeypadButton extends JButton {

    private Color baseColor;
    private static final Color[] PALETTE = {
        new Color(255, 107, 107), // Merah Pastel
        new Color(78, 205, 196),  // Tosca
        new Color(255, 217, 61),  // Kuning
        new Color(84, 160, 255),  // Biru
        new Color(155, 89, 182),  // Ungu
        new Color(255, 159, 67)   // Orange
    };

    public GlassKeypadButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        setForeground(Color.WHITE); // Teks Putih agar kontras
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Pilih warna berdasarkan karakter hurufnya agar pola warnanya tetap (A selalu Merah, B selalu Tosca, dst)
        int charValue = (text.length() > 0) ? text.charAt(0) : 0;
        this.baseColor = PALETTE[charValue % PALETTE.length];
        
        // Ukuran default
        setPreferredSize(new Dimension(60, 55));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Efek Tekan (Lebih gelap saat ditekan)
        Color color = getModel().isPressed() ? baseColor.darker() : baseColor;

        // 2. Background Transparan (Glass Effect)
        // Alpha 200 dari 255 (Sekitar 80% Opacity)
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));
        g2.fillRoundRect(0, 0, w, h, 15, 15);

        // 3. Kilauan Kaca (Glossy Top)
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 100), 0, h / 2, new Color(255, 255, 255, 0)));
        g2.fillRoundRect(0, 0, w, h, 15, 15);

        // 4. Border Tipis Putih
        g2.setColor(new Color(255, 255, 255, 150));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, w - 2, h - 2, 15, 15);

        // 5. Draw Text dengan Shadow agar JELAS
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int x = (w - fm.stringWidth(text)) / 2;
        int y = (h - fm.getHeight()) / 2 + fm.getAscent() - 2;

        // Shadow Hitam Tipis
        g2.setColor(new Color(0, 0, 0, 50));
        g2.drawString(text, x + 2, y + 2);

        // Teks Utama Putih
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);

        g2.dispose();
    }
}