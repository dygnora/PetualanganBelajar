package com.petualanganbelajar.ui.component;

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

public class PauseMenuDialog extends JDialog {

    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    // UI Components
    private JCheckBox chkMute, chkFullScreen;
    private JSlider sliderBGM, sliderSFX;
    
    // Logic
    private final UserRepository userRepo;
    private boolean isLoadingUI = false;

    // --- COLOR PALETTE (PLAYFUL POP STYLE) ---
    private static final Color COL_OVERLAY = new Color(0, 0, 0, 140); 
    private static final Color COL_CARD_BG = new Color(255, 252, 240); 
    private static final Color COL_CARD_BORDER = new Color(255, 160, 60); 
    private static final Color COL_TEXT_TITLE = new Color(70, 130, 180); 
    private static final Color COL_TEXT_LABEL = new Color(100, 100, 100); 
    private static final Color COL_ACCENT_GREEN = new Color(100, 200, 50);

    public PauseMenuDialog(JFrame parent) {
        super(parent, "Pause", true);
        this.userRepo = new UserRepository();

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); 

        initUI();
        
        setSize(parent.getSize()); 
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        // 1. PANEL UTAMA (Overlay + Card Container)
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // A. Overlay Gelap Fullscreen
                g2.setColor(COL_OVERLAY);
                g2.fillRect(0, 0, w, h);

                // B. Kartu Menu (Tengah)
                int cardW = 420;
                int cardH = 520;
                int x = (w - cardW) / 2;
                int y = (h - cardH) / 2;

                // Shadow
                g2.setColor(new Color(0,0,0,60));
                g2.fillRoundRect(x+8, y+8, cardW, cardH, 40, 40);

                // Background
                g2.setColor(COL_CARD_BG);
                g2.fillRoundRect(x, y, cardW, cardH, 40, 40);

                // Border
                g2.setColor(COL_CARD_BORDER);
                g2.setStroke(new BasicStroke(8f));
                g2.drawRoundRect(x+4, y+4, cardW-8, cardH-8, 40, 40);
                
                // Hiasan Sudut
                g2.setColor(COL_TEXT_TITLE);
                g2.fillOval(x+20, y+20, 12, 12);
                g2.fillOval(x+cardW-32, y+20, 12, 12);
                g2.fillOval(x+20, y+cardH-32, 12, 12);
                g2.fillOval(x+cardW-32, y+cardH-32, 12, 12);

                g2.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // 2. KONTEN (Card Layout)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(380, 480)); 

        cardPanel.add(createMainMenu(), "MENU");
        cardPanel.add(createSettings(), "SETTINGS");

        mainPanel.add(cardPanel);
        setContentPane(mainPanel);
    }

    // --- CARD 1: MENU UTAMA ---
    private JPanel createMainMenu() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel lblTitle = new JLabel("ISTIRAHAT");
        lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 42));
        lblTitle.setForeground(COL_TEXT_TITLE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tombol Gaya Baru
        GameButton btnResume = new GameButton("LANJUT MAIN", new Color(102, 187, 106), new Color(56, 142, 60));
        GameButton btnSet = new GameButton("PENGATURAN", new Color(255, 167, 38), new Color(230, 81, 0));
        GameButton btnExit = new GameButton("KELUAR", new Color(239, 83, 80), new Color(198, 40, 40));

        btnResume.addActionListener(e -> dispose());
        
        btnSet.addActionListener(e -> {
            loadRealtimeData(); // Load data saat pindah ke setting
            cardLayout.show(cardPanel, "SETTINGS");
        });
        
        btnExit.addActionListener(e -> {
            dispose();
            ScreenManager.getInstance().showScreen("MODULE_SELECT");
        });

        p.add(Box.createVerticalGlue());
        p.add(lblTitle);
        p.add(Box.createVerticalStrut(40));
        p.add(btnResume);
        p.add(Box.createVerticalStrut(20));
        p.add(btnSet);
        p.add(Box.createVerticalStrut(20));
        p.add(btnExit);
        p.add(Box.createVerticalGlue());

        return p;
    }

    // --- CARD 2: PENGATURAN ---
    private JPanel createSettings() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel lblTitle = new JLabel("PENGATURAN");
        lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        lblTitle.setForeground(COL_TEXT_TITLE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Checkbox Panel
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        checkPanel.setOpaque(false);
        
        chkFullScreen = createCustomCheckbox("Layar Penuh");
        chkMute = createCustomCheckbox("Senyap");
        
        // Listener Full Screen (Menggunakan Logic Lama Anda)
        chkFullScreen.addActionListener(e -> toggleFS(chkFullScreen.isSelected()));

        // Listener Mute (Realtime)
        chkMute.addActionListener(e -> {
            if (isLoadingUI) return;
            boolean isMuted = chkMute.isSelected();
            toggleSliders(!isMuted);
            SoundPlayer.getInstance().setMute(isMuted);
            if(!isMuted) {
                // Restore volume saat unmute
                SoundPlayer.getInstance().setVolumeBGM(sliderBGM.getValue());
            }
        });

        checkPanel.add(chkFullScreen);
        checkPanel.add(chkMute);

        // Slider BGM
        sliderBGM = createNatureSlider();
        sliderBGM.addChangeListener(e -> {
            if (!sliderBGM.getValueIsAdjusting() && !isLoadingUI && !chkMute.isSelected()) {
                SoundPlayer.getInstance().setVolumeBGM(sliderBGM.getValue());
            }
        });

        // Slider SFX
        sliderSFX = createNatureSlider();
        sliderSFX.addChangeListener(e -> {
            if (!sliderSFX.getValueIsAdjusting() && !isLoadingUI && !chkMute.isSelected()) {
                SoundPlayer.getInstance().setVolumeSFX(sliderSFX.getValue());
            }
        });

        GameButton btnBack = new GameButton("SIMPAN", new Color(102, 187, 106), new Color(56, 142, 60));
        btnBack.addActionListener(e -> {
            saveSettings();
            cardLayout.show(cardPanel, "MENU");
        });

        // Layout Susunan
        p.add(Box.createVerticalStrut(10));
        p.add(lblTitle);
        p.add(Box.createVerticalStrut(20));
        p.add(checkPanel);
        p.add(Box.createVerticalStrut(20));
        p.add(createLabel("Musik Latar"));
        p.add(sliderBGM);
        p.add(Box.createVerticalStrut(15));
        p.add(createLabel("Efek Suara"));
        p.add(sliderSFX);
        p.add(Box.createVerticalStrut(30));
        p.add(btnBack);
        p.add(Box.createVerticalGlue());

        return p;
    }

    // --- LOGIKA UTAMA ---

    private void loadRealtimeData() {
        isLoadingUI = true;

        // 1. Cek Fullscreen (Ambil dari window ancestor)
        JFrame f = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (f != null) {
            chkFullScreen.setSelected(f.isUndecorated());
        }

        // 2. Cek Sound & Slider (Dari Memory)
        boolean isMuted = SoundPlayer.getInstance().isMuted();
        chkMute.setSelected(isMuted);
        toggleSliders(!isMuted);

        sliderBGM.setValue(SoundPlayer.getInstance().getVolumeBGM());
        sliderSFX.setValue(SoundPlayer.getInstance().getVolumeSFX());

        isLoadingUI = false;
    }

    private void saveSettings() {
        // Simpan ke DB
        int bgmVol = sliderBGM.getValue();
        int sfxVol = sliderSFX.getValue();
        UserModel u = GameState.getCurrentUser();
        if (u != null) {
            u.setBgmVolume(bgmVol);
            u.setSfxVolume(sfxVol);
            new Thread(() -> userRepo.updateVolume(u.getId(), bgmVol, sfxVol)).start();
        }
        playSound("click");
    }

    // [FIX] Ini adalah Logika Fullscreen dari KODE LAMA Anda yang terbukti bekerja
    private void toggleFS(boolean b) {
        if(isLoadingUI) return;

        JFrame f = (JFrame) SwingUtilities.getWindowAncestor(this);
        if(f == null) return;

        // 1. Tutup Dialog Pause
        dispose(); 
        
        // 2. Matikan Frame Utama
        f.dispose();
        
        // 3. Ubah Dekorasi
        f.setUndecorated(b);
        
        // 4. Set State (Full vs Normal)
        if(b) {
            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else { 
            f.setExtendedState(JFrame.NORMAL); 
            f.setSize(1024, 768); 
            f.setLocationRelativeTo(null); 
        }
        
        // 5. Nyalakan Lagi
        f.setVisible(true);
    }

    private void toggleSliders(boolean enabled) {
        sliderBGM.setEnabled(enabled);
        sliderSFX.setEnabled(enabled);
    }
    
    private void playSound(String name) {
        try { SoundPlayer.getInstance().playSFX(name + ".wav"); } catch (Exception ignored) {}
    }

    // --- UI HELPERS (STYLE BARU) ---

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        l.setForeground(COL_TEXT_LABEL);
        l.setAlignmentX(CENTER_ALIGNMENT);
        l.setBorder(new EmptyBorder(0,0,5,0));
        return l;
    }
    
    private JCheckBox createCustomCheckbox(String text) {
        JCheckBox c = new JCheckBox(text);
        c.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        c.setForeground(COL_TEXT_TITLE);
        c.setOpaque(false);
        c.setFocusPainted(false);
        c.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        c.setIcon(new Icon() {
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
            public void paintIcon(Component cmp, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, y, 24, 24, 8, 8);
                g2.setColor(COL_CARD_BORDER);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(x, y, 24, 24, 8, 8);
            }
        });
        
        c.setSelectedIcon(new Icon() {
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
            public void paintIcon(Component cmp, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, y, 24, 24, 8, 8);
                g2.setColor(COL_ACCENT_GREEN);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(x+5, y+12, x+10, y+18);
                g2.drawLine(x+10, y+18, x+20, y+6);
                g2.setColor(COL_CARD_BORDER);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(x, y, 24, 24, 8, 8);
            }
        });
        return c;
    }

    private JSlider createNatureSlider() {
        JSlider slider = new JSlider(0, 100);
        slider.setOpaque(false);
        slider.setFocusable(false);
        slider.setPreferredSize(new Dimension(280, 40));
        slider.setMaximumSize(new Dimension(280, 40));
        slider.setCursor(new Cursor(Cursor.HAND_CURSOR));

        slider.setUI(new BasicSliderUI(slider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = 10;
                int y = trackRect.y + (trackRect.height - h) / 2;
                g2.setColor(new Color(220, 220, 220)); 
                g2.fillRoundRect(trackRect.x, y, trackRect.width, h, 10, 10);
                if (slider.isEnabled()) {
                    g2.setColor(COL_ACCENT_GREEN);
                    int fillW = thumbRect.x + (thumbRect.width/2) - trackRect.x;
                    if(fillW > 0) g2.fillRoundRect(trackRect.x, y, fillW, h, 10, 10);
                }
            }
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
                g2.setColor(COL_CARD_BORDER); 
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
            @Override
            protected Dimension getThumbSize() {
                return new Dimension(22, 22);
            }
        });
        
        MouseAdapter ma = new MouseAdapter() {
            public void mousePressed(MouseEvent e) { move(e); }
            public void mouseDragged(MouseEvent e) { move(e); }
            private void move(MouseEvent e) {
                if(!slider.isEnabled()) return;
                JSlider s = (JSlider)e.getSource();
                int val = (int)Math.round(((double)e.getX()/s.getWidth())*100);
                s.setValue(val);
            }
        };
        slider.addMouseListener(ma);
        slider.addMouseMotionListener(ma);
        return slider;
    }

    class GameButton extends JButton {
        private Color cTop, cBot;
        private boolean hover;
        public GameButton(String text, Color top, Color bot) {
            super(text); this.cTop = top; this.cBot = bot;
            setFont(new Font("Comic Sans MS", Font.BOLD, 18)); setForeground(Color.WHITE);
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR)); setAlignmentX(Component.CENTER_ALIGNMENT);
            setPreferredSize(new Dimension(280, 50)); setMaximumSize(new Dimension(280, 50));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight(); int offset = 5;
            g2.setColor(new Color(0,0,0,30));
            g2.fillRoundRect(3, 6, w-6, h-6, 20, 20);
            g2.setColor(cBot); 
            g2.fillRoundRect(0, offset, w, h-offset, 20, 20);
            int yPos = (getModel().isPressed()) ? 4 : 0;
            g2.setColor(hover ? cTop.brighter() : cTop);
            g2.fillRoundRect(0, yPos, w, h-offset, 20, 20);
            g2.setColor(new Color(255,255,255,50)); 
            g2.drawRoundRect(0, yPos, w-1, h-offset-1, 20, 20);
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2; 
            int y = (h - offset - fm.getHeight()) / 2 + fm.getAscent() + yPos;
            g2.setColor(new Color(0,0,0,40)); 
            g2.drawString(getText(), x+1, y+2);
            g2.setColor(Color.WHITE); 
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}