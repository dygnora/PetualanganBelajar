package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class MainMenuScreen extends JPanel {

    private Image bgImage;

    public MainMenuScreen() {
        setLayout(new GridBagLayout()); // Layout Utama
        loadAssets();
        initUI();
    }

    private void loadAssets() {
        try {
            File fBg = new File("resources/images/bg_menu.png");
            if (fBg.exists()) bgImage = new ImageIcon(fBg.getAbsolutePath()).getImage();
        } catch (Exception e) {}
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();

        // ========================================================
        // 1. PANEL MENU TENGAH (Mulai, Leaderboard, Setting, Keluar)
        // ========================================================
        // Kita gunakan GridBagLayout juga di dalam panel ini agar jaraknya RAPAT
        JPanel centerMenuPanel = new JPanel(new GridBagLayout());
        centerMenuPanel.setOpaque(false);

        GridBagConstraints gbcMenu = new GridBagConstraints();
        gbcMenu.gridx = 0;
        gbcMenu.anchor = GridBagConstraints.CENTER;
        
        // --- Setting Jarak Antar Tombol (Insets) ---
        // Top, Left, Bottom, Right. Kita beri jarak bawah 10px saja.
        gbcMenu.insets = new Insets(0, 0, -50, 0); 

        // 1. Tombol MULAI BARU (Paling Besar)
        ImageButton btnNewGame = new ImageButton("btn_start.png");
        btnNewGame.setButtonSize(400, 200); // Lebar & Besar
        btnNewGame.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("PROFILE_CREATE");
        });
        
        // 2. Tombol LEADERBOARD
        ImageButton btnLeaderboard = new ImageButton("btn_leaderboard.png");
        btnLeaderboard.setButtonSize(380, 190);
        btnLeaderboard.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("LEADERBOARD");
        });

        // 3. Tombol SETTINGS
        ImageButton btnSettings = new ImageButton("btn_settings.png");
        btnSettings.setButtonSize(380, 190);
        btnSettings.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("SETTINGS");
        });

        // 4. Tombol KELUAR
        ImageButton btnExit = new ImageButton("btn_exit.png");
        btnExit.setButtonSize(390, 200);
        btnExit.addActionListener(e -> {
            playSound("click");
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Yakin mau istirahat?", "Keluar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        // Masukkan ke panel menu (Berurutan ke bawah)
        gbcMenu.gridy = 0; centerMenuPanel.add(btnNewGame, gbcMenu);
        gbcMenu.gridy = 1; centerMenuPanel.add(btnLeaderboard, gbcMenu);
        gbcMenu.gridy = 2; centerMenuPanel.add(btnSettings, gbcMenu);
        gbcMenu.gridy = 3; centerMenuPanel.add(btnExit, gbcMenu);

        // Tambahkan Panel Menu ke Layar Utama (CENTER)
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.weightx = 1.0; 
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(centerMenuPanel, gbc);

        // ========================================================
        // 2. TOMBOL LANJUTKAN (POJOK KANAN BAWAH)
        // ========================================================
        ImageButton btnContinue = new ImageButton("btn_continue.png");
        btnContinue.setButtonSize(260, 100); // Ukuran Pas
        btnContinue.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("PROFILE_SELECT");
        });

        // Konfigurasi Pojok Kanan Bawah
        GridBagConstraints gbcCorner = new GridBagConstraints();
        gbcCorner.gridx = 0;
        gbcCorner.gridy = 0;
        gbcCorner.weightx = 1.0;
        gbcCorner.weighty = 1.0;
        gbcCorner.anchor = GridBagConstraints.SOUTHEAST; // KUNCI POJOK
        // Margin dari pinggir layar (Bawah 30, Kanan 30)
        gbcCorner.insets = new Insets(0, 0, 30, 30); 
        
        // Bungkus agar aman
        JPanel cornerContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cornerContainer.setOpaque(false);
        cornerContainer.add(btnContinue);
        
        add(cornerContainer, gbcCorner);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback
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
    // CLASS: IMAGE BUTTON (ANIMASI ELASTIS & HD)
    // ============================================================
    class ImageButton extends JButton {
        private Image img;
        private boolean isHovered = false;
        
        // Animasi Variables
        private float scale = 1.0f;
        private float targetScale = 1.0f;
        private Timer animTimer;

        public ImageButton(String filename) {
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);

            try {
                File f = new File("resources/images/" + filename);
                if (f.exists()) {
                    img = new ImageIcon(f.getAbsolutePath()).getImage();
                } else {
                    setText(filename); 
                    setForeground(Color.RED);
                    setFont(new Font("Arial", Font.BOLD, 24));
                }
            } catch (Exception e) {}

            // Timer Animasi (60 FPS Smooth)
            animTimer = new Timer(16, e -> updateAnimation());
            animTimer.start();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    targetScale = 1.1f; // Membesar 10%
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    targetScale = 1.0f; // Kembali normal
                }
            });
        }
        
        public void setButtonSize(int width, int height) {
            setPreferredSize(new Dimension(width, height));
            // Minimum & Maximum size diset agar layout manager patuh
            setMinimumSize(new Dimension(width, height));
            setMaximumSize(new Dimension(width, height));
        }

        // Logika Animasi Membal (Easing)
        private void updateAnimation() {
            if (Math.abs(scale - targetScale) > 0.001f) {
                scale += (targetScale - scale) * 0.2f; // Smooth transition
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (img != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Rendering High Quality agar gambar tidak pecah saat di-resize
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int baseW = getWidth();
                int baseH = getHeight();

                // Hitung ukuran animasi
                int drawW = (int) (baseW * scale);
                int drawH = (int) (baseH * scale);
                
                // Posisi Center Pivot (Agar membesar dari tengah)
                int x = (baseW - drawW) / 2;
                int y = (baseH - drawH) / 2;

                // Efek Glow Putih saat Hover
                if (isHovered) {
                    g2.setColor(new Color(255, 255, 255, 60));
                    g2.fillRoundRect(x + 10, y + 10, drawW - 20, drawH - 20, 30, 30);
                }

                // Gambar Tombol (Dipaksa menyesuaikan ukuran yang kita set)
                g2.drawImage(img, x, y, drawW, drawH, this);
                
                g2.dispose();
            } else {
                super.paintComponent(g);
            }
        }
    }
}