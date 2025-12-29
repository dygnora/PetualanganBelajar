package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.UserRepository;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.io.File;
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
        userList = userRepo.getAllActiveUsers();
        if (!userList.isEmpty()) selectedIndex = 0;

        initUI();
    }

    private void loadAssets() {
        try {
            File bg = new File("resources/images/bg_profile_select.png");
            if (bg.exists()) bgImage = ImageIO.read(bg);
            File title = new File("resources/images/title_continue.png");
            if (title.exists()) titleImage = ImageIO.read(title);
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
        if (userList.isEmpty()) selectedIndex = -1;
        else if (selectedIndex >= userList.size()) selectedIndex = 0;
        
        if(carouselPanel != null) carouselPanel.repaint();
    }

    private void initUI() {
        // --- 1. HEADER ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(-50, 0, 0, 0));
        header.setPreferredSize(new Dimension(800, 250));

        JLabel lblTitle = new JLabel();
        if (titleImage != null) {
            lblTitle.setIcon(new ImageIcon(titleImage.getScaledInstance(500, 400, Image.SCALE_SMOOTH)));
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
        
        // Tombol Kembali (Redesign: Capsule Red)
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
        // Overlay Gelap Tipis
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // =========================================================================
    // INNER CLASS: CAROUSEL PANEL (LOGIC FIX)
    // =========================================================================
    private class CarouselPanel extends JPanel {
        
        // Bounds dinamis untuk navigasi
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
                    if (userList.isEmpty()) return;
                    Point p = e.getPoint();

                    // 1. KLIK CARD TENGAH (FOKUS)
                    if (centerCardBounds.contains(p)) {
                        
                        // FIX UTAMA: Hitung bounds sampah secara real-time
                        Rectangle realTrash = calculateTrashBounds();

                        if (realTrash.contains(p)) {
                            // Klik Sampah -> Hapus
                            playSound("click");
                            deleteAction();
                        } else {
                            // Klik Badan Kartu -> Main
                            playSound("click");
                            UserModel u = userList.get(selectedIndex);
                            System.out.println("Login: " + u.getName());
                            GameState.setCurrentUser(u);
                            ScreenManager.getInstance().showScreen("MODULE_SELECT");
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
                    if (userList.isEmpty()) return;
                    Point p = e.getPoint();
                    
                    boolean prevHover = isTrashHovered;
                    
                    // Cek hover menggunakan kalkulasi real-time yang sama
                    if (centerCardBounds.contains(p)) {
                        Rectangle realTrash = calculateTrashBounds();
                        isTrashHovered = realTrash.contains(p);
                    } else {
                        isTrashHovered = false;
                    }
                    
                    // Repaint hanya jika state berubah (hemat resource)
                    if (prevHover != isTrashHovered) repaint();
                }
            });
        }
        
        // --- LOGIC HELPER (THE FIX) ---
        // Menghitung posisi tombol sampah berdasarkan ukuran panel saat ini
        private Rectangle calculateTrashBounds() {
            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2;

            // Parameter Kartu Tengah (Harus sama dengan drawCard saat scale 1.15f)
            int baseW = 220;
            int baseH = 280;
            float scale = 1.15f; 

            int dw = (int) (baseW * scale);
            int dh = (int) (baseH * scale);
            int dx = cx - (dw / 2);
            int dy = cy - (dh / 2);

            int trashSize = 32;
            // Posisi relatif sampah di dalam kartu
            int txPos = dx + dw - trashSize - 15;
            int tyPos = dy + 15;

            return new Rectangle(txPos, tyPos, trashSize, trashSize);
        }
        
        private void rotateLeft() {
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = userList.size() - 1;
            repaint();
        }
        
        private void rotateRight() {
            selectedIndex++;
            if (selectedIndex >= userList.size()) selectedIndex = 0;
            repaint();
        }
        
        private void deleteAction() {
            UserModel u = userList.get(selectedIndex);
            int confirm = JOptionPane.showConfirmDialog(ProfileSelectionScreen.this, 
                "Hapus " + u.getName() + "?", "Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                userRepo.deleteUser(u.getId());
                refreshData();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int cy = h / 2;
            int cx = w / 2;

            if (userList.isEmpty()) {
                drawEmptyState(g2, w, h);
                g2.dispose();
                return;
            }

            int leftIdx = (selectedIndex - 1 + userList.size()) % userList.size();
            int rightIdx = (selectedIndex + 1) % userList.size();
            boolean singleMode = userList.size() == 1;
            boolean dualMode = userList.size() == 2;

            // DRAW BACKGROUND CARDS
            if (!singleMode) {
                if (!dualMode) {
                    drawCard(g2, userList.get(leftIdx), cx - 240, cy, 0.75f, 0.6f, false, leftCardBounds);
                }
                drawCard(g2, userList.get(rightIdx), cx + 240, cy, 0.75f, 0.6f, false, rightCardBounds);
            }

            // DRAW CENTER CARD
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

            // Update bounds utama untuk deteksi klik
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
                File f = new File("resources/images/" + fName);
                if (f.exists()) {
                    Image img = new ImageIcon(f.getAbsolutePath()).getImage();
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

            // 6. Ikon Sampah (GAMBAR SAJA, Logika klik ada di calculateTrashBounds)
            if (isCenter) {
                int trashBaseSize = 32;
                // Animasi Visual Hover
                int currentSize = isTrashHovered ? trashBaseSize + 4 : trashBaseSize;
                
                // Gunakan koordinat calculation logic
                Rectangle r = calculateTrashBounds();
                int txPos = r.x - (currentSize - trashBaseSize)/2; // Center zoom
                int tyPos = r.y - (currentSize - trashBaseSize)/2;
                
                // Background Bulat Merah
                g2.setColor(new Color(220, 53, 69)); 
                if (isTrashHovered) g2.setColor(new Color(255, 80, 80)); // Lebih terang saat hover
                
                g2.fillOval(txPos, tyPos, currentSize, currentSize);
                
                // Huruf X Putih
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
    // CUSTOM GAME BUTTON (Capsule Style)
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

            // Shadow
            g2.setColor(new Color(0,0,0,50));
            g2.fillRoundRect(5, 8, w-10, h-10, 50, 50);

            // Bottom 3D part
            g2.setColor(color.darker());
            g2.fillRoundRect(0, yOffset+5, w, h-5-yOffset, 50, 50);

            // Top Face
            Color c1 = hover ? color.brighter() : color;
            Color c2 = color;
            g2.setPaint(new GradientPaint(0, yOffset, c1, 0, h, c2));
            g2.fillRoundRect(0, yOffset, w, h-8, 50, 50);

            // Shine (Kilau Putih di atas)
            g2.setPaint(new GradientPaint(0, yOffset, new Color(255,255,255,80), 0, h/2, new Color(255,255,255,0)));
            g2.fillRoundRect(5, yOffset+2, w-10, (h/2)-5, 40, 40);

            // Text
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = (h - 8 - fm.getHeight()) / 2 + fm.getAscent() + yOffset;
            
            // Text Shadow
            g2.setColor(new Color(0,0,0,50));
            g2.drawString(getText(), tx+1, ty+2);
            
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), tx, ty);

            g2.dispose();
        }
    }
}