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
import com.petualanganbelajar.repository.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Layar Permainan Utama.
 * Updated: Support 'COUNTING' & 'MATH' dengan aset dinamis.
 */
public class GameScreen extends JPanel {

    // Data Game
    private ModuleModel currentModule;
    private int currentLevel;
    private List<QuestionModel> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int maxScore = 0;

    // Komponen UI
    private JLabel lblUserInfo;
    private JLabel lblLevelInfo;
    private JLabel lblScore;
    
    // Panel Area Tengah (Soal)
    private JPanel questionPanel; 
    private JLabel lblQuestionText;
    private JLabel lblQuestionImage; // Untuk gambar statis atau Teks Besar
    private JPanel dynamicGridPanel; // Untuk gambar dinamis (Counting/Math)

    // Panel Area Bawah (Jawaban)
    private JPanel answerAreaPanel;

    public GameScreen() {
        setLayout(new BorderLayout());
        setBackground(GameConfig.COLOR_BG);

        // --- 1. HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GameConfig.COLOR_PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        lblUserInfo = new JLabel("Player");
        lblUserInfo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        lblUserInfo.setForeground(Color.WHITE);
        headerPanel.add(lblUserInfo, BorderLayout.WEST);

        lblLevelInfo = new JLabel("LEVEL -");
        lblLevelInfo.setFont(GameConfig.FONT_SUBTITLE);
        lblLevelInfo.setForeground(Color.WHITE);
        lblLevelInfo.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblLevelInfo, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        lblScore = new JLabel("SKOR: 0");
        lblScore.setFont(GameConfig.FONT_SUBTITLE);
        lblScore.setForeground(Color.YELLOW);
        JButton btnPause = new JButton("⏸ MENU");
        btnPause.setFont(new Font("Arial", Font.BOLD, 14));
        btnPause.setBackground(new Color(255, 140, 0));
        btnPause.setForeground(Color.WHITE);
        btnPause.setFocusPainted(false);
        btnPause.addActionListener(e -> showPauseMenu());
        rightPanel.add(lblScore);
        rightPanel.add(btnPause);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. AREA SOAL (Tengah) ---
        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBackground(Color.WHITE);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Komponen visual
        lblQuestionImage = new JLabel();
        lblQuestionImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblQuestionImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblQuestionImage.setVisible(false);

        dynamicGridPanel = new JPanel();
        dynamicGridPanel.setOpaque(false);
        dynamicGridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dynamicGridPanel.setVisible(false);

        lblQuestionText = new JLabel("Memuat Soal...", SwingConstants.CENTER);
        lblQuestionText.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        lblQuestionText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        questionPanel.add(Box.createVerticalGlue());
        questionPanel.add(lblQuestionImage); // Slot 1
        questionPanel.add(dynamicGridPanel); // Slot 2
        questionPanel.add(Box.createVerticalStrut(20));
        questionPanel.add(lblQuestionText);
        questionPanel.add(Box.createVerticalGlue());

        add(questionPanel, BorderLayout.CENTER);

        // --- 3. AREA JAWABAN (Bawah) ---
        answerAreaPanel = new JPanel();
        answerAreaPanel.setBackground(GameConfig.COLOR_BG);
        answerAreaPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        add(answerAreaPanel, BorderLayout.SOUTH);
    }

    // --- LOGIKA GAMEPLAY ---
    public void startGame(ModuleModel module, int level) {
        this.currentModule = module;
        this.currentLevel = level;
        this.score = 0;
        this.currentQuestionIndex = 0;

        UserModel u = GameState.getCurrentUser();
        String avatar = (u != null && u.getAvatar().contains("1")) ? "🧒 " : (u != null && u.getAvatar().contains("2")) ? "👧 " : "😊 ";
        lblUserInfo.setText(avatar + (u != null ? u.getName() : "Tamu"));
        lblLevelInfo.setText(module.getName() + " - LVL " + level);
        lblScore.setText("SKOR: 0");

        QuestionRepository repo = new QuestionRepository();
        questionList = repo.getQuestionsByModule(module.getId(), level);

        if (questionList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Soal belum tersedia untuk level ini.");
            ScreenManager.getInstance().showScreen("LEVEL_SELECT");
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
        
        // --- 1. HANDLING GAMBAR / VISUAL ---
        if ("COUNTING".equals(type)) {
            // Tampilkan Grid Gambar Berulang
            lblQuestionImage.setVisible(false);
            int count = 1; 
            try { count = Integer.parseInt(q.getCorrectAnswer()); } catch (Exception e) {}
            setupDynamicGrid(q.getQuestionImage(), count);
            dynamicGridPanel.setVisible(true);

        } else if ("MATH".equals(type)) {
            // Tampilkan Rumus Matematika (2 Apel + 1 Apel)
            lblQuestionImage.setVisible(false);
            setupMathVisual(q.getQuestionImage(), q.getOptionA()); // Option A nyimpan rumus "2|1"
            dynamicGridPanel.setVisible(true);

        } else {
            // Mode Normal (Gambar Statis atau Teks Besar)
            dynamicGridPanel.setVisible(false);
            String imgName = q.getQuestionImage();

            if (imgName != null && !imgName.isEmpty()) {
                // Tampilkan Gambar dari File
                try {
                    File imgFile = new File("resources/images/" + imgName);
                    if(imgFile.exists()) {
                        ImageIcon icon = new ImageIcon(imgFile.toString());
                        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                        lblQuestionImage.setIcon(new ImageIcon(img));
                        lblQuestionImage.setText("");
                    } else {
                        lblQuestionImage.setIcon(null);
                        lblQuestionImage.setText("[Gambar: " + imgName + "]");
                        lblQuestionImage.setFont(new Font("Arial", Font.ITALIC, 14));
                    }
                    lblQuestionImage.setVisible(true);
                } catch (Exception e) {
                    lblQuestionImage.setVisible(false);
                }
            } else {
                // Jika Gambar Kosong (misal Modul Huruf Level 1)
                // Kita tampilkan HURUF BESAR pakai Font
                if ("CLICK".equals(type) && q.getCorrectAnswer().length() == 1) {
                    lblQuestionImage.setIcon(null);
                    lblQuestionImage.setText(q.getCorrectAnswer()); // Huruf "A"
                    lblQuestionImage.setFont(new Font("Comic Sans MS", Font.BOLD, 120));
                    lblQuestionImage.setForeground(GameConfig.COLOR_PRIMARY);
                    lblQuestionImage.setVisible(true);
                } else {
                    lblQuestionImage.setVisible(false);
                }
            }
        }

        // --- 2. HANDLING AUDIO ---
        if (q.getQuestionAudio() != null) {
            SoundPlayer.getInstance().playSFX(q.getQuestionAudio());
        }

        // --- 3. HANDLING TOMBOL JAWABAN ---
        setupDynamicUI(q);
    }

    // Helper: Buat Grid Gambar (COUNTING)
    private void setupDynamicGrid(String imageName, int count) {
        dynamicGridPanel.removeAll();
        dynamicGridPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        ImageIcon icon = loadIcon(imageName, 80);
        
        for (int i = 0; i < count; i++) {
            JLabel label = new JLabel();
            if (icon != null) {
                label.setIcon(icon);
            } else {
                label.setText("📦");
                label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            }
            dynamicGridPanel.add(label);
        }
        dynamicGridPanel.revalidate();
        dynamicGridPanel.repaint();
    }

    // Helper: Buat Visual Matematika (MATH)
    private void setupMathVisual(String imageName, String formula) {
        dynamicGridPanel.removeAll();
        dynamicGridPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        ImageIcon icon = loadIcon(imageName, 60);
        
        // Parse Rumus "2|1"
        int left = 0, right = 0;
        try {
            String[] parts = formula.split("\\|");
            left = Integer.parseInt(parts[0]);
            right = Integer.parseInt(parts[1]);
        } catch (Exception e) { left=1; right=1; }

        // Render Kiri
        for(int i=0; i<left; i++) dynamicGridPanel.add(new JLabel(icon != null ? icon : new ImageIcon()));
        
        // Tanda Tambah
        JLabel lblPlus = new JLabel("+");
        lblPlus.setFont(new Font("Arial", Font.BOLD, 40));
        dynamicGridPanel.add(lblPlus);
        
        // Render Kanan
        for(int i=0; i<right; i++) dynamicGridPanel.add(new JLabel(icon != null ? icon : new ImageIcon()));
        
        // Tanda Sama Dengan & Tanya
        JLabel lblEq = new JLabel("= ?");
        lblEq.setFont(new Font("Arial", Font.BOLD, 40));
        lblEq.setForeground(Color.RED);
        dynamicGridPanel.add(lblEq);

        dynamicGridPanel.revalidate();
        dynamicGridPanel.repaint();
    }

    // Utilitas Load Gambar Aman
    private ImageIcon loadIcon(String imageName, int size) {
        try {
            File imgFile = new File("resources/images/" + imageName);
            if (imgFile.exists()) {
                Image img = new ImageIcon(imgFile.toString()).getImage();
                return new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {}
        return null;
    }

    // Setup Area Jawaban
    private void setupDynamicUI(QuestionModel q) {
        answerAreaPanel.removeAll(); 
        String type = q.getQuestionType();
        if (type == null) type = "CHOICE"; 

        switch (type) {
            case "COUNTING": 
            case "CHOICE":
            case "SEQUENCE": 
                setupChoiceUI(q);
                break;
            case "TYPING":
                setupTypingUI(q);
                break;
            case "KEYPAD":
                setupKeypadUI(q);
                break;
            case "CLICK": // Reuse Click UI (tombol kotak-kotak)
            case "MATH":  
                setupClickUI(q);
                break;
            default:
                setupChoiceUI(q); 
        }
        answerAreaPanel.revalidate();
        answerAreaPanel.repaint();
    }

    // --- SUB-METODE UI ---
    
    private void setupChoiceUI(QuestionModel q) {
        answerAreaPanel.setLayout(new GridLayout(1, 3, 20, 0));
        String[] options = {q.getOptionA(), q.getOptionB(), q.getOptionC()};
        for (String opt : options) {
            if (opt == null) continue;
            JButton btn = createGameButton(opt);
            btn.addActionListener(e -> checkAnswer(q, opt));
            answerAreaPanel.add(btn);
        }
    }

    private void setupTypingUI(QuestionModel q) {
        answerAreaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel lblInstruction = new JLabel("KETIK JAWABANMU: ");
        lblInstruction.setFont(GameConfig.FONT_SUBTITLE);
        JTextField txtInput = new JTextField(10);
        txtInput.setFont(new Font("Arial", Font.BOLD, 32));
        txtInput.setHorizontalAlignment(JTextField.CENTER);
        JButton btnSubmit = new JButton("KIRIM");
        btnSubmit.setFont(GameConfig.FONT_SUBTITLE);
        btnSubmit.setBackground(new Color(50, 205, 50)); 
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(120, 50));
        ActionListener submitAction = e -> {
            String ans = txtInput.getText().trim();
            if (!ans.isEmpty()) checkAnswer(q, ans);
        };
        btnSubmit.addActionListener(submitAction);
        txtInput.addActionListener(submitAction); 
        answerAreaPanel.add(lblInstruction);
        answerAreaPanel.add(txtInput);
        answerAreaPanel.add(btnSubmit);
        SwingUtilities.invokeLater(txtInput::requestFocusInWindow);
    }

    private void setupKeypadUI(QuestionModel q) {
        answerAreaPanel.setLayout(new GridLayout(4, 7, 5, 5));
        for (char c = 'A'; c <= 'Z'; c++) {
            String letter = String.valueOf(c);
            JButton btn = new JButton(letter);
            btn.setFont(new Font("Arial", Font.BOLD, 20));
            btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> checkAnswer(q, letter));
            answerAreaPanel.add(btn);
        }
    }

    private void setupClickUI(QuestionModel q) {
        answerAreaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        List<String> clickables = new ArrayList<>();
        clickables.add(q.getCorrectAnswer()); 
        
        // Generate Distractor (Jawaban Salah)
        if (q.getCorrectAnswer().matches("[0-9]+")) { // Jika angka
            int ans = Integer.parseInt(q.getCorrectAnswer());
            clickables.add(String.valueOf(ans + 1));
            clickables.add(String.valueOf(ans > 1 ? ans - 1 : ans + 2));
        } else { // Jika huruf/kata
            clickables.add("X"); 
            clickables.add("Z"); 
        }
        Collections.shuffle(clickables); 
        
        for (String label : clickables) {
            JButton btn = createGameButton(label);
            btn.setPreferredSize(new Dimension(120, 120)); 
            btn.addActionListener(e -> checkAnswer(q, label));
            answerAreaPanel.add(btn);
        }
    }

    private JButton createGameButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setBackground(GameConfig.COLOR_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 100));
        return btn;
    }

    private void checkAnswer(QuestionModel q, String answer) {
        if (q.checkAnswer(answer)) {
            SoundPlayer.getInstance().playSFX("correct.wav");
            score += 10;
            lblScore.setText("SKOR: " + score);
            JOptionPane.showMessageDialog(this, "HEBAT! Jawabanmu Benar! (+10)");
        } else {
            SoundPlayer.getInstance().playSFX("wrong.wav");
            JOptionPane.showMessageDialog(this, "Yah kurang tepat... Jawabannya: " + q.getCorrectAnswer());
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
                
                // Cek Tamat Global
                if (currentLevel == 3 && repo.isGameCompleted(user.getId())) {
                    int choice = JOptionPane.showConfirmDialog(this, 
                        "LUAR BIASA! Kamu telah menyelesaikan SEMUA tugas pesta!\nSiap melihat Pesta Hutan?", 
                        "Pesta Siap!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    ModuleModel epilogModule = new ModuleModel(99, "EPILOGUE", "Tamat");
                    ScreenManager.getInstance().showStory(epilogModule, 1);
                    return;
                }
            }
        }
        ScreenManager.getInstance().showResult(currentModule, currentLevel, score, maxScore);
    }

    private void showPauseMenu() {
        JPanel pausePanel = new JPanel();
        pausePanel.setLayout(new BoxLayout(pausePanel, BoxLayout.Y_AXIS));
        pausePanel.setPreferredSize(new Dimension(300, 150));
        
        UserModel user = GameState.getCurrentUser();
        int currentBGM = (user != null) ? user.getBgmVolume() : 80;
        int currentSFX = (user != null) ? user.getSfxVolume() : 100;

        JLabel lblBGM = new JLabel("Musik: " + currentBGM + "%");
        lblBGM.setAlignmentX(Component.CENTER_ALIGNMENT);
        JSlider sliderBGM = new JSlider(0, 100, currentBGM);
        sliderBGM.setAlignmentX(Component.CENTER_ALIGNMENT);
        sliderBGM.addChangeListener(e -> {
            int val = sliderBGM.getValue();
            lblBGM.setText("Musik: " + val + "%");
            SoundPlayer.getInstance().setBGMVolume(val);
        });

        JLabel lblSFX = new JLabel("Efek Suara: " + currentSFX + "%");
        lblSFX.setAlignmentX(Component.CENTER_ALIGNMENT);
        JSlider sliderSFX = new JSlider(0, 100, currentSFX);
        sliderSFX.setAlignmentX(Component.CENTER_ALIGNMENT);
        sliderSFX.addChangeListener(e -> {
            int val = sliderSFX.getValue();
            lblSFX.setText("Efek Suara: " + val + "%");
            SoundPlayer.getInstance().setSFXVolume(val);
        });

        pausePanel.add(new JLabel("--- PENGATURAN SUARA ---"));
        pausePanel.add(Box.createVerticalStrut(10));
        pausePanel.add(lblBGM);
        pausePanel.add(sliderBGM);
        pausePanel.add(Box.createVerticalStrut(10));
        pausePanel.add(lblSFX);
        pausePanel.add(sliderSFX);

        Object[] options = {"Lanjut Main", "Ganti Modul", "Keluar ke Menu"};
        int choice = JOptionPane.showOptionDialog(this, pausePanel, "Menu Jeda", 
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (user != null) {
            int newBGM = sliderBGM.getValue();
            int newSFX = sliderSFX.getValue();
            user.setBgmVolume(newBGM);
            user.setSfxVolume(newSFX);
            UserRepository userRepo = new UserRepository();
            userRepo.updateVolume(user.getId(), newBGM, newSFX);
        }

        if (choice == 1) { 
            ScreenManager.getInstance().showScreen("MODULE_SELECT");
        } else if (choice == 2) { 
            ScreenManager.getInstance().showScreen("MAIN_MENU");
        }
    }
}