package com.petualanganbelajar.ui.component;

import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.util.LevelManager;
import com.petualanganbelajar.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class UserProfileHUD extends JPanel {
    private JLabel lblUserName;
    private AnimatedXPBar xpBar;
    
    // [UPDATE] Simpan referensi container agar bisa diakses method setProfileTheme
    private SparklyBubblePanel container; 

    public UserProfileHUD() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        // Warna Default (Rainbow)
        Color[] defaultColors = {
            new Color(255, 107, 107), new Color(255, 159, 64),
            new Color(255, 206, 86), new Color(75, 192, 192)
        };
        
        container = new SparklyBubblePanel(new FlowLayout(FlowLayout.LEFT, 15, 8), defaultColors);
        
        // Avatar
        JLabel lblAvatar = new JLabel();
        ImageIcon icon = UIHelper.loadIcon("user_icon.png", 45, 45);
        if (icon != null) lblAvatar.setIcon(icon);
        else { lblAvatar.setText("😊"); lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40)); }
        
        // Nama
        lblUserName = new JLabel("Player");
        lblUserName.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        lblUserName.setForeground(Color.WHITE);
        
        // XP Bar
        xpBar = new AnimatedXPBar();
        
        container.add(lblAvatar);
        container.add(lblUserName);
        container.add(xpBar);
        
        add(container);
    }
    
    // [BARU] Method untuk mengganti tema warna profil
    public void setProfileTheme(Color[] newColors) {
        if (container != null) {
            container.setColors(newColors);
        }
    }
    
    public void updateProfile(int currentTotalXP) {
        UserModel u = GameState.getCurrentUser();
        String name = (u != null) ? u.getName() : "Teman";
        if (name.length() > 10) name = name.substring(0, 8) + "..";
        lblUserName.setText(name);
        
        float progress = LevelManager.getProgressToNextLevel(currentTotalXP);
        int currentLvl = LevelManager.calculateLevelFromScore(currentTotalXP);
        xpBar.setLevelAndProgress(currentLvl, progress);
    }

    // --- Inner Classes: Visuals ---
    
    private static class SparklyBubblePanel extends JPanel {
        private Color[] colors; // [UPDATE] Hapus final agar bisa diganti
        private Timer sparkleTimer;
        private java.util.List<Sparkle> sparkles = new ArrayList<>();
        
        public SparklyBubblePanel(LayoutManager layout, Color[] colors) {
            super(layout); 
            this.colors = colors; 
            setOpaque(false);
            setBorder(new EmptyBorder(5, 15, 5, 15));
            
            sparkleTimer = new Timer(150, e -> {
                if (Math.random() > 0.7) sparkles.add(new Sparkle((int)(Math.random()*getWidth()), (int)(Math.random()*getHeight())));
                sparkles.removeIf(s -> s.life <= 0); 
                sparkles.forEach(Sparkle::update); 
                repaint();
            });
            sparkleTimer.start();
        }

        // [BARU] Setter warna tema
        public void setColors(Color[] c) {
            this.colors = c;
            repaint();
        }

        @Override 
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(), h = getHeight();
            
            // [UPDATE] Gradient dengan array warna dinamis
            if (colors != null && colors.length >= 2) {
                float[] fractions;
                // Buat fraksi otomatis berdasarkan jumlah warna
                if (colors.length == 2) fractions = new float[]{0f, 1f};
                else if (colors.length == 3) fractions = new float[]{0f, 0.5f, 1f};
                else fractions = new float[]{0f, 0.33f, 0.66f, 1f}; // Default 4 warna

                LinearGradientPaint gradient = new LinearGradientPaint(0, 0, w, h, fractions, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
                g2.setPaint(gradient); 
                g2.fillRoundRect(0, 0, w, h, h, h);
            } else {
                // Fallback color jika null
                g2.setColor(Color.GRAY);
                g2.fillRoundRect(0, 0, w, h, h, h);
            }
            
            // Highlight
            g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,120), 0, h/2, new Color(255,255,255,0)));
            g2.fillOval(5, 2, w-10, h/2);
            
            // Border
            g2.setColor(Color.WHITE); 
            g2.setStroke(new BasicStroke(3f)); 
            g2.drawRoundRect(1, 1, w-2, h-2, h, h);
            
            // Sparkles
            for(Sparkle s : sparkles) s.draw(g2);
            
            g2.dispose();
        }
        
        private static class Sparkle {
            int x, y, life = 20; float size = (float)(Math.random()*3+2);
            Sparkle(int x, int y) { this.x=x; this.y=y; }
            void update() { life--; y-=1; size*=0.95f; }
            void draw(Graphics2D g) { float a=life/20f; g.setColor(new Color(255,255,255,(int)(a*200))); g.fill(new Ellipse2D.Float(x,y,size,size)); }
        }
    }

    private static class AnimatedXPBar extends JPanel {
        private float targetProgress = 0f, currentProgress = 0f;
        private int userLevel = 1;
        private Timer animTimer;
        
        public AnimatedXPBar() {
            setOpaque(false); setPreferredSize(new Dimension(160, 24));
            animTimer = new Timer(30, e -> {
                // Animasi naik
                if (currentProgress < targetProgress) {
                    currentProgress += 0.02f;
                    if (currentProgress > targetProgress) currentProgress = targetProgress;
                    repaint();
                }
                // Animasi reset/turun (jika target < current, misal setelah level up)
                else if (currentProgress > targetProgress) {
                     currentProgress = targetProgress; // Langsung snap agar tidak aneh saat level up
                     repaint();
                }
            });
            animTimer.start();
        }
        
        public void setLevelAndProgress(int lvl, float prog) {
            this.userLevel = lvl; 
            this.targetProgress = Math.max(0f, Math.min(1f, prog));
        }
        
        @Override 
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(), h = getHeight();
            
            // BG Bar
            g2.setColor(new Color(0,0,0,80)); 
            g2.fillRoundRect(0,0,w,h,h,h);
            
            // Fill Bar
            int fillW = (int)(w * currentProgress);
            if(fillW > 0) {
                g2.setClip(new RoundRectangle2D.Float(0,0,fillW,h,h,h));
                g2.setPaint(new GradientPaint(0,0,new Color(124,252,0),0,h,new Color(34,197,94)));
                g2.fillRect(0,0,fillW,h);
                g2.setClip(null);
            }
            
            // [MODIFIKASI] Logika Teks (Persentase & FULL)
            g2.setColor(Color.WHITE); 
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            
            String text;
            if (currentProgress >= 0.99f) {
                text = "FULL!";
            } else {
                int percent = (int)(currentProgress * 100);
                text = "Lvl " + userLevel + " (" + percent + "%)";
            }
            
            int tw = g2.getFontMetrics().stringWidth(text);
            
            // Draw Shadow Text
            g2.setColor(new Color(0,0,0,100));
            g2.drawString(text, (w-tw)/2 + 1, h/2 + 6);
            // Draw Main Text
            g2.setColor(Color.WHITE);
            g2.drawString(text, (w-tw)/2, h/2 + 5);
            
            g2.dispose();
        }
    }
}