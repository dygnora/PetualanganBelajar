package com.petualanganbelajar.ui.component;

import javax.swing.*;
import java.awt.*;

public class ActionAnswerButton extends JButton {

    private Color baseColor;

    public ActionAnswerButton(String text) {
        super(text);
        this.baseColor = new Color(0, 150, 136); // Teal Default
        
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(150, 60));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Warna Dasar (Berubah jika ditekan/hover)
        Color c = baseColor;
        if (getModel().isPressed()) c = c.darker();
        else if (getModel().isRollover()) c = c.brighter();

        // 2. Bayangan Bawah (Depth Effect)
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(2, 4, w - 4, h - 4, 20, 20);

        // 3. Background Utama
        g2.setColor(c);
        g2.fillRoundRect(0, 0, w, h - 4, 20, 20);

        // 4. Border Halus
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, w - 2, h - 6, 20, 20);

        // 5. Text Centering
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int x = (w - fm.stringWidth(text)) / 2;
        int y = (h - 4 - fm.getHeight()) / 2 + fm.getAscent(); // -4 kompensasi bayangan

        // Text Shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.drawString(text, x + 1, y + 2);

        // Main Text
        g2.setColor(getForeground());
        g2.drawString(text, x, y);

        g2.dispose();
    }
}