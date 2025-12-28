package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer; 
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.UserRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileCreateScreen extends JPanel {

    private final UserRepository userRepo;
    private final JTextField nameField;
    private String selectedAvatar = "avatar_1.png";
    
    // UI Components
    private List<CharacterSpotlight> avatarOptions = new ArrayList<>();
    private Image bgImage;

    // Palet Warna Tema "Jurnal Petualang"
    private final Color COLOR_BOARD_BG = new Color(255, 248, 225); // Krem Kertas
    private final Color COLOR_BOARD_BORDER = new Color(139, 69, 19); // Coklat Kayu
    private final Color COLOR_ACCENT = new Color(255, 140, 0);     // Oranye
    private final Color COLOR_TEXT = new Color(93, 64, 55);        // Coklat Tua

    public ProfileCreateScreen() {
        this.userRepo = new UserRepository();
        setLayout(new GridBagLayout()); 
        loadBackground();

        // Konfigurasi Layout Tengah
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // ========================================================
        // 1. THE ADVENTURE BOARD (PANEL UTAMA)
        // ========================================================
        AdventureBoard mainBoard = new AdventureBoard();
        mainBoard.setLayout(new BoxLayout(mainBoard, BoxLayout.Y_AXIS));
        // Ukuran board sedikit diperlebar agar avatar besar muat dengan lega
        mainBoard.setPreferredSize(new Dimension(650, 720)); 
        mainBoard.setBorder(new EmptyBorder(30, 40, 30, 40)); 

        // --- A. HEADER (JUDUL) ---
        JLabel lblTitle = new JLabel("-- Yuk kenalan dulu! --");
        lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        lblTitle.setForeground(COLOR_BOARD_BORDER);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- B. INPUT NAMA (STICKER STYLE) ---
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        namePanel.setOpaque(false);
        namePanel.setMaximumSize(new Dimension(500, 90));
        
        JLabel lblNameTag = new JLabel("Namaku:");
        lblNameTag.setFont(new Font("Arial", Font.BOLD, 22));
        lblNameTag.setForeground(COLOR_TEXT);
        
        nameField = new JTextField(12);
        nameField.setFont(new Font("Comic Sans MS", Font.BOLD, 32)); // Font input diperbesar
        nameField.setForeground(COLOR_TEXT);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, COLOR_ACCENT), // Garis lebih tebal
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        nameField.setOpaque(false);
        nameField.setBackground(new Color(0,0,0,0));

        namePanel.add(lblNameTag);
        namePanel.add(nameField);

        // --- C. PILIHAN AVATAR (SPOTLIGHT STYLE) ---
        JLabel lblChoose = new JLabel("Pilih Karaktermu:");
        lblChoose.setFont(new Font("Arial", Font.BOLD, 18));
        lblChoose.setForeground(Color.GRAY);
        lblChoose.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel avatarContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        avatarContainer.setOpaque(false);
        
        // Avatar 1
        CharacterSpotlight char1 = new CharacterSpotlight("avatar_1.png", "Si Pemberani");
        char1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { selectAvatar(0); }
        });
        
        // Avatar 2
        CharacterSpotlight char2 = new CharacterSpotlight("avatar_2.png", "Si Pintar");
        char2.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { selectAvatar(1); }
        });
        
        avatarOptions.add(char1);
        avatarOptions.add(char2);
        avatarContainer.add(char1);
        avatarContainer.add(char2);
        
        selectAvatar(0); // Default

        // --- D. TOMBOL (WOODEN BUTTON STYLE) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setOpaque(false);

        WoodenButton btnBack = new WoodenButton("KEMBALI", new Color(192, 57, 43));
        btnBack.setPreferredSize(new Dimension(160, 55));
        btnBack.addActionListener(e -> ScreenManager.getInstance().showScreen("MAIN_MENU"));

        WoodenButton btnSave = new WoodenButton("SIAP BERANGKAT!", new Color(46, 204, 113));
        btnSave.setPreferredSize(new Dimension(260, 65)); // Tombol lebih besar sedikit
        btnSave.setFont(new Font("Arial", Font.BOLD, 24));
        btnSave.addActionListener(e -> saveAndPlay());

        buttonPanel.add(btnBack);
        buttonPanel.add(btnSave);

        // --- MENYUSUN ELEMEN ---
        mainBoard.add(lblTitle);
        mainBoard.add(Box.createVerticalStrut(15));
        mainBoard.add(new JSeparator(SwingConstants.HORIZONTAL));
        mainBoard.add(Box.createVerticalStrut(25));
        mainBoard.add(namePanel);
        mainBoard.add(Box.createVerticalStrut(15));
        mainBoard.add(lblChoose);
        mainBoard.add(Box.createVerticalStrut(5));
        mainBoard.add(avatarContainer);
        mainBoard.add(Box.createVerticalStrut(30)); // Jarak ke tombol
        mainBoard.add(buttonPanel);

        add(mainBoard, gbc);
    }

    // --- LOGIC ---
    private void selectAvatar(int index) {
        for (int i = 0; i < avatarOptions.size(); i++) {
            avatarOptions.get(i).setSelected(i == index);
        }
        selectedAvatar = "avatar_" + (index + 1) + ".png";
        playSound("click"); 
        repaint();
    }

    private void saveAndPlay() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong, Petualang!", "Perhatian", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userRepo.createUser(name, selectedAvatar)) {
            UserModel newUser = userRepo.getAllActiveUsers().stream()
                    .filter(u -> u.getName().equals(name))
                    .reduce((first, second) -> second)
                    .orElse(null);

            if (newUser != null) {
                GameState.setCurrentUser(newUser);
                nameField.setText("");
                playSound("success");
                JOptionPane.showMessageDialog(this, "Selamat Datang, " + newUser.getName() + "!");
                ScreenManager.getInstance().showScreen("MODULE_SELECT");
            }
        } else {
            playSound("error");
            JOptionPane.showMessageDialog(this, "Buku petualang penuh! Hapus satu dulu.");
        }
    }
    
    private void playSound(String type) {
        try {
            SoundPlayer.getInstance().playSFX(type + ".wav");
        } catch (Exception e) {}
    }

    private void loadBackground() {
        try {
            File f = new File("resources/images/bg_profile.png");
            if (f.exists()) bgImage = new ImageIcon(f.getAbsolutePath()).getImage();
        } catch (Exception e) {}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(100, 180, 100));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // ============================================================
    // CUSTOM 1: ADVENTURE BOARD
    // ============================================================
    class AdventureBoard extends JPanel {
        public AdventureBoard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Bayangan
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(15, 15, w-20, h-20, 40, 40);

            // Border Kayu
            g2.setColor(COLOR_BOARD_BORDER);
            g2.fillRoundRect(0, 0, w-10, h-10, 40, 40);

            // Kertas
            g2.setColor(COLOR_BOARD_BG);
            g2.fillRoundRect(10, 10, w-30, h-30, 30, 30);
            
            // Paku
            g2.setColor(new Color(160, 82, 45)); 
            int pakuSize = 14;
            g2.fillOval(25, 25, pakuSize, pakuSize);
            g2.fillOval(w-45, 25, pakuSize, pakuSize);
            g2.fillOval(25, h-45, pakuSize, pakuSize);
            g2.fillOval(w-45, h-45, pakuSize, pakuSize);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ============================================================
    // CUSTOM 2: CHARACTER SPOTLIGHT (DIPERBESAR)
    // ============================================================
    class CharacterSpotlight extends JPanel {
        private Image img;
        private String label;
        private boolean isSelected = false;
        private boolean isHovered = false;

        public CharacterSpotlight(String filename, String label) {
            this.label = label;
            // UKURAN PANEL DIPERBESAR
            setPreferredSize(new Dimension(200, 260)); 
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            try {
                File f = new File("resources/images/" + filename);
                if (f.exists()) img = new ImageIcon(f.getAbsolutePath()).getImage();
            } catch (Exception e) {}

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        public void setSelected(boolean sel) {
            this.isSelected = sel;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = getWidth() / 2;
            
            // UKURAN GAMBAR DIPERBESAR
            int imgSize = 160; // Sebelumnya mungkin 120
            int imgY = 15;     // Geser sedikit ke atas

            // 1. Sorotan Cahaya
            if (isSelected || isHovered) {
                Color glowColor = isSelected ? new Color(255, 223, 0, 150) : new Color(255, 255, 200, 100);
                g2.setColor(glowColor);
                // Glow mengikuti ukuran baru
                g2.fillOval(centerX - (imgSize/2) - 15, imgY - 15, imgSize + 30, imgSize + 30);
            }

            // 2. Lingkaran Dasar
            g2.setColor(Color.WHITE);
            g2.fillOval(centerX - (imgSize/2), imgY, imgSize, imgSize);
            
            // 3. Border Lingkaran
            if (isSelected) {
                g2.setColor(COLOR_ACCENT);
                g2.setStroke(new BasicStroke(6)); // Border lebih tebal
                g2.drawOval(centerX - (imgSize/2), imgY, imgSize, imgSize);
            }

            // 4. Gambar Avatar
            if (img != null) {
                Shape oldClip = g2.getClip();
                g2.setClip(new java.awt.geom.Ellipse2D.Float(centerX - (imgSize/2) + 6, imgY + 6, imgSize - 12, imgSize - 12));
                g2.drawImage(img, centerX - (imgSize/2) + 6, imgY + 6, imgSize - 12, imgSize - 12, this);
                g2.setClip(oldClip);
            }

            // 5. Label Nama Karakter
            g2.setColor(isSelected ? COLOR_ACCENT : Color.GRAY);
            // Font diperbesar sedikit
            g2.setFont(new Font("Arial", Font.BOLD, 20)); 
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(label)) / 2;
            g2.drawString(label, textX, getHeight() - 15);
            
            // 6. Centang (Badge) - Posisi disesuaikan untuk lingkaran besar
            if (isSelected) {
                int badgeSize = 40;
                // Posisi badge di pojok kanan bawah lingkaran
                int badgeX = centerX + 45; 
                int badgeY = imgY + 115;
                
                g2.setColor(new Color(46, 204, 113));
                g2.fillOval(badgeX, badgeY, badgeSize, badgeSize);
                
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(4));
                g2.drawLine(badgeX + 8, badgeY + 20, badgeX + 16, badgeY + 28);
                g2.drawLine(badgeX + 16, badgeY + 28, badgeX + 30, badgeY + 12);
            }

            g2.dispose();
        }
    }

    // ============================================================
    // CUSTOM 3: WOODEN BUTTON
    // ============================================================
    class WoodenButton extends JButton {
        private Color baseColor;
        private Color hoverColor;
        private boolean isHovered = false;

        public WoodenButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            this.hoverColor = color.brighter();
            
            setFont(new Font("Arial", Font.BOLD, 18));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int offset = getModel().isPressed() ? 2 : 0;
            
            g2.setColor(baseColor.darker());
            g2.fillRoundRect(0, offset + 6, getWidth(), getHeight()-6, 18, 18);
            
            g2.setColor(isHovered ? hoverColor : baseColor);
            g2.fillRoundRect(0, offset, getWidth(), getHeight()-6, 18, 18);
            
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + offset - 3;
            
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), x, y);
            
            g2.dispose();
        }
    }
}