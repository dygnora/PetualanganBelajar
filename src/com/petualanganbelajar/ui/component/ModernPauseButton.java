package com.petualanganbelajar.ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernPauseButton extends JButton {
    
    private Color primaryColor;

    public ModernPauseButton() {
        this(new Color(255, 165, 0)); // Default Oranye
    }

    public ModernPauseButton(Color color) {
        this.primaryColor = color;
        setPreferredSize(new Dimension(65, 65)); 
        setContentAreaFilled(false); 
        setFocusPainted(false); 
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setToolTipText("Pause Game");

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { getModel().setRollover(true); repaint(); }
            public void mouseExited(MouseEvent e) { getModel().setRollover(false); repaint(); }
        });
    }

    @Override 
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean hover = getModel().isRollover(); 
        int s = Math.min(getWidth(), getHeight());
        
        Color dark = primaryColor.darker();
        Color light = hover ? primaryColor.brighter() : primaryColor;

        // Shadow Bawah
        g2.setColor(dark.darker());
        g2.fillOval(2, 6, s-4, s-8);

        // Border Putih
        g2.setColor(Color.WHITE);
        g2.fillOval(0, 0, s, s-4);

        // Muka Tombol
        g2.setColor(light);
        g2.fillOval(4, 4, s-8, s-12);

        // Simbol Pause (II)
        g2.setColor(dark); 
        g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = s/2; int cy = (s-6)/2 + 3;
        g2.drawLine(cx-7, cy-8, cx-7, cy+8);
        g2.drawLine(cx+7, cy-8, cx+7, cy+8);
        
        // Highlight Simbol
        g2.setColor(new Color(255,255,255,150));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(cx-9, cy-8, cx-9, cy+8);
        g2.drawLine(cx+5, cy-8, cx+5, cy+8);

        g2.dispose();
    }
}