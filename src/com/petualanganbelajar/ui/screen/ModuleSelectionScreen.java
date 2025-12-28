package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.ModuleRepository;
import com.petualanganbelajar.repository.ProgressRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class ModuleSelectionScreen extends JPanel {

    // Data
    private List<ModuleModel> modules;
    private int currentIndex = 0;
    
    // Background Image
    private Image currentBgImage;

    // Komponen UI
    private JLabel lblUserInfo;
    private JLabel lblTotalScore;
    
    // Komponen Carousel
    private RoundedPanel cardPanel; 
    private JLabel lblModuleName;
    private JTextArea txtModuleDesc;
    private JButton btnPlayModule;
    private JLabel lblIndexIndicator;
    private JPanel centerContainer;
    private JPanel footer;
    
    // Tombol Navigasi
    private JButton btnPrev;
    private JButton btnNext;

    public ModuleSelectionScreen() {
        setLayout(new BorderLayout());
        
        // ==========================================
        // 1. HEADER (User Info & Score)
        // ==========================================
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(GameConfig.COLOR_ACCENT);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        lblUserInfo = new JLabel("Player: -");
        lblUserInfo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        lblUserInfo.setForeground(Color.WHITE);
        topBar.add(lblUserInfo, BorderLayout.WEST);

        lblTotalScore = new JLabel("🏆 Skor: 0");
        lblTotalScore.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        lblTotalScore.setForeground(Color.YELLOW);
        topBar.add(lblTotalScore, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // ==========================================
        // 2. CENTER (CAROUSEL) - FIXED WITH GRIDBAGLAYOUT
        // ==========================================
        // Kita gunakan GridBagLayout agar Tombol Kiri, Kartu, dan Tombol Kanan
        // berada di SATU BARIS (Row 0) yang sama, sehingga pasti sejajar tengah (Center).
        centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false); 
        centerContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();

        // A. TOMBOL KIRI (PREV)
        btnPrev = createAnimatedImageButton("btn_prev.png", "<"); 
        btnPrev.addActionListener(e -> navigate(-1));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;                // ⬅️ PENTING: span 2 baris
        gbc.weightx = 0.15;
        gbc.weighty = 1.0;                 // ⬅️ agar benar-benar center vertikal
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        centerContainer.add(btnPrev, gbc);


        // B. KARTU MODUL (TENGAH)
        cardPanel = new RoundedPanel(40); 
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE); 
        cardPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Isi Kartu
        lblModuleName = new JLabel("MODUL", SwingConstants.CENTER);
        lblModuleName.setFont(new Font("Comic Sans MS", Font.BOLD, 55));
        lblModuleName.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtModuleDesc = new JTextArea("Deskripsi modul...");
        txtModuleDesc.setFont(new Font("Segoe UI", Font.PLAIN, 22)); 
        txtModuleDesc.setForeground(Color.DARK_GRAY); 
        txtModuleDesc.setWrapStyleWord(true);
        txtModuleDesc.setLineWrap(true);
        txtModuleDesc.setOpaque(false);
        txtModuleDesc.setEditable(false);
        txtModuleDesc.setFocusable(false);
        txtModuleDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtModuleDesc.setMaximumSize(new Dimension(500, 80));
        
        btnPlayModule = new JButton("MULAI BELAJAR");
        btnPlayModule.setFont(new Font("Arial", Font.BOLD, 26));
        btnPlayModule.setBackground(new Color(50, 205, 50));
        btnPlayModule.setForeground(Color.WHITE);
        btnPlayModule.setFocusPainted(false);
        btnPlayModule.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPlayModule.setPreferredSize(new Dimension(320, 80));
        btnPlayModule.setMaximumSize(new Dimension(320, 80));
        btnPlayModule.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnPlayModule.addActionListener(e -> {
            if (modules != null && !modules.isEmpty()) {
                ScreenManager.getInstance().showLevelSelect(modules.get(currentIndex));
            }
        });

        // Susun Layout Kartu
        cardPanel.add(Box.createVerticalGlue());
        cardPanel.add(lblModuleName);
        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(txtModuleDesc);
        cardPanel.add(Box.createVerticalStrut(30));
        cardPanel.add(btnPlayModule);
        cardPanel.add(Box.createVerticalGlue());

        gbc.gridx = 1;      // Kolom 1 (Tengah)
        gbc.gridy = 0;      // Baris 0 (Sejajar dengan tombol)
        gbc.weightx = 0.7;  // Ambil ruang paling banyak
        gbc.anchor = GridBagConstraints.CENTER;
        centerContainer.add(cardPanel, gbc);

        // C. TOMBOL KANAN (NEXT)
        btnNext = createAnimatedImageButton("btn_next.png", ">");
        btnNext.addActionListener(e -> navigate(1));

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;                // ⬅️ PENTING: span 2 baris
        gbc.weightx = 0.15;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        centerContainer.add(btnNext, gbc);


        // D. INDIKATOR HALAMAN (DI BAWAH KARTU)
        lblIndexIndicator = new JLabel(" 1 / 4 ", SwingConstants.CENTER);
        lblIndexIndicator.setFont(new Font("Arial", Font.BOLD, 20));
        lblIndexIndicator.setForeground(Color.WHITE); 
        lblIndexIndicator.setBackground(new Color(0,0,0,150));
        lblIndexIndicator.setOpaque(true);
        lblIndexIndicator.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        JPanel indicatorPanel = new JPanel();
        indicatorPanel.setOpaque(false);
        indicatorPanel.add(lblIndexIndicator);

        gbc.gridx = 1;       // Kolom 1 (Di bawah kartu)
        gbc.gridy = 1;       // Baris 1
        gbc.weightx = 0;
        gbc.insets = new Insets(20, 0, 0, 0); // Jarak 20px dari kartu
        gbc.anchor = GridBagConstraints.NORTH; // Nempel ke atas (mendekati kartu)
        centerContainer.add(indicatorPanel, gbc);

        add(centerContainer, BorderLayout.CENTER);

        // ==========================================
        // 3. FOOTER (Ganti Profil / Keluar)
        // ==========================================
        
        FunnyButton btnBack = new FunnyButton("Ganti Profil / Keluar", new Color(255, 85, 85)); 
        
        btnBack.addActionListener(e -> ScreenManager.getInstance().showScreen("MAIN_MENU"));
        
        footer = new JPanel();
        footer.setOpaque(false); 
        footer.setBorder(new EmptyBorder(0,0,20,0)); 
        footer.add(btnBack);
        add(footer, BorderLayout.SOUTH);
    }

    // --- PAINT COMPONENT (BACKGROUND) ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentBgImage != null) {
            g.drawImage(currentBgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(GameConfig.COLOR_BG);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // --- LOGIC ---
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            refreshUserInfo();
            loadModules();
        }
    }

    private void loadModules() {
        ModuleRepository repo = new ModuleRepository();
        this.modules = repo.getAllModules();
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
        
        String colorHex = getHexColor(mod.getId());
        String htmlTitle = String.format(
            "<html><center><span style='color:%s; font-size:45px; font-family:Comic Sans MS;'>%s</span></center></html>", 
            colorHex, mod.getName()
        );
        lblModuleName.setText(htmlTitle);
        
        txtModuleDesc.setText(mod.getDescription());
        lblIndexIndicator.setText((currentIndex + 1) + " / " + modules.size());
        
        updateCardColor(mod.getId());
        loadBackgroundImage(mod.getId());
        repaint();
    }

    private String getHexColor(int modId) {
        switch (modId % 4) {
            case 1: return "#FFA000"; 
            case 2: return "#FF1493"; 
            case 3: return "#FF4500"; 
            case 0: return "#32CD32"; 
            default: return "#000000";
        }
    }
    
    private void updateCardColor(int modId) {
        Color color;
        switch (modId % 4) {
            case 1: color = new Color(255, 193, 7); break; 
            case 2: color = new Color(255, 105, 180); break;
            case 3: color = new Color(255, 69, 0); break; 
            case 0: color = new Color(50, 205, 50); break; 
            default: color = GameConfig.COLOR_PRIMARY;
        }
        cardPanel.setBorderColor(color);
    }
    
    private void loadBackgroundImage(int modId) {
        String filename = "bg_module_" + modId + ".png";
        String filePath = "resources/images/" + filename; 
        try {
            File imgFile = new File(filePath);
            if (imgFile.exists()) {
                currentBgImage = new ImageIcon(filePath).getImage();
            } else {
                currentBgImage = null; 
            }
        } catch (Exception e) {
            currentBgImage = null;
        }
    }
    
    private void refreshUserInfo() {
        UserModel u = GameState.getCurrentUser();
        if (u != null) {
            String avatarVisual = "👤";
            if ("avatar_1.png".equals(u.getAvatar())) avatarVisual = "🦊";
            if ("avatar_2.png".equals(u.getAvatar())) avatarVisual = "🐯";
            if ("avatar_3.png".equals(u.getAvatar())) avatarVisual = "🦁";

            lblUserInfo.setText(avatarVisual + " " + u.getName());
            ProgressRepository progressRepo = new ProgressRepository();
            int totalScore = progressRepo.calculateTotalScore(u.getName());
            lblTotalScore.setText("🏆 Total Skor: " + totalScore);
        }
    }

    private JButton createAnimatedImageButton(String filename, String fallbackText) {
        JButton btn = new JButton();
        String path = "resources/images/" + filename;
        File file = new File(path);
        
        int normalSize = 130;
        
        ImageIcon normalIcon = null;
        ImageIcon hoverIcon = null;

        if (file.exists()) {
            try {
                ImageIcon raw = new ImageIcon(path);
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
            @Override
            public void mouseEntered(MouseEvent e) {
                if (iconHoverFinal != null) {
                    btn.setIcon(iconHoverFinal);
                } else {
                    btn.setFont(new Font("SansSerif", Font.BOLD, 70));
                    btn.setForeground(Color.RED);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (iconNormalFinal != null) {
                    btn.setIcon(iconNormalFinal);
                } else {
                    btn.setFont(new Font("SansSerif", Font.BOLD, 60));
                    btn.setForeground(Color.ORANGE);
                }
            }
        });
        
        return btn;
    }

    // =========================================================
    // CUSTOM ROUNDED PANEL
    // =========================================================
    class RoundedPanel extends JPanel {
        private Color borderColor = GameConfig.COLOR_PRIMARY;
        private int cornerRadius = 30;

        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false); 
        }
        
        public void setBorderColor(Color c) {
            this.borderColor = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);

            graphics.setColor(borderColor);
            graphics.setStroke(new BasicStroke(5)); 
            graphics.drawRoundRect(2, 2, width - 5, height - 5, arcs.width, arcs.height);
        }
    }

    // =========================================================
    // CUSTOM FUNNY BUTTON (Fixed: Anti-Clipping & Manual Arrow)
    // =========================================================
    class FunnyButton extends JButton {
        private Color baseColor;
        private Timer animTimer;
        private float scale = 1.0f;     
        private float targetScale = 1.0f; 
        
        public FunnyButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            
            // Setup dasar
            setFont(new Font("Comic Sans MS", Font.BOLD, 22)); 
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Ukuran Canvas (Lebih Besar) agar tidak terpotong saat zoom
            setPreferredSize(new Dimension(380, 100));

            animTimer = new Timer(16, e -> {
                if (Math.abs(targetScale - scale) > 0.01f) {
                    scale += (targetScale - scale) * 0.2f; 
                    repaint();
                } else {
                    scale = targetScale;
                    ((Timer)e.getSource()).stop();
                    repaint();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    targetScale = 1.1f; 
                    if (!animTimer.isRunning()) animTimer.start();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    targetScale = 1.0f; 
                    if (!animTimer.isRunning()) animTimer.start();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int centerX = w / 2;
            int centerY = h / 2;

            // Efek Zoom
            g2.translate(centerX, centerY);
            g2.scale(scale, scale);
            g2.translate(-centerX, -centerY);

            // Ukuran Gambar Tombol
            int btnWidth = 320; 
            int btnHeight = 60; 
            
            int x = (w - btnWidth) / 2;
            int y = (h - btnHeight) / 2;

            // 1. Shadow (Bayangan Bawah)
            g2.setColor(baseColor.darker().darker());
            g2.fillRoundRect(x, y + 8, btnWidth, btnHeight, 50, 50);

            // 2. Body Utama
            GradientPaint gp = new GradientPaint(x, y, baseColor.brighter(), x, y + btnHeight, baseColor);
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, btnWidth, btnHeight, 50, 50);

            // 3. Highlight
            g2.setColor(new Color(255, 255, 255, 80));
            g2.fillRoundRect(x + 10, y + 5, btnWidth - 20, btnHeight / 2 - 5, 40, 40);

            // 4. Border Putih
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(x, y, btnWidth, btnHeight, 50, 50);

            // 5. Gambar Ikon Panah (Segitiga)
            g2.setColor(Color.WHITE);
            int arrowSize = 8;
            int arrowX = x + 35; 
            int arrowY = centerY; 
            
            Polygon arrow = new Polygon();
            arrow.addPoint(arrowX + arrowSize, arrowY - arrowSize); 
            arrow.addPoint(arrowX, arrowY);                         
            arrow.addPoint(arrowX + arrowSize, arrowY + arrowSize); 
            
            g2.fillPolygon(arrow);
            
            // 6. Gambar Teks
            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
            
            // Text Centering
            int textX = x + (btnWidth - stringBounds.width) / 2 + 10; 
            int textY = y + (btnHeight - stringBounds.height) / 2 + fm.getAscent() - 2;

            // Shadow Teks
            g2.setColor(new Color(0, 0, 0, 50));
            g2.drawString(getText(), textX + 1, textY + 2);

            // Teks Putih
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }
}