package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.ProgressRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ModuleSelectionScreen extends JPanel {

    // Data
    private List<ModuleModel> modules;
    private int currentIndex = 0;
    
    // Background Image
    private Image currentBgImage;

    // UI Components
    private JLabel lblUserInfo;
    private JLabel lblTotalScore;
    
    // Carousel Components
    private PaperCardPanel cardPanel;
    private JLabel lblModuleName;
    private JTextArea txtModuleDesc;
    
    // Level Selection Panel
    private JPanel levelButtonPanel; 
    
    private JPanel centerContainer;
    private JPanel footer;
    
    // Navigation Buttons
    private JButton btnPrev;
    private JButton btnNext;

    private SoundPlayer soundPlayer = SoundPlayer.getInstance();
    private ProgressRepository progressRepo = new ProgressRepository();

    public ModuleSelectionScreen() {
        setLayout(new BorderLayout());
        
        // ==========================================
        // 1. HEADER (Floating UI)
        // ==========================================
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false); 
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel profileBadge = new BadgePanel(new Color(101, 67, 33)); 
        lblUserInfo = new JLabel("Player: -");
        lblUserInfo.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        lblUserInfo.setForeground(Color.WHITE);
        profileBadge.add(lblUserInfo);
        topBar.add(profileBadge, BorderLayout.WEST);

        JPanel scoreBadge = new BadgePanel(new Color(255, 193, 7)); 
        lblTotalScore = new JLabel("Total Skor: 0");
        lblTotalScore.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        lblTotalScore.setForeground(new Color(62, 39, 35)); 
        scoreBadge.add(lblTotalScore);
        topBar.add(scoreBadge, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // ==========================================
        // 2. CENTER (CAROUSEL + LEVEL BUTTONS)
        // ==========================================
        centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false); 
        centerContainer.setBorder(new EmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();

        btnPrev = createAnimatedImageButton("btn_prev.png", "<"); 
        btnPrev.addActionListener(e -> navigate(-1));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.weightx = 0.15; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        centerContainer.add(btnPrev, gbc);

        cardPanel = new PaperCardPanel(); 
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        lblModuleName = new JLabel("MODUL", SwingConstants.CENTER);
        lblModuleName.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        lblModuleName.setForeground(new Color(93, 64, 55)); 
        lblModuleName.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtModuleDesc = new JTextArea("Deskripsi modul...");
        txtModuleDesc.setFont(new Font("Comic Sans MS", Font.PLAIN, 18)); 
        txtModuleDesc.setForeground(new Color(117, 117, 117)); 
        txtModuleDesc.setWrapStyleWord(true);
        txtModuleDesc.setLineWrap(true);
        txtModuleDesc.setOpaque(false);
        txtModuleDesc.setEditable(false);
        txtModuleDesc.setFocusable(false);
        txtModuleDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtModuleDesc.setMaximumSize(new Dimension(450, 60));
        
        levelButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        levelButtonPanel.setOpaque(false);
        levelButtonPanel.setMaximumSize(new Dimension(500, 100)); 

        cardPanel.add(Box.createVerticalGlue());
        cardPanel.add(lblModuleName);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(txtModuleDesc);
        cardPanel.add(Box.createVerticalStrut(25));
        
        JLabel lblPilih = new JLabel("Pilih Level");
        lblPilih.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        lblPilih.setForeground(new Color(141, 110, 99)); 
        lblPilih.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblPilih);
        cardPanel.add(Box.createVerticalStrut(10));
        
        cardPanel.add(levelButtonPanel); 
        cardPanel.add(Box.createVerticalGlue());

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        centerContainer.add(cardPanel, gbc);

        btnNext = createAnimatedImageButton("btn_next.png", ">");
        btnNext.addActionListener(e -> navigate(1));

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.15;
        centerContainer.add(btnNext, gbc);

        add(centerContainer, BorderLayout.CENTER);

        // ==========================================
        // 3. FOOTER
        // ==========================================
        FunnyButton btnBack = new FunnyButton("Kembali ke Menu", new Color(239, 83, 80)); 
        btnBack.addActionListener(e -> ScreenManager.getInstance().showScreen("MAIN_MENU"));
        
        footer = new JPanel();
        footer.setOpaque(false); 
        footer.setBorder(new EmptyBorder(0,0,20,0)); 
        footer.add(btnBack);
        add(footer, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentBgImage != null) {
            g.drawImage(currentBgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(GameConfig.COLOR_BG);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        g.setColor(new Color(0, 0, 0, 30));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            refreshUserInfo();
            loadModules();
        }
    }

    private void loadModules() {
        this.modules = new ArrayList<>();
        modules.add(new ModuleModel(1, "ANGKA", "Belajar Berhitung"));
        modules.add(new ModuleModel(2, "HURUF", "Membaca & Menulis"));
        modules.add(new ModuleModel(3, "WARNA", "Mengenal Warna"));
        modules.add(new ModuleModel(4, "BENTUK", "Geometri Dasar"));
        
        this.currentIndex = 0;
        updateCarousel(); 
    }

    private void navigate(int direction) {
        if (modules == null || modules.isEmpty()) return;
        currentIndex += direction;
        if (currentIndex < 0) currentIndex = modules.size() - 1;
        else if (currentIndex >= modules.size()) currentIndex = 0;
        updateCarousel();
    }

    private void updateCarousel() {
        if (modules == null || modules.isEmpty()) return;

        ModuleModel mod = modules.get(currentIndex);
        
        lblModuleName.setText(mod.getName().toUpperCase()); 
        txtModuleDesc.setText(mod.getDescription());
        
        loadBackgroundImage(mod.getId());
        updateLevelButtons(mod);
        
        repaint();
    }
    
    private Color getModuleColor(int modId) {
        switch (modId % 4) {
            case 1: return new Color(255, 193, 7);   
            case 2: return new Color(255, 105, 180); 
            case 3: return new Color(255, 69, 0);    
            case 0: return new Color(50, 205, 50);   
            default: return GameConfig.COLOR_PRIMARY;
        }
    }

    private void updateLevelButtons(ModuleModel module) {
        levelButtonPanel.removeAll();
        
        // FIX ERROR 4: GameState statis
        UserModel user = GameState.getCurrentUser();
        
        int highestUnlocked = 1; 
        if (user != null) {
            highestUnlocked = progressRepo.getHighestLevelUnlocked(user.getId(), module.getId());
        }

        Color themeColor = getModuleColor(module.getId());

        for (int i = 1; i <= 3; i++) {
            int lvlNum = i;
            boolean isUnlocked = (lvlNum <= highestUnlocked);
            
            LevelButton btnLvl = new LevelButton(lvlNum, isUnlocked, themeColor);
            btnLvl.addActionListener(e -> {
                if (isUnlocked) {
                    playSound("click");
                    // FIX ERROR 5: Gunakan method bawaan ScreenManager
                    ScreenManager.getInstance().showGame(module, lvlNum);
                } else {
                    playSound("error"); 
                }
            });
            
            levelButtonPanel.add(btnLvl);
        }
        
        levelButtonPanel.revalidate();
        levelButtonPanel.repaint();
    }
    
    private void loadBackgroundImage(int modId) {
        String filename = "bg_module_" + modId + ".png";
        try {
            URL url = getClass().getResource("/images/" + filename);
            if (url != null) {
                currentBgImage = new ImageIcon(url).getImage();
            } else {
                currentBgImage = null; 
            }
        } catch (Exception e) {
            currentBgImage = null;
        }
    }
    
    private void refreshUserInfo() {
        // FIX ERROR 6: GameState statis
        UserModel u = GameState.getCurrentUser();
        if (u != null) {
            lblUserInfo.setText("Player: " + u.getName());
            int totalScore = progressRepo.calculateTotalScore(u.getId());
            lblTotalScore.setText("Total Skor: " + totalScore);
        }
    }

    private void playSound(String name) {
        try { soundPlayer.playSFX(name + ".wav"); } catch (Exception ignored) {}
    }

    private JButton createAnimatedImageButton(String filename, String fallbackText) {
        JButton btn = new JButton();
        
        int normalSize = 130;
        ImageIcon normalIcon = null;
        ImageIcon hoverIcon = null;

        URL url = getClass().getResource("/images/" + filename);

        if (url != null) {
            try {
                ImageIcon raw = new ImageIcon(url);
                normalIcon = new ImageIcon(raw.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH));
                hoverIcon = new ImageIcon(raw.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH));
                btn.setIcon(normalIcon);
            } catch (Exception e) {
                btn.setText(fallbackText);
            }
        } else {
            btn.setText(fallbackText);
            btn.setFont(new Font("SansSerif", Font.BOLD, 60));
            btn.setForeground(Color.ORANGE);
        }

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(normalSize, normalSize));

        final ImageIcon iconNormalFinal = normalIcon;
        final ImageIcon iconHoverFinal = hoverIcon;

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (iconHoverFinal != null) btn.setIcon(iconHoverFinal); }
            @Override public void mouseExited(MouseEvent e) { if (iconNormalFinal != null) btn.setIcon(iconNormalFinal); }
        });
        return btn;
    }
    
    class LevelButton extends JButton {
        private int level;
        private boolean isUnlocked;
        private Color themeColor;
        private boolean hover;
        private boolean pressed;

        public LevelButton(int level, boolean unlocked, Color themeColor) {
            this.level = level;
            this.isUnlocked = unlocked;
            this.themeColor = themeColor;
            
            setPreferredSize(new Dimension(80, 80)); 
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(isUnlocked ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if(isUnlocked) { hover = true; repaint(); } }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                public void mousePressed(MouseEvent e) { if(isUnlocked) { pressed = true; repaint(); } }
                public void mouseReleased(MouseEvent e) { if(isUnlocked) { pressed = false; repaint(); } }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(); int h = getHeight(); int offset = pressed ? 2 : 0; 
            
            if (isUnlocked) {
                Color base = themeColor; 
                Color dark = themeColor.darker(); 
                if (hover) base = themeColor.brighter();
                
                g2.setColor(new Color(0,0,0,30)); g2.fillRoundRect(5, 8, w-10, h-10, 20, 20); 
                g2.setColor(dark); g2.fillRoundRect(5, 5+offset, w-10, h-10-offset, 20, 20); 
                g2.setColor(base); g2.fillRoundRect(5, 0+offset, w-10, h-10, 20, 20); 
                
                g2.setColor(Color.WHITE); g2.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
                FontMetrics fm = g2.getFontMetrics();
                String text = String.valueOf(level);
                int x = (w - fm.stringWidth(text))/2;
                int y = (h - 10 - fm.getHeight())/2 + fm.getAscent() + offset;
                g2.drawString(text, x, y);
                
            } else {
                g2.setColor(new Color(224, 224, 224)); g2.fillRoundRect(5, 0, w-10, h-10, 20, 20);
                g2.setColor(new Color(189, 189, 189)); g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                FontMetrics fm = g2.getFontMetrics();
                String text = "🔒";
                int x = (w - fm.stringWidth(text))/2;
                int y = (h - 10 - fm.getHeight())/2 + fm.getAscent();
                g2.drawString(text, x, y);
            }
            g2.dispose();
        }
    }

    class PaperCardPanel extends JPanel {
        public PaperCardPanel() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight(); int arc = 20; 
            g2.setColor(new Color(0,0,0,20)); g2.fillRoundRect(5, 5, w-10, h-10, arc, arc); 
            g2.setColor(new Color(255, 248, 225)); g2.fillRoundRect(0, 0, w-5, h-5, arc, arc); 
            g2.setColor(new Color(215, 204, 200)); g2.setStroke(new BasicStroke(1)); 
            g2.drawRoundRect(0, 0, w-5, h-5, arc, arc); 
        }
    }

    class FunnyButton extends JButton {
        private Color baseColor; private Timer animTimer; private float scale = 1.0f; private float targetScale = 1.0f; 
        public FunnyButton(String text, Color color) {
            super(text); this.baseColor = color;
            setFont(new Font("Comic Sans MS", Font.BOLD, 22)); setForeground(Color.WHITE);
            setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false); setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(300, 70));
            animTimer = new Timer(16, e -> {
                if (Math.abs(targetScale - scale) > 0.01f) { scale += (targetScale - scale) * 0.2f; repaint(); } 
                else { scale = targetScale; ((Timer)e.getSource()).stop(); repaint(); }
            });
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { targetScale = 1.05f; if (!animTimer.isRunning()) animTimer.start(); }
                public void mouseExited(MouseEvent e) { targetScale = 1.0f; if (!animTimer.isRunning()) animTimer.start(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight(); int centerX = w / 2; int centerY = h / 2;
            g2.translate(centerX, centerY); g2.scale(scale, scale); g2.translate(-centerX, -centerY);
            int btnWidth = 280; int btnHeight = 55; int x = (w - btnWidth) / 2; int y = (h - btnHeight) / 2;
            g2.setColor(new Color(0,0,0,30)); g2.fillRoundRect(x+3, y+5, btnWidth, btnHeight, 40, 40);
            g2.setColor(baseColor); g2.fillRoundRect(x, y, btnWidth, btnHeight, 40, 40);
            g2.setColor(new Color(255,255,255,50)); g2.fillRoundRect(x+5, y+5, btnWidth-10, btnHeight/2, 30, 30);
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
            int textX = x + (btnWidth - stringBounds.width) / 2; int textY = y + (btnHeight - stringBounds.height) / 2 + fm.getAscent() - 2;
            g2.drawString(getText(), textX, textY);
            g2.dispose();
        }
    }
    
    class BadgePanel extends JPanel {
        private Color bgColor;
        public BadgePanel(Color color) {
            super(new FlowLayout(FlowLayout.CENTER, 0, 0));
            this.bgColor = color;
            setOpaque(false);
            setBorder(new EmptyBorder(8, 20, 8, 20)); 
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
        }
    }
}