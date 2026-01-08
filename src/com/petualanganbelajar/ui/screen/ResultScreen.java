package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.UserRepository;     
import com.petualanganbelajar.util.LevelManager;             
import com.petualanganbelajar.db.DatabaseConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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

    private JLabel lblTitle;
    private JLabel lblScore;
    private JPanel starsPanel;
    private JPanel buttonsPanel;
    
    private ModernButton btnNextLevel;
    private ModernButton btnRetry;
    private ModernButton btnModuleMenu;

    private final Font FONT_TITLE = new Font("Comic Sans MS", Font.BOLD, 48);
    private final Font FONT_SCORE = new Font("Comic Sans MS", Font.BOLD, 28);
    
    // Repository untuk update level
    private final UserRepository userRepo = new UserRepository();
    
    public ResultScreen() {
        setLayout(new BorderLayout());
        loadAssets(); 
        initUI();
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
        JPanel resultCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(); int h = getHeight();
                g2.setColor(new Color(0,0,0,60)); g2.fillRoundRect(5, 5, w-10, h-10, 50, 50);
                g2.setColor(new Color(255, 255, 255, 240)); g2.fillRoundRect(0, 0, w-5, h-5, 50, 50);
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

        lblTitle = new JLabel("SELESAI!", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);
        
        starsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        starsPanel.setOpaque(false);
        starsPanel.setMaximumSize(new Dimension(450, 150));
        starsPanel.setAlignmentX(CENTER_ALIGNMENT);
        
        lblScore = new JLabel("SKOR: 0", SwingConstants.CENTER);
        lblScore.setFont(FONT_SCORE);
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
        resultCard.add(Box.createVerticalStrut(20));
        resultCard.add(starsPanel);
        resultCard.add(Box.createVerticalStrut(20));
        resultCard.add(lblScore);
        resultCard.add(Box.createVerticalStrut(30));
        resultCard.add(buttonsPanel);

        add(cardWrapper, BorderLayout.CENTER);
    }

    // --- LOGIC UTAMA DISINI ---
    public void showResult(ModuleModel module, int level, int score, int maxScore) {
        this.lastModule = module;
        this.lastLevel = level;
        updateBackground(module.getId());

        // 1. Tampilkan UI Bintang & Skor
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
        boolean isPassed = percentage >= 60;
        
        if (isPassed && level < 3 && !module.getName().equalsIgnoreCase("EPILOGUE")) {
            btnNextLevel.setText("LANJUT KE LEVEL " + (level + 1));
            buttonsPanel.add(btnNextLevel);
            buttonsPanel.add(Box.createVerticalStrut(15));
        }
        buttonsPanel.add(btnRetry);
        buttonsPanel.add(Box.createVerticalStrut(15));
        buttonsPanel.add(btnModuleMenu);

        revalidate(); repaint();
        
        // 2. [LOGIC LEVEL UP] Hanya jika skor > 0
        if (score > 0) {
            processLevelUp(module.getId(), level, score);
        }
    }

    // --- METHOD PROSES LEVEL UP ---
    private void processLevelUp(int moduleId, int levelId, int currentScore) {
        UserModel currentUser = GameState.getCurrentUser();
        if (currentUser == null) return; 

        // A. Simpan Hasil Game ke Database
        saveGameResult(currentUser.getId(), moduleId, levelId, currentScore);

        // B. Hitung Total Skor User
        int totalScore = getTotalScoreByUserId(currentUser.getId());

        // C. Hitung Level Baru
        int oldLevel = currentUser.getLevel();
        int newLevel = LevelManager.calculateLevelFromScore(totalScore);

        // D. Cek Apakah Naik Level?
        if (newLevel > oldLevel) {
            // Update Database & Memory
            userRepo.updateUserLevel(currentUser.getId(), newLevel);
            currentUser.setLevel(newLevel);
            
            // Tampilkan Dialog Level Up & Mainkan Suara
            SwingUtilities.invokeLater(() -> {
                LevelUpDialog dialog = new LevelUpDialog((Frame) SwingUtilities.getWindowAncestor(this), newLevel);
                dialog.setVisible(true);
                
                // --- INI PEMANGGILAN SUARA NYA ---
                // Pastikan file src/audio/levelup.wav ada
                playSound("levelup"); 
            });
            
            System.out.println("LEVEL UP! " + oldLevel + " -> " + newLevel);
        }
    }

    // Helper: Simpan ke tabel game_results
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

    // Helper: Ambil total skor
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
        // Asumsi SoundPlayer membaca dari folder /audio/ atau root classpath
        // Jika file ada di src/audio/levelup.wav, maka name="levelup" akan menjadi "levelup.wav"
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
            setPreferredSize(new Dimension(120, 120)); 
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.drawImage(img, x, y, size, size, null);
            }
        }
    }

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
    
    // --- DIALOG LEVEL UP (POPUP) ---
    private class LevelUpDialog extends JDialog {
        public LevelUpDialog(Frame parent, int newLevel) {
            super(parent, true);
            setUndecorated(true);
            setBackground(new Color(0,0,0,0));
            
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Background Emas Gradien
                    GradientPaint gp = new GradientPaint(0, 0, new Color(255, 215, 0), 0, getHeight(), new Color(255, 140, 0));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                    
                    // Border Putih
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(5));
                    g2.drawRoundRect(2, 2, getWidth()-5, getHeight()-5, 40, 40);
                }
            };
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(new EmptyBorder(30, 30, 30, 30));
            
            JLabel lblTitle = new JLabel("LEVEL UP!");
            lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblMsg = new JLabel("Selamat! Kamu naik ke Level " + newLevel);
            lblMsg.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
            lblMsg.setForeground(new Color(255, 255, 224));
            lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JButton btnOk = new JButton("HEBAT!");
            btnOk.setFont(new Font("Arial", Font.BOLD, 20));
            btnOk.setBackground(Color.WHITE);
            btnOk.setForeground(new Color(255, 140, 0));
            btnOk.setFocusPainted(false);
            btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnOk.addActionListener(e -> dispose());
            
            panel.add(lblTitle);
            panel.add(Box.createVerticalStrut(20));
            panel.add(lblMsg);
            panel.add(Box.createVerticalStrut(30));
            panel.add(btnOk);
            
            setContentPane(panel);
            setSize(400, 300);
            setLocationRelativeTo(parent);
        }
    }
}