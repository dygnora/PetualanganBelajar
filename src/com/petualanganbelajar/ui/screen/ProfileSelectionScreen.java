package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.UserRepository;
import com.petualanganbelajar.ui.component.UserProfileCard;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.List;

public class ProfileSelectionScreen extends JPanel {

    private Image bgImage;
    private Image titleImage;
    private Image imgBtnPrev, imgBtnNext; 
    
    private final UserRepository userRepo;
    private List<UserModel> userList;
    
    // --- STATE INFINITE SCROLL ---
    private int selectedIndex = 0; 
    private float visualIndex = 0; 
    private Timer animTimer;

    // Komponen UI
    private JPanel headerPanel;
    private JPanel footerPanel;
    private JPanel centerPanel;
    private JLabel lblTitle;
    private GameButton btnBack;
    
    // Tombol Navigasi
    private NavButton navPrev, navNext;
    private CarouselPanel carouselPanel;

    private final float BASE_W = 1920f;
    private final float BASE_H = 1080f;
    private float scaleFactor = 1.0f;

    public ProfileSelectionScreen() {
        this.userRepo = new UserRepository();
        setLayout(new BorderLayout());
        loadAssets();
        refreshData();
        initUI();
        setupAnimation();
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateScaleFactor();
                updateResponsiveLayout();
            }
        });
    }

    private void setupAnimation() {
        animTimer = new Timer(16, e -> {
            if (Math.abs(visualIndex - selectedIndex) > 0.005f) {
                visualIndex += (selectedIndex - visualIndex) * 0.15f;
                carouselPanel.repaint();
            } else {
                visualIndex = selectedIndex;
                carouselPanel.repaint();
            }
        });
        animTimer.start();
    }

    private void calculateScaleFactor() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        float sW = (float) getWidth() / BASE_W;
        float sH = (float) getHeight() / BASE_H;
        this.scaleFactor = Math.min(sW, sH);
        if (this.scaleFactor < 0.5f) this.scaleFactor = 0.5f;
    }

    private void updateResponsiveLayout() {
        if (headerPanel != null) {
            headerPanel.setPreferredSize(new Dimension((int)(900*scaleFactor), (int)(300*scaleFactor))); 
            headerPanel.setBorder(new EmptyBorder((int)(-30*scaleFactor), 0, 0, 0));
        }
        if (lblTitle != null) {
            if (titleImage != null) {
                Image scaled = titleImage.getScaledInstance((int)(800*scaleFactor), (int)(650*scaleFactor), Image.SCALE_SMOOTH);
                lblTitle.setIcon(new ImageIcon(scaled));
                lblTitle.setText("");
            } else {
                lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(84 * scaleFactor)));
            }
        }

        if (footerPanel != null) {
            footerPanel.setPreferredSize(new Dimension((int)(800*scaleFactor), (int)(120*scaleFactor)));
            footerPanel.setBorder(new EmptyBorder(0, 0, (int)(30*scaleFactor), 0));
        }
        if (btnBack != null) btnBack.updateScale(scaleFactor);

        if (centerPanel != null) {
            centerPanel.setBorder(new EmptyBorder(0, (int)(50*scaleFactor), 0, (int)(50*scaleFactor)));
        }
        
        int btnSize = (int)(120 * scaleFactor);
        if (navPrev != null) navPrev.setPreferredSize(new Dimension(btnSize, btnSize));
        if (navNext != null) navNext.setPreferredSize(new Dimension(btnSize, btnSize));

        if (carouselPanel != null) carouselPanel.repaint();

        revalidate();
        repaint();
    }

    private void loadAssets() {
        try {
            URL bgUrl = getClass().getResource("/images/bg_profile_select.png");
            if (bgUrl != null) bgImage = ImageIO.read(bgUrl);
            URL titleUrl = getClass().getResource("/images/title_continue.png");
            if (titleUrl != null) titleImage = ImageIO.read(titleUrl);
            URL prevUrl = getClass().getResource("/images/btn_prev.png");
            if (prevUrl != null) imgBtnPrev = ImageIO.read(prevUrl);
            URL nextUrl = getClass().getResource("/images/btn_next.png");
            if (nextUrl != null) imgBtnNext = ImageIO.read(nextUrl);
        } catch (Exception ignored) {}
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            refreshData();
            SwingUtilities.invokeLater(() -> {
                calculateScaleFactor();
                updateResponsiveLayout();
            });
        }
    }

    private void refreshData() {
        userList = userRepo.getAllActiveUsers();
        if (userList == null || userList.isEmpty()) {
            selectedIndex = 0;
            visualIndex = 0;
        } else {
            selectedIndex = 0;
            visualIndex = 0;
        }
        if(carouselPanel != null) carouselPanel.repaint();
    }

    private void initUI() {
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        lblTitle = new JLabel();
        if (titleImage == null) {
            lblTitle.setText("SIAPA KAMU?");
            lblTitle.setForeground(Color.WHITE);
        }
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        
        navPrev = new NavButton(imgBtnPrev, "<");
        navPrev.addActionListener(e -> {
            playSound("click");
            if (userList != null && !userList.isEmpty()) selectedIndex--;
        });
        
        navNext = new NavButton(imgBtnNext, ">");
        navNext.addActionListener(e -> {
            playSound("click");
            if (userList != null && !userList.isEmpty()) selectedIndex++;
        });

        carouselPanel = new CarouselPanel();
        
        JPanel leftWrapper = new JPanel(new GridBagLayout());
        leftWrapper.setOpaque(false);
        leftWrapper.add(navPrev);
        
        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.add(navNext);

        centerPanel.add(leftWrapper, BorderLayout.WEST);
        centerPanel.add(carouselPanel, BorderLayout.CENTER);
        centerPanel.add(rightWrapper, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);

        footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        btnBack = new GameButton("KEMBALI", new Color(220, 53, 69));
        btnBack.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("MAIN_MENU");
        });
        footerPanel.add(btnBack);
        add(footerPanel, BorderLayout.SOUTH);
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
            g.setColor(new Color(100, 200, 100));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // =========================================================================
    // INFINITE CAROUSEL PANEL
    // =========================================================================
    private class CarouselPanel extends JPanel {
        
        private boolean isTrashHovered = false;
        private final int CARD_SPACING = 380; 

        public CarouselPanel() {
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR)); 

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (userList == null || userList.isEmpty()) return;
                    handleMouseClick(e.getPoint());
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (userList == null || userList.isEmpty()) return;
                    handleMouseMove(e.getPoint());
                }
            });
        }

        private UserModel getUserAt(int index) {
            if (userList == null || userList.isEmpty()) return null;
            int realIndex = Math.floorMod(index, userList.size());
            return userList.get(realIndex);
        }

        private void handleMouseClick(Point p) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            
            UserModel centerUser = getUserAt(selectedIndex);
            if (centerUser == null) return;

            UserProfileCard dummy = new UserProfileCard(centerUser);
            dummy.setVisualProperties(scaleFactor, 1.15f, 1.0f);
            
            Rectangle cardBounds = dummy.getCardBounds(cx, cy);
            Rectangle trashBounds = dummy.getTrashBounds(cx, cy);

            if (cardBounds.contains(p)) {
                if (trashBounds.contains(p)) {
                    playSound("click");
                    deleteAction();
                } else {
                    playSound("click");
                    loginUser();
                }
            }
        }

        private void handleMouseMove(Point p) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            
            UserModel centerUser = getUserAt(selectedIndex);
            if (centerUser == null) return;

            UserProfileCard dummy = new UserProfileCard(centerUser);
            dummy.setVisualProperties(scaleFactor, 1.15f, 1.0f);
            Rectangle centerBounds = dummy.getCardBounds(cx, cy);
            Rectangle trashBounds = dummy.getTrashBounds(cx, cy);

            boolean inCard = centerBounds.contains(p);
            boolean inTrash = trashBounds.contains(p);
            boolean nowTrashHover = inCard && inTrash;
            
            if (nowTrashHover != isTrashHovered) {
                isTrashHovered = nowTrashHover;
                repaint();
            }
        }

        private void deleteAction() {
            UserModel u = getUserAt(selectedIndex);
            if (u == null) return;

            AdventureConfirmDialog dialog = new AdventureConfirmDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Hapus Profil?",
                "Kamu yakin mau menghapus " + u.getName() + "?",
                scaleFactor 
            );
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                userRepo.deleteUser(u.getId());
                refreshData(); 
            }
        }
        
        private void loginUser() {
            UserModel u = getUserAt(selectedIndex);
            if (u != null) {
                GameState.setCurrentUser(u);
                ScreenManager.getInstance().showScreen("MODULE_SELECT");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (userList == null || userList.isEmpty()) {
                drawEmptyState((Graphics2D) g);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2;
            int spacing = (int)(CARD_SPACING * scaleFactor);

            int centerIdx = Math.round(visualIndex);
            
            for (int i = centerIdx - 2; i <= centerIdx + 2; i++) {
                float dist = i - visualIndex; 
                float absDist = Math.abs(dist);

                int x = cx + (int)(dist * spacing);
                int y = cy;

                float scale = Math.max(0.7f, 1.15f - (absDist * 0.3f));
                float alpha = Math.max(0.4f, 1.0f - (absDist * 0.5f));
                
                if (scale < 0.5f) scale = 0.5f;
                if (alpha < 0.0f) alpha = 0.0f;

                boolean isCenter = (Math.round(visualIndex) == i) && (absDist < 0.1f);

                UserModel user = getUserAt(i);
                if (user != null) {
                    UserProfileCard card = new UserProfileCard(user);
                    card.setVisualProperties(scaleFactor, scale, alpha);
                    card.paint(g2, x, y, (isCenter && isTrashHovered));
                }
            }
            g2.dispose();
        }

        private void drawEmptyState(Graphics2D g2) {
            String msg = "Belum ada teman bermain.";
            String sub = "Buat profil baru di menu utama ya!";
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(48*scaleFactor)));
            FontMetrics fm = g2.getFontMetrics();
            int w = getWidth(); int h = getHeight();
            g2.drawString(msg, (w - fm.stringWidth(msg))/2, h/2 - (int)(30*scaleFactor));
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, (int)(32*scaleFactor)));
            fm = g2.getFontMetrics();
            g2.drawString(sub, (w - fm.stringWidth(sub))/2, h/2 + (int)(40*scaleFactor));
        }
    }

    // ============================================================
    // NAV BUTTON (PREV/NEXT)
    // ============================================================
    private class NavButton extends JButton {
        private final Image iconImg;
        private boolean hover;

        public NavButton(Image img, String fallbackText) {
            this.iconImg = img;
            setText(img == null ? fallbackText : "");
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 40));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            int w = getWidth(); int h = getHeight();
            float zoom = hover ? 1.1f : 1.0f;
            int dw = (int)(w * zoom); int dh = (int)(h * zoom);
            int dx = (w - dw) / 2; int dy = (h - dh) / 2;

            if (iconImg != null) {
                g2.drawImage(iconImg, dx, dy, dw, dh, null);
            } else {
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillOval(5, 5, w-10, h-10);
                g2.setColor(hover ? Color.YELLOW : Color.WHITE);
                super.paintComponent(g);
            }
            g2.dispose();
        }
    }

    // ============================================================
    // [PERBAIKAN] CUSTOM CONFIRM DIALOG - FONT LEBIH BESAR
    // ============================================================
    class AdventureConfirmDialog extends JDialog {
        private boolean confirmed = false;

        public AdventureConfirmDialog(Frame parent, String title, String message, float scale) {
            super(parent, true);
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(); int h = getHeight();
                    int arc = (int)(30 * scale);
                    int shadow = (int)(5 * scale);

                    // Shadow
                    g2.setColor(new Color(0,0,0,80));
                    g2.fillRoundRect(shadow, shadow, w-(shadow*2), h-(shadow*2), arc, arc);
                    
                    // Board Background
                    g2.setColor(new Color(255, 248, 225));
                    g2.fillRoundRect(0, 0, w-shadow, h-shadow, arc, arc);
                    
                    // Border Merah
                    g2.setColor(new Color(192, 57, 43));
                    g2.setStroke(new BasicStroke((int)(5*scale)));
                    int pad = (int)(2*scale);
                    g2.drawRoundRect(pad, pad, w-shadow-(pad*2)-1, h-shadow-(pad*2)-1, arc, arc);
                    g2.dispose();
                }
            };
            
            panel.setLayout(new BorderLayout());
            panel.setBorder(new EmptyBorder((int)(25*scale), (int)(20*scale), (int)(20*scale), (int)(20*scale)));
            
            // 1. JUDUL (DIPERBESAR: 32 -> 42)
            JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
            lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(42*scale))); 
            lblTitle.setForeground(new Color(192, 57, 43));
            panel.add(lblTitle, BorderLayout.NORTH);
            
            // 2. PESAN (DIPERBESAR: 24 -> 32)
            JTextPane txtMsg = new JTextPane();
            txtMsg.setText(message);
            txtMsg.setFont(new Font("Comic Sans MS", Font.PLAIN, (int)(32*scale))); 
            txtMsg.setForeground(new Color(93, 64, 55));
            txtMsg.setOpaque(false);
            txtMsg.setEditable(false);
            txtMsg.setFocusable(false);
            
            // Logika Rata Tengah TextPane
            StyledDocument doc = txtMsg.getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), center, false);
            
            txtMsg.setBorder(new EmptyBorder((int)(15*scale), (int)(10*scale), (int)(15*scale), (int)(10*scale)));
            panel.add(txtMsg, BorderLayout.CENTER);
            
            // Tombol Panel
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, (int)(20*scale), 0));
            btnPanel.setOpaque(false);
            
            GameButton btnYes = new GameButton("YA", new Color(192, 57, 43));
            btnYes.updateScale(scale);
            // Ukuran tombol di dalam dialog sedikit diperbesar menyesuaikan font
            btnYes.setPreferredSize(new Dimension((int)(200*scale), (int)(60*scale))); 
            btnYes.addActionListener(e -> { confirmed = true; dispose(); });
            
            GameButton btnNo = new GameButton("TIDAK", new Color(46, 204, 113));
            btnNo.updateScale(scale);
            btnNo.setPreferredSize(new Dimension((int)(200*scale), (int)(60*scale))); 
            btnNo.addActionListener(e -> { confirmed = false; dispose(); });
            
            btnPanel.add(btnYes);
            btnPanel.add(btnNo);
            panel.add(btnPanel, BorderLayout.SOUTH);
            
            setContentPane(panel);
            // Ukuran Dialog diperlebar sedikit lagi agar muat font besar (550 -> 600)
            setSize((int)(600*scale), (int)(350*scale)); 
            setLocationRelativeTo(parent);
        }
        
        public boolean isConfirmed() { return confirmed; }
    }

    private class GameButton extends JButton {
        private final Color color;
        private boolean hover;
        private float scale = 1.0f;
        public GameButton(String text, Color color) {
            super(text);
            this.color = color;
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR)); setForeground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        public void updateScale(float s) {
            this.scale = s;
            setFont(new Font("Comic Sans MS", Font.BOLD, (int)(24*s))); 
            setPreferredSize(new Dimension((int)(260*s), (int)(70*s))); 
            revalidate(); repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            int yOffset = getModel().isPressed() ? (int)(3*scale) : 0;
            int arc = (int)(50 * scale);
            g2.setColor(new Color(0,0,0,50));
            g2.fillRoundRect((int)(5*scale), (int)(8*scale), w-(int)(10*scale), h-(int)(10*scale), arc, arc);
            g2.setColor(color.darker());
            g2.fillRoundRect(0, yOffset+(int)(5*scale), w, h-(int)(5*scale)-yOffset, arc, arc);
            Color c1 = hover ? color.brighter() : color;
            g2.setPaint(new GradientPaint(0, yOffset, c1, 0, h, color));
            g2.fillRoundRect(0, yOffset, w, h-(int)(8*scale), arc, arc);
            g2.setPaint(new GradientPaint(0, yOffset, new Color(255,255,255,80), 0, h/2, new Color(255,255,255,0)));
            g2.fillRoundRect((int)(5*scale), yOffset+(int)(2*scale), w-(int)(10*scale), (h/2)-(int)(5*scale), (int)(40*scale), (int)(40*scale));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = (h - (int)(8*scale) - fm.getHeight()) / 2 + fm.getAscent() + yOffset;
            g2.setColor(new Color(0,0,0,50));
            g2.drawString(getText(), tx+(int)(1*scale), ty+(int)(2*scale));
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), tx, ty);
            g2.dispose();
        }
    }
}