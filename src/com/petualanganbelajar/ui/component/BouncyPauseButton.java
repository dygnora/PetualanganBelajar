package com.petualanganbelajar.ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BouncyPauseButton extends JButton {
    private float scale = 1f;
    private Timer bounceTimer;

    public BouncyPauseButton() {
        // [UPDATE] Diperbesar menjadi 80x80 agar ada ruang untuk animasi bounce
        setPreferredSize(new Dimension(80, 80));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { startBounce(); }
            @Override
            public void mouseExited(MouseEvent e) { stopBounce(); }
        });
    }

    private void startBounce() {
        if (bounceTimer != null) bounceTimer.stop();
        bounceTimer = new Timer(30, e -> {
            // Animasi sin wave (naik turun halus)
            scale = 1f + (float) (Math.sin(System.currentTimeMillis() / 100.0) * 0.15); // Scale max ~1.15x
            repaint();
        });
        bounceTimer.start();
    }

    private void stopBounce() {
        if (bounceTimer != null) {
            bounceTimer.stop();
            scale = 1f;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int centerX = w / 2;
        int centerY = h / 2;

        // [UPDATE] Tentukan ukuran visual tombol (lebih kecil dari ukuran komponen)
        // Agar saat scale up tidak terpotong border komponen
        int buttonSize = 60; 
        int drawX = (w - buttonSize) / 2;
        int drawY = (h - buttonSize) / 2;

        // Transformasi Scale dari Titik Tengah
        g2.translate(centerX, centerY);
        g2.scale(scale, scale);
        g2.translate(-centerX, -centerY);

        // 1. Shadow (Digambar sedikit offset ke bawah)
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillOval(drawX + 4, drawY + 6, buttonSize, buttonSize);

        // 2. Button Background Gradient (Red)
        GradientPaint gp = new GradientPaint(
            0, drawY, new Color(220, 20, 60), 
            0, drawY + buttonSize, new Color(178, 34, 34)
        );
        g2.setPaint(gp);
        g2.fillOval(drawX, drawY, buttonSize, buttonSize);

        // 3. Glossy Highlight (Efek Kilau di atas)
        g2.setPaint(new GradientPaint(
            0, drawY, new Color(255, 255, 255, 120), 
            0, drawY + (buttonSize / 2), new Color(255, 255, 255, 0)
        ));
        // Buat highlight sedikit lebih kecil dari tombolnya
        g2.fillOval(drawX + 5, drawY + 3, buttonSize - 10, (buttonSize / 2) - 5);

        // 4. Border Putih Tebal
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3f));
        g2.drawOval(drawX + 2, drawY + 2, buttonSize - 4, buttonSize - 4);

        // 5. Icon Pause (Bars) - Ukuran disesuaikan proporsional
        g2.setColor(Color.WHITE);
        int barW = 7; 
        int barH = 24; 
        int spacing = 9;
        
        // Hitung posisi bar agar tepat di tengah visual tombol
        int barStartX = centerX - barW - (spacing / 2);
        int barStartY = centerY - (barH / 2);
        
        g2.fillRoundRect(barStartX, barStartY, barW, barH, 5, 5);
        g2.fillRoundRect(barStartX + barW + spacing, barStartY, barW, barH, 5, 5);

        g2.dispose();
    }
}