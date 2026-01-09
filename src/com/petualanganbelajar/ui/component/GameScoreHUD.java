package com.petualanganbelajar.ui.component;

import com.petualanganbelajar.util.UIHelper;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GameScoreHUD extends JPanel {
    private JLabel lblScore;
    private JLabel lblStarIcon; 
    private StarBurstPanel starPanel;
    
    // [FIX UTAMA] Inisialisasi ke 0f agar updateScale(1.0f) pertama kali dijalankan
    private float currentScale = 0f;
    
    public GameScoreHUD() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        
        starPanel = new StarBurstPanel();
        starPanel.setLayout(new GridBagLayout()); // Center Alignment
        
        lblStarIcon = new JLabel();
        lblScore = new JLabel("0");
        lblScore.setForeground(Color.WHITE); 
        
        add(starPanel);
        
        // Karena currentScale = 0f, pemanggilan ini SEKARANG AKAN dieksekusi
        updateScale(1.0f);
    }
    
    public void updateScale(float scale) {
        // Logika optimasi ini sekarang aman
        if (Math.abs(this.currentScale - scale) < 0.01f) return;
        
        this.currentScale = scale;
        
        // 1. Update Padding Container Utama
        int padTop = (int)(5 * scale);
        int padRight = (int)(10 * scale);
        setBorder(new EmptyBorder(padTop, 0, padTop, padRight));
        
        // 2. Update Ukuran StarPanel (Base: 200x80)
        int panelW = (int)(200 * scale); 
        int panelH = (int)(80 * scale);
        
        // [PENTING] Set Preferred Size agar GridBagLayout parent tahu ukuran barunya
        starPanel.setPreferredSize(new Dimension(panelW, panelH));
        starPanel.setMinimumSize(new Dimension(panelW, panelH)); // Safety
        
        // 3. Update Icon Bintang (Base: 40)
        int iconSize = (int)(40 * scale); 
        ImageIcon icon = UIHelper.loadIcon("star_icon.png", iconSize, iconSize);
        if (icon != null) {
            lblStarIcon.setIcon(icon);
            lblStarIcon.setText("");
        } else {
            lblStarIcon.setIcon(null);
            lblStarIcon.setText("⭐");
            lblStarIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, (int)(36 * scale)));
        }
        
        // 4. Update Font Skor (Base: 32)
        int fontSize = (int)(32 * scale); 
        lblScore.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        
        // 5. Susun Ulang Komponen (GridBagLayout Centering)
        starPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        
        int gap = (int)(15 * scale);
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, gap); 
        gbc.anchor = GridBagConstraints.CENTER;
        starPanel.add(lblStarIcon, gbc);
        
        gbc.gridx = 1; 
        gbc.insets = new Insets(0, 0, 0, 0); 
        starPanel.add(lblScore, gbc);
        
        revalidate();
        repaint();
    }
    
    // ... (Sisa method setScoreTheme, updateScore, dan StarBurstPanel SAMA SEPERTI SEBELUMNYA) ...
    public void setScoreTheme(Color c1, Color c2) { if (starPanel != null) starPanel.setColors(c1, c2); }
    public void updateScore(int score) { lblScore.setText(String.valueOf(score)); if (score > 0 && starPanel != null) starPanel.triggerBurst(); }

    private static class StarBurstPanel extends JPanel {
        private Color col1 = new Color(255, 165, 0); 
        private Color col2 = new Color(255, 69, 0);
        private java.util.List<BurstStar> stars = new ArrayList<>();
        private Timer burstTimer;
        
        public StarBurstPanel() {
            setOpaque(false); 
            burstTimer = new Timer(30, e -> {
                if (!stars.isEmpty()) {
                    try { stars.removeIf(s -> s.life <= 0); stars.forEach(BurstStar::update); } catch (Exception ex) {} 
                    repaint();
                }
            });
            burstTimer.start();
        }
        public void setColors(Color a, Color b) { this.col1 = a; this.col2 = b; repaint(); }
        public void triggerBurst() {
            int cx = (int)(30 * (getHeight() / 60.0)); int cy = getHeight() / 2;
            for(int i=0; i<10; i++) {
                double angle = Math.random() * Math.PI * 2;
                stars.add(new BurstStar(cx, cy, angle, getHeight() / 60.0f)); 
            }
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), arc = h; 
            g2.setColor(new Color(0, 0, 0, 50)); g2.fillRoundRect(2, 4, w - 4, h - 4, arc, arc);
            GradientPaint gp = new GradientPaint(0, 0, col1, 0, h, col2);
            g2.setPaint(gp); g2.fillRoundRect(0, 0, w, h - 4, arc, arc);
            GradientPaint shine = new GradientPaint(0, 0, new Color(255, 255, 255, 120), 0, h/2, new Color(255, 255, 255, 0));
            g2.setPaint(shine); g2.fillRoundRect(2, 2, w - 4, (h/2), arc, arc);
            g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(3f)); g2.drawRoundRect(1, 1, w - 2, h - 5, arc, arc);
            try { for(BurstStar s : stars) s.draw(g2); } catch (Exception ignored) {}
            g2.dispose();
        }
        private static class BurstStar {
            float x, y, vx, vy; int life = 20; float size;
            BurstStar(int cx, int cy, double angle, float scaleFactor) {
                x = cx; y = cy;
                float baseSpeed = (float)(Math.random() * 3 + 2);
                float speed = baseSpeed * scaleFactor; 
                vx = (float)(Math.cos(angle) * speed);
                vy = (float)(Math.sin(angle) * speed);
                float baseSize = (float)(Math.random() * 6 + 4);
                size = baseSize * scaleFactor; 
            }
            void update() { life--; x += vx; y += vy; vx *= 0.9f; vy *= 0.9f; }
            void draw(Graphics2D g) {
                float alpha = Math.max(0, life / 20f);
                g.setColor(new Color(255, 255, 200, (int)(alpha * 255)));
                int s = (int)size;
                g.fillOval((int)x, (int)y, s, s);
            }
        }
    }
}