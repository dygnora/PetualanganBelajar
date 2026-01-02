package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.UserRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ProfileCreateScreen extends JPanel {

    private final UserRepository userRepo;
    private final JTextField nameField;
    private final JTextField ageField;
    private String selectedAvatar = "avatar_1.png";
    
    private List<CharacterSpotlight> avatarOptions = new ArrayList<>();
    private Image bgImage;

    // PALETTE
    private final Color COLOR_BOARD_BG = new Color(255, 248, 225);
    private final Color COLOR_BOARD_BORDER = new Color(139, 69, 19);
    private final Color COLOR_ACCENT = new Color(255, 140, 0);
    private final Color COLOR_TEXT = new Color(93, 64, 55);
    private final Color COLOR_PAPER_LINE = new Color(173, 216, 230);

    public ProfileCreateScreen() {
        this.userRepo = new UserRepository();
        setLayout(new GridBagLayout()); 
        loadBackground();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        AdventureBoard mainBoard = new AdventureBoard();
        mainBoard.setLayout(new GridBagLayout()); 
        mainBoard.setPreferredSize(new Dimension(650, 720)); 
        mainBoard.setBorder(new EmptyBorder(20, 40, 20, 40)); 

        GridBagConstraints boardGbc = new GridBagConstraints();
        boardGbc.gridx = 0;
        boardGbc.fill = GridBagConstraints.HORIZONTAL;
        boardGbc.anchor = GridBagConstraints.CENTER;

        // HEADER
        JLabel lblTitle = new JLabel("BIODATA SAYA");
        lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        lblTitle.setForeground(COLOR_BOARD_BORDER);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        boardGbc.gridy = 0;
        boardGbc.insets = new Insets(20, 0, 40, 0);
        mainBoard.add(lblTitle, boardGbc);
        
        // FORM
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(0, 0, 20, 10);

        JLabel lblName = new JLabel("Namaku  :");
        lblName.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        lblName.setForeground(COLOR_TEXT);
        
        nameField = createTransparentField();
        nameField.setPreferredSize(new Dimension(300, 40));
        
        formGbc.gridx = 0; formGbc.gridy = 0;
        formPanel.add(lblName, formGbc);
        formGbc.gridx = 1; 
        formPanel.add(nameField, formGbc);

        JLabel lblAge = new JLabel("Umurku   :");
        lblAge.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        lblAge.setForeground(COLOR_TEXT);
        
        ageField = createTransparentField();
        ageField.setPreferredSize(new Dimension(100, 40));
        
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

        boardGbc.gridy = 1;
        boardGbc.insets = new Insets(0, 0, 20, 0);
        mainBoard.add(formPanel, boardGbc);

        // AVATAR
        JLabel lblChoose = new JLabel("Pilih Fotoku:");
        lblChoose.setFont(new Font("Arial", Font.BOLD, 18));
        lblChoose.setForeground(Color.GRAY);
        lblChoose.setHorizontalAlignment(SwingConstants.CENTER);
        
        boardGbc.gridy = 2;
        boardGbc.insets = new Insets(-10, 0, 15, 0);
        boardGbc.anchor = GridBagConstraints.CENTER;
        mainBoard.add(lblChoose, boardGbc);

        JPanel avatarContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        avatarContainer.setOpaque(false);
        
        CharacterSpotlight char1 = new CharacterSpotlight("avatar_1.png", "Si Pemberani");
        char1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { selectAvatar(0, true); }
        });
        
        CharacterSpotlight char2 = new CharacterSpotlight("avatar_2.png", "Si Pintar");
        char2.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { selectAvatar(1, true); }
        });
        
        avatarOptions.add(char1); avatarOptions.add(char2);
        avatarContainer.add(char1); avatarContainer.add(char2);
        selectAvatar(0, false); 

        boardGbc.gridy = 3;
        boardGbc.weighty = 1.0;
        boardGbc.fill = GridBagConstraints.BOTH;
        boardGbc.insets = new Insets(0, 0, 0, 0);
        mainBoard.add(avatarContainer, boardGbc);

        // BUTTONS
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
        boardGbc.insets = new Insets(0, 0, 20, 0); 
        mainBoard.add(buttonPanel, boardGbc);

        add(mainBoard, gbc);
    }

    private JTextField createTransparentField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        field.setForeground(new Color(0, 0, 139));
        field.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        field.setOpaque(false);
        return field;
    }

    private void selectAvatar(int index, boolean playSound) {
        for (int i = 0; i < avatarOptions.size(); i++) {
            avatarOptions.get(i).setSelected(i == index);
        }
        selectedAvatar = "avatar_" + (index + 1) + ".png";
        if (playSound) playSound("click"); 
        repaint();
    }

    private void saveAndPlay() {
        String name = nameField.getText().trim();
        String age = ageField.getText().trim();

        if (name.isEmpty()) {
            playSound("error");
            showCustomDialog("Ups!", "Isi namamu dulu ya!", true);
            return;
        }
        
        if (!age.isEmpty() && !age.matches("\\d+")) {
             playSound("error");
             showCustomDialog("Ups!", "Umur harus angka ya!", true);
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
                
                // Show success dialog, then switch screen
                showCustomDialog("Hore!", "Halo " + newUser.getName() + "!\nSelamat Datang!", false);
                ScreenManager.getInstance().showScreen("MODULE_SELECT");
            }
        } else {
            playSound("error");
            showCustomDialog("Maaf", "Profil penuh!\nHapus satu dulu.", true);
        }
    }
    
    private void showCustomDialog(String title, String message, boolean isWarning) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        AdventureDialog dialog = new AdventureDialog((Frame) parentWindow, title, message, isWarning);
        dialog.setVisible(true);
    }

    private void playSound(String type) {
        try { SoundPlayer.getInstance().playSFX(type + ".wav"); } catch (Exception e) {}
    }

    private void loadBackground() {
        try {
            java.net.URL imgUrl = getClass().getResource("/images/bg_profile.png");
            if (imgUrl != null) bgImage = new ImageIcon(imgUrl).getImage();
        } catch (Exception e) {}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        else { g.setColor(new Color(100, 180, 100)); g.fillRect(0, 0, getWidth(), getHeight()); }
    }

    // --- INNER CLASS: ADVENTURE BOARD ---
    class AdventureBoard extends JPanel {
        public AdventureBoard() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            g2.setColor(new Color(0, 0, 0, 60)); g2.fillRoundRect(15, 15, w-20, h-20, 40, 40);
            g2.setColor(COLOR_BOARD_BORDER); g2.fillRoundRect(0, 0, w-10, h-10, 40, 40);
            g2.setColor(COLOR_BOARD_BG); g2.fillRoundRect(10, 10, w-30, h-30, 30, 30);
            g2.setColor(COLOR_PAPER_LINE); g2.setStroke(new BasicStroke(2));
            int startY = 170; int gap = 60; 
            for (int y = startY; y < h - 150; y += gap) g2.drawLine(40, y, w - 40, y);
            g2.setColor(new Color(255, 182, 193)); g2.drawLine(80, 20, 80, h - 30);
            g2.setColor(new Color(160, 82, 45)); int pakuSize = 14;
            g2.fillOval(25, 25, pakuSize, pakuSize); g2.fillOval(w-45, 25, pakuSize, pakuSize);
            g2.fillOval(25, h-45, pakuSize, pakuSize); g2.fillOval(w-45, h-45, pakuSize, pakuSize);
            g2.dispose(); super.paintComponent(g);
        }
    }

    // --- INNER CLASS: AVATAR SPOTLIGHT ---
    class CharacterSpotlight extends JPanel {
        private Image img;
        private String label;
        private boolean isSelected = false;
        private boolean isHovered = false;

        public CharacterSpotlight(String filename, String label) {
            this.label = label;
            setPreferredSize(new Dimension(200, 230)); 
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            try {
                java.net.URL imgUrl = getClass().getResource("/images/" + filename);
                if (imgUrl != null) img = new ImageIcon(imgUrl).getImage();
            } catch (Exception e) {}
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }
        public void setSelected(boolean sel) { this.isSelected = sel; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int centerX = getWidth() / 2; int imgSize = 150; int imgY = 10;     
            if (isSelected || isHovered) {
                Color glowColor = isSelected ? new Color(255, 223, 0, 150) : new Color(255, 255, 200, 100);
                g2.setColor(glowColor); g2.fillOval(centerX - (imgSize/2) - 10, imgY - 10, imgSize + 20, imgSize + 20);
            }
            g2.setColor(Color.WHITE); g2.fillOval(centerX - (imgSize/2), imgY, imgSize, imgSize);
            if (isSelected) {
                g2.setColor(COLOR_ACCENT); g2.setStroke(new BasicStroke(5)); 
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
                int badgeSize = 36; int badgeX = centerX + 40; int badgeY = imgY + 100;
                g2.setColor(new Color(46, 204, 113)); g2.fillOval(badgeX, badgeY, badgeSize, badgeSize);
                g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(3));
                g2.drawLine(badgeX + 8, badgeY + 18, badgeX + 14, badgeY + 24);
                g2.drawLine(badgeX + 14, badgeY + 24, badgeX + 26, badgeY + 12);
            }
            g2.dispose();
        }
    }

    // --- INNER CLASS: WOODEN BUTTON ---
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
            setFocusPainted(false); setBorderPainted(false);
            setContentAreaFilled(false); setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                public void mousePressed(MouseEvent e) { if (isEnabled()) playSound("click"); }
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
            g2.setColor(Color.WHITE); g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    // --- UPDATED INNER CLASS: SIMPLE & CLEAN CUSTOM DIALOG ---
    class AdventureDialog extends JDialog {
        public AdventureDialog(Frame parent, String title, String message, boolean isWarning) {
            super(parent, true);
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0)); // Transparent bg

            // Main Panel with Background Painting
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(); int h = getHeight();
                    
                    // Shadow
                    g2.setColor(new Color(0,0,0,80));
                    g2.fillRoundRect(5, 5, w-10, h-10, 30, 30);
                    
                    // Board Background
                    g2.setColor(COLOR_BOARD_BG);
                    g2.fillRoundRect(0, 0, w-5, h-5, 30, 30);
                    
                    // Border (Red for warning, brown for normal)
                    g2.setColor(isWarning ? new Color(192, 57, 43) : COLOR_BOARD_BORDER);
                    g2.setStroke(new BasicStroke(5));
                    g2.drawRoundRect(2, 2, w-9, h-9, 30, 30);
                    
                    g2.dispose();
                }
            };
            
            panel.setLayout(new BorderLayout());
            // Sedikit padding agar teks tidak terlalu mepet ke atas/bawah
            panel.setBorder(new EmptyBorder(25, 20, 20, 20));
            
            // Title Label
            JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
            lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
            lblTitle.setForeground(isWarning ? new Color(192, 57, 43) : COLOR_BOARD_BORDER);
            panel.add(lblTitle, BorderLayout.NORTH);
            
            // Message Area (Using JTextPane for better centering and no cursor)
            JTextPane txtMsg = new JTextPane();
            txtMsg.setText(message);
            // Font diperbesar sedikit agar lebih jelas
            txtMsg.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
            txtMsg.setForeground(COLOR_TEXT);
            txtMsg.setOpaque(false);
            txtMsg.setEditable(false);
            txtMsg.setFocusable(false); // PENTING: Agar kursor tidak muncul
            
            // Center align the text in JTextPane
            StyledDocument doc = txtMsg.getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), center, false);
            
            // Add padding around message
            txtMsg.setBorder(new EmptyBorder(15, 10, 15, 10));
            panel.add(txtMsg, BorderLayout.CENTER);
            
            // OK Button
            WoodenButton btnOk = new WoodenButton("SIAP!", isWarning ? new Color(192, 57, 43) : new Color(46, 204, 113));
            btnOk.setPreferredSize(new Dimension(120, 50));
            btnOk.addActionListener(e -> dispose());
            
            JPanel btnPanel = new JPanel();
            btnPanel.setOpaque(false);
            btnPanel.add(btnOk);
            panel.add(btnPanel, BorderLayout.SOUTH);
            
            setContentPane(panel);
            // Ukuran dialog sedikit disesuaikan agar pas
            setSize(380, 240);
            setLocationRelativeTo(parent);
        }
    }
}