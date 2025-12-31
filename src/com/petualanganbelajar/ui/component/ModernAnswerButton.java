package com.petualanganbelajar.ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernAnswerButton extends JButton {
    private Color baseColor;
    private boolean hover;
    private boolean pressed;

    public ModernAnswerButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        
        setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        setForeground(Color.WHITE);
        
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setAlignmentX(CENTER_ALIGNMENT);
        
        // Ukuran standar
        setPreferredSize(new Dimension(220, 70));
        setMaximumSize(new Dimension(250, 70));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { 
                hover = true; 
                repaint(); 
            }
            public void mouseExited(MouseEvent e) { 
                hover = false; 
                pressed = false;
                repaint(); 
            }
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // LOGIKA ANIMASI: SCALE
        int margin = hover ? 0 : 4; 
        if (pressed) margin = 6;

        int drawW = w - (margin * 2);
        int drawH = h - (margin * 2);

        // Warna Dinamis
        Color paintColor = baseColor;
        
        // Jika Tombol Punya Icon (Isinya Gambar), backgroundnya Putih saja biar bersih
        if (getIcon() != null) {
            paintColor = Color.WHITE;
        } else {
            // Jika Teks, gunakan warna baseColor (Biru/Hijau/dll)
            if (hover && !pressed) paintColor = baseColor.brighter();
            else if (pressed) paintColor = baseColor.darker();
        }

        // 1. Shadow
        if (!pressed) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(margin + 2, margin + 4, drawW, drawH, 40, 40);
        }

        // 2. Body Tombol
        g2.setColor(paintColor);
        g2.fillRoundRect(margin, margin, drawW, drawH, 40, 40);
        
        // Jika ini tombol gambar, kasih border tipis biar kelihatan batasnya
        if (getIcon() != null) {
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(margin, margin, drawW, drawH, 40, 40);
        }

        // 3. RENDER KONTEN (ICON ATAU TEKS?)
        Icon icon = getIcon();
        
        if (icon != null) {
            // --- GAMBAR ICON ---
            int ix = (w - icon.getIconWidth()) / 2;
            int iy = (h - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g2, ix, iy);
        } 
        else {
            // --- GAMBAR TEKS ---
            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            
            String txt = getText();
            // Fallback jika text null
            if (txt == null) txt = ""; 

            int txtW = fm.stringWidth(txt);
            int txtH = fm.getAscent();

            int txtX = (w - txtW) / 2;
            int txtY = (h - fm.getHeight()) / 2 + txtH;

            // Shadow Teks
            g2.setColor(new Color(0,0,0,40));
            g2.drawString(txt, txtX + 1, txtY + 1);

            // Teks Utama
            g2.setColor(Color.WHITE);
            g2.drawString(txt, txtX, txtY);
        }

        g2.dispose();
    }
}