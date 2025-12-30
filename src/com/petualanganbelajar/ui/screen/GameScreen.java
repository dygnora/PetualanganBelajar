package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.QuestionModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.QuestionRepository;
import com.petualanganbelajar.repository.ProgressRepository;
import com.petualanganbelajar.util.GameVisualizer;

// IMPORT KOMPONEN UI BARU
import com.petualanganbelajar.ui.component.ModernAnswerButton;
import com.petualanganbelajar.ui.component.ModernPauseButton;
import com.petualanganbelajar.ui.component.PauseMenuDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameScreen extends JPanel {

    private ModuleModel currentModule;
    private int currentLevel;
    private List<QuestionModel> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int maxScore = 0;

    // --- THEME COLORS (Dinamis) ---
    private Color themePrimaryColor = new Color(70, 130, 180);
    private Color themeBgTopColor = new Color(200, 230, 255);
    private Color themeBgBottomColor = Color.WHITE;
    private Color themeAccentColor = new Color(100, 100, 255);

    // UI Components
    private JLabel lblUserInfo;
    private JLabel lblLevelInfo;
    private JLabel lblScore;
    
    // Panel Custom (Inner Class)
    private ModernBoardPanel questionPanel;
    
    private JLabel lblInstruction; 
    private JPanel visualContainer;
    private JPanel answerAreaPanel;

    private final Font FONT_HEADER = new Font("Comic Sans MS", Font.BOLD, 20);
    private final Font FONT_BADGE = new Font("Comic Sans MS", Font.BOLD, 18);
    private final Font FONT_INSTRUCTION = new Font("Comic Sans MS", Font.BOLD, 24);
    
    private QuestionRepository questionRepo = new QuestionRepository();
    private ProgressRepository progressRepo = new ProgressRepository();
    private SoundPlayer soundPlayer = SoundPlayer.getInstance(); 

    public GameScreen() {
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(25, 20, 40, 20)); 

        // LEFT: USER BADGE
        ModernBadgePanel userBadge = new ModernBadgePanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JLabel lblIconUser = createLabel("👤", FONT_BADGE.deriveFont(22f), Color.WHITE);
        lblUserInfo = createLabel("Player", FONT_BADGE, Color.WHITE);
        userBadge.add(lblIconUser);
        userBadge.add(lblUserInfo);

        // CENTER: LEVEL BADGE
        ModernBadgePanel levelBadge = new ModernBadgePanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        lblLevelInfo = createLabel("LEVEL 1", new Font("Comic Sans MS", Font.BOLD, 28), Color.WHITE);
        levelBadge.add(lblLevelInfo);

        // RIGHT: SCORE BADGE & PAUSE
        JPanel rightContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightContainer.setOpaque(false);

        ModernBadgePanel scoreBadge = new ModernBadgePanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JLabel lblIconScore = createLabel("⭐", FONT_BADGE.deriveFont(22f), new Color(255, 223, 0)); 
        lblScore = createLabel("0", FONT_BADGE, Color.WHITE);
        scoreBadge.add(lblScore);
        scoreBadge.add(lblIconScore);
        
        // MENGGUNAKAN KOMPONEN MODULAR
        ModernPauseButton btnPause = new ModernPauseButton(); 
        btnPause.addActionListener(e -> showPauseMenu());

        rightContainer.add(scoreBadge);
        rightContainer.add(btnPause);

        // Wrapper panels to align correctly
        JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,0)); leftWrapper.setOpaque(false); leftWrapper.add(userBadge);
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0)); centerWrapper.setOpaque(false); centerWrapper.add(levelBadge);
        
        headerPanel.add(leftWrapper, BorderLayout.WEST);
        headerPanel.add(centerWrapper, BorderLayout.CENTER);
        headerPanel.add(rightContainer, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- GAME CONTAINER ---
        JPanel gameContainer = new JPanel(new BorderLayout());
        gameContainer.setOpaque(false);
        gameContainer.setBorder(new EmptyBorder(10, 50, 20, 50)); 
        
        questionPanel = new ModernBoardPanel();
        questionPanel.setLayout(new BorderLayout());
        questionPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        lblInstruction = createLabel("Instruksi", FONT_INSTRUCTION, new Color(80, 80, 80));
        lblInstruction.setHorizontalAlignment(SwingConstants.CENTER);
        
        visualContainer = new JPanel();
        visualContainer.setOpaque(false);
        visualContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        questionPanel.add(lblInstruction, BorderLayout.NORTH);
        questionPanel.add(visualContainer, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
        wrapper.add(questionPanel, gbc);
        
        gameContainer.add(wrapper, BorderLayout.CENTER);
        
        answerAreaPanel = new JPanel();
        answerAreaPanel.setOpaque(false);
        answerAreaPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        gameContainer.add(answerAreaPanel, BorderLayout.SOUTH);
        
        add(gameContainer, BorderLayout.CENTER);
    }

    private void applyTheme(int moduleId) {
        switch (moduleId) {
            case 1: // ANGKA
                themePrimaryColor = new Color(46, 139, 87);    
                themeBgTopColor = new Color(152, 251, 152);    
                themeBgBottomColor = new Color(240, 255, 240); 
                themeAccentColor = new Color(34, 139, 34, 50); 
                break;
            case 2: // HURUF
                themePrimaryColor = new Color(30, 144, 255);   
                themeBgTopColor = new Color(135, 206, 250);    
                themeBgBottomColor = new Color(240, 248, 255); 
                themeAccentColor = new Color(255, 255, 255, 100); 
                break;
            case 3: // WARNA
                themePrimaryColor = new Color(199, 21, 133);   
                themeBgTopColor = new Color(255, 182, 193);    
                themeBgBottomColor = new Color(255, 240, 245); 
                themeAccentColor = new Color(255, 105, 180, 50); 
                break;
            case 4: // BENTUK
                themePrimaryColor = new Color(210, 105, 30);   
                themeBgTopColor = new Color(255, 222, 173);    
                themeBgBottomColor = new Color(255, 250, 240); 
                themeAccentColor = new Color(255, 140, 0, 40); 
                break;
            default:
                themePrimaryColor = new Color(70, 130, 180);
                themeBgTopColor = new Color(200, 230, 255);
                themeBgBottomColor = Color.WHITE;
                themeAccentColor = new Color(0,0,0,20);
        }
        repaint();
    }

    public void startGame(ModuleModel module, int level) {
        this.currentModule = module;
        this.currentLevel = level;
        applyTheme(module.getId());
        this.score = 0;
        this.currentQuestionIndex = 0;
        
        UserModel u = GameState.getCurrentUser();
        lblUserInfo.setText(u != null ? u.getName() : "Tamu");
        lblLevelInfo.setText(module.getName() + " - LVL " + level);
        lblScore.setText("0");

        List<QuestionModel> allQuestions = questionRepo.getQuestionsByModule(module.getId(), level);
        if (allQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Soal belum tersedia.");
            ScreenManager.getInstance().showScreen("MODULE_SELECT");
            return;
        }
        Collections.shuffle(allQuestions); 
        int limit = Math.min(allQuestions.size(), 5);
        this.questionList = new ArrayList<>(allQuestions.subList(0, limit));
        this.maxScore = this.questionList.size() * 10;
        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestionIndex >= questionList.size()) {
            finishGame();
            return;
        }
        QuestionModel q = questionList.get(currentQuestionIndex);
        
        GameVisualizer.render(visualContainer, lblInstruction, q, currentModule.getId(), currentLevel);

        if (q.getQuestionAudio() != null) soundPlayer.playSFX(q.getQuestionAudio());

        setupAnswerButtons(q);
        revalidate(); repaint();
    }

    private void setupAnswerButtons(QuestionModel q) {
        answerAreaPanel.removeAll();
        answerAreaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
        String[] options = {q.getOptionA(), q.getOptionB(), q.getOptionC()};
        Color[] colors = { new Color(100, 181, 246), new Color(129, 199, 132), new Color(255, 183, 77) };

        for (int i = 0; i < options.length; i++) {
            if (options[i] == null) continue;
            String val = options[i];
            
            // MENGGUNAKAN KOMPONEN MODULAR
            ModernAnswerButton btn = new ModernAnswerButton(val, colors[i % 3]);
            btn.addActionListener(e -> checkAnswer(q, val));
            
            answerAreaPanel.add(btn);
        }
    }

    private void checkAnswer(QuestionModel q, String answer) {
        if (answer.equalsIgnoreCase(q.getCorrectAnswer())) {
            soundPlayer.playSFX("correct.wav");
            score += 10;
            lblScore.setText(String.valueOf(score));
            JOptionPane.showMessageDialog(this, "BENAR! (+10)", "Hebat!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            soundPlayer.playSFX("wrong.wav");
            JOptionPane.showMessageDialog(this, "Jawaban: " + q.getCorrectAnswer(), "Ups Salah!", JOptionPane.WARNING_MESSAGE);
        }
        currentQuestionIndex++;
        showQuestion();
    }

    private void finishGame() {
        UserModel user = GameState.getCurrentUser();
        if (user != null) {
            progressRepo.saveScore(user.getId(), currentModule.getId(), currentLevel, score);
            if (score >= 20) { 
                progressRepo.unlockNextLevel(user.getId(), currentModule.getId(), currentLevel);
                soundPlayer.playSFX("level_complete.wav");
            }
        }
        ScreenManager.getInstance().showResult(currentModule, currentLevel, score, maxScore);
    }
    
    private void showPauseMenu() {
        // MENGGUNAKAN DIALOG MODULAR
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        PauseMenuDialog dialog = new PauseMenuDialog(topFrame);
        dialog.setVisible(true);
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text); 
        l.setFont(font); 
        l.setForeground(color); 
        return l;
    }

    // --- DRAWING & INNER CLASSES FOR PANELS ---
    
    // 1. Background (Polka & Waves)
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient
        GradientPaint gp = new GradientPaint(0, 0, themeBgTopColor, 0, getHeight(), themeBgBottomColor);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Polka Dots
        g2.setColor(themeAccentColor);
        int dotSize = 15;
        int spacing = 50;
        for (int y = 0; y < getHeight(); y += spacing) {
            for (int x = (y % (spacing*2) == 0) ? 0 : spacing/2; x < getWidth(); x += spacing) {
                g2.fillOval(x, y, dotSize, dotSize);
            }
        }
        
        // Wavy Header
        g2.setColor(themePrimaryColor);
        int headerH = 100;
        GeneralPath path = new GeneralPath();
        path.moveTo(0, 0);
        path.lineTo(0, headerH);
        for (int x = 0; x < getWidth(); x += 100) {
            path.quadTo(x + 50, headerH + 20, x + 100, headerH);
        }
        path.lineTo(getWidth(), 0);
        path.closePath();
        
        g2.setColor(new Color(0,0,0,30));
        g2.translate(0, 5);
        g2.fill(path);
        g2.translate(0, -5);
        
        g2.setColor(themePrimaryColor);
        g2.fill(path);
    }

    // 2. Badge Panel (Stiker Kapsul) - Disimpan disini karena butuh 'themePrimaryColor'
    class ModernBadgePanel extends JPanel {
        public ModernBadgePanel(LayoutManager layout) {
            setLayout(layout);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 15, 8, 15)); 
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            
            // Border Putih
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, h, h);
            
            // Background Tema
            g2.setColor(themePrimaryColor.darker());
            int borderSize = 4;
            g2.fillRoundRect(borderSize, borderSize, w-(borderSize*2), h-(borderSize*2), h, h);
            
            // Glossy
            g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,80), 0, h/2, new Color(255,255,255,0)));
            g2.fillRoundRect(borderSize+2, borderSize+2, w-(borderSize*2)-4, h/2, h, h);
            
            g2.dispose();
        }
    }

    // 3. Board Panel (Jahitan) - Disimpan disini karena butuh 'themePrimaryColor'
    class ModernBoardPanel extends JPanel {
        public ModernBoardPanel() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillRoundRect(8, 8, w-16, h-16, 50, 50);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w-8, h-8, 50, 50);

            g2.setColor(themePrimaryColor);
            g2.setStroke(new BasicStroke(8));
            g2.drawRoundRect(4, 4, w-16, h-16, 50, 50);
            
            Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
            g2.setStroke(dashed);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(4, 4, w-16, h-16, 50, 50);

            g2.dispose();
        }
    }
}