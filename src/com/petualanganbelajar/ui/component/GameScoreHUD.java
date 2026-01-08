package com.petualanganbelajar.ui.component;

import com.petualanganbelajar.util.UIHelper;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GameScoreHUD extends JPanel {
    private JLabel lblScore;
    private StarBurstPanel starPanel;
    
    public GameScoreHUD() {
        setOpaque(false);
        setBorder(new EmptyBorder(5, 0, 5, 10)); 
        setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        
        starPanel = new StarBurstPanel();
        starPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JLabel lblStarIcon = new JLabel();
        ImageIcon icon = UIHelper.loadIcon("star_icon.png", 32, 32);
        if (icon != null) {
            lblStarIcon.setIcon(icon);
        } else {
            lblStarIcon.setText("⭐");
            lblStarIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        }
        
        lblScore = new JLabel("0");
        lblScore.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        lblScore.setForeground(Color.WHITE); 
        
        starPanel.add(lblStarIcon);
        starPanel.add(lblScore);
        
        add(starPanel);
    }
    
    // [BARU] Setter untuk mengubah tema warna
    public void setScoreTheme(Color c1, Color c2) {
        if (starPanel != null) {
            starPanel.setColors(c1, c2);
        }
    }
    
    public void updateScore(int score) {
        lblScore.setText(String.valueOf(score));
        if (score > 0 && starPanel != null) starPanel.triggerBurst();
    }
    
    // =======================================================================
    // INNER CLASS: PANEL VISUAL
    // =======================================================================
    private static class StarBurstPanel extends JPanel {
        // [UPDATE] Warna default (Orange) tapi tidak final
        private Color col1 = new Color(255, 165, 0); 
        private Color col2 = new Color(255, 69, 0);
        
        private java.util.List<BurstStar> stars = new ArrayList<>();
        private Timer burstTimer;
        
        public StarBurstPanel() {
            setOpaque(false); 
            setPreferredSize(new Dimension(150, 50)); 
            
            burstTimer = new Timer(30, e -> {
                if (!stars.isEmpty()) {
                    stars.removeIf(s -> s.life <= 0);
                    stars.forEach(BurstStar::update);
                    repaint();
                }
            });
            burstTimer.start();
        }
        
        // [BARU] Method ganti warna
        public void setColors(Color a, Color b) {
            this.col1 = a;
            this.col2 = b;
            repaint();
        }
        
        public void triggerBurst() {
            int cx = 30; 
            int cy = getHeight() / 2;
            for(int i=0; i<10; i++) {
                double angle = Math.random() * Math.PI * 2;
                stars.add(new BurstStar(cx, cy, angle));
            }
        }
        
        @Override 
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int arc = h; 
            
            // Drop Shadow
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillRoundRect(2, 4, w - 4, h - 4, arc, arc);
            
            // [UPDATE] Menggunakan variabel warna dinamis
            GradientPaint gp = new GradientPaint(0, 0, col1, 0, h, col2);
            g2.setPaint(gp); 
            g2.fillRoundRect(0, 0, w, h - 4, arc, arc);
            
            // Highlight
            GradientPaint shine = new GradientPaint(
                0, 0, new Color(255, 255, 255, 120), 
                0, h/2, new Color(255, 255, 255, 0)
            );
            g2.setPaint(shine);
            g2.fillRoundRect(2, 2, w - 4, (h/2), arc, arc);
            
            // Border
            g2.setColor(Color.WHITE); 
            g2.setStroke(new BasicStroke(3f)); 
            g2.drawRoundRect(1, 1, w - 2, h - 5, arc, arc);
            
            // Stars
            for(BurstStar s : stars) s.draw(g2);
            
            g2.dispose();
        }
        
        private static class BurstStar {
            float x, y, vx, vy; 
            int life = 20;
            float size;
            
            BurstStar(int cx, int cy, double angle) {
                x = cx; y = cy;
                float speed = (float)(Math.random() * 3 + 2);
                vx = (float)(Math.cos(angle) * speed);
                vy = (float)(Math.sin(angle) * speed);
                size = (float)(Math.random() * 6 + 4);
            }
            
            void update() { 
                life--; x += vx; y += vy; vx *= 0.9f; vy *= 0.9f; 
            }
            
            void draw(Graphics2D g) {
                float alpha = life / 20f;
                g.setColor(new Color(255, 255, 200, (int)(alpha * 255)));
                int s = (int)size;
                g.fillOval((int)x, (int)y, s, s);
            }
        }
    }
}