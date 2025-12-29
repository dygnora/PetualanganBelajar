package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.QuestionModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.QuestionRepository;
import com.petualanganbelajar.repository.ProgressRepository;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Layar Permainan Utama (Updated: Static BG & Custom Pause)
 */
public class GameScreen extends JPanel {

    // Data Game
    private ModuleModel currentModule;
    private int currentLevel;
    private List<QuestionModel> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int maxScore = 0;
    
    // Aset Gambar
    private Image bgImage;          // Background Utama (Static)
    private Image imgBoard;         // Papan Soal
    private Image imgPause;         // Icon Pause

    // Komponen UI Header
    private JLabel lblUserInfo;
    private JLabel lblLevelInfo;
    private JLabel lblScore;
    
    // Panel Area Tengah (Soal)
    private BoardPanel questionPanel; 
    private JLabel lblQuestionText;
    private JLabel lblQuestionImage; 
    private JPanel dynamicGridPanel; 

    // Panel Area Bawah (Jawaban)
    private JPanel answerAreaPanel;

    public GameScreen() {
        setLayout(new BorderLayout());
        loadAssets(); 
        initUI();
    }
    
    private void loadAssets() {
        try {
            // 1. BACKGROUND STATIS (Sesuai Permintaan)
            File bgFile = new File("resources/images/bg_gamescreen.png");
            if(bgFile.exists()) {
                bgImage = ImageIO.read(bgFile);
            } else {
                // Fallback jika gambar belum ada
                System.out.println("Warning: bg_gamescreen.png not found.");
            }
            
            // Papan Soal (Wooden Board)
            if(new File("resources/images/bg_board_wood.png").exists())
                imgBoard = ImageIO.read(new File("resources/images/bg_board_wood.png"));
            
            // Icon Pause
            if(new File("resources/images/btn_pause.png").exists())
                imgPause = ImageIO.read(new File("resources/images/btn_pause.png"));
                
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method loadModuleAssets DIHAPUS karena background sekarang statis.

    private void initUI() {
        // --- 1. HEADER (Floating UI) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(15, 20, 0, 20));

        // KIRI: Profil Player (Badge Kayu)
        JPanel profileBadge = new BadgePanel(new Color(101, 67, 33)); 
        lblUserInfo = new JLabel("Player");
        lblUserInfo.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        lblUserInfo.setForeground(Color.WHITE);
        profileBadge.add(lblUserInfo);
        headerPanel.add(profileBadge, BorderLayout.WEST);

        // TENGAH: Info Level
        lblLevelInfo = new JLabel("LEVEL -", SwingConstants.CENTER);
        lblLevelInfo.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        lblLevelInfo.setForeground(Color.WHITE);
        headerPanel.add(lblLevelInfo, BorderLayout.CENTER);

        // KANAN: Skor & Pause
        JPanel rightContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightContainer.setOpaque(false);
        
        JPanel scoreBadge = new BadgePanel(new Color(255, 193, 7));
        lblScore = new JLabel("SKOR: 0");
        lblScore.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        lblScore.setForeground(new Color(62, 39, 35));
        scoreBadge.add(lblScore);
        
        // Tombol Pause
        JButton btnPause = new JButton("||");
        if (imgPause != null) {
            btnPause.setText("");
            btnPause.setIcon(new ImageIcon(imgPause.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        }
        btnPause.setPreferredSize(new Dimension(55, 55));
        btnPause.setContentAreaFilled(false);
        btnPause.setBorderPainted(false);
        btnPause.setFocusPainted(false);
        btnPause.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPause.addActionListener(e -> showCustomPauseMenu()); // Memanggil menu pause baru

        rightContainer.add(scoreBadge);
        rightContainer.add(btnPause);
        headerPanel.add(rightContainer, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. AREA SOAL (Tengah) ---
        questionPanel = new BoardPanel(); 
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBorder(new EmptyBorder(40, 50, 40, 50)); 

        lblQuestionImage = new JLabel();
        lblQuestionImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblQuestionImage.setVisible(false);

        dynamicGridPanel = new JPanel();
        dynamicGridPanel.setOpaque(false);
        dynamicGridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dynamicGridPanel.setVisible(false);

        lblQuestionText = new JLabel("Memuat Soal...", SwingConstants.CENTER);
        lblQuestionText.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        lblQuestionText.setForeground(new Color(62, 39, 35));
        lblQuestionText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        questionPanel.add(Box.createVerticalGlue());
        questionPanel.add(lblQuestionImage); 
        questionPanel.add(dynamicGridPanel); 
        questionPanel.add(Box.createVerticalStrut(20));
        questionPanel.add(lblQuestionText);
        questionPanel.add(Box.createVerticalGlue());

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(questionPanel);
        
        add(centerWrapper, BorderLayout.CENTER);

        // --- 3. AREA JAWABAN (Bawah) ---
        answerAreaPanel = new JPanel();
        answerAreaPanel.setOpaque(false);
        answerAreaPanel.setBorder(new EmptyBorder(10, 20, 30, 20));
        add(answerAreaPanel, BorderLayout.SOUTH);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback Gradient jika gambar bg_gamescreen.png tidak ada
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, new Color(135, 206, 235), 0, getHeight(), new Color(255, 255, 255)));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        // Overlay tipis
        g.setColor(new Color(0, 0, 0, 20));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    public void startGame(ModuleModel module, int level) {
        this.currentModule = module;
        this.currentLevel = level;
        this.score = 0;
        this.currentQuestionIndex = 0;
        
        // Tidak perlu loadModuleAssets lagi
        
        UserModel u = GameState.getCurrentUser();
        lblUserInfo.setText(u != null ? u.getName() : "Tamu");
        
        lblLevelInfo.setText("<html><span style='color:white; font-size:36px; font-family:Comic Sans MS; text-shadow: 2px 2px #000000;'>" 
                + module.getName() + " - LVL " + level + "</span></html>");
        
        lblScore.setText("SKOR: 0");

        QuestionRepository repo = new QuestionRepository();
        questionList = repo.getQuestionsByModule(module.getId(), level);

        if (questionList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Soal belum tersedia untuk level ini.");
            ScreenManager.getInstance().showScreen("MODULE_SELECT");
            return;
        }
        this.maxScore = questionList.size() * 10;
        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestionIndex >= questionList.size()) {
            finishGame();
            return;
        }

        QuestionModel q = questionList.get(currentQuestionIndex);
        lblQuestionText.setText("<html><center>" + q.getQuestionText() + "</center></html>");
        String type = q.getQuestionType();
        
        // --- HANDLING GAMBAR & VISUAL (Sama seperti sebelumnya) ---
        // (Kode handling visual dipersingkat di sini agar fokus pada perubahan pause)
        if ("COUNTING".equals(type)) {
            lblQuestionImage.setVisible(false);
            int count = 1; try { count = Integer.parseInt(q.getCorrectAnswer()); } catch (Exception e) {}
            setupDynamicGrid(q.getQuestionImage(), count);
            dynamicGridPanel.setVisible(true);
        } else if ("MATH".equals(type)) {
            lblQuestionImage.setVisible(false);
            setupMathVisual(q.getQuestionImage(), q.getOptionA());
            dynamicGridPanel.setVisible(true);
        } else {
            dynamicGridPanel.setVisible(false);
            String imgName = q.getQuestionImage();
            if (imgName != null && !imgName.isEmpty()) {
                File imgFile = new File("resources/images/" + imgName);
                if(imgFile.exists()) {
                    ImageIcon icon = new ImageIcon(imgFile.toString());
                    Image img = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
                    lblQuestionImage.setIcon(new ImageIcon(img));
                    lblQuestionImage.setText("");
                } else {
                    lblQuestionImage.setIcon(null); lblQuestionImage.setText("[?]");
                }
                lblQuestionImage.setVisible(true);
            } else {
                 if ("CLICK".equals(type) && q.getCorrectAnswer().length() == 1) {
                    lblQuestionImage.setIcon(null); lblQuestionImage.setText(q.getCorrectAnswer()); 
                    lblQuestionImage.setFont(new Font("Comic Sans MS", Font.BOLD, 140));
                    lblQuestionImage.setForeground(new Color(230, 81, 0));
                    lblQuestionImage.setVisible(true);
                } else {
                    lblQuestionImage.setVisible(false);
                }
            }
        }

        if (q.getQuestionAudio() != null) {
            SoundPlayer.getInstance().playSFX(q.getQuestionAudio());
        }

        setupDynamicUI(q);
        revalidate(); repaint();
    }

    // --- Helper Visual (Grid & Math) Tetap Sama ---
    private void setupDynamicGrid(String imageName, int count) {
        dynamicGridPanel.removeAll();
        int cols = count > 5 ? 5 : count; int rows = (int) Math.ceil((double)count / cols);
        dynamicGridPanel.setLayout(new GridLayout(rows, cols, 10, 10));
        ImageIcon icon = loadIcon(imageName, 90);
        for (int i = 0; i < count; i++) {
            JLabel label = new JLabel(); label.setHorizontalAlignment(SwingConstants.CENTER);
            if (icon != null) label.setIcon(icon);
            else { label.setText("📦"); label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50)); }
            dynamicGridPanel.add(label);
        }
    }
    
    private void setupMathVisual(String imageName, String formula) {
        dynamicGridPanel.removeAll();
        dynamicGridPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        ImageIcon icon = loadIcon(imageName, 70);
        int left = 1, right = 1; try { String[] parts = formula.split("\\|"); left = Integer.parseInt(parts[0]); right = Integer.parseInt(parts[1]); } catch (Exception e) {}
        JPanel pLeft = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); pLeft.setOpaque(false);
        for(int i=0; i<left; i++) pLeft.add(new JLabel(icon != null ? icon : new ImageIcon()));
        dynamicGridPanel.add(pLeft);
        JLabel lblPlus = new JLabel("+"); lblPlus.setFont(new Font("Comic Sans MS", Font.BOLD, 50)); lblPlus.setForeground(new Color(46, 125, 50));
        dynamicGridPanel.add(lblPlus);
        JPanel pRight = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); pRight.setOpaque(false);
        for(int i=0; i<right; i++) pRight.add(new JLabel(icon != null ? icon : new ImageIcon()));
        dynamicGridPanel.add(pRight);
        JLabel lblEq = new JLabel("= ?"); lblEq.setFont(new Font("Comic Sans MS", Font.BOLD, 50)); lblEq.setForeground(new Color(198, 40, 40));
        dynamicGridPanel.add(lblEq);
    }
    
    private ImageIcon loadIcon(String imageName, int size) {
        try {
            File imgFile = new File("resources/images/" + imageName);
            if (imgFile.exists()) return new ImageIcon(new ImageIcon(imgFile.toString()).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        return null;
    }

    // --- SETUP TOMBOL JAWABAN (Switch Case) ---
    private void setupDynamicUI(QuestionModel q) {
        answerAreaPanel.removeAll(); 
        String type = q.getQuestionType();
        if (type == null) type = "CHOICE"; 

        switch (type) {
            case "COUNTING": case "CHOICE": case "SEQUENCE": setupChoiceUI(q); break;
            case "TYPING": setupTypingUI(q); break;
            case "KEYPAD": setupKeypadUI(q); break;
            case "CLICK": case "MATH": setupClickUI(q); break;
            default: setupChoiceUI(q); 
        }
        answerAreaPanel.revalidate(); answerAreaPanel.repaint();
    }

    // ... (Method setupChoiceUI, setupTypingUI, setupKeypadUI, setupClickUI, checkAnswer, finishGame SAMA seperti sebelumnya) ...
    // Saya ringkas agar fokus ke fitur baru, namun Anda tetap perlu menyertakan logika tersebut di file asli.
    
    private void setupChoiceUI(QuestionModel q) {
        answerAreaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
        String[] options = {q.getOptionA(), q.getOptionB(), q.getOptionC()};
        Color[] colors = { new Color(66, 165, 245), new Color(102, 187, 106), new Color(255, 167, 38) };
        for (int i = 0; i < options.length; i++) {
            if (options[i] == null) continue;
            String opt = options[i];
            AnswerButton btn = new AnswerButton(opt, colors[i % 3]);
            btn.addActionListener(e -> checkAnswer(q, opt));
            answerAreaPanel.add(btn);
        }
    }
    
    private void setupTypingUI(QuestionModel q) { /* Logika Typing sama... */ }
    private void setupKeypadUI(QuestionModel q) { /* Logika Keypad sama... */ }
    private void setupClickUI(QuestionModel q) { /* Logika Click sama... */ }

    private void checkAnswer(QuestionModel q, String answer) {
        if (q.checkAnswer(answer)) {
            SoundPlayer.getInstance().playSFX("correct.wav");
            score += 10;
            lblScore.setText("SKOR: " + score);
            JOptionPane.showMessageDialog(this, "HEBAT! Jawabanmu Benar! (+10)", "Benar!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            SoundPlayer.getInstance().playSFX("wrong.wav");
            JOptionPane.showMessageDialog(this, "Yah kurang tepat... Jawabannya: " + q.getCorrectAnswer(), "Ups!", JOptionPane.WARNING_MESSAGE);
        }
        currentQuestionIndex++;
        showQuestion();
    }
    
    private void finishGame() {
        UserModel user = GameState.getCurrentUser();
        if (user != null) {
            ProgressRepository repo = new ProgressRepository();
            repo.saveScore(user.getName(), user.getAvatar(), currentModule.getId(), currentLevel, score);
            if (score >= (maxScore * 0.6)) {
                repo.unlockNextLevel(user.getId(), currentModule.getId(), currentLevel);
                SoundPlayer.getInstance().playSFX("level_complete.wav");
            }
        }
        ScreenManager.getInstance().showResult(currentModule, currentLevel, score, maxScore);
    }

    // =========================================================
    // FITUR BARU: MENU PAUSE CUSTOM
    // =========================================================
    
    private void showCustomPauseMenu() {
        // Buat Dialog Modal (Memblokir input ke game di belakangnya)
        JDialog pauseDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Paused", true);
        pauseDialog.setUndecorated(true); // Hilangkan border window standar
        pauseDialog.setBackground(new Color(0, 0, 0, 0)); // Transparan agar bentuk custom terlihat
        
        // Panel Konten Utama (Bentuk Papan Kayu)
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gambar Kayu
                if (imgBoard != null) {
                    g2.drawImage(imgBoard, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(new Color(139, 69, 19));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                }
                
                // Border Emas
                g2.setColor(new Color(255, 215, 0));
                g2.setStroke(new BasicStroke(5));
                g2.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 25, 25);
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        contentPanel.setPreferredSize(new Dimension(400, 400));
        
        // 1. Judul PAUSE
        JLabel lblTitle = new JLabel("ISTIRAHAT DULU");
        lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        lblTitle.setForeground(new Color(62, 39, 35));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // 2. Volume Slider
        JLabel lblVol = new JLabel("Volume Suara");
        lblVol.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        lblVol.setForeground(new Color(62, 39, 35));
        lblVol.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblVol);
        
        JSlider volumeSlider = new JSlider(0, 100, 50); // Default 50
        volumeSlider.setOpaque(false);
        volumeSlider.setMaximumSize(new Dimension(250, 40));
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Sambungkan ke SoundPlayer logic
                // float vol = volumeSlider.getValue() / 100f;
                // SoundPlayer.getInstance().setVolume(vol); 
            }
        });
        contentPanel.add(volumeSlider);
        contentPanel.add(Box.createVerticalStrut(30));
        
        // 3. Tombol-Tombol
        // Tombol Resume
        JButton btnResume = createMenuButton("Lanjut Main", new Color(102, 187, 106));
        btnResume.addActionListener(e -> pauseDialog.dispose());
        
        // Tombol Pilihan Modul
        JButton btnModules = createMenuButton("Pilih Modul", new Color(255, 167, 38));
        btnModules.addActionListener(e -> {
            pauseDialog.dispose();
            ScreenManager.getInstance().showScreen("MODULE_SELECT");
        });
        
        // Tombol Exit
        JButton btnExit = createMenuButton("Keluar (Menu Utama)", new Color(229, 57, 53));
        btnExit.addActionListener(e -> {
            pauseDialog.dispose();
            ScreenManager.getInstance().showScreen("MAIN_MENU");
        });

        contentPanel.add(btnResume);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(btnModules);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(btnExit);
        
        pauseDialog.add(contentPanel);
        pauseDialog.pack();
        pauseDialog.setLocationRelativeTo(this); // Muncul di tengah layar game
        pauseDialog.setVisible(true);
    }
    
    // Helper untuk membuat tombol menu pause yang konsisten
    private JButton createMenuButton(String text, Color color) {
        AnswerButton btn = new AnswerButton(text, color);
        btn.setFont(new Font("Comic Sans MS", Font.BOLD, 18)); // Font lebih kecil dari tombol jawaban
        btn.setPreferredSize(new Dimension(250, 50));
        btn.setMaximumSize(new Dimension(250, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    // =========================================================
    // KOMPONEN CUSTOM (Tetap Sama)
    // =========================================================

    class BoardPanel extends JPanel {
        public BoardPanel() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (imgBoard != null) {
                g2.drawImage(imgBoard, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2.setColor(new Color(255, 250, 240));
                g2.fillRoundRect(10, 10, getWidth()-20, getHeight()-20, 40, 40);
                g2.setColor(new Color(139, 69, 19));
                g2.setStroke(new BasicStroke(8));
                g2.drawRoundRect(10, 10, getWidth()-20, getHeight()-20, 40, 40);
            }
            super.paintComponent(g);
        }
    }

    class BadgePanel extends JPanel {
        private Color c;
        public BadgePanel(Color c) { 
            super(new FlowLayout(FlowLayout.CENTER, 0, 0)); 
            this.c=c; setOpaque(false); 
            setBorder(new EmptyBorder(8, 20, 8, 20)); 
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c); g2.fillRoundRect(0,0,getWidth(),getHeight(),30,30);
            g2.setColor(new Color(255,255,255,100)); g2.setStroke(new BasicStroke(2)); 
            g2.drawRoundRect(2,2,getWidth()-4,getHeight()-4,25,25);
            super.paintComponent(g);
        }
    }

    class AnswerButton extends JButton {
        private Color baseColor; 
        private boolean hover;
        public AnswerButton(String text, Color color) {
            super(text); this.baseColor = color;
            setFont(new Font("Comic Sans MS", Font.BOLD, 32)); 
            setForeground(Color.WHITE);
            setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false); 
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(200, 80));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight(); 
            int offset = getModel().isPressed() ? 4 : 0;
            g2.setColor(new Color(0,0,0,40)); g2.fillRoundRect(5, 10, w-10, h-10, 30, 30);
            g2.setColor(baseColor.darker()); g2.fillRoundRect(0, 8+offset, w, h-8-offset, 30, 30);
            g2.setColor(hover ? baseColor.brighter() : baseColor); g2.fillRoundRect(0, offset, w, h-8, 30, 30);
            super.paintComponent(g2); g2.dispose();
        }
    }
}