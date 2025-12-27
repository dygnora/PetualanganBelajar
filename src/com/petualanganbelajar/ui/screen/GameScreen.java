package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer; // [PENTING] Import Sound
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.QuestionModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.QuestionRepository;
import com.petualanganbelajar.repository.ProgressRepository; // [PENTING] Import Progress
import javax.swing.*;
import java.awt.*;
import java.util.List;

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
    
    private JLabel lblQuestionText;
    private JLabel lblQuestionImage;
    private JButton btnA, btnB, btnC;

    public GameScreen() {
        setLayout(new BorderLayout());
        setBackground(GameConfig.COLOR_BG);
        
        // 1. HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GameConfig.COLOR_PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // KIRI: User Info
        lblUserInfo = new JLabel("👤 Player");
        lblUserInfo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        lblUserInfo.setForeground(Color.WHITE);
        headerPanel.add(lblUserInfo, BorderLayout.WEST);
        
        // TENGAH: Level Info
        lblLevelInfo = new JLabel("LEVEL -");
        lblLevelInfo.setFont(GameConfig.FONT_SUBTITLE);
        lblLevelInfo.setForeground(Color.WHITE);
        lblLevelInfo.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblLevelInfo, BorderLayout.CENTER);
        
        // KANAN: Skor
        lblScore = new JLabel("SKOR: 0");
        lblScore.setFont(GameConfig.FONT_SUBTITLE);
        lblScore.setForeground(Color.YELLOW);
        headerPanel.add(lblScore, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // 2. Area Soal
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBackground(Color.WHITE);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        lblQuestionImage = new JLabel();
        lblQuestionImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblQuestionImage.setVisible(false); 
        
        lblQuestionText = new JLabel("Memuat Soal...", SwingConstants.CENTER);
        lblQuestionText.setFont(GameConfig.FONT_TITLE);
        lblQuestionText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        questionPanel.add(Box.createVerticalGlue());
        questionPanel.add(lblQuestionImage);
        questionPanel.add(Box.createVerticalStrut(20));
        questionPanel.add(lblQuestionText);
        questionPanel.add(Box.createVerticalGlue());
        
        add(questionPanel, BorderLayout.CENTER);
        
        // 3. Area Jawaban
        JPanel optionsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        optionsPanel.setBackground(GameConfig.COLOR_BG);
        
        btnA = createOptionButton();
        btnB = createOptionButton();
        btnC = createOptionButton();
        
        optionsPanel.add(btnA);
        optionsPanel.add(btnB);
        optionsPanel.add(btnC);
        add(optionsPanel, BorderLayout.SOUTH);
    }
    
    private JButton createOptionButton() {
        JButton btn = new JButton("-");
        btn.setFont(GameConfig.FONT_SUBTITLE);
        btn.setBackground(GameConfig.COLOR_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 100));
        return btn;
    }

    // --- LOGIKA GAMEPLAY ---

    public void startGame(ModuleModel module, int level) {
        this.currentModule = module;
        this.currentLevel = level;
        this.score = 0;
        this.currentQuestionIndex = 0;
        
        // TAMPILKAN USER INFO
        UserModel u = GameState.getCurrentUser();
        if (u != null) {
            String avatarVisual = "👤"; 
            if ("avatar_1.png".equals(u.getAvatar())) avatarVisual = "👦";
            if ("avatar_2.png".equals(u.getAvatar())) avatarVisual = "👧";
            if ("avatar_3.png".equals(u.getAvatar())) avatarVisual = "🐱";
            lblUserInfo.setText(avatarVisual + " " + u.getName());
        }
        
        lblLevelInfo.setText(module.getName() + " - LVL " + level);
        lblScore.setText("SKOR: 0");
        
        // Load Soal
        QuestionRepository repo = new QuestionRepository();
        questionList = repo.getQuestionsByModule(module.getId(), level); 
        
        if (questionList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada soal untuk Level " + level + "!");
            ScreenManager.getInstance().showScreen("LEVEL_SELECT");
            return;
        }
        
        int pointsPerQuestion = currentLevel * 10;
        this.maxScore = questionList.size() * pointsPerQuestion;
        
        showQuestion();
    }
    
    private void showQuestion() {
        if (currentQuestionIndex >= questionList.size()) {
            finishGame();
            return;
        }
        
        QuestionModel q = questionList.get(currentQuestionIndex);
        lblQuestionText.setText("<html><center>" + q.getQuestionText() + "</center></html>");
        
        // 1. Logic Gambar
        String imageName = q.getQuestionImage();
        if (imageName != null && !imageName.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon("resources/images/" + imageName);
                if (icon.getIconWidth() > 0) {
                     Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                     lblQuestionImage.setIcon(new ImageIcon(img));
                     lblQuestionImage.setVisible(true);
                } else {
                     lblQuestionImage.setVisible(false);
                }
            } catch (Exception e) {
                lblQuestionImage.setVisible(false);
            }
        } else {
            lblQuestionImage.setVisible(false);
        }

        // 2. [BARU] Logic Audio Soal (Voiceover)
        // Jika soal punya rekaman suara (misal: "Hitung jumlah apel"), mainkan!
        String audioName = q.getQuestionAudio();
        if (audioName != null && !audioName.isEmpty()) {
            SoundPlayer.getInstance().playSFX(audioName); 
        }

        btnA.setText(q.getOptionA());
        btnB.setText(q.getOptionB());
        btnC.setText(q.getOptionC());
        
        // Reset Listener
        for (java.awt.event.ActionListener al : btnA.getActionListeners()) btnA.removeActionListener(al);
        for (java.awt.event.ActionListener al : btnB.getActionListeners()) btnB.removeActionListener(al);
        for (java.awt.event.ActionListener al : btnC.getActionListeners()) btnC.removeActionListener(al);
        
        btnA.addActionListener(e -> checkAnswer(q, q.getOptionA()));
        btnB.addActionListener(e -> checkAnswer(q, q.getOptionB()));
        btnC.addActionListener(e -> checkAnswer(q, q.getOptionC()));
    }
    
    private void checkAnswer(QuestionModel q, String selectedAnswer) {
        if (q.checkAnswer(selectedAnswer)) {
            int points = currentLevel * 10; 
            score += points;
            
            // [LOGIC BARU] SUARA BENAR
            SoundPlayer.getInstance().playSFX("correct.wav"); 
            
            JOptionPane.showMessageDialog(this, "HEBAT! Jawaban Benar! (+" + points + ")");
        } else {
            // [LOGIC BARU] SUARA SALAH
            SoundPlayer.getInstance().playSFX("wrong.wav"); 
            
            JOptionPane.showMessageDialog(this, "Yah salah... Jawaban yang benar: " + q.getOptionA()); 
        }
        
        lblScore.setText("SKOR: " + score);
        currentQuestionIndex++;
        showQuestion(); 
    }
    
    private void finishGame() {
        // [LOGIC BARU] SAVE PROGRESS & UNLOCK LEVEL
        UserModel user = GameState.getCurrentUser();
        if (user != null) {
            ProgressRepository progressRepo = new ProgressRepository();
            
            // 1. Simpan history skor ke Leaderboard
            progressRepo.saveScore(user.getName(), user.getAvatar(), currentModule.getId(), currentLevel, score);
            
            // 2. Cek apakah lulus KKM (70%)
            double percentage = ((double) score / maxScore) * 100;
            if (percentage >= 70) {
                System.out.println("Lulus! Membuka level berikutnya...");
                progressRepo.unlockNextLevel(user.getId(), currentModule.getId(), currentLevel);
                
                // Mainkan suara menang level (Opsional)
                SoundPlayer.getInstance().playSFX("level_complete.wav");
            }
        }
        
        ScreenManager.getInstance().showResult(currentModule, currentLevel, score, maxScore);
    }
}