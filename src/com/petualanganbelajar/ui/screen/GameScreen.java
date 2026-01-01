package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.content.StoryDataManager;
import com.petualanganbelajar.core.GameInputManager;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.QuestionModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.ProgressRepository;
import com.petualanganbelajar.repository.QuestionRepository;
import com.petualanganbelajar.repository.StoryRepository;
import com.petualanganbelajar.util.DialogScene;
import com.petualanganbelajar.util.GameVisualizer;
import com.petualanganbelajar.util.UIHelper;
import com.petualanganbelajar.ui.component.GameFeedbackDialog;
import com.petualanganbelajar.ui.component.ModernPauseButton;
import com.petualanganbelajar.ui.component.PauseMenuDialog;
import com.petualanganbelajar.ui.component.StoryDialogPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameScreen extends JPanel {

    private ModuleModel currentModule;
    private int currentLevel;
    private List<QuestionModel> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int maxScore = 0;
    
    private boolean isFirstAttempt = true; 
    private int pointsPerQuestion = 10; 

    private boolean isStoryMode = false;
    private Image moduleBgImage;          
    
    private boolean isEpilogueMode = false; 
    private Image epilogueBgImage;       

    private String currentDisplayPattern;
    private Queue<String> answerQueue;

    private JLayeredPane layeredPane; 
    private JPanel gameContentPanel;  
    private StoryDialogPanel storyPanel; 

    private JLabel lblUserInfo, lblLevelInfo, lblScore, lblInstruction;
    private JPanel visualContainer, answerAreaPanel;
    private ModernBoardPanel questionPanel;
    
    private final QuestionRepository questionRepo = new QuestionRepository();
    private final ProgressRepository progressRepo = new ProgressRepository();
    private final StoryRepository storyRepo = new StoryRepository();
    private final SoundPlayer soundPlayer = SoundPlayer.getInstance();
    
    private Color themePrimaryColor, themeBgTopColor, themeBgBottomColor, themeAccentColor;
    private Image imgGrass;

    public GameScreen() {
        setLayout(new BorderLayout());
        imgGrass = UIHelper.loadRawImage("grass_decoration.png");
        loadEpilogueBackground();
        initUI();
    }
    
    private void loadEpilogueBackground() {
        try {
            URL url = getClass().getResource("/images/bg_epilogue.png");
            if (url != null) epilogueBgImage = new ImageIcon(url).getImage();
        } catch (Exception e) { epilogueBgImage = null; }
    }

    private void initUI() {
        layeredPane = new JLayeredPane();
        layeredPane.setOpaque(false); 
        add(layeredPane, BorderLayout.CENTER);

        gameContentPanel = new JPanel(new BorderLayout());
        gameContentPanel.setOpaque(false); 
        setupGameContentUI(); 
        layeredPane.add(gameContentPanel, JLayeredPane.DEFAULT_LAYER);

        storyPanel = new StoryDialogPanel();
        storyPanel.setVisible(false);
        layeredPane.add(storyPanel, JLayeredPane.PALETTE_LAYER);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                layeredPane.setBounds(0, 0, w, h);
                gameContentPanel.setBounds(0, 0, w, h);
                storyPanel.setBounds(0, 0, w, h);
                revalidate(); repaint();
            }
        });
    }

    private void setupGameContentUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JPanel userBadge = createStatBadge("Player", "user_icon.png", false);
        lblUserInfo = (JLabel) ((ModernBadgePanel) userBadge).getComponent(0);

        ModernBadgePanel levelBadge = new ModernBadgePanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        lblLevelInfo = new JLabel("LEVEL 1"); 
        lblLevelInfo.setFont(new Font("Comic Sans MS", Font.BOLD, 28)); lblLevelInfo.setForeground(Color.WHITE);
        levelBadge.add(lblLevelInfo);

        JPanel rightContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightContainer.setOpaque(false);
        JPanel scoreBadge = createStatBadge("0", "star_icon.png", true);
        lblScore = (JLabel) ((ModernBadgePanel) scoreBadge).getComponent(0);
        ModernPauseButton btnPause = new ModernPauseButton();
        btnPause.addActionListener(e -> showPauseMenu());

        rightContainer.add(scoreBadge); rightContainer.add(btnPause);
        
        JPanel leftW = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,0)); leftW.setOpaque(false); leftW.add(userBadge);
        JPanel centW = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0)); centW.setOpaque(false); centW.add(levelBadge);

        headerPanel.add(leftW, BorderLayout.WEST); headerPanel.add(centW, BorderLayout.CENTER); headerPanel.add(rightContainer, BorderLayout.EAST);
        gameContentPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel gameContainer = new JPanel(new BorderLayout());
        gameContainer.setOpaque(false); gameContainer.setBorder(new EmptyBorder(0, 80, 10, 80));

        questionPanel = new ModernBoardPanel();
        questionPanel.setLayout(new GridBagLayout());
        questionPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        lblInstruction = new JLabel("Instruksi");
        lblInstruction.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        lblInstruction.setForeground(new Color(80, 80, 80));
        lblInstruction.setHorizontalAlignment(SwingConstants.CENTER);

        visualContainer = new JPanel();
        visualContainer.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 20, 0);
        questionPanel.add(lblInstruction, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 0, 0);
        questionPanel.add(visualContainer, gbc);

        JPanel boardWrapper = new JPanel(new GridBagLayout()); boardWrapper.setOpaque(false);
        GridBagConstraints gbcW = new GridBagConstraints(); gbcW.weightx = 1; gbcW.weighty = 1; gbcW.fill = GridBagConstraints.BOTH; gbcW.insets = new Insets(10, 0, 10, 0);
        boardWrapper.add(questionPanel, gbcW);
        gameContainer.add(boardWrapper, BorderLayout.CENTER);

        answerAreaPanel = new JPanel();
        answerAreaPanel.setOpaque(false); answerAreaPanel.setBorder(new EmptyBorder(15, 0, 25, 0));
        gameContainer.add(answerAreaPanel, BorderLayout.SOUTH);
        gameContentPanel.add(gameContainer, BorderLayout.CENTER);
    }

    public void startGame(ModuleModel module, int level) {
        this.currentModule = module;
        this.currentLevel = level;
        applyTheme(module.getId());
        loadModuleBackground(module.getId());
        
        this.score = 0;
        this.currentQuestionIndex = 0;
        this.pointsPerQuestion = (level <= 0) ? 10 : level * 10;

        UserModel u = GameState.getCurrentUser();
        lblUserInfo.setText(u != null ? u.getName() : "Teman");
        lblLevelInfo.setText(module.getName() + " - LVL " + level);
        lblScore.setText("0");

        List<QuestionModel> allQuestions = questionRepo.getQuestionsByModule(module.getId(), level);
        if (allQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Soal belum tersedia.");
            ScreenManager.getInstance().showScreen("MODULE_SELECT");
            return;
        }
        Collections.shuffle(allQuestions);
        int limit = Math.min(allQuestions.size(), 15);
        this.questionList = new ArrayList<>(allQuestions.subList(0, limit));
        
        this.maxScore = this.questionList.size() * pointsPerQuestion;
        
        playModuleBGM();
        checkAndPlayIntroStory();
    }
    
    private void playModuleBGM() {
        String bgmFile = "bgm_menu.wav";
        if (currentModule != null) {
            switch (currentModule.getId()) {
                case 1: bgmFile = "bgm_angka.wav"; break;
                case 2: bgmFile = "bgm_huruf.wav"; break;
                case 3: bgmFile = "bgm_warna.wav"; break;
                case 4: bgmFile = "bgm_bentuk.wav"; break;
            }
        }
        soundPlayer.playBGM(bgmFile);
    }

    private void loadModuleBackground(int modId) {
        String filename = "bg_module_" + modId + ".png";
        try {
            URL url = getClass().getResource("/images/" + filename);
            if (url != null) moduleBgImage = new ImageIcon(url).getImage();
            else moduleBgImage = null;
        } catch (Exception e) { moduleBgImage = null; }
    }

    private void checkAndPlayIntroStory() {
        UserModel u = GameState.getCurrentUser();
        int userId = (u != null) ? u.getId() : 0;
        
        boolean seen = storyRepo.hasSeenStory(userId, currentModule.getId(), currentLevel, "START");
        List<DialogScene> scenes = StoryDataManager.getIntroStory(currentModule.getId(), currentLevel);
        
        if (!seen && !scenes.isEmpty()) {
            setStoryMode(true);
            storyPanel.startStory(scenes, () -> {
                storyRepo.markStoryAsSeen(userId, currentModule.getId(), currentLevel, "START");
                setStoryMode(false); 
                showQuestion(); 
            });
        } else {
            setStoryMode(false);
            showQuestion();
        }
    }

    private void setStoryMode(boolean active) {
        this.isStoryMode = active;
        if (!active) this.isEpilogueMode = false;

        if (active) {
            gameContentPanel.setVisible(false);
            storyPanel.setVisible(true);       
        } else {
            gameContentPanel.setVisible(true);  
            storyPanel.setVisible(false);       
        }
        repaint();
    }

    @Override 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (isEpilogueMode && epilogueBgImage != null) {
            g.drawImage(epilogueBgImage, 0, 0, getWidth(), getHeight(), this);
            g.setColor(new Color(0, 0, 0, 30)); 
            g.fillRect(0, 0, getWidth(), getHeight());
            
        } else if (isStoryMode && moduleBgImage != null) {
            g.drawImage(moduleBgImage, 0, 0, getWidth(), getHeight(), this);
            g.setColor(new Color(0, 0, 0, 30)); 
            g.fillRect(0, 0, getWidth(), getHeight());
            
        } else {
            Graphics2D g2 = (Graphics2D) g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            
            g2.setPaint(new GradientPaint(0, 0, themeBgTopColor, 0, h, themeBgBottomColor)); 
            g2.fillRect(0, 0, w, h);
            
            g2.setColor(themeAccentColor); 
            for (int y = 0; y < h; y += 60) for (int x = (y % 120 == 0) ? 0 : 30; x < w; x += 60) g2.fillOval(x, y, 10, 10);
            
            if (imgGrass != null) { 
                int grassW = 200; int grassH = 120; 
                for (int x = 0; x < w; x += grassW) g2.drawImage(imgGrass, x, h - grassH, grassW, grassH, null); 
            }
            
            g2.setColor(themePrimaryColor); 
            GeneralPath path = new GeneralPath(); path.moveTo(0, 0); path.lineTo(0, 90);
            for (int x = 0; x < w; x += 100) path.quadTo(x + 50, 105, x + 100, 90);
            path.lineTo(w, 0); path.closePath();
            g2.setColor(new Color(0,0,0,30)); g2.translate(0, 5); g2.fill(path); g2.translate(0, -5);
            g2.setColor(themePrimaryColor); g2.fill(path);
        }
    }

    private void checkAndPlayOutroStoryAndFinish() {
        UserModel u = GameState.getCurrentUser();
        int userId = (u != null) ? u.getId() : 0;

        if (score >= (maxScore * 0.5)) {
            boolean seen = storyRepo.hasSeenStory(userId, currentModule.getId(), currentLevel, "SUCCESS");
            List<DialogScene> scenes = StoryDataManager.getOutroStory(currentModule.getId(), currentLevel);

            if (!seen && !scenes.isEmpty()) {
                String epilogBgm = "bgm_menu.wav";
                switch(currentModule.getId()) {
                    case 1: epilogBgm = "bgm_epilog_angka.wav"; break;
                    case 2: epilogBgm = "bgm_epilog_huruf.wav"; break;
                    case 3: epilogBgm = "bgm_epilog_warna.wav"; break;
                    case 4: epilogBgm = "bgm_epilog_bentuk.wav"; break;
                }
                soundPlayer.playBGM(epilogBgm);

                setStoryMode(true);
                storyPanel.startStory(scenes, () -> {
                    storyRepo.markStoryAsSeen(userId, currentModule.getId(), currentLevel, "SUCCESS");
                    checkEpilogue(userId); 
                });
                return; 
            }
        }
        
        if (score >= (maxScore * 0.5)) {
             checkEpilogue(userId);
        } else {
             goToResultScreen();
        }
    }

    private void checkEpilogue(int userId) {
        if (progressRepo.isGameFullyCompleted(userId)) {
            if (!storyRepo.hasSeenStory(userId, 0, 0, "EPILOGUE")) {
                
                soundPlayer.playBGM("bgm_ending.wav");

                isEpilogueMode = true;
                setStoryMode(true); 
                repaint(); 
                
                List<DialogScene> epilogue = StoryDataManager.getEpilogueStory();
                storyPanel.startStory(epilogue, () -> {
                    storyRepo.markStoryAsSeen(userId, 0, 0, "EPILOGUE");
                    isEpilogueMode = false; 
                    goToResultScreen();
                });
                return;
            }
        }
        goToResultScreen();
    }

    private void goToResultScreen() {
        setStoryMode(false); 
        ScreenManager.getInstance().showResult(currentModule, currentLevel, score, maxScore);
    }
    
    private void showQuestion() {
        if (currentQuestionIndex >= questionList.size()) { finishGame(); return; }
        isFirstAttempt = true; 
        QuestionModel q = questionList.get(currentQuestionIndex);
        String type = q.getQuestionType().toString();
        visualContainer.removeAll(); answerAreaPanel.removeAll();
        if ("CLICK".equalsIgnoreCase(type)) {
            lblInstruction.setText("<html><center>" + q.getQuestionText() + "</center></html>");
        } else if ("SEQUENCE_MULTI".equalsIgnoreCase(type)) {
            String[] parts = q.getQuestionText().split("##");
            this.currentDisplayPattern = parts[0] + " ## " + parts[1];
            this.answerQueue = new LinkedList<>();
            String[] ans = parts[2].trim().split(",");
            for(String a : ans) answerQueue.add(a.trim());
            GameVisualizer.renderSequenceMulti(visualContainer, lblInstruction, currentDisplayPattern);
        } else {
            GameVisualizer.render(visualContainer, lblInstruction, q, currentModule.getId(), currentLevel);
        }
        if (q.getQuestionAudio() != null) soundPlayer.playSFX(q.getQuestionAudio());
        GameInputManager.setupInput(q, answerAreaPanel, visualContainer, currentModule.getId(), currentDisplayPattern, 
            (answer) -> handleAnswer(q, answer));
        visualContainer.revalidate(); visualContainer.repaint();
        answerAreaPanel.revalidate(); answerAreaPanel.repaint();
    }

    private String getPlayerName() {
        UserModel user = GameState.getCurrentUser();
        return (user != null && user.getName() != null) ? user.getName() : "Teman";
    }

    private void handleAnswer(QuestionModel q, String answer) {
        String type = q.getQuestionType().toString();
        if ("SEQUENCE_MULTI".equalsIgnoreCase(type)) handleMultiStepAnswer(q, answer);
        else handleStandardAnswer(q, answer);
    }

    private void handleStandardAnswer(QuestionModel q, String answer) {
        if (answer.trim().equalsIgnoreCase(q.getCorrectAnswer())) {
            soundPlayer.playSFX("correct.wav");
            revealRealImage(q);
            int earned = 0; String msgText = ""; String name = getPlayerName();
            if (isFirstAttempt) { earned = pointsPerQuestion; score += earned; msgText = "Kamu dapat +" + earned + " Poin!"; } 
            else { msgText = "Hebat! Tapi +0 Poin karena tadi sempat salah."; }
            lblScore.setText(String.valueOf(score));
            Window window = SwingUtilities.getWindowAncestor(this);
            new GameFeedbackDialog(window, "Hebat " + name + "!", msgText, GameFeedbackDialog.TYPE_SUCCESS).setVisible(true);
            Timer t = new Timer(500, e -> { currentQuestionIndex++; showQuestion(); });
            t.setRepeats(false); t.start();
        } else {
            soundPlayer.playSFX("wrong.wav"); isFirstAttempt = false; 
            String name = getPlayerName(); Window window = SwingUtilities.getWindowAncestor(this);
            new GameFeedbackDialog(window, "Ups Salah " + name + "!", "Ayo coba lagi ya...", GameFeedbackDialog.TYPE_ERROR).setVisible(true);
        }
    }

    private void handleMultiStepAnswer(QuestionModel q, String input) {
        if (answerQueue == null || answerQueue.isEmpty()) return;
        String target = answerQueue.peek();
        if (input.equalsIgnoreCase(target)) {
            soundPlayer.playSFX("correct.wav"); answerQueue.poll();
            String[] parts = currentDisplayPattern.split("##");
            String pola = parts[1].replaceFirst("_", input);
            this.currentDisplayPattern = parts[0] + "##" + pola;
            GameVisualizer.renderSequenceMulti(visualContainer, lblInstruction, currentDisplayPattern);
            if (answerQueue.isEmpty()) {
                int earned = 0; String msgText = ""; String name = getPlayerName();
                if (isFirstAttempt) { earned = pointsPerQuestion; score += earned; msgText = "Kamu dapat +" + earned + " Poin!"; } 
                else { msgText = "Lengkap! Tapi +0 Poin karena tadi ada yang salah."; }
                lblScore.setText(String.valueOf(score));
                Window window = SwingUtilities.getWindowAncestor(this);
                new GameFeedbackDialog(window, "Hebat " + name + "!", msgText, GameFeedbackDialog.TYPE_SUCCESS).setVisible(true);
                Timer t = new Timer(500, x -> { currentQuestionIndex++; showQuestion(); }); t.setRepeats(false); t.start();
            }
        } else {
            soundPlayer.playSFX("wrong.wav"); isFirstAttempt = false;
            String name = getPlayerName(); Window window = SwingUtilities.getWindowAncestor(this);
            new GameFeedbackDialog(window, "Salah " + name + "!", "Ayo coba lagi.", GameFeedbackDialog.TYPE_ERROR).setVisible(true);
        }
    }

    private boolean revealRealImage(QuestionModel q) {
        String qImg = q.getQuestionImage();
        if (qImg != null && qImg.startsWith("SILHOUETTE:")) {
            String realFile = qImg.replace("SILHOUETTE:", "");
            for (Component c : visualContainer.getComponents()) {
                if (c instanceof JLabel) {
                    ImageIcon colorIcon = UIHelper.loadIcon(realFile, 200, 200);
                    if (colorIcon != null) { ((JLabel) c).setIcon(colorIcon); visualContainer.repaint(); return true; }
                }
            }
        }
        return false;
    }

    private void finishGame() {
        UserModel user = GameState.getCurrentUser();
        if (user != null) {
            progressRepo.saveScore(user.getId(), currentModule.getId(), currentLevel, score);
            if (score >= (maxScore * 0.6)) { 
                progressRepo.unlockNextLevel(user.getId(), currentModule.getId(), currentLevel);
                soundPlayer.playSFX("level_complete.wav");
            } else {
                soundPlayer.playSFX("level_failed.wav");
            }
        }
        checkAndPlayOutroStoryAndFinish();
    }

    private void showPauseMenu() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        new PauseMenuDialog(topFrame).setVisible(true);
    }

    private void applyTheme(int moduleId) {
        switch (moduleId) {
            case 1: themePrimaryColor = new Color(46, 139, 87); themeBgTopColor = new Color(200, 255, 200); themeBgBottomColor = new Color(240, 255, 240); themeAccentColor = new Color(34, 139, 34, 50); break;
            case 2: themePrimaryColor = new Color(30, 144, 255); themeBgTopColor = new Color(135, 206, 250); themeBgBottomColor = new Color(240, 248, 255); themeAccentColor = new Color(255, 255, 255, 100); break;
            case 3: themePrimaryColor = new Color(255, 112, 67); themeBgTopColor = new Color(255, 224, 178); themeBgBottomColor = new Color(255, 243, 224); themeAccentColor = new Color(255, 255, 255, 100); break;
            default: themePrimaryColor = new Color(70, 130, 180); themeBgTopColor = new Color(200, 230, 255); themeBgBottomColor = Color.WHITE; themeAccentColor = new Color(0,0,0,20);
        }
    }
    
    private JPanel createStatBadge(String d, String i, boolean r) { 
        ModernBadgePanel b = new ModernBadgePanel(new FlowLayout(r?FlowLayout.RIGHT:FlowLayout.LEFT,0,0)); 
        JLabel l=new JLabel(d); l.setFont(new Font("Comic Sans MS", Font.BOLD, 18)); l.setForeground(Color.WHITE);
        ImageIcon ic=UIHelper.loadIcon(i,28,28); 
        if(ic!=null){l.setIcon(ic);l.setIconTextGap(12);l.setHorizontalTextPosition(r?SwingConstants.LEFT:SwingConstants.RIGHT);l.setVerticalTextPosition(SwingConstants.CENTER);} 
        b.add(l); return b; 
    }
    class ModernBadgePanel extends JPanel {
        public ModernBadgePanel(LayoutManager l){setLayout(l);setOpaque(false);setBorder(new EmptyBorder(8,15,12,15));}
        @Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);int w=getWidth(),h=getHeight();g2.setColor(new Color(0,0,0,50));g2.fillRoundRect(2,6,w-4,h-4,h-4,h-4);g2.setColor(Color.WHITE);g2.fillRoundRect(0,0,w,h-4,h-4,h-4);g2.setColor(themePrimaryColor.darker());g2.fillRoundRect(4,4,w-8,h-12,h-4,h-4);g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,80),0,h/2,new Color(255,255,255,0)));g2.fillRoundRect(6,6,w-12,h-4,h-4,h-4);g2.dispose();}
    }
    class ModernBoardPanel extends JPanel {
        public ModernBoardPanel(){setOpaque(false);}
        @Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);int w=getWidth(),h=getHeight();g2.setColor(new Color(0,0,0,40));g2.fillRoundRect(8,8,w-16,h-16,50,50);g2.setColor(Color.WHITE);g2.fillRoundRect(0,0,w-8,h-8,50,50);g2.setColor(themePrimaryColor);g2.setStroke(new BasicStroke(8));g2.drawRoundRect(4,4,w-16,h-16,50,50);g2.setColor(Color.WHITE);g2.setStroke(new BasicStroke(2));g2.drawRoundRect(6,6,w-20,h-20,45,45);g2.dispose();}
    }
}