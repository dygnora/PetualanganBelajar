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

    private JPanel mainContainer;
    private CardLayout cardLayout;
    private JCheckBox chkMute, chkFullScreen;
    private JSlider sliderBGM, sliderSFX;
    private UserRepository userRepo;

    private static final Color COL_BG = new Color(255, 250, 240);
    private static final Color COL_PRIMARY = new Color(70, 130, 180);
    private static final Color COL_TRACK = new Color(210, 210, 210);
    private static final Color COL_TEXT = new Color(60, 60, 60);

    public PauseMenuDialog(JFrame parent) {
        super(parent, "Pause", true);
        this.userRepo = new UserRepository();
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 40, 40);
                g2.setColor(COL_BG);
                g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 40, 40);
                g2.setColor(COL_PRIMARY);
                g2.setStroke(new BasicStroke(6));
                g2.drawRoundRect(3, 3, getWidth()-11, getHeight()-11, 40, 40);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setOpaque(false);

        mainContainer.add(createMainMenu(), "MENU");
        mainContainer.add(createSettings(), "SETTINGS");

        backgroundPanel.add(mainContainer, BorderLayout.CENTER);
        setContentPane(backgroundPanel);
    }

    private JPanel createMainMenu() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel title = new JLabel("PAUSE");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        title.setForeground(COL_PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JoyBtn btnResume = new JoyBtn("LANJUT MAIN", new Color(129, 199, 132));
        JoyBtn btnSet = new JoyBtn("PENGATURAN", new Color(255, 183, 77));
        JoyBtn btnExit = new JoyBtn("KELUAR", new Color(255, 100, 100));

        btnResume.addActionListener(e -> dispose());
        btnSet.addActionListener(e -> { loadData(); cardLayout.show(mainContainer, "SETTINGS"); });
        btnExit.addActionListener(e -> { dispose(); ScreenManager.getInstance().showScreen("MODULE_SELECT"); });

        p.add(Box.createVerticalGlue());
        p.add(title);
        p.add(Box.createVerticalStrut(25));
        p.add(btnResume);
        p.add(Box.createVerticalStrut(15));
        p.add(btnSet);
        p.add(Box.createVerticalStrut(15));
        p.add(btnExit);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel createSettings() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel title = new JLabel("PENGATURAN");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        title.setForeground(COL_PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        chkFullScreen = new JCheckBox("Layar Penuh");
        chkMute = new JCheckBox("Suara Mati");
        setupCheck(chkFullScreen); setupCheck(chkMute);
        
        JPanel checks = new JPanel(new FlowLayout());
        checks.setOpaque(false);
        checks.add(chkFullScreen); checks.add(chkMute);

        sliderBGM = createSmoothSlider();
        sliderSFX = createSmoothSlider();

        JoyBtn btnSave = new JoyBtn("SIMPAN", new Color(129, 199, 132));
        btnSave.addActionListener(e -> { saveSettings(); cardLayout.show(mainContainer, "MENU"); });

        p.add(title);
        p.add(Box.createVerticalStrut(15));
        p.add(checks);
        p.add(createLabel("Musik (BGM)")); p.add(sliderBGM);
        p.add(Box.createVerticalStrut(15));
        p.add(createLabel("Efek Suara")); p.add(sliderSFX);
        p.add(Box.createVerticalStrut(25));
        p.add(btnSave);
        
        chkFullScreen.addActionListener(e -> toggleFS(chkFullScreen.isSelected()));
        chkMute.addActionListener(e -> {
            boolean muted = chkMute.isSelected();
            sliderBGM.setEnabled(!muted);
            sliderSFX.setEnabled(!muted);
        });

        return p;
    }

    private void setupCheck(JCheckBox c) {
        c.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        c.setOpaque(false); c.setForeground(COL_TEXT);
        c.setFocusPainted(false);
        c.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        l.setAlignmentX(CENTER_ALIGNMENT);
        l.setBorder(new EmptyBorder(0,0,5,0));
        return l;
    }

    private JSlider createSmoothSlider() {
        JSlider slider = new JSlider(0, 100);
        slider.setOpaque(false);
        slider.setFocusable(false);
        slider.setPreferredSize(new Dimension(280, 45));
        slider.setMaximumSize(new Dimension(280, 45));
        slider.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // UI Custom untuk membuat slide lebih mulus
        slider.setUI(new BasicSliderUI(slider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(thumbRect.x + 2, thumbRect.y + 2, thumbRect.width - 4, thumbRect.height - 4);
                g2.setColor(slider.isEnabled() ? COL_PRIMARY : Color.GRAY);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(thumbRect.x + 2, thumbRect.y + 2, thumbRect.width - 4, thumbRect.height - 4);
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int trackHeight = 10;
                int y = trackRect.y + (trackRect.height - trackHeight) / 2;
                
                // Background Track
                g2.setColor(COL_TRACK);
                g2.fillRoundRect(trackRect.x, y, trackRect.width, trackHeight, trackHeight, trackHeight);
                
                // Fill Track (Progress)
                int fillWidth = thumbRect.x + (thumbRect.width / 2) - trackRect.x;
                if (slider.isEnabled()) {
                    g2.setColor(COL_PRIMARY);
                    g2.fillRoundRect(trackRect.x, y, fillWidth, trackHeight, trackHeight, trackHeight);
                }
            }
            
            // Mengatur ukuran thumb agar lebih enak di-klik
            @Override
            protected Dimension getThumbSize() {
                return new Dimension(22, 22);
            }
        });

        // Mouse Listener agar Slider bisa langsung "lompat" ke posisi klik (Smooth interaction)
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (slider.isEnabled()) {
                    int value = getValueAt(e.getX());
                    slider.setValue(value);
                }
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (slider.isEnabled()) {
                    int value = getValueAt(e.getX());
                    slider.setValue(value);
                }
            }
            private int getValueAt(int x) {
                double min = slider.getMinimum();
                double max = slider.getMaximum();
                double range = max - min;
                double width = slider.getWidth();
                double relativeX = x;
                return (int) (min + (relativeX / width) * range);
            }
        };
        slider.addMouseListener(ma);
        slider.addMouseMotionListener(ma);

        return slider;
    }

    private void loadData() {
        JFrame f = (JFrame) SwingUtilities.getWindowAncestor(this);
        if(f != null) chkFullScreen.setSelected(f.isUndecorated());
        UserModel u = GameState.getCurrentUser();
        if(u != null) { 
            sliderBGM.setValue(u.getBgmVolume()); 
            sliderSFX.setValue(u.getSfxVolume()); 
        }
    }

    private void saveSettings() {
        UserModel u = GameState.getCurrentUser();
        if(u != null) {
            u.setBgmVolume(sliderBGM.getValue()); 
            u.setSfxVolume(sliderSFX.getValue());
            SoundPlayer.getInstance().setMute(chkMute.isSelected());
            // Simpan ke DB di background thread agar tidak lag
            new Thread(() -> userRepo.updateVolume(u.getId(), u.getBgmVolume(), u.getSfxVolume())).start();
        }
    }

    private void toggleFS(boolean b) {
        JFrame f = (JFrame) SwingUtilities.getWindowAncestor(this);
        if(f == null) return;
        dispose(); 
        f.dispose();
        f.setUndecorated(b);
        if(b) f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        else { 
            f.setExtendedState(JFrame.NORMAL); 
            f.setSize(1024, 768); 
            f.setLocationRelativeTo(null); 
        }
        f.setVisible(true);
    }

    class JoyBtn extends JButton {
        private Color c; private boolean h;
        public JoyBtn(String t, Color color) {
            super(t); this.c = color;
            setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            setForeground(Color.WHITE); setContentAreaFilled(false);
            setBorderPainted(false); setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(CENTER_ALIGNMENT);
            setPreferredSize(new Dimension(220, 55));
            setMaximumSize(new Dimension(220, 55));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { h = true; repaint(); }
                public void mouseExited(MouseEvent e) { h = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int m = h ? 0 : 3;
            // Shadow
            g2.setColor(c.darker());
            g2.fillRoundRect(m, m+4, getWidth()-(m*2), getHeight()-(m*2)-4, 25, 25);
            // Main Button
            g2.setColor(h ? c.brighter() : c);
            g2.fillRoundRect(m, m, getWidth()-(m*2), getHeight()-(m*2)-4, 25, 25);
            
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(Color.WHITE); 
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}