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
    private final JTextField ageField;
    private String selectedAvatar = "avatar_1.png";
    
    // UI Components
    private List<CharacterSpotlight> avatarOptions = new ArrayList<>();
    private Image bgImage;

    // --- PALET WARNA ---
    private final Color COLOR_BOARD_BG = new Color(255, 248, 225); // Krem Kertas
    private final Color COLOR_BOARD_BORDER = new Color(139, 69, 19); // Coklat Kayu
    private final Color COLOR_ACCENT = new Color(255, 140, 0);     // Oranye
    private final Color COLOR_TEXT = new Color(93, 64, 55);        // Coklat Tua
    private final Color COLOR_PAPER_LINE = new Color(173, 216, 230); // Biru Langit Pudar

    public ProfileCreateScreen() {
        this.userRepo = new UserRepository();
        setLayout(new GridBagLayout()); 
        loadBackground();

        // Agar panel utama berada di tengah layar
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // ========================================================
        // 1. ADVENTURE BOARD (LEMBAR BIODATA)
        // ========================================================
        AdventureBoard mainBoard = new AdventureBoard();
        // Menggunakan GridBagLayout di dalam board agar TIDAK MENUMPUK
        mainBoard.setLayout(new GridBagLayout()); 
        mainBoard.setPreferredSize(new Dimension(650, 720)); 
        mainBoard.setBorder(new EmptyBorder(20, 40, 20, 40)); 

        GridBagConstraints boardGbc = new GridBagConstraints();
        boardGbc.gridx = 0;
        boardGbc.fill = GridBagConstraints.HORIZONTAL;
        boardGbc.anchor = GridBagConstraints.CENTER;

        // --- A. HEADER (Baris 0) ---
        JLabel lblTitle = new JLabel("BIODATA SAYA");
        lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        lblTitle.setForeground(COLOR_BOARD_BORDER);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        boardGbc.gridy = 0;
        boardGbc.insets = new Insets(20, 0, 40, 0); // Jarak bawah besar agar tidak kena garis
        mainBoard.add(lblTitle, boardGbc);
        
        // --- B. FORMULIR BIODATA (Baris 1) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(0, 0, 20, 10); // Spasi antar elemen form

        // Baris 1: Namaku
        JLabel lblName = new JLabel("Namaku  :");
        lblName.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        lblName.setForeground(COLOR_TEXT);
        
        nameField = createTransparentField();
        nameField.setPreferredSize(new Dimension(300, 40));
        
        formGbc.gridx = 0; formGbc.gridy = 0;
        formPanel.add(lblName, formGbc);
        
        formGbc.gridx = 1; 
        formPanel.add(nameField, formGbc);

        // Baris 2: Umurku
        JLabel lblAge = new JLabel("Umurku   :");
        lblAge.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        lblAge.setForeground(COLOR_TEXT);
        
        ageField = createTransparentField();
        ageField.setPreferredSize(new Dimension(100, 40));
        
        // Wrapper untuk umur + teks "Tahun"
        JPanel ageWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ageWrapper.setOpaque(false);
        ageWrapper.add(ageField);
        
        JLabel lblYears = new JLabel(" Tahun");
        lblYears.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        lblYears.setForeground(COLOR_TEXT);
        ageWrapper.add(lblYears);
        
        formGbc.gridx = 0; formGbc.gridy = 1;
        formPanel.add(lblAge, formGbc);
        
        formGbc.gridx = 1; 
        formPanel.add(ageWrapper, formGbc);

        // Masukkan Form ke Board
        boardGbc.gridy = 1;
        boardGbc.insets = new Insets(0, 0, 20, 0); // Margin kiri agar agak ke tengah
        mainBoard.add(formPanel, boardGbc);


        // --- C. PILIHAN AVATAR (Baris 2 & 3) ---
        JLabel lblChoose = new JLabel("Pilih Fotoku:");
        lblChoose.setFont(new Font("Arial", Font.BOLD, 18));
        lblChoose.setForeground(Color.GRAY);
        lblChoose.setHorizontalAlignment(SwingConstants.CENTER);
        
        boardGbc.gridy = 2;
        boardGbc.insets = new Insets(-10, 0, 15, 0);
        boardGbc.anchor = GridBagConstraints.CENTER;
        mainBoard.add(lblChoose, boardGbc);

        // Container Avatar
        JPanel avatarContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        avatarContainer.setOpaque(false);
        
        CharacterSpotlight char1 = new CharacterSpotlight("avatar_1.png", "Si Pemberani");
        char1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { selectAvatar(0); }
        });
        
        CharacterSpotlight char2 = new CharacterSpotlight("avatar_2.png", "Si Pintar");
        char2.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { selectAvatar(1); }
        });
        
        avatarOptions.add(char1);
        avatarOptions.add(char2);
        avatarContainer.add(char1);
        avatarContainer.add(char2);
        
        selectAvatar(0); 

        boardGbc.gridy = 3;
        boardGbc.weighty = 1.0; // Memberi ruang vertikal agar tidak tertindih
        boardGbc.fill = GridBagConstraints.BOTH;
        boardGbc.insets = new Insets(0, 0, 0, 0);
        mainBoard.add(avatarContainer, boardGbc);


        // --- D. TOMBOL (Baris 4) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);

        WoodenButton btnBack = new WoodenButton("KEMBALI", new Color(192, 57, 43));
        btnBack.setPreferredSize(new Dimension(150, 55));
        btnBack.addActionListener(e -> ScreenManager.getInstance().showScreen("MAIN_MENU"));

        WoodenButton btnSave = new WoodenButton("SELESAI!", new Color(46, 204, 113));
        btnSave.setPreferredSize(new Dimension(220, 65));
        btnSave.setFont(new Font("Arial", Font.BOLD, 24));
        btnSave.addActionListener(e -> saveAndPlay());

        buttonPanel.add(btnBack);
        buttonPanel.add(btnSave);

        boardGbc.gridy = 4;
        boardGbc.weighty = 0;
        boardGbc.fill = GridBagConstraints.HORIZONTAL;
        boardGbc.insets = new Insets(0, 0, 20, 0); // Jarak dari bawah papan
        mainBoard.add(buttonPanel, boardGbc);

        add(mainBoard, gbc);
    }

    // --- Helper: Input Tanpa Border (Agar menyatu dengan garis background) ---
    private JTextField createTransparentField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        field.setForeground(new Color(0, 0, 139)); // Biru Tinta
        // Tidak pakai border, karena kita akan pakai garis background
        field.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        field.setOpaque(false);
        return field;
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
        String age = ageField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi namamu dulu ya!", "Ups", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!age.isEmpty() && !age.matches("\\d+")) {
             JOptionPane.showMessageDialog(this, "Umur harus angka ya!", "Ups", JOptionPane.WARNING_MESSAGE);
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
                ageField.setText("");
                playSound("success");
                JOptionPane.showMessageDialog(this, "Halo " + newUser.getName() + "!\nSelamat Datang!");
                ScreenManager.getInstance().showScreen("MODULE_SELECT");
            }
        } else {
            playSound("error");
            JOptionPane.showMessageDialog(this, "Profil penuh! Hapus satu dulu.");
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
    // CUSTOM 1: ADVENTURE BOARD (KERTAS BERGARIS PRESISI)
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

            // 1. Bayangan Papan
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(15, 15, w-20, h-20, 40, 40);

            // 2. Border Kayu
            g2.setColor(COLOR_BOARD_BORDER);
            g2.fillRoundRect(0, 0, w-10, h-10, 40, 40);

            // 3. Kertas Dasar
            g2.setColor(COLOR_BOARD_BG);
            g2.fillRoundRect(10, 10, w-30, h-30, 30, 30);
            
            // --- GARIS BUKU TULIS ---
            g2.setColor(COLOR_PAPER_LINE); 
            g2.setStroke(new BasicStroke(2));
            
            // Logika Garis: 
            // Header ada di atas. Mulai garis dari Y = 170 ke bawah.
            // Jarak antar garis (gap) = 60 pixel.
            // Posisi input field nanti akan "menumpang" di garis ini.
            
            int startY = 170; 
            int gap = 60; 
            
            // Gambar garis sampai area avatar
            for (int y = startY; y < h - 150; y += gap) {
                g2.drawLine(40, y, w - 40, y);
            }
            
            // Garis Margin Vertikal Merah
            g2.setColor(new Color(255, 182, 193)); 
            g2.drawLine(80, 20, 80, h - 30);
            
            // 4. Paku di Sudut
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
    // CUSTOM 2: CHARACTER SPOTLIGHT (SIZE DISESUAIKAN)
    // ============================================================
    class CharacterSpotlight extends JPanel {
        private Image img;
        private String label;
        private boolean isSelected = false;
        private boolean isHovered = false;

        public CharacterSpotlight(String filename, String label) {
            this.label = label;
            // Ukuran sedikit dikecilkan agar muat di board (200x230)
            setPreferredSize(new Dimension(200, 230)); 
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
            int imgSize = 150; // Gambar avatar 150px
            int imgY = 10;     

            if (isSelected || isHovered) {
                Color glowColor = isSelected ? new Color(255, 223, 0, 150) : new Color(255, 255, 200, 100);
                g2.setColor(glowColor);
                g2.fillOval(centerX - (imgSize/2) - 10, imgY - 10, imgSize + 20, imgSize + 20);
            }

            g2.setColor(Color.WHITE);
            g2.fillOval(centerX - (imgSize/2), imgY, imgSize, imgSize);
            
            if (isSelected) {
                g2.setColor(COLOR_ACCENT);
                g2.setStroke(new BasicStroke(5)); 
                g2.drawOval(centerX - (imgSize/2), imgY, imgSize, imgSize);
            }

            if (img != null) {
                Shape oldClip = g2.getClip();
                g2.setClip(new java.awt.geom.Ellipse2D.Float(centerX - (imgSize/2) + 5, imgY + 5, imgSize - 10, imgSize - 10));
                g2.drawImage(img, centerX - (imgSize/2) + 5, imgY + 5, imgSize - 10, imgSize - 10, this);
                g2.setClip(oldClip);
            }

            g2.setColor(isSelected ? COLOR_ACCENT : Color.GRAY);
            g2.setFont(new Font("Arial", Font.BOLD, 18)); 
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(label)) / 2;
            g2.drawString(label, textX, getHeight() - 10);
            
            if (isSelected) {
                int badgeSize = 36;
                int badgeX = centerX + 40; 
                int badgeY = imgY + 100;
                g2.setColor(new Color(46, 204, 113));
                g2.fillOval(badgeX, badgeY, badgeSize, badgeSize);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(badgeX + 8, badgeY + 18, badgeX + 14, badgeY + 24);
                g2.drawLine(badgeX + 14, badgeY + 24, badgeX + 26, badgeY + 12);
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