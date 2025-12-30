package com.petualanganbelajar.ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ModernAnswerButton extends JButton {
    private Color baseColor;
    private boolean hover;
    private boolean pressed;

    public ModernAnswerButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        
        setFont(new Font("Comic Sans MS", Font.BOLD, 26)); // Font sedikit diperkecil agar rapi
        setForeground(Color.WHITE);
        
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setAlignmentX(CENTER_ALIGNMENT);
        
        // Ukuran tetap
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

        // LOGIKA ANIMASI: SCALE (MEMBESAR)
        // Jika hover, margin 0 (Full size). Jika diam, margin 3 (Lebih kecil).
        // Ini menciptakan efek "Zoom In" yang halus tanpa memotong gambar.
        int margin = hover ? 0 : 4; 
        
        // Jika ditekan, tombol sedikit mengecil lagi untuk feedback
        if (pressed) margin = 6;

        int drawW = w - (margin * 2);
        int drawH = h - (margin * 2);

        // Warna Dinamis
        Color paintColor = baseColor;
        if (hover && !pressed) {
            paintColor = baseColor.brighter(); // Lebih terang saat hover
        } else if (pressed) {
            paintColor = baseColor.darker(); // Lebih gelap saat ditekan
        }

        // 1. Shadow Tipis (Soft Shadow) di bawah
        // Hanya digambar jika tidak sedang ditekan (agar terasa "masuk" saat ditekan)
        if (!pressed) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(margin + 2, margin + 4, drawW, drawH, 40, 40);
        }

        // 2. Body Tombol Utama (Bentuk Pil / Rounded Besar)
        g2.setColor(paintColor);
        g2.fillRoundRect(margin, margin, drawW, drawH, 40, 40);

        // 3. Border Putih Tebal (Opsional - Style Stiker)
        // Uncomment 3 baris di bawah jika ingin border putih tebal ala stiker
        // g2.setColor(Color.WHITE);
        // g2.setStroke(new BasicStroke(3));
        // g2.drawRoundRect(margin + 1, margin + 1, drawW - 2, drawH - 2, 40, 40);

        // 4. Teks Centering
        g2.setColor(Color.WHITE);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        
        String txt = getText();
        int txtW = fm.stringWidth(txt);
        int txtH = fm.getAscent();

        int txtX = (w - txtW) / 2;
        int txtY = (h - fm.getHeight()) / 2 + txtH;

        // Shadow Teks Tipis
        g2.setColor(new Color(0,0,0,40));
        g2.drawString(txt, txtX + 1, txtY + 1);

        // Teks Utama
        g2.setColor(Color.WHITE);
        g2.drawString(txt, txtX, txtY);

        g2.dispose();
    }
}