package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.UserRepository;     
import com.petualanganbelajar.util.LevelManager;             
import com.petualanganbelajar.db.DatabaseConnection;
import com.petualanganbelajar.ui.component.LevelUpDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.*;

public class ResultScreen extends JPanel {

    private ModuleModel lastModule;
    private int lastLevel;
    
    private Image bgImage; 
    private Image starBrightImg; 
    private Image starDarkImg;    

    // Komponen UI
    private JPanel resultCard;
    private JLabel lblTitle;
    private JLabel lblScore;
    private JPanel starsPanel;
    private JPanel buttonsPanel;
    
    private ModernButton btnNextLevel;
    private ModernButton btnRetry;
    private ModernButton btnModuleMenu;

    // Variabel Responsif
    private final float BASE_W = 1920f;
    private final float BASE_H = 1080f;
    private float scaleFactor = 1.0f;
    
    private final UserRepository userRepo = new UserRepository();
    
    public ResultScreen() {
        setLayout(new BorderLayout());
        loadAssets(); 
        initUI();
        
        // Listener untuk Auto-Scaling
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateScaleFactor();
                updateResponsiveLayout();
            }
        });
    }

    private void calculateScaleFactor() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        float sW = (float) getWidth() / BASE_W;
        float sH = (float) getHeight() / BASE_H;
        this.scaleFactor = Math.min(sW, sH);
        if (this.scaleFactor < 0.5f) this.scaleFactor = 0.5f;
    }

    private void updateResponsiveLayout() {
        if (resultCard == null) return;

        // 1. Update Padding Card
        int padV = (int)(40 * scaleFactor);
        int padH = (int)(50 * scaleFactor);
        resultCard.setBorder(new EmptyBorder(padV, padH, padV, padH));

        // 2. Update Font Judul & Skor
        lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(48 * scaleFactor)));
        lblScore.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(28 * scaleFactor)));

        // 3. Update Ukuran Tombol
        if (btnNextLevel != null) btnNextLevel.updateScale(scaleFactor);
        if (btnRetry != null) btnRetry.updateScale(scaleFactor);
        if (btnModuleMenu != null) btnModuleMenu.updateScale(scaleFactor);

        // 4. Update Ukuran Bintang
        if (starsPanel != null) {
            // Lebar panel bintang menyesuaikan
            starsPanel.setMaximumSize(new Dimension((int)(450*scaleFactor), (int)(150*scaleFactor)));
            // Jarak antar bintang
            ((GridLayout)starsPanel.getLayout()).setHgap((int)(15*scaleFactor));
            
            // Resize panel bintang di dalamnya
            for (Component c : starsPanel.getComponents()) {
                if (c instanceof StarImagePanel) {
                    ((StarImagePanel)c).setPreferredSize(new Dimension((int)(120*scaleFactor), (int)(120*scaleFactor)));
                }
            }
        }

        revalidate();
        repaint();
    }

    private void loadAssets() {
        try {
            URL brightUrl = getClass().getResource("/images/star_bright.png"); 
            if (brightUrl == null) brightUrl = getClass().getResource("/images/star_bright.jpg");
            if (brightUrl != null) starBrightImg = ImageIO.read(brightUrl);

            URL darkUrl = getClass().getResource("/images/star_dark.png");
            if (darkUrl == null) darkUrl = getClass().getResource("/images/star_dark.jpg");
            if (darkUrl != null) starDarkImg = ImageIO.read(darkUrl);
            
        } catch (Exception e) {
            System.err.println("Gagal memuat aset bintang: " + e.getMessage());
        }
    }

    private void initUI() {
        resultCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(); int h = getHeight();
                int arc = (int)(50 * scaleFactor); // Arc responsif
                
                g2.setColor(new Color(0,0,0,60)); 
                g2.fillRoundRect((int)(5*scaleFactor), (int)(5*scaleFactor), w-(int)(10*scaleFactor), h-(int)(10*scaleFactor), arc, arc);
                
                g2.setColor(new Color(255, 255, 255, 240)); 
                g2.fillRoundRect(0, 0, w-(int)(5*scaleFactor), h-(int)(5*scaleFactor), arc, arc);
                
                g2.setColor(Color.WHITE); 
                g2.setStroke(new BasicStroke(3 * scaleFactor)); 
                g2.drawRoundRect(0, 0, w-(int)(5*scaleFactor), h-(int)(5*scaleFactor), arc, arc);
                g2.dispose();
            }
        };
        resultCard.setOpaque(false);
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
        
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.add(resultCard);

        lblTitle = new JLabel("SELESAI!", SwingConstants.CENTER);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);
        
        starsPanel = new JPanel(new GridLayout(1, 3));
        starsPanel.setOpaque(false);
        starsPanel.setAlignmentX(CENTER_ALIGNMENT);
        
        lblScore = new JLabel("SKOR: 0", SwingConstants.CENTER);
        lblScore.setForeground(new Color(100, 100, 100));
        lblScore.setAlignmentX(CENTER_ALIGNMENT);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);

        btnNextLevel = new ModernButton("LANJUT LEVEL >>", new Color(102, 187, 106));
        btnNextLevel.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showGame(lastModule, lastLevel + 1);
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
        resultCard.add(Box.createVerticalStrut(20)); // Spacer statis, nanti akan direfresh layoutnya
        resultCard.add(starsPanel);
        resultCard.add(Box.createVerticalStrut(20));
        resultCard.add(lblScore);
        resultCard.add(Box.createVerticalStrut(30));
        resultCard.add(buttonsPanel);

        add(cardWrapper, BorderLayout.CENTER);
    }

    // --- Override setVisible agar layout terupdate saat layar muncul ---
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            SwingUtilities.invokeLater(() -> {
                calculateScaleFactor();
                updateResponsiveLayout();
            });
        }
    }

    public void showResult(ModuleModel module, int level, int score, int maxScore) {
        this.lastModule = module;
        this.lastLevel = level;
        updateBackground(module.getId());

        double percentage = maxScore > 0 ? ((double) score / maxScore) * 100.0 : 0;
        int goldStars = 0;
        
        if (percentage == 100) {
            goldStars = 3; lblTitle.setText("SEMPURNA!"); lblTitle.setForeground(new Color(255, 140, 0)); playSound("level_complete");
        } else if (percentage >= 60) {
            goldStars = 2; lblTitle.setText("BAGUS!"); lblTitle.setForeground(new Color(76, 175, 80)); playSound("level_complete");
        } else if (percentage > 0) {
            goldStars = 1; lblTitle.setText("LUMAYAN!"); lblTitle.setForeground(new Color(33, 150, 243)); playSound("level_failed");
        } else {
            goldStars = 0; lblTitle.setText("JANGAN MENYERAH!"); lblTitle.setForeground(new Color(229, 57, 53)); playSound("level_failed");
        }
        
        lblScore.setText("SKOR KAMU: " + score + " / " + maxScore);

        starsPanel.removeAll();
        for (int i = 1; i <= 3; i++) {
            Image imgToUse = (i <= goldStars) ? starBrightImg : starDarkImg;
            starsPanel.add(new StarImagePanel(imgToUse));
        }

        buttonsPanel.removeAll();
        // Gunakan Box.createRigidArea agar bisa disesuaikan ukurannya nanti
        boolean isPassed = percentage >= 60;
        
        if (isPassed && level < 3 && !module.getName().equalsIgnoreCase("EPILOGUE")) {
            btnNextLevel.setText("LANJUT KE LEVEL " + (level + 1));
            buttonsPanel.add(btnNextLevel);
            buttonsPanel.add(Box.createRigidArea(new Dimension(0, (int)(15*scaleFactor))));
        }
        buttonsPanel.add(btnRetry);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, (int)(15*scaleFactor))));
        buttonsPanel.add(btnModuleMenu);

        // Panggil update layout lagi untuk memastikan posisi benar
        updateResponsiveLayout(); 
        
        if (score > 0) {
            processLevelUp(module.getId(), level, score);
        }
    }

    private void processLevelUp(int moduleId, int levelId, int currentScore) {
        UserModel currentUser = GameState.getCurrentUser();
        if (currentUser == null) return; 

        saveGameResult(currentUser.getId(), moduleId, levelId, currentScore);
        int totalScore = getTotalScoreByUserId(currentUser.getId());
        int oldLevel = currentUser.getLevel();
        int newLevel = LevelManager.calculateLevelFromScore(totalScore);

        if (newLevel > oldLevel) {
            userRepo.updateUserLevel(currentUser.getId(), newLevel);
            currentUser.setLevel(newLevel);
            
            SwingUtilities.invokeLater(() -> {
                LevelUpDialog dialog = new LevelUpDialog((Frame) SwingUtilities.getWindowAncestor(this), newLevel);
                dialog.setVisible(true);
                playSound("levelup"); 
            });
            System.out.println("LEVEL UP! " + oldLevel + " -> " + newLevel);
        }
    }

    private void saveGameResult(int userId, int modId, int lvl, int score) {
        String sql = "INSERT INTO game_results (user_id, module_id, level, score, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, modId);
            pstmt.setInt(3, lvl);
            pstmt.setInt(4, score);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private int getTotalScoreByUserId(int userId) {
        String sql = "SELECT SUM(score) as total FROM game_results WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
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
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private class StarImagePanel extends JPanel {
        private Image img;
        public StarImagePanel(Image img) {
            this.img = img;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                // Gambar bintang responsive
                g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }

    class ModernButton extends JButton {
        private Color baseColor;
        private boolean hover;
        
        public ModernButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            setForeground(Color.WHITE);
            setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        
        public void updateScale(float s) {
            setFont(new Font("Comic Sans MS", Font.BOLD, (int)(20 * s)));
            // Update ukuran tombol
            setPreferredSize(new Dimension((int)(280*s), (int)(55*s)));
            setMaximumSize(new Dimension((int)(280*s), (int)(55*s)));
            revalidate(); repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            int offset = getModel().isPressed() ? 3 : 0;
            int arc = (int)(40 * scaleFactor); // Arc responsive
            
            g2.setColor(new Color(0,0,0,40)); 
            g2.fillRoundRect((int)(3*scaleFactor), (int)(6*scaleFactor), w-(int)(6*scaleFactor), h-(int)(6*scaleFactor), arc, arc);
            
            g2.setColor(hover ? baseColor.brighter() : baseColor); 
            g2.fillRoundRect(0, offset, w, h-offset-2, arc, arc);
            
            g2.setColor(Color.WHITE); FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = (h - offset - 2 - fm.getHeight()) / 2 + fm.getAscent() + offset;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}