package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.repository.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class MainMenuScreen extends JPanel {

    private Image bgImage;
    private final UserRepository userRepo;
    
    // Referensi tombol agar bisa di-resize
    private ImageButton btnContinue;
    private ImageButton btnNewGame;
    private ImageButton btnLeaderboard;
    private ImageButton btnSettings;
    private ImageButton btnExit;
    private JPanel centerMenuPanel; 

    // Variabel Skala
    private final float BASE_W = 1920f;
    private final float BASE_H = 1080f;
    private float scaleFactor = 1.0f;

    public MainMenuScreen() {
        this.userRepo = new UserRepository();
        setLayout(new GridBagLayout()); 
        loadAssets();
        initUI();
        
        // Listener Resize
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateScaleFactor();
                updateResponsiveLayout();
            }
        });
    }
    
    // --- HITUNG SKALA ---
    private void calculateScaleFactor() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        float sW = (float) getWidth() / BASE_W;
        float sH = (float) getHeight() / BASE_H;
        this.scaleFactor = Math.min(sW, sH);
        
        if (this.scaleFactor < 0.5f) this.scaleFactor = 0.5f;
    }

    // --- [UPDATE] UKURAN TOMBOL JUMBO DI SINI ---
    private void updateResponsiveLayout() {
        if (centerMenuPanel == null) return;
        
        GridBagLayout layout = (GridBagLayout) centerMenuPanel.getLayout();

        // 1. TOMBOL CONTINUE (Diperbesar dari 400x220 -> 520x280)
        if (btnContinue != null) {
            btnContinue.setButtonSize((int)(600 * scaleFactor), (int)(320 * scaleFactor));
            GridBagConstraints gbc = layout.getConstraints(btnContinue);
            // Insets disesuaikan agar tumpukan pas
            gbc.insets = new Insets((int)(100 * scaleFactor), 0, (int)(-95 * scaleFactor), 0);
            layout.setConstraints(btnContinue, gbc);
        }

        // 2. TOMBOL NEW GAME (Diperbesar dari 400x200 -> 520x260)
        if (btnNewGame != null) {
            btnNewGame.setButtonSize((int)(600 * scaleFactor), (int)(300 * scaleFactor));
            GridBagConstraints gbc = layout.getConstraints(btnNewGame);
            gbc.insets = new Insets((int)(-20 * scaleFactor), 0, (int)(-60 * scaleFactor), 0);
            layout.setConstraints(btnNewGame, gbc);
        }

        // 3. TOMBOL LEADERBOARD (Diperbesar dari 380x190 -> 480x240)
        if (btnLeaderboard != null) {
            btnLeaderboard.setButtonSize((int)(600 * scaleFactor), (int)(260 * scaleFactor));
            GridBagConstraints gbc = layout.getConstraints(btnLeaderboard);
            gbc.insets = new Insets((int)(-20 * scaleFactor), 0, (int)(-70 * scaleFactor), 0);
            layout.setConstraints(btnLeaderboard, gbc);
        }

        // 4. TOMBOL SETTINGS (Diperbesar dari 400x250 -> 520x320)
        if (btnSettings != null) {
            btnSettings.setButtonSize((int)(600 * scaleFactor), (int)(340 * scaleFactor));
            GridBagConstraints gbc = layout.getConstraints(btnSettings);
            gbc.insets = new Insets((int)(-70 * scaleFactor), 0, (int)(-40 * scaleFactor), 0);
            layout.setConstraints(btnSettings, gbc);
        }

        // 5. TOMBOL EXIT (Diperbesar dari 380x190 -> 480x240)
        if (btnExit != null) {
            btnExit.setButtonSize((int)(550 * scaleFactor), (int)(280 * scaleFactor));
            GridBagConstraints gbc = layout.getConstraints(btnExit);
            gbc.insets = new Insets((int)(-40 * scaleFactor), 0, 0, 0);
            layout.setConstraints(btnExit, gbc);
        }
        
        centerMenuPanel.revalidate();
        centerMenuPanel.repaint();
    }
    
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            try {
                SoundPlayer.getInstance().playBGM("bgm_menu.wav");
            } catch (Exception e) {}
            
            updateContinueButtonState();
            
            SwingUtilities.invokeLater(() -> {
                calculateScaleFactor();
                updateResponsiveLayout();
            });
        }
    }

    private void updateContinueButtonState() {
        if (btnContinue == null) return;

        boolean hasUser = !userRepo.getAllActiveUsers().isEmpty();
        
        String imgName = hasUser ? "btn_continue.png" : "btn_not_continue.png";
        btnContinue.setImage(imgName); 

        if (hasUser) {
            btnContinue.setAnimationEnabled(true);
            btnContinue.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            for (var l : btnContinue.getActionListeners()) btnContinue.removeActionListener(l);
            
            btnContinue.addActionListener(e -> {
                playSound("click");
                ScreenManager.getInstance().showScreen("PROFILE_SELECT");
            });
        } else {
            btnContinue.setAnimationEnabled(false);
            btnContinue.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            
            for (var l : btnContinue.getActionListeners()) btnContinue.removeActionListener(l);
        }
        
        btnContinue.repaint();
    }

    private void loadAssets() {
        try {
            URL bgUrl = getClass().getResource("/images/bg_menu.png");
            if (bgUrl != null) bgImage = new ImageIcon(bgUrl).getImage();
        } catch (Exception e) {}
    }

    private void initUI() {
        centerMenuPanel = new JPanel(new GridBagLayout());
        centerMenuPanel.setOpaque(false);

        GridBagConstraints gbcMenu = new GridBagConstraints();
        gbcMenu.gridx = 0;
        gbcMenu.anchor = GridBagConstraints.CENTER;

        // --- TOMBOL 0: LANJUTKAN (Set ukuran JUMBO awal) ---
        btnContinue = new ImageButton("btn_not_continue.png");
        btnContinue.setButtonSize(520, 280); 
        
        gbcMenu.gridy = 0;
        gbcMenu.insets = new Insets(100, 0, -70, 0); 
        centerMenuPanel.add(btnContinue, gbcMenu);

        // --- TOMBOL 1: MULAI BARU ---
        btnNewGame = new ImageButton("btn_start.png");
        btnNewGame.setButtonSize(520, 260); 
        btnNewGame.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("PROFILE_CREATE");
        });

        gbcMenu.gridy = 1;
        gbcMenu.insets = new Insets(-20, 0, -60, 0); 
        centerMenuPanel.add(btnNewGame, gbcMenu);

        // --- TOMBOL 2: LEADERBOARD ---
        btnLeaderboard = new ImageButton("btn_leaderboard.png");
        btnLeaderboard.setButtonSize(480, 240);
        btnLeaderboard.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("LEADERBOARD");
        });

        gbcMenu.gridy = 2;
        gbcMenu.insets = new Insets(-20, 0, -70, 0); 
        centerMenuPanel.add(btnLeaderboard, gbcMenu);

        // --- TOMBOL 3: SETTINGS ---
        btnSettings = new ImageButton("btn_settings.png");
        btnSettings.setButtonSize(520, 320);
        btnSettings.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("SETTINGS");
        });

        gbcMenu.gridy = 3;
        gbcMenu.insets = new Insets(-70, 0, -40, 0); 
        centerMenuPanel.add(btnSettings, gbcMenu);

        // --- TOMBOL 4: KELUAR ---
        btnExit = new ImageButton("btn_exit.png");
        btnExit.setButtonSize(480, 240);
        btnExit.addActionListener(e -> {
            playSound("click");
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Yakin mau istirahat?", "Keluar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        gbcMenu.gridy = 4;
        gbcMenu.insets = new Insets(-40, 0, 0, 0); 
        centerMenuPanel.add(btnExit, gbcMenu);

        // Tambahkan ke Layar Utama
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0; gbcMain.gridy = 0;
        gbcMain.weightx = 1.0; gbcMain.weighty = 1.0;
        gbcMain.anchor = GridBagConstraints.CENTER;
        add(centerMenuPanel, gbcMain);
        
        updateContinueButtonState();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, new Color(135, 206, 235), 0, getHeight(), new Color(34, 139, 34));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void playSound(String name) {
        try {
            SoundPlayer.getInstance().playSFX(name + ".wav");
        } catch (Exception e) {}
    }

    // ============================================================
    // CLASS: IMAGE BUTTON
    // ============================================================
    class ImageButton extends JButton {
        private Image img;
        private float scale = 1.0f;
        private float targetScale = 1.0f;
        private Timer animTimer;
        private boolean animationEnabled = true;

        public ImageButton(String filename) {
            loadImage(filename); 
            
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setAlignmentX(Component.CENTER_ALIGNMENT);

            animTimer = new Timer(16, e -> updateAnimation());
            animTimer.start();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (animationEnabled) targetScale = 1.1f;
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (animationEnabled) targetScale = 1.0f;
                }
            });
        }
        
        public void setImage(String filename) {
            loadImage(filename);
            repaint();
        }
        
        private void loadImage(String filename) {
            try {
                URL url = getClass().getResource("/images/" + filename);
                if (url != null) {
                    img = new ImageIcon(url).getImage();
                    setText(""); 
                } else {
                    setText(filename);
                    setForeground(Color.RED);
                    setFont(new Font("Arial", Font.BOLD, 24));
                }
            } catch (Exception e) {}
        }
        
        public void setButtonSize(int width, int height) {
            setPreferredSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
            setMaximumSize(new Dimension(width, height));
        }
        
        public void setAnimationEnabled(boolean enabled) {
            this.animationEnabled = enabled;
            if (!enabled) {
                targetScale = 1.0f; 
            }
        }

        private void updateAnimation() {
            if (Math.abs(scale - targetScale) > 0.001f) {
                scale += (targetScale - scale) * 0.2f; 
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (img != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int baseW = getWidth();
                int baseH = getHeight();
                
                int drawW = (int) (baseW * scale);
                int drawH = (int) (baseH * scale);
                int x = (baseW - drawW) / 2;
                int y = (baseH - drawH) / 2;

                g2.drawImage(img, x, y, drawW, drawH, this);
                g2.dispose();
            } else {
                super.paintComponent(g);
            }
        }
    }
}