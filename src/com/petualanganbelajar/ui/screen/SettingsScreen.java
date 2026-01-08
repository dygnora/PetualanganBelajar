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
    
    // UI Components References
    private JPanel settingsPanel;
    private JLabel titleLabel;
    private JLabel lblBGM, lblSFX;
    private GameButton btnSave;
    private JPanel checkPanel;
    
    private boolean isLoadingUI = false; 

    // --- VARIABEL RESPONSIVE ---
    private final float BASE_W = 1920f;
    private final float BASE_H = 1080f;
    private float scaleFactor = 1.0f;
    private float lastScaleFactor = 0.0f; 

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

    // --- LOGIC RESPONSIVE ---
    private void calculateScaleFactor() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        float sW = (float) getWidth() / BASE_W;
        float sH = (float) getHeight() / BASE_H;
        this.scaleFactor = Math.min(sW, sH);
        if (this.scaleFactor < 0.5f) this.scaleFactor = 0.5f;
    }

    private void updateResponsiveLayout() {
        if (settingsPanel == null) return;

        // 1. Update Panel Border & Padding
        int padY = (int)(40 * scaleFactor);
        int padX = (int)(60 * scaleFactor);
        settingsPanel.setBorder(new EmptyBorder(padY, padX, padY, padX));

        // 2. Update Fonts
        int titleSize = (int)(42 * scaleFactor);
        int labelSize = (int)(18 * scaleFactor);
        int checkSize = (int)(18 * scaleFactor);
        
        if (titleLabel != null) titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, titleSize));
        if (lblBGM != null) lblBGM.setFont(new Font("Comic Sans MS", Font.BOLD, labelSize));
        if (lblSFX != null) lblSFX.setFont(new Font("Comic Sans MS", Font.BOLD, labelSize));
        if (chkFullScreen != null) chkFullScreen.setFont(new Font("Comic Sans MS", Font.BOLD, checkSize));
        if (chkMute != null) chkMute.setFont(new Font("Comic Sans MS", Font.BOLD, checkSize));

        // 3. Update Components Size
        if (sliderBGM != null) {
            Dimension dim = new Dimension((int)(350 * scaleFactor), (int)(45 * scaleFactor));
            sliderBGM.setPreferredSize(dim);
            sliderBGM.setMaximumSize(dim);
        }
        if (sliderSFX != null) {
            Dimension dim = new Dimension((int)(350 * scaleFactor), (int)(45 * scaleFactor));
            sliderSFX.setPreferredSize(dim);
            sliderSFX.setMaximumSize(dim);
        }
        if (btnSave != null) {
            btnSave.updateScale(scaleFactor);
        }
        
        // 4. Update Spacers (PENTING: Memaksa layout menghitung ulang ukuran spacer)
        settingsPanel.revalidate();
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
        settingsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(); int h = getHeight();
                int arc = (int)(60 * scaleFactor);
                float stroke = Math.max(2f, 6f * scaleFactor);

                g2.setColor(COL_BOARD_BG); 
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                
                g2.setColor(COL_BOARD_BORDER); 
                g2.setStroke(new BasicStroke(stroke)); 
                int offset = (int)(stroke/2);
                g2.drawRoundRect(offset, offset, w-(offset*2), h-(offset*2), arc, arc);
                g2.dispose();
            }
        };
        
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setOpaque(false);
        
        // 1. TITLE
        titleLabel = new JLabel("PENGATURAN");
        titleLabel.setForeground(COL_BOARD_BORDER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(titleLabel);
        
        // [FIX] Ganti Glue dengan ResponsiveSpacer (Jarak Title ke Checkbox: 30px)
        settingsPanel.add(new ResponsiveSpacer(30));

        // 2. OPTIONS
        checkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        checkPanel.setOpaque(false);
        checkPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        chkFullScreen = createCustomCheckbox(" Layar Penuh");
        chkFullScreen.addActionListener(e -> toggleFullScreen(chkFullScreen.isSelected()));
        checkPanel.add(chkFullScreen);

        chkMute = createCustomCheckbox(" Senyapkan Suara");
        chkMute.addActionListener(e -> {
            if (isLoadingUI) return; 
            boolean isMuted = chkMute.isSelected();
            toggleSliders(!isMuted);
            SoundPlayer.getInstance().setMute(isMuted);
            if (!isMuted) {
                SoundPlayer.getInstance().setVolumeBGM(sliderBGM.getValue());
                SoundPlayer.getInstance().setVolumeSFX(sliderSFX.getValue());
            }
        });
        checkPanel.add(chkMute);

        settingsPanel.add(checkPanel);
        
        // [FIX] Jarak Checkbox ke Label BGM: 30px
        settingsPanel.add(new ResponsiveSpacer(30));

        // 3. BGM SLIDER
        lblBGM = createLabel("Musik Latar (BGM)");
        settingsPanel.add(lblBGM);
        
        // [FIX] Jarak Label ke Slider: 10px
        settingsPanel.add(new ResponsiveSpacer(10));
        
        sliderBGM = createNatureSlider();
        sliderBGM.addChangeListener(e -> {
            if (!isLoadingUI && !chkMute.isSelected()) {
                SoundPlayer.getInstance().setVolumeBGM(sliderBGM.getValue());
            }
        });
        settingsPanel.add(sliderBGM);
        
        // [FIX] Jarak Slider BGM ke Label SFX: 20px
        settingsPanel.add(new ResponsiveSpacer(20));

        // 4. SFX SLIDER
        lblSFX = createLabel("Efek Suara (SFX)");
        settingsPanel.add(lblSFX);
        
        // [FIX] Jarak Label ke Slider: 10px
        settingsPanel.add(new ResponsiveSpacer(10));
        
        sliderSFX = createNatureSlider();
        sliderSFX.addChangeListener(e -> {
            if (!isLoadingUI && !chkMute.isSelected()) {
                SoundPlayer.getInstance().setVolumeSFX(sliderSFX.getValue());
            }
        });
        settingsPanel.add(sliderSFX);
        
        // [FIX] Jarak Slider SFX ke Tombol Simpan: 40px
        settingsPanel.add(new ResponsiveSpacer(40));

        // 5. SAVE BUTTON
        btnSave = new GameButton("SIMPAN", new Color(46, 204, 113), new Color(39, 174, 96));
        btnSave.addActionListener(e -> saveSettings());
        settingsPanel.add(btnSave);

        wrapper.add(settingsPanel);
        add(wrapper, BorderLayout.CENTER);
    }
    
    // --- CLASS BARU UNTUK MEMPERBAIKI JARAK (SPACING) ---
    // Spacer ini akan otomatis menghitung tingginya berdasarkan scaleFactor
    class ResponsiveSpacer extends JComponent {
        private final int baseHeight;
        
        public ResponsiveSpacer(int height) {
            this.baseHeight = height;
            // Set transparan
            setOpaque(false);
        }
        
        @Override
        public Dimension getPreferredSize() {
            // Hitung tinggi berdasarkan skala saat ini
            int h = (int)(baseHeight * scaleFactor);
            // Lebar 1 pixel (dummy), tinggi sesuai skala
            return new Dimension(1, h);
        }
        
        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
        
        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            isLoadingUI = true; 

            // Sync Fullscreen
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if(frame != null && chkFullScreen != null) {
                chkFullScreen.setSelected(frame.isUndecorated());
            }

            // Sync Sound
            boolean currentMute = SoundPlayer.getInstance().isMuted();
            chkMute.setSelected(currentMute);
            toggleSliders(!currentMute); 

            sliderBGM.setValue(SoundPlayer.getInstance().getVolumeBGM());
            sliderSFX.setValue(SoundPlayer.getInstance().getVolumeSFX());
            
            isLoadingUI = false;
            
            SwingUtilities.invokeLater(() -> {
                calculateScaleFactor();
                updateResponsiveLayout();
                repaint();
            });
        }
    }
    
    // ... (Sisa method toggleFullScreen, saveSettings, playSound, toggleSliders SAMA) ...
    private void toggleFullScreen(boolean enableFull) {
        if (isLoadingUI) return;
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;
        SwingUtilities.invokeLater(() -> {
            frame.dispose();
            frame.setUndecorated(enableFull);
            if (enableFull) frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            else { frame.setExtendedState(JFrame.NORMAL); frame.setSize(1024, 768); frame.setLocationRelativeTo(null); }
            frame.setVisible(true); frame.revalidate(); frame.repaint();
        });
    }

    private void saveSettings() {
        int bgmVol = sliderBGM.getValue();
        int sfxVol = sliderSFX.getValue();
        UserModel user = GameState.getCurrentUser();
        if (user != null) {
            user.setBgmVolume(bgmVol); user.setSfxVolume(sfxVol);
            try { userRepo.updateVolume(user.getId(), bgmVol, sfxVol); } catch (Exception e) {}
        }
        playSound("click");
        ScreenManager.getInstance().showScreen("MAIN_MENU");
    }

    private void playSound(String name) {
        try { SoundPlayer.getInstance().playSFX(name + ".wav"); } catch (Exception ignored) {}
    }
    
    private void toggleSliders(boolean enabled) {
        sliderBGM.setEnabled(enabled); sliderSFX.setEnabled(enabled);
    }
    
    // ... (UI Helper Methods SAMA SEPERTI SEBELUMNYA) ...
    private JCheckBox createCustomCheckbox(String text) {
        JCheckBox chk = new JCheckBox(text);
        chk.setForeground(COL_TEXT); chk.setOpaque(false); chk.setFocusPainted(false); chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chk.setIcon(new ResponsiveCheckIcon(false)); chk.setSelectedIcon(new ResponsiveCheckIcon(true));
        return chk;
    }
    
    class ResponsiveCheckIcon implements Icon {
        private final boolean selected;
        public ResponsiveCheckIcon(boolean selected) { this.selected = selected; }
        @Override public int getIconWidth() { return (int)(24 * scaleFactor); }
        @Override public int getIconHeight() { return (int)(24 * scaleFactor); }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = (int)(24 * scaleFactor); int arc = (int)(8 * scaleFactor); float stroke = Math.max(1.5f, 2f * scaleFactor);
            g2.setColor(new Color(0,0,0,100)); g2.fillRoundRect(x, y, size, size, arc, arc);
            g2.setColor(COL_BOARD_BORDER); g2.setStroke(new BasicStroke(stroke)); g2.drawRoundRect(x, y, size, size, arc, arc);
            if (selected) {
                g2.setColor(Color.GREEN); g2.setStroke(new BasicStroke(Math.max(2f, 3f * scaleFactor)));
                g2.drawLine(x+(int)(5*scaleFactor), y+(int)(12*scaleFactor), x+(int)(10*scaleFactor), y+(int)(18*scaleFactor)); 
                g2.drawLine(x+(int)(10*scaleFactor), y+(int)(18*scaleFactor), x+(int)(20*scaleFactor), y+(int)(6*scaleFactor));
            }
        }
    }
    
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text); lbl.setForeground(new Color(230, 230, 230)); lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JSlider createNatureSlider() {
        JSlider slider = new JSlider(0, 100, 50); slider.setOpaque(false); slider.setFocusable(false); slider.setCursor(new Cursor(Cursor.HAND_CURSOR));
        slider.setUI(new BasicSliderUI(slider) {
            @Override public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = c.getWidth(); int h = c.getHeight(); int val = slider.getValue();
                int arc = (int)(20 * scaleFactor); int trackMargin = (int)(5 * scaleFactor);
                g2.setColor(COL_SLIDER_TRACK); g2.fillRoundRect(0, trackMargin, w, h-(trackMargin*2), arc, arc);
                if (slider.isEnabled()) g2.setColor(COL_SLIDER_FILL); else g2.setColor(Color.GRAY);
                int fillWidth = (int) (w * (val / 100.0));
                if (fillWidth > 0) g2.fillRoundRect(0, trackMargin, fillWidth, h-(trackMargin*2), arc, arc);
                g2.setColor(new Color(255, 255, 255, 50)); g2.setStroke(new BasicStroke(1f)); g2.drawRoundRect(0, trackMargin, w - 1, h - (trackMargin*2) - 1, arc, arc);
                String text = val + "%"; g2.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(16 * scaleFactor)));
                FontMetrics fm = g2.getFontMetrics(); int txtW = fm.stringWidth(text); int textY = (h / 2) + (fm.getAscent() / 3);
                g2.setColor(new Color(0,0,0,150)); g2.drawString(text, (w - txtW)/2 + 2, textY + 2);
                g2.setColor(Color.WHITE); g2.drawString(text, (w - txtW)/2, textY);
            }
        });
        MouseAdapter ma = new MouseAdapter() {
            private void move(MouseEvent e) {
                if (!slider.isEnabled()) return; JSlider s = (JSlider) e.getSource();
                int val = (int) Math.round(((double) e.getX() / s.getWidth()) * 100); s.setValue(Math.max(0, Math.min(100, val)));
            }
            @Override public void mousePressed(MouseEvent e) { move(e); } @Override public void mouseDragged(MouseEvent e) { move(e); }
        };
        slider.addMouseListener(ma); slider.addMouseMotionListener(ma);
        return slider;
    }

    @Override
    protected void paintComponent(Graphics g) {
        calculateScaleFactor();
        if (Math.abs(scaleFactor - lastScaleFactor) > 0.001f) {
            lastScaleFactor = scaleFactor;
            SwingUtilities.invokeLater(this::updateResponsiveLayout);
        }
        super.paintComponent(g);
        if (bgImage != null) g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        else { g.setColor(new Color(30, 100, 50)); g.fillRect(0, 0, getWidth(), getHeight()); }
        g.setColor(new Color(0,0,0,50)); g.fillRect(0,0,getWidth(),getHeight());
    }

    class GameButton extends JButton {
        private Color cTop, cBot; private boolean hover;
        public GameButton(String text, Color top, Color bot) {
            super(text); this.cTop = top; this.cBot = bot; setForeground(Color.WHITE); setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR)); setAlignmentX(Component.CENTER_ALIGNMENT);
            addMouseListener(new MouseAdapter() { public void mouseEntered(MouseEvent e) { hover = true; repaint(); } public void mouseExited(MouseEvent e) { hover = false; repaint(); } });
        }
        public void updateScale(float s) {
            int w = (int)(300 * s); int h = (int)(60 * s); Dimension d = new Dimension(w, h);
            setPreferredSize(d); setMaximumSize(d); setFont(new Font("Comic Sans MS", Font.BOLD, (int)(20 * s)));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight(); int offset = (int)(6 * scaleFactor); int arc = (int)(20 * scaleFactor);
            g2.setColor(new Color(0,0,0,60)); g2.fillRoundRect((int)(2*scaleFactor), (int)(8*scaleFactor), w-(int)(4*scaleFactor), h-(int)(8*scaleFactor), arc, arc);
            g2.setColor(cBot.darker()); g2.fillRoundRect(0, offset, w, h-offset, arc, arc);
            int yPos = (getModel().isPressed()) ? (int)(4*scaleFactor) : 0; Color c1 = hover ? cTop.brighter() : cTop; Color c2 = hover ? cTop : cTop.darker();
            g2.setPaint(new GradientPaint(0, yPos, c1, 0, h, c2)); g2.fillRoundRect(0, yPos, w, h-offset, arc, arc);
            g2.setColor(new Color(255,255,255,50)); g2.drawRoundRect(0, yPos, w-1, h-offset-1, arc, arc);
            FontMetrics fm = g2.getFontMetrics(); int x = (w - fm.stringWidth(getText())) / 2; int y = (h - offset - fm.getHeight()) / 2 + fm.getAscent() + yPos;
            g2.setColor(new Color(0,0,0,50)); g2.drawString(getText(), x+1, y+2); g2.setColor(Color.WHITE); g2.drawString(getText(), x, y); g2.dispose();
        }
    }
}