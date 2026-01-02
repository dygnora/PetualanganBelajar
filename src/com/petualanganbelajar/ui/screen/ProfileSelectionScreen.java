package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.UserRepository;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.net.URL;
import java.util.List;

public class ProfileSelectionScreen extends JPanel {

    private Image bgImage;
    private Image titleImage;
    
    private final UserRepository userRepo;
    private List<UserModel> userList;
    private int selectedIndex = 0;

    // Komponen
    private CarouselPanel carouselPanel;

    // Warna-warni Card (Vibrant & Ceria)
    private static final Color[] CARD_COLORS = {
        new Color(66, 165, 245), // Biru Cerah
        new Color(255, 167, 38), // Orange Cerah
        new Color(102, 187, 106), // Hijau Cerah
        new Color(171, 71, 188)   // Ungu Cerah
    };

    public ProfileSelectionScreen() {
        this.userRepo = new UserRepository();
        setLayout(new BorderLayout());
        loadAssets();
        
        // Load data awal
        refreshData();

        initUI();
    }

    private void loadAssets() {
        try {
            if (getClass().getResource("/images/bg_profile_select.png") != null) {
                bgImage = ImageIO.read(getClass().getResource("/images/bg_profile_select.png"));
            }
            if (getClass().getResource("/images/title_continue.png") != null) {
                titleImage = ImageIO.read(getClass().getResource("/images/title_continue.png"));
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            refreshData();
        }
    }

    private void refreshData() {
        userList = userRepo.getAllActiveUsers();
        
        if (userList == null || userList.isEmpty()) {
            selectedIndex = -1;
        } else {
            if (selectedIndex >= userList.size() || selectedIndex < 0) {
                selectedIndex = 0;
            }
        }
        
        if(carouselPanel != null) carouselPanel.repaint();
    }

    private void initUI() {
        // --- 1. HEADER ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(-50, 0, 0, 0));
        header.setPreferredSize(new Dimension(850, 320));

        JLabel lblTitle = new JLabel();
        if (titleImage != null) {
            lblTitle.setIcon(new ImageIcon(titleImage.getScaledInstance(600, 500, Image.SCALE_SMOOTH)));
        } else {
            lblTitle.setText("SIAPA KAMU?");
            lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
            lblTitle.setForeground(Color.WHITE);
        }
        header.add(lblTitle);
        add(header, BorderLayout.NORTH);

        // --- 2. CAROUSEL (INTI LAYAR) ---
        carouselPanel = new CarouselPanel();
        add(carouselPanel, BorderLayout.CENTER);

        // --- 3. FOOTER ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 0, 20, 0));
        footer.setPreferredSize(new Dimension(800, 90));
        
        // Tombol Kembali
        GameButton btnBack = new GameButton("KEMBALI", new Color(220, 53, 69));
        btnBack.addActionListener(e -> {
            playSound("click");
            ScreenManager.getInstance().showScreen("MAIN_MENU");
        });
        
        footer.add(btnBack);
        add(footer, BorderLayout.SOUTH);
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
    // INNER CLASS: CAROUSEL PANEL
    // =========================================================================
    private class CarouselPanel extends JPanel {
        
        private Rectangle centerCardBounds = new Rectangle();
        private Rectangle leftCardBounds = new Rectangle();
        private Rectangle rightCardBounds = new Rectangle();
        
        private boolean isTrashHovered = false;

        public CarouselPanel() {
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR)); 

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (userList == null || userList.isEmpty()) return;
                    
                    Point p = e.getPoint();

                    // 1. KLIK CARD TENGAH
                    if (centerCardBounds.contains(p)) {
                        Rectangle realTrash = calculateTrashBounds();

                        if (realTrash.contains(p)) {
                            // HAPUS USER
                            playSound("click");
                            deleteAction();
                        } else {
                            // LOGIN USER
                            playSound("click");
                            if (selectedIndex >= 0 && selectedIndex < userList.size()) {
                                UserModel u = userList.get(selectedIndex);
                                GameState.setCurrentUser(u);
                                ScreenManager.getInstance().showScreen("MODULE_SELECT");
                            }
                        }
                    }
                    // 2. NAVIGASI KIRI/KANAN
                    else if (leftCardBounds.contains(p) || p.x < getWidth() / 4) {
                        playSound("click");
                        rotateLeft();
                    }
                    else if (rightCardBounds.contains(p) || p.x > getWidth() * 3 / 4) {
                        playSound("click");
                        rotateRight();
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (userList == null || userList.isEmpty()) return;
                    Point p = e.getPoint();
                    
                    boolean prevHover = isTrashHovered;
                    
                    if (centerCardBounds.contains(p)) {
                        Rectangle realTrash = calculateTrashBounds();
                        isTrashHovered = realTrash.contains(p);
                    } else {
                        isTrashHovered = false;
                    }
                    
                    if (prevHover != isTrashHovered) repaint();
                }
            });
        }
        
        private Rectangle calculateTrashBounds() {
            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2;

            int baseW = 220;
            int baseH = 280;
            float scale = 1.15f; 

            int dw = (int) (baseW * scale);
            int dh = (int) (baseH * scale);
            int dx = cx - (dw / 2);
            int dy = cy - (dh / 2);

            int trashSize = 32;
            int txPos = dx + dw - trashSize - 15;
            int tyPos = dy + 15;

            return new Rectangle(txPos, tyPos, trashSize, trashSize);
        }
        
        private void rotateLeft() {
            if (userList.isEmpty()) return;
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = userList.size() - 1;
            repaint();
        }
        
        private void rotateRight() {
            if (userList.isEmpty()) return;
            selectedIndex++;
            if (selectedIndex >= userList.size()) selectedIndex = 0;
            repaint();
        }
        
        // --- CUSTOM CONFIRM DIALOG LOGIC ---
        private void deleteAction() {
            if (userList == null || userList.isEmpty()) return;
            
            UserModel u = userList.get(selectedIndex);
            
            // Show Custom Dialog
            AdventureConfirmDialog dialog = new AdventureConfirmDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Hapus Profil?",
                "Kamu yakin mau menghapus " + u.getName() + "?"
            );
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                userRepo.deleteUser(u.getId());
                refreshData(); 
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (userList == null || userList.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawEmptyState(g2, getWidth(), getHeight());
                g2.dispose();
                return;
            }

            if (selectedIndex >= userList.size()) selectedIndex = 0;
            if (selectedIndex < 0) selectedIndex = 0;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int cy = h / 2;
            int cx = w / 2;

            int leftIdx = (selectedIndex - 1 + userList.size()) % userList.size();
            int rightIdx = (selectedIndex + 1) % userList.size();
            boolean singleMode = userList.size() == 1;
            boolean dualMode = userList.size() == 2;

            if (!singleMode) {
                if (!dualMode) {
                    drawCard(g2, userList.get(leftIdx), cx - 240, cy, 0.75f, 0.6f, false, leftCardBounds);
                }
                drawCard(g2, userList.get(rightIdx), cx + 240, cy, 0.75f, 0.6f, false, rightCardBounds);
            }

            drawCard(g2, userList.get(selectedIndex), cx, cy, 1.15f, 1.0f, true, centerCardBounds);

            g2.dispose();
        }

        private void drawCard(Graphics2D g2, UserModel user, int x, int y, float scale, float alpha, boolean isCenter, Rectangle bounds) {
            int baseW = 220; 
            int baseH = 280;
            
            int dw = (int) (baseW * scale);
            int dh = (int) (baseH * scale);
            int dx = x - (dw / 2);
            int dy = y - (dh / 2);

            bounds.setBounds(dx, dy, dw, dh);

            Composite oldComp = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            Color cardColor = CARD_COLORS[user.getId() % CARD_COLORS.length];

            // 1. Sticker Border
            int border = 8;
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(dx - border, dy - border, dw + (border*2), dh + (border*2), 50, 50);

            // 2. Card Body
            g2.setColor(cardColor);
            g2.fillRoundRect(dx, dy, dw, dh, 40, 40);

            // 3. Avatar Circle
            int circleSize = (int) (120 * scale);
            int cxCircle = dx + (dw - circleSize) / 2;
            int cyCircle = dy + (int) (30 * scale);
            
            g2.setColor(Color.WHITE);
            g2.fillOval(cxCircle, cyCircle, circleSize, circleSize);

            // 4. Avatar Image
            try {
                String fName = (user.getAvatar() == null) ? "default.png" : user.getAvatar();
                URL url = getClass().getResource("/images/" + fName);
                if (url != null) {
                    Image img = new ImageIcon(url).getImage();
                    g2.setClip(new Ellipse2D.Float(cxCircle+5, cyCircle+5, circleSize-10, circleSize-10));
                    g2.drawImage(img, cxCircle+5, cyCircle+5, circleSize-10, circleSize-10, null);
                    g2.setClip(null);
                }
            } catch (Exception e) {}

            // 5. Nama User
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(26 * scale)));
            FontMetrics fm = g2.getFontMetrics();
            String name = user.getName();
            if (fm.stringWidth(name) > dw - 20) name = name.substring(0, Math.min(name.length(), 8)) + "..";
            
            int tx = dx + (dw - fm.stringWidth(name)) / 2;
            int ty = dy + dh - (int)(55 * scale);
            
            g2.setColor(new Color(0,0,0,40));
            g2.drawString(name, tx+2, ty+2);
            g2.setColor(Color.WHITE);
            g2.drawString(name, tx, ty);

            // 6. Ikon Sampah
            if (isCenter) {
                int trashBaseSize = 32;
                int currentSize = isTrashHovered ? trashBaseSize + 4 : trashBaseSize;
                
                Rectangle r = calculateTrashBounds();
                int txPos = r.x - (currentSize - trashBaseSize)/2; 
                int tyPos = r.y - (currentSize - trashBaseSize)/2;
                
                g2.setColor(new Color(220, 53, 69)); 
                if (isTrashHovered) g2.setColor(new Color(255, 80, 80)); 
                
                g2.fillOval(txPos, tyPos, currentSize, currentSize);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, isTrashHovered ? 18 : 16));
                fm = g2.getFontMetrics();
                int xText = txPos + (currentSize - fm.stringWidth("x")) / 2;
                int yText = tyPos + ((currentSize - fm.getHeight()) / 2) + fm.getAscent() - 2;
                g2.drawString("x", xText, yText);
            }

            g2.setComposite(oldComp);
        }

        private void drawEmptyState(Graphics2D g2, int w, int h) {
            String msg = "Belum ada teman bermain.";
            String sub = "Buat profil baru di menu utama ya!";
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (w - fm.stringWidth(msg))/2, h/2 - 20);
            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
            fm = g2.getFontMetrics();
            g2.drawString(sub, (w - fm.stringWidth(sub))/2, h/2 + 20);
        }
    }

    // ============================================================
    // NEW INNER CLASS: CUSTOM CONFIRM DIALOG (FIXED CENTERED TEXT)
    // ============================================================
    class AdventureConfirmDialog extends JDialog {
        private boolean confirmed = false;

        public AdventureConfirmDialog(Frame parent, String title, String message) {
            super(parent, true);
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(); int h = getHeight();
                    
                    // Shadow
                    g2.setColor(new Color(0,0,0,80));
                    g2.fillRoundRect(5, 5, w-10, h-10, 30, 30);
                    
                    // Board
                    g2.setColor(new Color(255, 248, 225));
                    g2.fillRoundRect(0, 0, w-5, h-5, 30, 30);
                    
                    // Warning Border
                    g2.setColor(new Color(192, 57, 43));
                    g2.setStroke(new BasicStroke(5));
                    g2.drawRoundRect(2, 2, w-9, h-9, 30, 30);
                    
                    g2.dispose();
                }
            };
            
            panel.setLayout(new BorderLayout());
            panel.setBorder(new EmptyBorder(25, 20, 20, 20));
            
            // Title
            JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
            lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
            lblTitle.setForeground(new Color(192, 57, 43));
            panel.add(lblTitle, BorderLayout.NORTH);
            
            // [FIX] Menggunakan JTextPane agar teks rata tengah sempurna & kursor hilang
            JTextPane txtMsg = new JTextPane();
            txtMsg.setText(message);
            txtMsg.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
            txtMsg.setForeground(new Color(93, 64, 55));
            txtMsg.setOpaque(false);
            txtMsg.setEditable(false);
            txtMsg.setFocusable(false); // HILANGKAN KURSOR
            txtMsg.setHighlighter(null); // HILANGKAN HIGHLIGHT
            txtMsg.setCursor(null); // HILANGKAN MOUSE CURSOR
            
            // Set text alignment to CENTER using StyledDocument
            StyledDocument doc = txtMsg.getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), center, false);
            
            // Add padding around message
            txtMsg.setBorder(new EmptyBorder(15, 10, 15, 10));
            panel.add(txtMsg, BorderLayout.CENTER);
            
            // Buttons
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            btnPanel.setOpaque(false);
            
            GameButton btnYes = new GameButton("YA", new Color(192, 57, 43));
            btnYes.setPreferredSize(new Dimension(100, 45));
            btnYes.addActionListener(e -> { confirmed = true; dispose(); });
            
            GameButton btnNo = new GameButton("TIDAK", new Color(46, 204, 113));
            btnNo.setPreferredSize(new Dimension(100, 45));
            btnNo.addActionListener(e -> { confirmed = false; dispose(); });
            
            btnPanel.add(btnYes);
            btnPanel.add(btnNo);
            panel.add(btnPanel, BorderLayout.SOUTH);
            
            setContentPane(panel);
            setSize(350, 220);
            setLocationRelativeTo(parent);
        }
        
        public boolean isConfirmed() { return confirmed; }
    }

    // ============================================================
    // CUSTOM BUTTON
    // ============================================================
    private class GameButton extends JButton {
        private final Color color;
        private boolean hover;

        public GameButton(String text, Color color) {
            super(text);
            this.color = color;
            setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(220, 55));
            
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
            int yOffset = getModel().isPressed() ? 3 : 0;

            g2.setColor(new Color(0,0,0,50));
            g2.fillRoundRect(5, 8, w-10, h-10, 50, 50);

            g2.setColor(color.darker());
            g2.fillRoundRect(0, yOffset+5, w, h-5-yOffset, 50, 50);

            Color c1 = hover ? color.brighter() : color;
            g2.setPaint(new GradientPaint(0, yOffset, c1, 0, h, color));
            g2.fillRoundRect(0, yOffset, w, h-8, 50, 50);

            g2.setPaint(new GradientPaint(0, yOffset, new Color(255,255,255,80), 0, h/2, new Color(255,255,255,0)));
            g2.fillRoundRect(5, yOffset+2, w-10, (h/2)-5, 40, 40);

            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = (h - 8 - fm.getHeight()) / 2 + fm.getAscent() + yOffset;
            
            g2.setColor(new Color(0,0,0,50));
            g2.drawString(getText(), tx+1, ty+2);
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), tx, ty);

            g2.dispose();
        }
    }
}