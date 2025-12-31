package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.ModuleModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter; // <--- INI YANG TADI KURANG
import java.awt.event.MouseEvent;   // <--- INI JUGA
import java.net.URL;

public class ResultScreen extends JPanel {

    private ModuleModel lastModule;
    private int lastLevel;
    
    // Asset Images
    private Image bgImage; 
    private Image starBrightImg; 
    private Image starDarkImg;   

    // UI Components
    private JLabel lblTitle;
    private JLabel lblScore;
    private JPanel starsPanel;
    private JPanel buttonsPanel;
    
    private ModernButton btnNextLevel;
    private ModernButton btnRetry;
    private ModernButton btnModuleMenu;

    private final Font FONT_TITLE = new Font("Comic Sans MS", Font.BOLD, 48);
    private final Font FONT_SCORE = new Font("Comic Sans MS", Font.BOLD, 28);
    
    public ResultScreen() {
        setLayout(new BorderLayout());
        loadAssets(); 
        initUI();
    }

    private void loadAssets() {
        try {
            // Load Star Bright (Emas)
            URL brightUrl = getClass().getResource("/images/star_bright.png"); 
            if (brightUrl == null) brightUrl = getClass().getResource("/images/star_bright.jpg");
            if (brightUrl != null) starBrightImg = ImageIO.read(brightUrl);

            // Load Star Dark (Gelap/Abu)
            URL darkUrl = getClass().getResource("/images/star_dark.png");
            if (darkUrl == null) darkUrl = getClass().getResource("/images/star_dark.jpg");
            if (darkUrl != null) starDarkImg = ImageIO.read(darkUrl);
            
        } catch (Exception e) {
            System.err.println("Gagal memuat aset bintang: " + e.getMessage());
        }
    }

    private void initUI() {
        // --- PANEL TENGAH (CARD) ---
        JPanel resultCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(); int h = getHeight();
                // Shadow
                g2.setColor(new Color(0,0,0,60)); g2.fillRoundRect(5, 5, w-10, h-10, 50, 50);
                // Background Putih
                g2.setColor(new Color(255, 255, 255, 240)); g2.fillRoundRect(0, 0, w-5, h-5, 50, 50);
                // Border Putih
                g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(3)); g2.drawRoundRect(0, 0, w-5, h-5, 50, 50);
                g2.dispose();
            }
        };
        resultCard.setOpaque(false);
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
        resultCard.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.add(resultCard);

        // 1. JUDUL
        lblTitle = new JLabel("SELESAI!", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);
        
        // 2. PANEL BINTANG (Grid 1 Baris, 3 Kolom)
        starsPanel = new JPanel(new GridLayout(1, 3, 15, 0)); // Gap 15px antar bintang
        starsPanel.setOpaque(false);
        starsPanel.setMaximumSize(new Dimension(450, 150)); // Batasi lebar agar rapi
        starsPanel.setAlignmentX(CENTER_ALIGNMENT);
        
        // 3. SKOR
        lblScore = new JLabel("SKOR: 0", SwingConstants.CENTER);
        lblScore.setFont(FONT_SCORE);
        lblScore.setForeground(new Color(100, 100, 100));
        lblScore.setAlignmentX(CENTER_ALIGNMENT);

        // 4. TOMBOL
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);

        btnNextLevel = new ModernButton("LANJUT LEVEL >>", new Color(102, 187, 106));
        btnNextLevel.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showStory(lastModule, lastLevel + 1);
        });

        btnRetry = new ModernButton("ULANGI LEVEL", new Color(66, 165, 245));
        btnRetry.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showGame(lastModule, lastLevel);
        });

        btnModuleMenu = new ModernButton("PILIH MODUL LAIN", new Color(255, 167, 38));
        btnModuleMenu.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("MODULE_SELECT");
        });

        resultCard.add(lblTitle);
        resultCard.add(Box.createVerticalStrut(20));
        resultCard.add(starsPanel);
        resultCard.add(Box.createVerticalStrut(20));
        resultCard.add(lblScore);
        resultCard.add(Box.createVerticalStrut(30));
        resultCard.add(buttonsPanel);

        add(cardWrapper, BorderLayout.CENTER);
    }

    public void showResult(ModuleModel module, int level, int score, int maxScore) {
        this.lastModule = module;
        this.lastLevel = level;
        updateBackground(module.getId());

        // 1. Hitung Persentase
        double percentage = maxScore > 0 ? ((double) score / maxScore) * 100.0 : 0;
        
        // 2. Tentukan Jumlah Bintang Emas (Logic Ketat)
        int goldStars = 0;
        if (percentage == 100) {
            goldStars = 3; // Sempurna
            lblTitle.setText("SEMPURNA!");
            lblTitle.setForeground(new Color(255, 140, 0));
            playSound("win_gold");
        } else if (percentage >= 60) {
            goldStars = 2; // Bagus (Ada salah dikit)
            lblTitle.setText("BAGUS!");
            lblTitle.setForeground(new Color(76, 175, 80));
            playSound("win_silver");
        } else if (percentage > 0) {
            goldStars = 1; // Kurang (Banyak salah)
            lblTitle.setText("LUMAYAN!");
            lblTitle.setForeground(new Color(33, 150, 243));
            playSound("game_over");
        } else {
            goldStars = 0; // Salah Semua (0 Poin)
            lblTitle.setText("JANGAN MENYERAH!");
            lblTitle.setForeground(new Color(229, 57, 53));
            playSound("game_over");
        }
        
        lblScore.setText("SKOR KAMU: " + score + " / " + maxScore);

        // 3. Render Loop Visual Bintang
        starsPanel.removeAll();
        for (int i = 1; i <= 3; i++) {
            Image imgToUse = (i <= goldStars) ? starBrightImg : starDarkImg;
            starsPanel.add(new StarImagePanel(imgToUse));
        }

        // 4. Tombol Navigasi
        buttonsPanel.removeAll();
        boolean isPassed = percentage >= 60; // Lulus jika minimal 2 bintang
        
        if (isPassed && level < 3 && !module.getName().equalsIgnoreCase("EPILOGUE")) {
            btnNextLevel.setText("LANJUT KE LEVEL " + (level + 1));
            buttonsPanel.add(btnNextLevel);
            buttonsPanel.add(Box.createVerticalStrut(15));
        }
        
        buttonsPanel.add(btnRetry);
        buttonsPanel.add(Box.createVerticalStrut(15));
        buttonsPanel.add(btnModuleMenu);

        revalidate();
        repaint();
    }

    private void updateBackground(int modId) {
        try {
            URL url = getClass().getResource("/images/bg_module_" + modId + ".png");
            if (url == null) url = getClass().getResource("/images/bg_menu.png");
            if (url != null) bgImage = new ImageIcon(url).getImage();
        } catch (Exception e) { bgImage = null; }
    }

    private void playSound(String name) {
        try { SoundPlayer.getInstance().playSFX(name + ".wav"); } catch (Exception ignored) {}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, new Color(100, 181, 246), 0, getHeight(), Color.WHITE));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g.setColor(new Color(0, 0, 0, 100)); // Dimming effect
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // --- Component Penampil Gambar Bintang ---
    private class StarImagePanel extends JPanel {
        private Image img;
        public StarImagePanel(Image img) {
            this.img = img;
            setOpaque(false);
            // Ukuran Bintang 120x120
            setPreferredSize(new Dimension(120, 120)); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                // Gambar di tengah panel dengan proporsi terjaga
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.drawImage(img, x, y, size, size, null);
            }
        }
    }

    // --- Custom Button ---
    class ModernButton extends JButton {
        private Color baseColor;
        private boolean hover;
        public ModernButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            setForeground(Color.WHITE);
            setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setPreferredSize(new Dimension(280, 55));
            setMaximumSize(new Dimension(280, 55));
            
            // MouseAdapter perlu import java.awt.event.*
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            int offset = getModel().isPressed() ? 3 : 0;
            g2.setColor(new Color(0,0,0,40)); g2.fillRoundRect(3, 6, w-6, h-6, 40, 40);
            g2.setColor(hover ? baseColor.brighter() : baseColor); g2.fillRoundRect(0, offset, w, h-offset-2, 40, 40);
            g2.setColor(Color.WHITE); FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = (h - offset - 2 - fm.getHeight()) / 2 + fm.getAscent() + offset;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}