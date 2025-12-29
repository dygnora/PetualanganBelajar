package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.UserRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class SettingsScreen extends JPanel {
    
    private Image bgImage;
    private JCheckBox chkMute; 
    private JSlider sliderBGM;
    private JSlider sliderSFX;
    private final UserRepository userRepo;

    // --- NATURE / WOOD THEME COLORS ---
    // Background papan (Coklat Kayu Transparan)
    private static final Color COL_BOARD_BG = new Color(80, 50, 20, 240); 
    // Border papan (Emas/Kuning Kayu)
    private static final Color COL_BOARD_BORDER = new Color(220, 180, 80); 
    // Warna Slider Isi (Hijau Daun)
    private static final Color COL_SLIDER_FILL = new Color(100, 200, 50);
    // Warna Slider Kosong (Coklat Gelap)
    private static final Color COL_SLIDER_TRACK = new Color(50, 30, 10);
    // Warna Teks (Krem/Putih Tulang)
    private static final Color COL_TEXT = new Color(255, 248, 220); 

    public SettingsScreen() {
        this.userRepo = new UserRepository();
        setLayout(new BorderLayout());
        loadAssets();
        initUI();
    }

    private void loadAssets() {
        try {
            File bg = new File("resources/images/bg_menu.png");
            if (bg.exists()) bgImage = new ImageIcon(bg.getAbsolutePath()).getImage();
        } catch (Exception ignored) {}
    }

    private void initUI() {
        // Wrapper Utama
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        // --- PANEL SETTING (WOODEN BOARD LOOK) ---
        // Kita override paintComponent langsung di sini untuk menggambar bentuk papan
        JPanel settingsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // 1. Gambar Background Papan (Rounded)
                g2.setColor(COL_BOARD_BG);
                g2.fillRoundRect(0, 0, w, h, 60, 60); // Radius 60 agar tumpul
                
                // 2. Gambar Border Tebal
                g2.setColor(COL_BOARD_BORDER);
                g2.setStroke(new BasicStroke(6f)); // Border tebal 6px
                g2.drawRoundRect(3, 3, w-6, h-6, 60, 60);
                
                g2.dispose();
            }
        };
        
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setOpaque(false); // Penting agar paintComponent bekerja transparan
        settingsPanel.setBorder(new EmptyBorder(40, 60, 40, 60)); // Padding dalam lebih besar
        
        // 1. JUDUL
        JLabel title = new JLabel("PENGATURAN");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 42)); // Font lebih fun
        title.setForeground(COL_BOARD_BORDER); // Warna emas
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Efek Shadow pada teks judul
        title.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,150));
                g2.drawString(title.getText(), 3, title.getFontMetrics(title.getFont()).getAscent()+3);
                super.paint(g, c);
            }
        });
        
        settingsPanel.add(title);
        settingsPanel.add(Box.createVerticalStrut(30));

        // 2. CHECKBOX MUTE
        chkMute = new JCheckBox(" Senyapkan Suara");
        chkMute.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        chkMute.setForeground(COL_TEXT);
        chkMute.setOpaque(false);
        chkMute.setFocusPainted(false);
        chkMute.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkMute.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Custom Icon Checkbox agar terlihat menyatu
        chkMute.setIcon(createCheckIcon(false));
        chkMute.setSelectedIcon(createCheckIcon(true));
        
        chkMute.addActionListener(e -> toggleSliders(!chkMute.isSelected()));
        settingsPanel.add(chkMute);
        settingsPanel.add(Box.createVerticalStrut(30));

        // 3. SLIDER BGM
        settingsPanel.add(createLabel("Musik Latar (BGM)"));
        settingsPanel.add(Box.createVerticalStrut(8));
        sliderBGM = createNatureSlider();
        settingsPanel.add(sliderBGM);
        settingsPanel.add(Box.createVerticalStrut(25));

        // 4. SLIDER SFX
        settingsPanel.add(createLabel("Efek Suara (SFX)"));
        settingsPanel.add(Box.createVerticalStrut(8));
        sliderSFX = createNatureSlider();
        settingsPanel.add(sliderSFX);
        settingsPanel.add(Box.createVerticalStrut(40));

        // 5. TOMBOL SIMPAN
        // Tombol warna hijau (Save)
        GameButton btnSave = new GameButton("SIMPAN", new Color(46, 204, 113), new Color(39, 174, 96));
        btnSave.addActionListener(e -> saveSettings());
        settingsPanel.add(btnSave);

        wrapper.add(settingsPanel);
        add(wrapper, BorderLayout.CENTER);
        
        // Tombol Kembali Kecil (Kiri Atas)
        addBackLink();
    }
    
    // Icon Checkbox Custom (Kotak sederhana warna border emas)
    private Icon createCheckIcon(boolean selected) {
        return new Icon() {
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,100));
                g2.fillRoundRect(x, y, 24, 24, 8, 8);
                g2.setColor(COL_BOARD_BORDER);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(x, y, 24, 24, 8, 8);
                if (selected) {
                    g2.setColor(Color.GREEN);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(x+5, y+12, x+10, y+18);
                    g2.drawLine(x+10, y+18, x+20, y+6);
                }
            }
        };
    }
    
    private void addBackLink() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(20,20,0,0));
        
        // Tombol Batal warna merah/orange
        GameButton btnBack = new GameButton("BATAL", new Color(231, 76, 60), new Color(192, 57, 43));
        btnBack.setPreferredSize(new Dimension(120, 40));
        btnBack.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        btnBack.addActionListener(e -> ScreenManager.getInstance().showScreen("MAIN_MENU"));
        
        topPanel.add(btnBack);
        add(topPanel, BorderLayout.NORTH);
    }
    
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        lbl.setForeground(new Color(230, 230, 230)); 
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /**
     * Slider dengan tema alam (Hijau dan Coklat)
     */
    private JSlider createNatureSlider() {
        JSlider slider = new JSlider(0, 100, 50);
        slider.setOpaque(false);
        slider.setFocusable(false);
        slider.setPreferredSize(new Dimension(350, 45)); 
        slider.setMaximumSize(new Dimension(350, 45));
        slider.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        slider.setUI(new BasicSliderUI(slider) {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = c.getWidth();
                int h = c.getHeight();
                int val = slider.getValue();
                
                // 1. Background Track (Coklat Gelap - seperti tanah/kayu)
                g2.setColor(COL_SLIDER_TRACK);
                g2.fillRoundRect(0, 5, w, h-10, 20, 20);
                
                // 2. Isi Slider (Hijau - seperti rumput/energy)
                if (slider.isEnabled()) {
                    g2.setColor(COL_SLIDER_FILL);
                } else {
                    g2.setColor(Color.GRAY);
                }
                
                int fillWidth = (int) (w * (val / 100.0));
                if (fillWidth > 0) {
                    g2.fillRoundRect(0, 5, fillWidth, h-10, 20, 20);
                }
                
                // 3. Border Track (Emas tipis)
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 5, w - 1, h - 11, 20, 20);
                
                // 4. Teks Persentase
                String text = val + "%";
                g2.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                int txtW = fm.stringWidth(text);
                
                // Shadow Text
                g2.setColor(new Color(0,0,0,150));
                g2.drawString(text, (w - txtW)/2 + 2, (h/2) + 6);
                
                // Main Text
                g2.setColor(Color.WHITE);
                g2.drawString(text, (w - txtW)/2, (h/2) + 4);
            }
        });

        // Hit logic
        MouseAdapter ma = new MouseAdapter() {
            private void move(MouseEvent e) {
                if (!slider.isEnabled()) return;
                JSlider s = (JSlider) e.getSource();
                int val = (int) Math.round(((double) e.getX() / s.getWidth()) * 100);
                s.setValue(Math.max(0, Math.min(100, val)));
            }
            @Override public void mousePressed(MouseEvent e) { move(e); }
            @Override public void mouseDragged(MouseEvent e) { move(e); }
        };
        slider.addMouseListener(ma);
        slider.addMouseMotionListener(ma);
        
        return slider;
    }
    
    private void toggleSliders(boolean enabled) {
        sliderBGM.setEnabled(enabled);
        sliderSFX.setEnabled(enabled);
    }
    
    // --- LOAD USER DATA ---
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            UserModel user = GameState.getCurrentUser();
            if (user != null) {
                sliderBGM.setValue(user.getBgmVolume());
                sliderSFX.setValue(user.getSfxVolume());
            } else {
                sliderBGM.setValue(80);
                sliderSFX.setValue(100);
            }
            chkMute.setSelected(false);
            toggleSliders(true);
        }
    }
    
    private void saveSettings() {
        boolean isMuted = chkMute.isSelected();
        int bgmVol = isMuted ? 0 : sliderBGM.getValue();
        int sfxVol = isMuted ? 0 : sliderSFX.getValue();
        
        // 1. Terapkan Audio
        try {
            SoundPlayer.getInstance().setMute(isMuted);
        } catch (Exception ex) {}
        
        // 2. Simpan
        UserModel user = GameState.getCurrentUser();
        if (user != null) {
            int savedBgm = sliderBGM.getValue();
            int savedSfx = sliderSFX.getValue();
            
            // PENTING: Update Memory
            user.setBgmVolume(savedBgm);
            user.setSfxVolume(savedSfx);
            
            // PENTING: Update Database
            try {
                userRepo.updateVolume(user.getId(), savedBgm, savedSfx);
            } catch (Exception e) {
                System.err.println("DB Save Error: " + e.getMessage());
            }
        }
        
        playSound("click");
        ScreenManager.getInstance().showScreen("MAIN_MENU");
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
            g.setColor(new Color(30, 100, 50)); // Fallback hijau
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        // Overlay tipis agar background tidak terlalu ramai
        g.setColor(new Color(0,0,0,50));
        g.fillRect(0,0,getWidth(),getHeight());
    }

    // --- TOMBOL TEMA GAME (3D style) ---
    class GameButton extends JButton {
        private Color cTop, cBot;
        private boolean hover;
        
        public GameButton(String text, Color top, Color bot) {
            super(text);
            this.cTop = top;
            this.cBot = bot;
            
            setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setPreferredSize(new Dimension(300, 60));
            setMaximumSize(new Dimension(300, 60));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int offset = 6; // Efek 3D ke bawah
            
            // Bayangan
            g2.setColor(new Color(0,0,0,60));
            g2.fillRoundRect(2, 8, w-4, h-8, 20, 20);
            
            // Bagian Bawah (Sisi 3D)
            g2.setColor(cBot.darker());
            g2.fillRoundRect(0, offset, w, h-offset, 20, 20);
            
            // Bagian Atas (Warna Utama) - Naik turun saat hover/click
            int yPos = (getModel().isPressed()) ? 4 : 0;
            
            Color c1 = hover ? cTop.brighter() : cTop;
            Color c2 = hover ? cTop : cTop.darker();
            g2.setPaint(new GradientPaint(0, yPos, c1, 0, h, c2));
            g2.fillRoundRect(0, yPos, w, h-offset, 20, 20);
            
            // Border
            g2.setColor(new Color(255,255,255,50));
            g2.drawRoundRect(0, yPos, w-1, h-offset-1, 20, 20);
            
            // Teks
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = (h - offset - fm.getHeight()) / 2 + fm.getAscent() + yPos;
            
            // Shadow Teks
            g2.setColor(new Color(0,0,0,50));
            g2.drawString(getText(), x+1, y+2);
            
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), x, y);
            
            g2.dispose();
        }
    }
}