package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.content.StoryDataManager;
import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.ProgressRepository;
import com.petualanganbelajar.repository.StoryRepository;
import com.petualanganbelajar.ui.component.StoryDialogPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ModuleSelectionScreen extends JPanel {

    // --- DATA & REPO ---
    private List<ModuleModel> modules;
    private int currentIndex = 0;
    private Image currentBgImage; 
    private boolean isPrologueMode = false; 
    private Image prologueBgImage; 

    private SoundPlayer soundPlayer = SoundPlayer.getInstance();
    private ProgressRepository progressRepo = new ProgressRepository();
    private StoryRepository storyRepo = new StoryRepository(); 

    // --- LAYERS ---
    private JLayeredPane layeredPane;
    private JPanel contentPanel;
    private StoryDialogPanel storyPanel;

    // --- UI COMPONENTS ---
    private JPanel topBar;
    private BadgePanel profileBadge, scoreBadge;
    private JLabel lblUserInfo;
    private JLabel lblTotalScore;
    private PaperCardPanel cardPanel;
    private JLabel lblModuleName;
    private JTextArea txtModuleDesc;
    private JLabel lblPilih;
    private JPanel levelButtonPanel;
    private JPanel centerContainer;
    private JPanel footer;
    private FunnyButton btnBack;
    
    // [GANTI] Menggunakan Class Khusus agar bisa animasi scale
    private AnimatedNavButton btnPrev;
    private AnimatedNavButton btnNext;

    // --- RESPONSIVE VARS ---
    private final float BASE_W = 1920f;
    private final float BASE_H = 1080f;
    private float scaleFactor = 1.0f;
    private float lastScaleFactor = 0.0f;

    public ModuleSelectionScreen() {
        setLayout(new BorderLayout());
        loadPrologueBackground(); 
        initLayeredUI(); 
    }
    
    // --- LOGIC RESPONSIVE ---
    private void calculateScaleFactor() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        float sW = (float) getWidth() / BASE_W;
        float sH = (float) getHeight() / BASE_H;
        this.scaleFactor = Math.min(sW, sH);
        if (this.scaleFactor < 0.5f) this.scaleFactor = 0.5f;
    }

    private void updateResponsiveLayout() {
        if (contentPanel == null) return;

        // 1. Update Header
        int badgePadH = (int)(20 * scaleFactor);
        int badgePadV = (int)(8 * scaleFactor);
        
        topBar.setBorder(BorderFactory.createEmptyBorder((int)(15*scaleFactor), (int)(20*scaleFactor), (int)(15*scaleFactor), (int)(20*scaleFactor)));
        profileBadge.setBorder(new EmptyBorder(badgePadV, badgePadH, badgePadV, badgePadH));
        scoreBadge.setBorder(new EmptyBorder(badgePadV, badgePadH, badgePadV, badgePadH));
        
        lblUserInfo.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(24 * scaleFactor))); 
        lblTotalScore.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(24 * scaleFactor)));

        // 2. Update Card Panel (Tengah)
        int cardPadH = (int)(50 * scaleFactor); 
        int cardPadV = (int)(40 * scaleFactor); 
        cardPanel.setBorder(new EmptyBorder(cardPadV, cardPadH, cardPadV, cardPadH));
        
        lblModuleName.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(64 * scaleFactor)));
        
        txtModuleDesc.setFont(new Font("Comic Sans MS", Font.PLAIN, (int)(24 * scaleFactor)));
        txtModuleDesc.setMaximumSize(new Dimension((int)(700 * scaleFactor), (int)(120 * scaleFactor))); 

        lblPilih.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(20 * scaleFactor)));
        
        // 3. Update Level Buttons 
        Component[] comps = levelButtonPanel.getComponents();
        for (Component c : comps) {
            if (c instanceof LevelButton) {
                ((LevelButton)c).updateScale(scaleFactor);
            }
        }
        
        // 4. Update Navigation Buttons
        if (btnPrev != null) btnPrev.updateScale(scaleFactor);
        if (btnNext != null) btnNext.updateScale(scaleFactor);
        
        // 5. Update Footer Button
        btnBack.updateScale(scaleFactor);
        footer.setBorder(new EmptyBorder(0,0, (int)(30*scaleFactor), 0));

        // [FIX] Force Re-Layout for all containers
        topBar.revalidate(); topBar.repaint();
        levelButtonPanel.revalidate(); levelButtonPanel.repaint();
        cardPanel.revalidate(); cardPanel.repaint();
        centerContainer.revalidate(); centerContainer.repaint();
        footer.revalidate(); footer.repaint();
        contentPanel.revalidate(); contentPanel.repaint();
    }
    
    private void loadPrologueBackground() {
        try {
            URL url = getClass().getResource("/images/bg_prologue.png");
            if (url != null) prologueBgImage = new ImageIcon(url).getImage();
        } catch (Exception e) { prologueBgImage = null; }
    }

    private void initLayeredUI() {
        layeredPane = new JLayeredPane();
        layeredPane.setOpaque(false);
        add(layeredPane, BorderLayout.CENTER);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        setupOldContentUI(); 
        layeredPane.add(contentPanel, JLayeredPane.DEFAULT_LAYER);

        storyPanel = new StoryDialogPanel();
        storyPanel.setVisible(false);
        layeredPane.add(storyPanel, JLayeredPane.PALETTE_LAYER);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth(); int h = getHeight();
                layeredPane.setBounds(0, 0, w, h);
                contentPanel.setBounds(0, 0, w, h);
                storyPanel.setBounds(0, 0, w, h);
                
                calculateScaleFactor();
                updateResponsiveLayout();
            }
        });
    }

    private void setupOldContentUI() {
        // HEADER
        topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false); 

        profileBadge = new BadgePanel(new Color(101, 67, 33)); 
        lblUserInfo = new JLabel("Player: -");
        lblUserInfo.setForeground(Color.WHITE);
        profileBadge.add(lblUserInfo);
        topBar.add(profileBadge, BorderLayout.WEST);

        scoreBadge = new BadgePanel(new Color(255, 193, 7)); 
        lblTotalScore = new JLabel("Total Skor: 0");
        lblTotalScore.setForeground(new Color(62, 39, 35)); 
        scoreBadge.add(lblTotalScore);
        topBar.add(scoreBadge, BorderLayout.EAST);

        contentPanel.add(topBar, BorderLayout.NORTH); 

        // CENTER
        centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false); 
        centerContainer.setBorder(new EmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();

        // Prev Button
        btnPrev = new AnimatedNavButton("btn_prev.png", "<"); 
        btnPrev.addActionListener(e -> {
            playSound("click");
            navigate(-1);
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.weightx = 0.1; 
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        centerContainer.add(btnPrev, gbc);

        // Card Panel
        cardPanel = new PaperCardPanel(); 
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        
        lblModuleName = new JLabel("MODUL", SwingConstants.CENTER);
        lblModuleName.setForeground(new Color(93, 64, 55)); 
        lblModuleName.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtModuleDesc = new JTextArea("Deskripsi modul...");
        txtModuleDesc.setForeground(new Color(117, 117, 117)); 
        txtModuleDesc.setWrapStyleWord(true);
        txtModuleDesc.setLineWrap(true);
        txtModuleDesc.setOpaque(false);
        txtModuleDesc.setEditable(false);
        txtModuleDesc.setFocusable(false);
        txtModuleDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        levelButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); 
        levelButtonPanel.setOpaque(false);

        cardPanel.add(Box.createVerticalGlue());
        cardPanel.add(lblModuleName);
        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(txtModuleDesc);
        cardPanel.add(Box.createVerticalStrut(40)); 
        
        lblPilih = new JLabel("Pilih Level");
        lblPilih.setForeground(new Color(141, 110, 99)); 
        lblPilih.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblPilih);
        cardPanel.add(Box.createVerticalStrut(15));
        
        cardPanel.add(levelButtonPanel); 
        cardPanel.add(Box.createVerticalGlue());

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8; 
        centerContainer.add(cardPanel, gbc);

        // Next Button
        btnNext = new AnimatedNavButton("btn_next.png", ">");
        btnNext.addActionListener(e -> {
            playSound("click");
            navigate(1);
        });

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        centerContainer.add(btnNext, gbc);

        contentPanel.add(centerContainer, BorderLayout.CENTER);

        // FOOTER
        btnBack = new FunnyButton("Kembali ke Menu", new Color(239, 83, 80)); 
        btnBack.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("MAIN_MENU");
        });
        
        footer = new JPanel();
        footer.setOpaque(false); 
        footer.add(btnBack);
        contentPanel.add(footer, BorderLayout.SOUTH);
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            refreshUserInfo();
            loadModules();
            checkAndPlayPrologue(); 
            SwingUtilities.invokeLater(() -> {
                calculateScaleFactor();
                updateResponsiveLayout();
                updateCarousel(); 
            });
        }
    }

    private void checkAndPlayPrologue() {
        UserModel user = GameState.getCurrentUser();
        if (user == null) return;
        if (!storyRepo.hasSeenStory(user.getId(), 0, 0, "PROLOGUE")) {
            isPrologueMode = true; 
            contentPanel.setVisible(false); 
            repaint(); 
            storyPanel.startStory(StoryDataManager.getPrologueStory(), () -> {
                storyRepo.markStoryAsSeen(user.getId(), 0, 0, "PROLOGUE");
                isPrologueMode = false; 
                contentPanel.setVisible(true); 
                repaint(); 
            });
        } else {
            contentPanel.setVisible(true);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Hapus logika resize di sini karena sudah ada di componentListener
        super.paintComponent(g);
        
        if (isPrologueMode && prologueBgImage != null) {
            g.drawImage(prologueBgImage, 0, 0, getWidth(), getHeight(), this);
        } else if (currentBgImage != null) {
            g.drawImage(currentBgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(GameConfig.COLOR_BG);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        g.setColor(new Color(0, 0, 0, 30));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void loadModules() {
        this.modules = new ArrayList<>();
        modules.add(new ModuleModel(1, "ANGKA", "Bantu Bobo menghitung makanan yang lezat!"));
        modules.add(new ModuleModel(2, "HURUF", "Cici butuh bantuan menulis nama tamu di undangan pesta."));
        modules.add(new ModuleModel(3, "WARNA", "Ayo hias panggung pesta Moli dengan warna-warni!"));
        modules.add(new ModuleModel(4, "BENTUK", "Bantu Tobi memperbaiki panggung dengan bentuk yang pas."));
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
            btnLvl.updateScale(scaleFactor); // Apply current scale
            
            btnLvl.addActionListener(e -> {
                if (isUnlocked) {
                    playSound("click");
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
            if (url != null) currentBgImage = new ImageIcon(url).getImage();
            else currentBgImage = null; 
        } catch (Exception e) { currentBgImage = null; }
    }
    
    private void refreshUserInfo() {
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

    // --- INNER CLASSES (CUSTOM COMPONENTS) ---
    
    class AnimatedNavButton extends JButton {
        private Image image;
        private Timer animTimer;
        private float animScale = 1.0f; 
        private float targetAnimScale = 1.0f;
        private int baseSize = 160;

        public AnimatedNavButton(String filename, String fallbackText) {
            URL url = getClass().getResource("/images/" + filename);
            if (url != null) {
                image = new ImageIcon(url).getImage();
            } else {
                setText(fallbackText);
                setForeground(Color.ORANGE);
            }
            
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            animTimer = new Timer(16, e -> {
                if (Math.abs(targetAnimScale - animScale) > 0.01f) {
                    animScale += (targetAnimScale - animScale) * 0.2f;
                    repaint();
                } else {
                    animScale = targetAnimScale;
                    ((Timer)e.getSource()).stop();
                    repaint();
                }
            });

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { targetAnimScale = 1.15f; if (!animTimer.isRunning()) animTimer.start(); }
                public void mouseExited(MouseEvent e) { targetAnimScale = 1.0f; if (!animTimer.isRunning()) animTimer.start(); }
            });
        }
        
        public void updateScale(float s) {
            int size = (int)(baseSize * s);
            if (image == null) setFont(new Font("SansSerif", Font.BOLD, (int)(80 * s)));
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size)); // Safety
            revalidate();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (image != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(); int h = getHeight();
                float finalScale = animScale;
                int imgW = (int)(w * finalScale); int imgH = (int)(h * finalScale);
                int x = (w - imgW) / 2; int y = (h - imgH) / 2;

                g2.drawImage(image, x, y, imgW, imgH, this);
                g2.dispose();
            } else {
                super.paintComponent(g);
            }
        }
    }

    class LevelButton extends JButton {
        private int level; private boolean isUnlocked; private Color themeColor; private boolean hover; private boolean pressed;
        
        public LevelButton(int level, boolean unlocked, Color themeColor) {
            this.level = level; this.isUnlocked = unlocked; this.themeColor = themeColor;
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setCursor(isUnlocked ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if(isUnlocked) { hover = true; repaint(); } }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                public void mousePressed(MouseEvent e) { if(isUnlocked) { pressed = true; repaint(); } }
                public void mouseReleased(MouseEvent e) { if(isUnlocked) { pressed = false; repaint(); } }
            });
        }
        
        public void updateScale(float s) {
            int size = (int)(110 * s);
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size)); // Safety
            revalidate();
        }
        
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight(); int offset = pressed ? 2 : 0; 
            int arc = (int)(25 * scaleFactor);
            
            if (isUnlocked) {
                Color base = themeColor; Color dark = themeColor.darker(); if (hover) base = themeColor.brighter();
                g2.setColor(new Color(0,0,0,30)); g2.fillRoundRect(5, 8, w-10, h-10, arc, arc); 
                g2.setColor(dark); g2.fillRoundRect(5, 5+offset, w-10, h-10-offset, arc, arc); 
                g2.setColor(base); g2.fillRoundRect(5, 0+offset, w-10, h-10, arc, arc); 
                g2.setColor(Color.WHITE); g2.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(48 * scaleFactor))); 
                FontMetrics fm = g2.getFontMetrics(); String text = String.valueOf(level);
                g2.drawString(text, (w - fm.stringWidth(text))/2, (h - 10 - fm.getHeight())/2 + fm.getAscent() + offset);
            } else {
                g2.setColor(new Color(224, 224, 224)); g2.fillRoundRect(5, 0, w-10, h-10, arc, arc);
                g2.setColor(new Color(189, 189, 189)); g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, (int)(32 * scaleFactor)));
                FontMetrics fm = g2.getFontMetrics(); String text = "🔒";
                g2.drawString(text, (w - fm.stringWidth(text))/2, (h - 10 - fm.getHeight())/2 + fm.getAscent());
            }
            g2.dispose();
        }
    }

    class PaperCardPanel extends JPanel {
        public PaperCardPanel() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight(); int arc = (int)(30 * scaleFactor); 
            g2.setColor(new Color(0,0,0,20)); g2.fillRoundRect(5, 5, w-10, h-10, arc, arc); 
            g2.setColor(new Color(255, 248, 225)); g2.fillRoundRect(0, 0, w-5, h-5, arc, arc); 
            g2.setColor(new Color(215, 204, 200)); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(0, 0, w-5, h-5, arc, arc); 
        }
    }

    class FunnyButton extends JButton {
        private Color baseColor; private Timer animTimer; private float scale = 1.0f; private float targetScale = 1.0f; 
        public FunnyButton(String text, Color color) {
            super(text); this.baseColor = color; setForeground(Color.WHITE);
            setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false); setCursor(new Cursor(Cursor.HAND_CURSOR));
            animTimer = new Timer(16, e -> {
                if (Math.abs(targetScale - scale) > 0.01f) { scale += (targetScale - scale) * 0.2f; repaint(); } 
                else { scale = targetScale; ((Timer)e.getSource()).stop(); repaint(); }
            });
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { targetScale = 1.05f; if (!animTimer.isRunning()) animTimer.start(); }
                public void mouseExited(MouseEvent e) { targetScale = 1.0f; if (!animTimer.isRunning()) animTimer.start(); }
            });
        }
        
        public void updateScale(float s) {
            int w = (int)(350 * s); 
            int h = (int)(80 * s);
            setPreferredSize(new Dimension(w, h));
            setMinimumSize(new Dimension(w, h));
            setFont(new Font("Comic Sans MS", Font.BOLD, (int)(26 * s)));
            revalidate();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight(); int centerX = w / 2; int centerY = h / 2;
            g2.translate(centerX, centerY); g2.scale(scale, scale); g2.translate(-centerX, -centerY);
            int btnWidth = w - 20; int btnHeight = h - 15; int x = (w - btnWidth) / 2; int y = (h - btnHeight) / 2; int arc = (int)(40 * scaleFactor);
            g2.setColor(new Color(0,0,0,30)); g2.fillRoundRect(x+3, y+5, btnWidth, btnHeight, arc, arc);
            g2.setColor(baseColor); g2.fillRoundRect(x, y, btnWidth, btnHeight, arc, arc);
            g2.setColor(new Color(255,255,255,50)); g2.fillRoundRect(x+5, y+5, btnWidth-10, btnHeight/2, (int)(30*scaleFactor), (int)(30*scaleFactor));
            g2.setColor(Color.WHITE); FontMetrics fm = g2.getFontMetrics(); Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
            int textX = x + (btnWidth - stringBounds.width) / 2; int textY = y + (btnHeight - stringBounds.height) / 2 + fm.getAscent() - 2;
            g2.drawString(getText(), textX, textY); g2.dispose();
        }
    }
    
    class BadgePanel extends JPanel {
        private Color bgColor;
        public BadgePanel(Color color) {
            super(new FlowLayout(FlowLayout.CENTER, 0, 0)); this.bgColor = color; setOpaque(false); 
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = (int)(30 * scaleFactor);
            g2.setColor(bgColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc); super.paintComponent(g);
        }
    }
}