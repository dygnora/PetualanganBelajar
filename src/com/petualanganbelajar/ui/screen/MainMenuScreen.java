package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.repository.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class MainMenuScreen extends JPanel {

    private Image bgImage;
    private final UserRepository userRepo;
    
    // Simpan referensi tombol lanjutkan agar bisa diupdate
    private ImageButton btnContinue;

    public MainMenuScreen() {
        this.userRepo = new UserRepository();
        setLayout(new GridBagLayout()); // Layout Utama
        loadAssets();
        initUI();
    }
    
    // --- UPDATE REALTIME SAAT LAYAR MUNCUL ---
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            // Mainkan BGM Menu saat layar ini muncul
            try {
                SoundPlayer.getInstance().playBGM("bgm_menu.wav");
            } catch (Exception e) {}
            
            updateContinueButtonState();
        }
    }

    private void updateContinueButtonState() {
        if (btnContinue == null) return;

        // Cek database terbaru
        boolean hasUser = !userRepo.getAllActiveUsers().isEmpty();
        
        // Update Gambar
        String imgName = hasUser ? "btn_continue.png" : "btn_not_continue.png";
        btnContinue.setImage(imgName); 

        // Update Logika
        if (hasUser) {
            btnContinue.setAnimationEnabled(true);
            btnContinue.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Hapus listener lama biar tidak dobel, lalu tambah baru
            for (var l : btnContinue.getActionListeners()) btnContinue.removeActionListener(l);
            
            btnContinue.addActionListener(e -> {
                playSound("click");
                ScreenManager.getInstance().showScreen("PROFILE_SELECT");
            });
        } else {
            btnContinue.setAnimationEnabled(false);
            btnContinue.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            
            // Hapus semua action listener agar tidak bisa diklik
            for (var l : btnContinue.getActionListeners()) btnContinue.removeActionListener(l);
        }
        
        btnContinue.repaint();
    }

    private void loadAssets() {
        try {
            // [FIX JAR] Load Background pakai getResource
            URL bgUrl = getClass().getResource("/images/bg_menu.png");
            if (bgUrl != null) bgImage = new ImageIcon(bgUrl).getImage();
        } catch (Exception e) {}
    }

    private void initUI() {
        // ========================================================
        // 1. PANEL MENU TENGAH
        // ========================================================
        JPanel centerMenuPanel = new JPanel(new GridBagLayout());
        centerMenuPanel.setOpaque(false);

        GridBagConstraints gbcMenu = new GridBagConstraints();
        gbcMenu.gridx = 0;
        gbcMenu.anchor = GridBagConstraints.CENTER;

        // --- TOMBOL 0: LANJUTKAN ---
        btnContinue = new ImageButton("btn_not_continue.png");
        btnContinue.setButtonSize(400, 220);
        
        gbcMenu.gridy = 0;
        gbcMenu.insets = new Insets(90, 0, -60, 0); 
        centerMenuPanel.add(btnContinue, gbcMenu);

        // --- TOMBOL 1: MULAI BARU ---
        ImageButton btnNewGame = new ImageButton("btn_start.png");
        btnNewGame.setButtonSize(400, 200); 
        btnNewGame.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("PROFILE_CREATE");
        });

        gbcMenu.gridy = 1;
        gbcMenu.insets = new Insets(-20, 0, -50, 0); 
        centerMenuPanel.add(btnNewGame, gbcMenu);

        // --- TOMBOL 2: LEADERBOARD ---
        ImageButton btnLeaderboard = new ImageButton("btn_leaderboard.png");
        btnLeaderboard.setButtonSize(380, 190);
        btnLeaderboard.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("LEADERBOARD");
        });

        gbcMenu.gridy = 2;
        gbcMenu.insets = new Insets(-20, 0, -55, 0); 
        centerMenuPanel.add(btnLeaderboard, gbcMenu);

        // --- TOMBOL 3: SETTINGS ---
        ImageButton btnSettings = new ImageButton("btn_settings.png");
        btnSettings.setButtonSize(400, 250);
        btnSettings.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("SETTINGS");
        });

        gbcMenu.gridy = 3;
        gbcMenu.insets = new Insets(-55, 0, -30, 0); 
        centerMenuPanel.add(btnSettings, gbcMenu);

        // --- TOMBOL 4: KELUAR ---
        ImageButton btnExit = new ImageButton("btn_exit.png");
        btnExit.setButtonSize(380, 190);
        btnExit.addActionListener(e -> {
            playSound("click");
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Yakin mau istirahat?", "Keluar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        gbcMenu.gridy = 4;
        gbcMenu.insets = new Insets(-30, 0, 0, 0); 
        centerMenuPanel.add(btnExit, gbcMenu);

        // Tambahkan ke Layar Utama
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0; gbcMain.gridy = 0;
        gbcMain.weightx = 1.0; gbcMain.weighty = 1.0;
        gbcMain.anchor = GridBagConstraints.CENTER;
        add(centerMenuPanel, gbcMain);
        
        // Panggil update status tombol sekali di awal
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
                // [FIX JAR] Load Button Images pakai getResource
                URL url = getClass().getResource("/images/" + filename);
                if (url != null) {
                    img = new ImageIcon(url).getImage();
                    setText(""); 
                } else {
                    // Fallback kalau gambar hilang
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