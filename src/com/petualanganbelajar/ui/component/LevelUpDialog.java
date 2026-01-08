package com.petualanganbelajar.ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Random;

public class LevelUpDialog extends JDialog {

    private final float BASE_W = 1920f;
    private final float BASE_H = 1080f;
    private float scaleFactor = 1.0f;
    
    // Konfigurasi Confetti
    private final int CONFETTI_COUNT = 50;
    private final Confetti[] confettiPieces;

    public LevelUpDialog(Frame parent, int newLevel) {
        super(parent, true); // Modal = true
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // Transparan untuk efek shadow

        // 1. Hitung Scale Factor
        if (parent != null) {
            float sW = (float) parent.getWidth() / BASE_W;
            float sH = (float) parent.getHeight() / BASE_H;
            this.scaleFactor = Math.min(sW, sH);
            if (this.scaleFactor < 0.5f) this.scaleFactor = 0.5f;
        }

        // 2. Generate Confetti Acak (Posisi & Warna)
        confettiPieces = new Confetti[CONFETTI_COUNT];
        int dialogW = (int)(600 * scaleFactor);
        int dialogH = (int)(500 * scaleFactor);
        Random rand = new Random();
        Color[] confettiColors = {
            new Color(255, 89, 94),   // Merah
            new Color(255, 202, 58),  // Kuning
            new Color(138, 201, 38),  // Hijau
            new Color(25, 130, 196),  // Biru
            new Color(106, 76, 147)   // Ungu
        };

        for(int i=0; i<CONFETTI_COUNT; i++) {
            confettiPieces[i] = new Confetti(
                rand.nextInt(dialogW), 
                rand.nextInt(dialogH), 
                (int)(rand.nextInt(10, 20) * scaleFactor), 
                confettiColors[rand.nextInt(confettiColors.length)]
            );
        }

        // 3. Panel Utama dengan Custom Painting
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                int arc = (int)(60 * scaleFactor);

                // A. Bayangan (Shadow)
                g2.setColor(new Color(0,0,0,60));
                g2.fillRoundRect((int)(10*scaleFactor), (int)(10*scaleFactor), w-(int)(20*scaleFactor), h-(int)(20*scaleFactor), arc, arc);

                // B. Card Background (Putih - Biru Langit Gradien)
                GradientPaint gp = new GradientPaint(0, 0, new Color(224, 247, 250), 0, h, new Color(178, 235, 242));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w-(int)(10*scaleFactor), h-(int)(10*scaleFactor), arc, arc);

                // C. Border Tebal Putih
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(8 * scaleFactor));
                g2.drawRoundRect((int)(4*scaleFactor), (int)(4*scaleFactor), w-(int)(18*scaleFactor), h-(int)(18*scaleFactor), arc, arc);

                // D. Gambar Confetti
                for (Confetti c : confettiPieces) {
                    g2.setColor(c.color);
                    g2.fillOval(c.x, c.y, c.size, c.size);
                }
                
                // E. Gambar Bintang Raksasa di Tengah
                int starSize = (int)(200 * scaleFactor);
                int starX = (w - starSize) / 2;
                int starY = (int)(90 * scaleFactor);
                drawStar(g2, starX, starY, starSize, starSize);

                g2.dispose();
            }
        };

        panel.setLayout(null); // Absolute layout
        
        // --- JUDUL "LEVEL UP!" ---
        OutlinedLabel lblTitle = new OutlinedLabel("LEVEL UP!", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(56 * scaleFactor)));
        lblTitle.setForeground(new Color(255, 193, 7)); // Warna Emas
        lblTitle.setOutlineColor(new Color(230, 81, 0)); // Outline Oranye Tua
        lblTitle.setBounds(0, (int)(30 * scaleFactor), dialogW, (int)(60 * scaleFactor));
        panel.add(lblTitle);

        // --- ANGKA LEVEL (Di dalam Bintang) ---
        OutlinedLabel lblLevelNum = new OutlinedLabel(String.valueOf(newLevel), SwingConstants.CENTER);
        lblLevelNum.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, (int)(80 * scaleFactor)));
        lblLevelNum.setForeground(Color.WHITE);
        lblLevelNum.setOutlineColor(new Color(255, 143, 0));
        lblLevelNum.setBounds(0, (int)(150 * scaleFactor), dialogW, (int)(90 * scaleFactor));
        panel.add(lblLevelNum);

        // --- [PERBAIKAN] LABEL "KAMU HEBAT" ---
        // Menggunakan OutlinedLabel agar lebih jelas
        OutlinedLabel lblMsg = new OutlinedLabel("Kamu sekarang lebih pintar!", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(24 * scaleFactor)));
        // Warna fill teks (Biru Tua agar kontras)
        lblMsg.setForeground(new Color(21, 67, 96)); 
        // Warna outline (Putih agar terpisah dari background)
        lblMsg.setOutlineColor(Color.WHITE);
        // Sedikit dipertinggi height-nya untuk mengakomodasi outline
        lblMsg.setBounds(0, (int)(300 * scaleFactor), dialogW, (int)(50 * scaleFactor));
        panel.add(lblMsg);

        // --- TOMBOL "KEREN!" ---
        KidButton btnOk = new KidButton("KEREN!", new Color(46, 204, 113)); // Hijau
        btnOk.updateScale(scaleFactor);
        btnOk.addActionListener(e -> dispose());
        int btnW = (int)(200 * scaleFactor);
        int btnH = (int)(70 * scaleFactor);
        btnOk.setBounds((dialogW - btnW)/2, (int)(370 * scaleFactor), btnW, btnH);
        panel.add(btnOk);

        setContentPane(panel);
        setSize(dialogW, dialogH);
        setLocationRelativeTo(parent);
    }

    // Helper untuk menggambar Bintang
    private void drawStar(Graphics2D g, int x, int y, int width, int height) {
        double[] xPoints = {0.5, 0.61, 0.98, 0.68, 0.79, 0.5, 0.21, 0.32, 0.02, 0.39};
        double[] yPoints = {0.0, 0.35, 0.35, 0.57, 0.91, 0.72, 0.91, 0.57, 0.35, 0.35};
        
        Path2D star = new Path2D.Double();
        star.moveTo(x + xPoints[0] * width, y + yPoints[0] * height);
        for (int i = 1; i < xPoints.length; i++) {
            star.lineTo(x + xPoints[i] * width, y + yPoints[i] * height);
        }
        star.closePath();

        g.setColor(new Color(255, 235, 59)); // Kuning
        g.fill(star);
        g.setColor(new Color(255, 160, 0)); // Oranye Stroke
        g.setStroke(new BasicStroke(5 * scaleFactor));
        g.draw(star);
    }

    // --- INNER CLASS: CONFETTI ---
    class Confetti {
        int x, y, size;
        Color color;
        public Confetti(int x, int y, int size, Color color) {
            this.x = x; this.y = y; this.size = size; this.color = color;
        }
    }

    // --- INNER CLASS: TEXT WITH OUTLINE (STROKE) ---
    class OutlinedLabel extends JLabel {
        private Color outlineColor = Color.BLACK;
        public OutlinedLabel(String text, int align) { super(text, align); }
        public void setOutlineColor(Color c) { this.outlineColor = c; }
        
        @Override
        public void paintComponent(Graphics g) {
            String text = getText();
            if (text == null || text.length() == 0) { super.paintComponent(g); return; }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font font = getFont();
            FontMetrics fm = g2.getFontMetrics(font);
            int x = (getWidth() - fm.stringWidth(text)) / 2; 
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

            // Draw Outline
            g2.setColor(outlineColor);
            g2.setFont(font);
            int stroke = Math.max(2, (int)(3 * scaleFactor)); // Stroke sedikit ditipiskan untuk teks kecil
            g2.drawString(text, x - stroke, y - stroke);
            g2.drawString(text, x + stroke, y - stroke);
            g2.drawString(text, x - stroke, y + stroke);
            g2.drawString(text, x + stroke, y + stroke);

            // Draw Main Text
            g2.setColor(getForeground());
            g2.drawString(text, x, y);
        }
    }

    // --- INNER CLASS: TOMBOL ANAK-ANAK ---
    class KidButton extends JButton {
        private Color color;
        public KidButton(String text, Color color) {
            super(text);
            this.color = color;
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setForeground(Color.WHITE); setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        public void updateScale(float s) {
            setFont(new Font("Comic Sans MS", Font.BOLD, (int)(28 * s)));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            int arc = h; 
            
            g2.setColor(color.darker());
            g2.fillRoundRect(0, (int)(6*scaleFactor), w, h-(int)(6*scaleFactor), arc, arc);
            
            if (getModel().isPressed()) {
                g2.setColor(color.darker());
                g2.fillRoundRect(0, (int)(6*scaleFactor), w, h-(int)(6*scaleFactor), arc, arc);
            } else {
                g2.setColor(color);
                g2.fillRoundRect(0, 0, w, h-(int)(6*scaleFactor), arc, arc);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillRoundRect((int)(10*scaleFactor), (int)(5*scaleFactor), w-(int)(20*scaleFactor), (h/2)-(int)(6*scaleFactor), arc, arc);
            }
            super.paintComponent(g);
        }
    }
}