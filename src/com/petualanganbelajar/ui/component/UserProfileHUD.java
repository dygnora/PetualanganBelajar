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
    private JLabel lblAvatar;
    private AnimatedXPBar xpBar;
    private SparklyBubblePanel container;
    
    // [FIX UTAMA] Inisialisasi ke 0f agar updateScale(1.0f) di constructor PASTI dijalankan
    private float currentScale = 0f;

    public UserProfileHUD() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        Color[] defaultColors = {
            new Color(255, 107, 107), new Color(255, 159, 64),
            new Color(255, 206, 86), new Color(75, 192, 192)
        };
        
        container = new SparklyBubblePanel(new FlowLayout(FlowLayout.LEFT, 15, 8), defaultColors);
        
        // Avatar
        lblAvatar = new JLabel();
        // Icon akan diload pertama kali via updateScale
        
        // Nama
        lblUserName = new JLabel("Player");
        lblUserName.setForeground(Color.WHITE);
        
        // XP Bar
        xpBar = new AnimatedXPBar();
        
        container.add(lblAvatar);
        container.add(lblUserName);
        container.add(xpBar);
        
        add(container);
        
        // Inisialisasi awal (Pasti jalan karena currentScale 0 != 1.0)
        updateScale(1.0f);
    }
    
    public void setProfileTheme(Color[] newColors) {
        if (container != null) container.setColors(newColors);
    }
    
    /**
     * [OPTIMASI] Method ini dipanggil oleh GameScreen HANYA saat resize.
     * Kita mengupdate font dan ukuran di sini, BUKAN di paintComponent.
     */
    public void updateScale(float scale) {
        // Cegah update berlebihan jika perubahan skala sangat kecil (Micro-optimization)
        if (Math.abs(this.currentScale - scale) < 0.01f) return;
        this.currentScale = scale;
        
        // 1. Update Gap & Padding Container
        int gapH = (int)(15 * scale);
        int gapV = (int)(8 * scale);
        ((FlowLayout)container.getLayout()).setHgap(gapH);
        ((FlowLayout)container.getLayout()).setVgap(gapV);
        container.setBorder(new EmptyBorder((int)(5*scale), gapH, (int)(5*scale), gapH));
        
        // 2. Update Ukuran Avatar (Load gambar sesuai skala)
        int iconSize = (int)(45 * scale);
        ImageIcon icon = UIHelper.loadIcon("user_icon.png", iconSize, iconSize);
        if (icon != null) lblAvatar.setIcon(icon);
        else { 
            lblAvatar.setText("😊"); 
            lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, (int)(40*scale))); 
        }
        
        // 3. Update Font Nama
        lblUserName.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(24 * scale)));
        
        // 4. Update Ukuran XP Bar
        xpBar.updateScale(scale);
        
        revalidate();
        repaint();
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

    // --- Inner Classes ---
    
    private static class SparklyBubblePanel extends JPanel {
        private Color[] colors;
        private Timer sparkleTimer;
        private java.util.List<Sparkle> sparkles = new ArrayList<>();
        
        public SparklyBubblePanel(LayoutManager layout, Color[] colors) {
            super(layout); this.colors = colors; setOpaque(false);
            
            // Timer berjalan independen, tidak terpengaruh resize
            sparkleTimer = new Timer(150, e -> {
                if (getWidth() > 0 && Math.random() > 0.7) 
                    sparkles.add(new Sparkle((int)(Math.random()*getWidth()), (int)(Math.random()*getHeight())));
                
                try {
                    sparkles.removeIf(s -> s.life <= 0); 
                    sparkles.forEach(Sparkle::update); 
                } catch (Exception ex) {} // Safety catch concurrency
                repaint();
            });
            sparkleTimer.start();
        }

        public void setColors(Color[] c) { this.colors = c; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            
            if (colors != null && colors.length >= 2) {
                float[] fractions = (colors.length==2) ? new float[]{0f,1f} : 
                                    (colors.length==3) ? new float[]{0f,0.5f,1f} : new float[]{0f,0.33f,0.66f,1f};
                g2.setPaint(new LinearGradientPaint(0,0,w,h,fractions,colors,MultipleGradientPaint.CycleMethod.NO_CYCLE));
                g2.fillRoundRect(0,0,w,h,h,h);
            } else {
                g2.setColor(Color.GRAY); g2.fillRoundRect(0,0,w,h,h,h);
            }
            g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,120),0,h/2,new Color(255,255,255,0)));
            g2.fillOval(5,2,w-10,h/2);
            g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(3f)); g2.drawRoundRect(1,1,w-2,h-2,h,h);
            
            try { for(Sparkle s : sparkles) s.draw(g2); } catch(Exception ignored){}
            g2.dispose();
        }
        
        private static class Sparkle {
            int x, y, life = 20; float size = 5f;
            Sparkle(int x, int y) { this.x=x; this.y=y; }
            void update() { life--; y-=1; size*=0.95f; }
            void draw(Graphics2D g) { 
                float a=Math.max(0, life/20f); 
                g.setColor(new Color(255,255,255,(int)(a*200))); 
                g.fill(new Ellipse2D.Float(x,y,size,size)); 
            }
        }
    }

    private static class AnimatedXPBar extends JPanel {
        private float targetProgress = 0f, currentProgress = 0f;
        private int userLevel = 1;
        private Timer animTimer;
        private float scale = 1.0f; // Scale lokal untuk font
        
        public AnimatedXPBar() {
            setOpaque(false); 
            setPreferredSize(new Dimension(160, 24)); // Ukuran Default
            
            animTimer = new Timer(30, e -> {
                boolean needRepaint = false;
                if (currentProgress < targetProgress) {
                    currentProgress += 0.02f;
                    if (currentProgress > targetProgress) currentProgress = targetProgress;
                    needRepaint = true;
                } else if (currentProgress > targetProgress) {
                     currentProgress = targetProgress; // Snap untuk reset level
                     needRepaint = true;
                }
                if(needRepaint) repaint(); // Optimasi: repaint hanya jika angka berubah
            });
            animTimer.start();
        }
        
        // Method khusus untuk update ukuran tanpa membuat objek baru
        public void updateScale(float s) {
            this.scale = s;
            int w = (int)(160 * s);
            int h = (int)(24 * s);
            setPreferredSize(new Dimension(w, h));
            revalidate(); // Revalidate layout, jangan repaint berat di sini
        }
        
        public void setLevelAndProgress(int lvl, float prog) {
            this.userLevel = lvl; 
            this.targetProgress = Math.max(0f, Math.min(1f, prog));
        }
        
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int arc = h; 
            
            // BG
            g2.setColor(new Color(0,0,0,80)); g2.fillRoundRect(0,0,w,h,arc,arc);
            
            // Fill
            int fillW = (int)(w * currentProgress);
            if(fillW > 0) {
                g2.setClip(new RoundRectangle2D.Float(0,0,fillW,h,arc,arc));
                g2.setPaint(new GradientPaint(0,0,new Color(124,252,0),0,h,new Color(34,197,94)));
                g2.fillRect(0,0,fillW,h);
                g2.setClip(null);
            }
            
            // Text Scale
            g2.setColor(Color.WHITE); 
            // Ukuran font mengikuti scale yang dikirim
            g2.setFont(new Font("Arial", Font.BOLD, Math.max(10, (int)(12 * scale)))); 
            
            // Logika Teks Persentase / FULL
            String text;
            if (currentProgress >= 0.99f) {
                text = "FULL!";
            } else {
                int percent = (int)(currentProgress * 100);
                text = "Lvl " + userLevel + " (" + percent + "%)";
            }
            
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text);
            int tx = (w - tw) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent(); // Center Vertical yang akurat
            
            g2.setColor(new Color(0,0,0,100)); g2.drawString(text, tx+1, ty+1);
            g2.setColor(Color.WHITE); g2.drawString(text, tx, ty);
            g2.dispose();
        }
    }
}