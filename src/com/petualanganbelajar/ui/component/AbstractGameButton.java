package com.petualanganbelajar.ui.component;

import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.util.StyleConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ABSTRACT GAME BUTTON (BASE CLASS)
 * Kelas induk untuk semua tombol. Menangani:
 * 1. Logika Mouse (Hover/Pressed)
 * 2. Efek Suara (SFX)
 * 3. Rendering Teks Terpusat
 */
public abstract class AbstractGameButton extends JButton {
    
    protected boolean isHover = false;
    protected boolean isPressed = false;
    protected Color baseColor;

    public AbstractGameButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        
        // Setup properti Swing dasar agar transparan & rapi
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Gunakan font standar dari StyleConstants
        setFont(StyleConstants.FONT_BUTTON);
        setForeground(Color.WHITE);

        // SATU MouseListener untuk semua logika interaksi
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Throttling: Hanya bunyi jika status berubah dari tidak hover -> hover
                if (!isHover) {
                    playSoundSafe("hover.wav");
                }
                isHover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHover = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                playSoundSafe("click.wav");
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    /**
     * Helper aman untuk memutar suara.
     * Mencegah crash jika SoundPlayer belum siap atau file audio hilang.
     */
    private void playSoundSafe(String filename) {
        SoundPlayer sp = SoundPlayer.getInstance();
        if (sp != null) {
            sp.playSFX(filename);
        }
    }

    /**
     * Menghitung warna tombol berdasarkan status (Hover/Press/Normal).
     */
    protected Color getRenderColor() {
        if (isPressed) return baseColor.darker();
        if (isHover) return baseColor.brighter();
        return baseColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Hitung offset animasi turun naik
        int yOffset = isPressed ? StyleConstants.ANIM_PRESS_OFFSET : 0;
        
        // TAHAP 1: Gambar Bentuk Tombol (Delegasi ke kelas anak)
        drawShape(g2, w, h, getRenderColor(), yOffset);

        // TAHAP 2: Gambar Teks (Otomatis di tengah)
        drawTextCentered(g2, w, h, yOffset);

        g2.dispose();
    }

    // --- Abstract Method (Wajib diisi oleh JoyBtn, ModernButton, dll) ---
    protected abstract void drawShape(Graphics2D g2, int w, int h, Color c, int yOffset);

    // --- Helper Text Rendering (Re-usable) ---
    protected void drawTextCentered(Graphics2D g2, int w, int h, int yOffset) {
        String text = getText();
        // Jika tombol tidak punya teks (cuma ikon), langsung keluar
        if (text == null || text.isEmpty()) return;

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        
        int x = (w - fm.stringWidth(text)) / 2;
        int y = ((h - fm.getHeight()) / 2) + fm.getAscent() + yOffset - 2; // -2 koreksi visual sedikit

        // Shadow Teks (Agar tulisan terbaca di background terang/gelap)
        g2.setColor(StyleConstants.COL_SHADOW);
        g2.drawString(text, x + 2, y + 2);

        // Teks Utama Putih
        g2.setColor(getForeground());
        g2.drawString(text, x, y);
    }
}