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
import java.net.URL;

public class SettingsScreen extends JPanel {
    
    private Image bgImage;
    private JCheckBox chkMute;
    private JCheckBox chkFullScreen;
    private JSlider sliderBGM;
    private JSlider sliderSFX;
    private final UserRepository userRepo;
    
    // Flag to prevent listener loops during initialization
    private boolean isLoadingUI = false; 

    // --- THEME COLORS ---
    private static final Color COL_BOARD_BG = new Color(80, 50, 20, 240);
    private static final Color COL_BOARD_BORDER = new Color(220, 180, 80);
    private static final Color COL_SLIDER_FILL = new Color(100, 200, 50);
    private static final Color COL_SLIDER_TRACK = new Color(50, 30, 10);
    private static final Color COL_TEXT = new Color(255, 248, 220);

    public SettingsScreen() {
        this.userRepo = new UserRepository();
        setLayout(new BorderLayout());
        loadAssets();
        initUI();
    }

    private void loadAssets() {
        try {
            URL bgUrl = getClass().getResource("/images/bg_menu.png");
            if (bgUrl != null) bgImage = new ImageIcon(bgUrl).getImage();
        } catch (Exception ignored) {}
    }

    private void initUI() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        // --- SETTINGS PANEL ---
        JPanel settingsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(); int h = getHeight();
                g2.setColor(COL_BOARD_BG); g2.fillRoundRect(0, 0, w, h, 60, 60);
                g2.setColor(COL_BOARD_BORDER); g2.setStroke(new BasicStroke(6f)); g2.drawRoundRect(3, 3, w-6, h-6, 60, 60);
                g2.dispose();
            }
        };
        
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setOpaque(false);
        settingsPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        
        // 1. TITLE
        JLabel title = new JLabel("PENGATURAN");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 42));
        title.setForeground(COL_BOARD_BORDER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(title);
        settingsPanel.add(Box.createVerticalStrut(30));

        // 2. OPTIONS (Fullscreen & Mute)
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        checkPanel.setOpaque(false);
        checkPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // A. Fullscreen Checkbox
        chkFullScreen = createCustomCheckbox(" Layar Penuh");
        chkFullScreen.addActionListener(e -> toggleFullScreen(chkFullScreen.isSelected()));
        checkPanel.add(chkFullScreen);

        // B. Mute Checkbox
        chkMute = createCustomCheckbox(" Senyapkan Suara");
        chkMute.addActionListener(e -> {
            if (isLoadingUI) return; 
            boolean isMuted = chkMute.isSelected();
            toggleSliders(!isMuted);
            
            // Update SoundPlayer directly
            SoundPlayer.getInstance().setMute(isMuted);
            // Jika unmute, kembalikan volume real
            if (!isMuted) {
                SoundPlayer.getInstance().setVolumeBGM(sliderBGM.getValue());
                SoundPlayer.getInstance().setVolumeSFX(sliderSFX.getValue());
            }
        });
        checkPanel.add(chkMute);

        settingsPanel.add(checkPanel);
        settingsPanel.add(Box.createVerticalStrut(30));

        // 3. BGM SLIDER
        settingsPanel.add(createLabel("Musik Latar (BGM)"));
        settingsPanel.add(Box.createVerticalStrut(8));
        sliderBGM = createNatureSlider();
        
        // Listener: Update Realtime
        sliderBGM.addChangeListener(e -> {
            if (!isLoadingUI && !chkMute.isSelected()) {
                SoundPlayer.getInstance().setVolumeBGM(sliderBGM.getValue());
            }
        });
        settingsPanel.add(sliderBGM);
        settingsPanel.add(Box.createVerticalStrut(20));

        // 4. SFX SLIDER
        settingsPanel.add(createLabel("Efek Suara (SFX)"));
        settingsPanel.add(Box.createVerticalStrut(8));
        sliderSFX = createNatureSlider();
        
        // Listener: Update Realtime
        sliderSFX.addChangeListener(e -> {
            if (!isLoadingUI && !chkMute.isSelected()) {
                SoundPlayer.getInstance().setVolumeSFX(sliderSFX.getValue());
            }
        });
        settingsPanel.add(sliderSFX);
        settingsPanel.add(Box.createVerticalStrut(40));

        // 5. SAVE BUTTON
        GameButton btnSave = new GameButton("SIMPAN", new Color(46, 204, 113), new Color(39, 174, 96));
        btnSave.addActionListener(e -> saveSettings());
        settingsPanel.add(btnSave);

        wrapper.add(settingsPanel);
        add(wrapper, BorderLayout.CENTER);
        
        // Tombol BATAL sudah dihapus dari sini
    }
    
    // --- KEY METHOD: PREVENT RESET & SYNC UI ---
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            isLoadingUI = true; // Lock listeners

            // 1. Sync Fullscreen UI (Cek apakah frame undecorated)
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if(frame != null && chkFullScreen != null) {
                chkFullScreen.setSelected(frame.isUndecorated());
            }

            // 2. Sync Mute UI with SoundPlayer
            boolean currentMute = SoundPlayer.getInstance().isMuted();
            chkMute.setSelected(currentMute);
            toggleSliders(!currentMute); 

            // 3. Sync Sliders with SoundPlayer Memory (Source of Truth)
            sliderBGM.setValue(SoundPlayer.getInstance().getVolumeBGM());
            sliderSFX.setValue(SoundPlayer.getInstance().getVolumeSFX());
            
            isLoadingUI = false; // Unlock listeners
        }
    }
    
    // --- FIX: LOGIKA LAYAR PENUH YANG LEBIH AMAN ---
    private void toggleFullScreen(boolean enableFull) {
        if (isLoadingUI) return;
        
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;
        
        // Jalankan di Event Dispatch Thread agar aman
        SwingUtilities.invokeLater(() -> {
            // 1. Matikan Frame (Harus dispose untuk ubah undecorated)
            frame.dispose();
            
            // 2. Ubah Dekorasi
            frame.setUndecorated(enableFull);
            
            // 3. Atur State Window
            if (enableFull) {
                // Mode Fullscreen (Maximized tanpa border)
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                // Mode Windowed Normal
                frame.setExtendedState(JFrame.NORMAL);
                frame.setSize(1024, 768); // Ukuran default
                frame.setLocationRelativeTo(null); // Tengah layar
            }
            
            // 4. Nyalakan Kembali
            frame.setVisible(true);
            
            // 5. Validasi Ulang Layout (Penting agar isi tidak berantakan)
            frame.revalidate();
            frame.repaint();
        });
    }

    private void saveSettings() {
        int bgmVol = sliderBGM.getValue();
        int sfxVol = sliderSFX.getValue();
        
        // Save to Database if user is logged in
        UserModel user = GameState.getCurrentUser();
        if (user != null) {
            user.setBgmVolume(bgmVol);
            user.setSfxVolume(sfxVol);
            try { 
                userRepo.updateVolume(user.getId(), bgmVol, sfxVol); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        playSound("click");
        ScreenManager.getInstance().showScreen("MAIN_MENU");
    }

    private void playSound(String name) {
        try { SoundPlayer.getInstance().playSFX(name + ".wav"); } catch (Exception ignored) {}
    }
    
    private void toggleSliders(boolean enabled) {
        sliderBGM.setEnabled(enabled);
        sliderSFX.setEnabled(enabled);
    }

    // --- UI COMPONENTS HELPERS ---

    private JCheckBox createCustomCheckbox(String text) {
        JCheckBox chk = new JCheckBox(text);
        chk.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        chk.setForeground(COL_TEXT);
        chk.setOpaque(false);
        chk.setFocusPainted(false);
        chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chk.setIcon(createCheckIcon(false));
        chk.setSelectedIcon(createCheckIcon(true));
        return chk;
    }

    private Icon createCheckIcon(boolean selected) {
        return new Icon() {
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,100)); g2.fillRoundRect(x, y, 24, 24, 8, 8);
                g2.setColor(COL_BOARD_BORDER); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(x, y, 24, 24, 8, 8);
                if (selected) {
                    g2.setColor(Color.GREEN); g2.setStroke(new BasicStroke(3));
                    g2.drawLine(x+5, y+12, x+10, y+18); g2.drawLine(x+10, y+18, x+20, y+6);
                }
            }
        };
    }
    
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        lbl.setForeground(new Color(230, 230, 230)); 
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

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
                int w = c.getWidth(); int h = c.getHeight(); int val = slider.getValue();
                g2.setColor(COL_SLIDER_TRACK); g2.fillRoundRect(0, 5, w, h-10, 20, 20);
                if (slider.isEnabled()) g2.setColor(COL_SLIDER_FILL); else g2.setColor(Color.GRAY);
                int fillWidth = (int) (w * (val / 100.0));
                if (fillWidth > 0) g2.fillRoundRect(0, 5, fillWidth, h-10, 20, 20);
                g2.setColor(new Color(255, 255, 255, 50)); g2.setStroke(new BasicStroke(1f)); g2.drawRoundRect(0, 5, w - 1, h - 11, 20, 20);
                String text = val + "%"; g2.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics(); int txtW = fm.stringWidth(text);
                g2.setColor(new Color(0,0,0,150)); g2.drawString(text, (w - txtW)/2 + 2, (h/2) + 6);
                g2.setColor(Color.WHITE); g2.drawString(text, (w - txtW)/2, (h/2) + 4);
            }
        });
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        else { g.setColor(new Color(30, 100, 50)); g.fillRect(0, 0, getWidth(), getHeight()); }
        g.setColor(new Color(0,0,0,50)); g.fillRect(0,0,getWidth(),getHeight());
    }

    class GameButton extends JButton {
        private Color cTop, cBot;
        private boolean hover;
        public GameButton(String text, Color top, Color bot) {
            super(text); this.cTop = top; this.cBot = bot;
            setFont(new Font("Comic Sans MS", Font.BOLD, 20)); setForeground(Color.WHITE);
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR)); setAlignmentX(Component.CENTER_ALIGNMENT);
            setPreferredSize(new Dimension(300, 60)); setMaximumSize(new Dimension(300, 60));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight(); int offset = 6;
            g2.setColor(new Color(0,0,0,60)); g2.fillRoundRect(2, 8, w-4, h-8, 20, 20);
            g2.setColor(cBot.darker()); g2.fillRoundRect(0, offset, w, h-offset, 20, 20);
            int yPos = (getModel().isPressed()) ? 4 : 0;
            Color c1 = hover ? cTop.brighter() : cTop; Color c2 = hover ? cTop : cTop.darker();
            g2.setPaint(new GradientPaint(0, yPos, c1, 0, h, c2)); g2.fillRoundRect(0, yPos, w, h-offset, 20, 20);
            g2.setColor(new Color(255,255,255,50)); g2.drawRoundRect(0, yPos, w-1, h-offset-1, 20, 20);
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2; int y = (h - offset - fm.getHeight()) / 2 + fm.getAscent() + yPos;
            g2.setColor(new Color(0,0,0,50)); g2.drawString(getText(), x+1, y+2);
            g2.setColor(Color.WHITE); g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}