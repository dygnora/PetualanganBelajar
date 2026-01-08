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

    // --- UI COMPONENTS REFERENCES (Untuk update responsive) ---
    private AdventureBoard mainBoard;
    private JLabel lblTitle;
    private JLabel lblName, lblAge, lblYears, lblChoose;
    private JPanel formPanel, avatarContainer, buttonPanel;
    private WoodenButton btnBack, btnSave;

    // --- RESPONSIVE VARS ---
    private final float BASE_W = 1920f;
    private final float BASE_H = 1080f;
    private float scaleFactor = 1.0f;
    private float lastScaleFactor = 0.0f;

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

        // Init Components
        nameField = createTransparentField();
        ageField = createTransparentField();
        
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
        if (mainBoard == null) return;

        // 1. Update Board Size & Padding
        int boardW = (int)(650 * scaleFactor);
        int boardH = (int)(750 * scaleFactor); // Sedikit dipertinggi agar muat
        mainBoard.setPreferredSize(new Dimension(boardW, boardH));
        
        int padH = (int)(40 * scaleFactor);
        int padV = (int)(20 * scaleFactor);
        mainBoard.setBorder(new EmptyBorder(padV, padH, padV, padH));

        // 2. Update Fonts
        lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(40 * scaleFactor)));
        lblName.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(26 * scaleFactor)));
        lblAge.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(26 * scaleFactor)));
        lblYears.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(20 * scaleFactor)));
        lblChoose.setFont(new Font("Arial", Font.BOLD, (int)(18 * scaleFactor)));
        
        // 3. Update TextField Font & Size
        Font fieldFont = new Font("Comic Sans MS", Font.BOLD, (int)(28 * scaleFactor));
        nameField.setFont(fieldFont);
        ageField.setFont(fieldFont);
        
        nameField.setPreferredSize(new Dimension((int)(300 * scaleFactor), (int)(40 * scaleFactor)));
        ageField.setPreferredSize(new Dimension((int)(100 * scaleFactor), (int)(40 * scaleFactor)));

        // 4. Update Avatars
        for (CharacterSpotlight cs : avatarOptions) {
            cs.updateScale(scaleFactor);
        }
        // Update gap antar avatar
        ((FlowLayout)avatarContainer.getLayout()).setHgap((int)(30 * scaleFactor));

        // 5. Update Buttons
        btnBack.updateScale(scaleFactor);
        btnSave.updateScale(scaleFactor);
        ((FlowLayout)buttonPanel.getLayout()).setHgap((int)(30 * scaleFactor));

        revalidate();
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        mainBoard = new AdventureBoard();
        mainBoard.setLayout(new GridBagLayout()); 

        GridBagConstraints boardGbc = new GridBagConstraints();
        boardGbc.gridx = 0;
        boardGbc.fill = GridBagConstraints.HORIZONTAL;
        boardGbc.anchor = GridBagConstraints.CENTER;

        // HEADER
        lblTitle = new JLabel("BIODATA SAYA");
        lblTitle.setForeground(COLOR_BOARD_BORDER);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        boardGbc.gridy = 0;
        boardGbc.insets = new Insets(20, 0, 40, 0); // Insets statis, gap diatur via scaling component
        mainBoard.add(lblTitle, boardGbc);
        
        // FORM
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.insets = new Insets(0, 0, 10, 10);

        lblName = new JLabel("Namaku  :");
        lblName.setForeground(COLOR_TEXT);
        
        formGbc.gridx = 0; formGbc.gridy = 0;
        formPanel.add(lblName, formGbc);
        formGbc.gridx = 1; 
        formPanel.add(nameField, formGbc);

        lblAge = new JLabel("Umurku   :");
        lblAge.setForeground(COLOR_TEXT);
        
        JPanel ageWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ageWrapper.setOpaque(false);
        ageWrapper.add(ageField);
        
        lblYears = new JLabel(" Tahun");
        lblYears.setForeground(COLOR_TEXT);
        ageWrapper.add(lblYears);
        
        formGbc.gridx = 0; formGbc.gridy = 1;
        formPanel.add(lblAge, formGbc);
        formGbc.gridx = 1; 
        formPanel.add(ageWrapper, formGbc);

        boardGbc.gridy = 1;
        boardGbc.insets = new Insets(0, 0, 20, 0);
        mainBoard.add(formPanel, boardGbc);

        // AVATAR LABEL
        lblChoose = new JLabel("Pilih Fotoku:");
        lblChoose.setForeground(Color.GRAY);
        lblChoose.setHorizontalAlignment(SwingConstants.CENTER);
        
        boardGbc.gridy = 2;
        boardGbc.insets = new Insets(-10, 0, 15, 0);
        mainBoard.add(lblChoose, boardGbc);

        // AVATAR CONTAINER
        avatarContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
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
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);

        btnBack = new WoodenButton("KEMBALI", new Color(192, 57, 43));
        btnBack.addActionListener(e -> ScreenManager.getInstance().showScreen("MAIN_MENU"));

        btnSave = new WoodenButton("SELESAI!", new Color(46, 204, 113));
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
        // [KEY] Calculate Scale
        calculateScaleFactor();
        if (Math.abs(scaleFactor - lastScaleFactor) > 0.001f) {
            lastScaleFactor = scaleFactor;
            SwingUtilities.invokeLater(this::updateResponsiveLayout);
        }

        super.paintComponent(g);
        if (bgImage != null) g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        else { g.setColor(new Color(100, 180, 100)); g.fillRect(0, 0, getWidth(), getHeight()); }
    }

    // --- INNER CLASS: ADVENTURE BOARD (Responsive) ---
    class AdventureBoard extends JPanel {
        public AdventureBoard() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            
            int arc = (int)(40 * scaleFactor);
            int pakuSize = (int)(14 * scaleFactor);
            int offset = (int)(10 * scaleFactor);

            // Shadow
            g2.setColor(new Color(0, 0, 0, 60)); 
            g2.fillRoundRect(offset + 5, offset + 5, w - (offset*2), h - (offset*2), arc, arc);
            
            // Border
            g2.setColor(COLOR_BOARD_BORDER); 
            g2.fillRoundRect(0, 0, w - offset, h - offset, arc, arc);
            
            // Paper
            g2.setColor(COLOR_BOARD_BG); 
            g2.fillRoundRect(offset, offset, w - (offset*3), h - (offset*3), (int)(30*scaleFactor), (int)(30*scaleFactor));
            
            // Lines
            g2.setColor(COLOR_PAPER_LINE); 
            g2.setStroke(new BasicStroke(Math.max(1, 2 * scaleFactor)));
            int startY = (int)(170 * scaleFactor); 
            int gap = (int)(60 * scaleFactor); 
            int margin = (int)(40 * scaleFactor);
            for (int y = startY; y < h - (150*scaleFactor); y += gap) g2.drawLine(margin, y, w - margin, y);
            
            // Red Line
            g2.setColor(new Color(255, 182, 193)); 
            g2.drawLine((int)(80*scaleFactor), (int)(20*scaleFactor), (int)(80*scaleFactor), h - (int)(30*scaleFactor));
            
            // Paku
            g2.setColor(new Color(160, 82, 45)); 
            int pakuM = (int)(25*scaleFactor);
            g2.fillOval(pakuM, pakuM, pakuSize, pakuSize); 
            g2.fillOval(w - pakuM - pakuSize - offset, pakuM, pakuSize, pakuSize);
            g2.fillOval(pakuM, h - pakuM - pakuSize - offset, pakuSize, pakuSize); 
            g2.fillOval(w - pakuM - pakuSize - offset, h - pakuM - pakuSize - offset, pakuSize, pakuSize);
            
            g2.dispose(); super.paintComponent(g);
        }
    }

    // --- INNER CLASS: AVATAR SPOTLIGHT (Responsive) ---
    class CharacterSpotlight extends JPanel {
        private Image img;
        private String label;
        private boolean isSelected = false;
        private boolean isHovered = false;

        public CharacterSpotlight(String filename, String label) {
            this.label = label;
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
        
        public void updateScale(float s) {
            int w = (int)(200 * s);
            int h = (int)(230 * s);
            setPreferredSize(new Dimension(w, h));
            revalidate();
        }

        public void setSelected(boolean sel) { this.isSelected = sel; repaint(); }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int centerX = getWidth() / 2; 
            int imgSize = (int)(150 * scaleFactor); 
            int imgY = (int)(10 * scaleFactor);      
            
            if (isSelected || isHovered) {
                Color glowColor = isSelected ? new Color(255, 223, 0, 150) : new Color(255, 255, 200, 100);
                g2.setColor(glowColor); 
                g2.fillOval(centerX - (imgSize/2) - 10, imgY - 10, imgSize + 20, imgSize + 20);
            }
            g2.setColor(Color.WHITE); g2.fillOval(centerX - (imgSize/2), imgY, imgSize, imgSize);
            if (isSelected) {
                g2.setColor(COLOR_ACCENT); g2.setStroke(new BasicStroke(5 * scaleFactor)); 
                g2.drawOval(centerX - (imgSize/2), imgY, imgSize, imgSize);
            }
            if (img != null) {
                Shape oldClip = g2.getClip();
                int pad = (int)(5 * scaleFactor);
                g2.setClip(new java.awt.geom.Ellipse2D.Float(centerX - (imgSize/2) + pad, imgY + pad, imgSize - (pad*2), imgSize - (pad*2)));
                g2.drawImage(img, centerX - (imgSize/2) + pad, imgY + pad, imgSize - (pad*2), imgSize - (pad*2), this);
                g2.setClip(oldClip);
            }
            g2.setColor(isSelected ? COLOR_ACCENT : Color.GRAY);
            g2.setFont(new Font("Arial", Font.BOLD, (int)(18 * scaleFactor))); 
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(label)) / 2;
            g2.drawString(label, textX, getHeight() - (int)(10*scaleFactor));
            
            if (isSelected) {
                int badgeSize = (int)(36 * scaleFactor); 
                int badgeX = centerX + (int)(40 * scaleFactor); 
                int badgeY = imgY + (int)(100 * scaleFactor);
                g2.setColor(new Color(46, 204, 113)); g2.fillOval(badgeX, badgeY, badgeSize, badgeSize);
                g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(3 * scaleFactor));
                g2.drawLine(badgeX + (int)(8*scaleFactor), badgeY + (int)(18*scaleFactor), badgeX + (int)(14*scaleFactor), badgeY + (int)(24*scaleFactor));
                g2.drawLine(badgeX + (int)(14*scaleFactor), badgeY + (int)(24*scaleFactor), badgeX + (int)(26*scaleFactor), badgeY + (int)(12*scaleFactor));
            }
            g2.dispose();
        }
    }

    // --- INNER CLASS: WOODEN BUTTON (Responsive) ---
    class WoodenButton extends JButton {
        private Color baseColor;
        private Color hoverColor;
        private boolean isHovered = false;

        public WoodenButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            this.hoverColor = color.brighter();
            setForeground(Color.WHITE);
            setFocusPainted(false); setBorderPainted(false);
            setContentAreaFilled(false); setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                public void mousePressed(MouseEvent e) { if (isEnabled()) playSound("click"); }
            });
        }
        
        public void updateScale(float s) {
            // Default size reference (150x55 or 220x65 based on text length logic inside updateResponsiveLayout)
            // But here we set via setPreferredSize in main class, so we just update Font
            int fontSize = getText().equals("SELESAI!") ? 24 : 18;
            setFont(new Font("Arial", Font.BOLD, (int)(fontSize * s)));
            
            // Adjust size based on button type
            int w = getText().equals("SELESAI!") ? 220 : 150;
            int h = getText().equals("SELESAI!") ? 65 : 55;
            setPreferredSize(new Dimension((int)(w*s), (int)(h*s)));
            revalidate();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int offset = getModel().isPressed() ? 2 : 0;
            int shadow = (int)(6 * scaleFactor);
            int arc = (int)(18 * scaleFactor);
            
            g2.setColor(baseColor.darker());
            g2.fillRoundRect(0, offset + shadow, getWidth(), getHeight()-shadow, arc, arc);
            g2.setColor(isHovered ? hoverColor : baseColor);
            g2.fillRoundRect(0, offset, getWidth(), getHeight()-shadow, arc, arc);
            
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + offset - (shadow/2);
            g2.setColor(Color.WHITE); g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    // --- INNER CLASS: ADVENTURE DIALOG (Scaled Simple) ---
    class AdventureDialog extends JDialog {
        public AdventureDialog(Frame parent, String title, String message, boolean isWarning) {
            super(parent, true);
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0)); 

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(); int h = getHeight();
                    g2.setColor(new Color(0,0,0,80)); g2.fillRoundRect(5, 5, w-10, h-10, 30, 30);
                    g2.setColor(COLOR_BOARD_BG); g2.fillRoundRect(0, 0, w-5, h-5, 30, 30);
                    g2.setColor(isWarning ? new Color(192, 57, 43) : COLOR_BOARD_BORDER);
                    g2.setStroke(new BasicStroke(5)); g2.drawRoundRect(2, 2, w-9, h-9, 30, 30);
                    g2.dispose();
                }
            };
            
            panel.setLayout(new BorderLayout());
            int pad = (int)(20 * scaleFactor);
            panel.setBorder(new EmptyBorder(pad+5, pad, pad, pad));
            
            JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
            lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(28 * scaleFactor)));
            lblTitle.setForeground(isWarning ? new Color(192, 57, 43) : COLOR_BOARD_BORDER);
            panel.add(lblTitle, BorderLayout.NORTH);
            
            JTextPane txtMsg = new JTextPane();
            txtMsg.setText(message);
            txtMsg.setFont(new Font("Comic Sans MS", Font.PLAIN, (int)(20 * scaleFactor)));
            txtMsg.setForeground(COLOR_TEXT);
            txtMsg.setOpaque(false); txtMsg.setEditable(false); txtMsg.setFocusable(false);
            
            StyledDocument doc = txtMsg.getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), center, false);
            txtMsg.setBorder(new EmptyBorder(pad/2, 0, pad/2, 0));
            panel.add(txtMsg, BorderLayout.CENTER);
            
            WoodenButton btnOk = new WoodenButton("SIAP!", isWarning ? new Color(192, 57, 43) : new Color(46, 204, 113));
            // Manual scale for dialog button
            btnOk.setFont(new Font("Arial", Font.BOLD, (int)(18 * scaleFactor)));
            btnOk.setPreferredSize(new Dimension((int)(120*scaleFactor), (int)(50*scaleFactor)));
            btnOk.addActionListener(e -> dispose());
            
            JPanel btnPanel = new JPanel(); btnPanel.setOpaque(false); btnPanel.add(btnOk);
            panel.add(btnPanel, BorderLayout.SOUTH);
            
            setContentPane(panel);
            setSize((int)(380 * scaleFactor), (int)(240 * scaleFactor));
            setLocationRelativeTo(parent);
        }
    }
}