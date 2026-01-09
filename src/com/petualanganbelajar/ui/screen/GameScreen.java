package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.content.StoryDataManager;
import com.petualanganbelajar.core.GameInputManager;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.QuestionModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.repository.LeaderboardRepository;
import com.petualanganbelajar.repository.ProgressRepository;
import com.petualanganbelajar.repository.QuestionRepository;
import com.petualanganbelajar.repository.StoryRepository;
import com.petualanganbelajar.ui.component.BouncyPauseButton;
import com.petualanganbelajar.ui.component.GameFeedbackDialog;
import com.petualanganbelajar.ui.component.GameScoreHUD;
import com.petualanganbelajar.ui.component.GameLevelHUD;
import com.petualanganbelajar.ui.component.PauseMenuDialog;
import com.petualanganbelajar.ui.component.StoryDialogPanel;
import com.petualanganbelajar.ui.component.UserProfileHUD;
import com.petualanganbelajar.util.DialogScene;
import com.petualanganbelajar.util.GameVisualizer;
import com.petualanganbelajar.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
    private int baseTotalXP = 0;

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

    // [MODULAR COMPONENTS]
    private UserProfileHUD userProfileHUD;
    private GameScoreHUD gameScoreHUD;
    private GameLevelHUD levelHUD;
    private BouncyPauseButton btnPause;

    private JLabel lblInstruction;
    private JPanel visualContainer, answerAreaPanel;
    private ModernBoardPanel questionPanel;
    private JPanel headerPanel;

    private final QuestionRepository questionRepo = new QuestionRepository();
    private final ProgressRepository progressRepo = new ProgressRepository();
    private final StoryRepository storyRepo = new StoryRepository();
    private final LeaderboardRepository xpRepo = new LeaderboardRepository();
    private final SoundPlayer soundPlayer = SoundPlayer.getInstance();

    private Color themePrimaryColor, themeBgTopColor, themeBgBottomColor, themeAccentColor;
    private Image imgGrass;

    // --- VARIABEL SKALA (SCALABLE) ---
    private final float BASE_W = 1920f;
    private final float BASE_H = 1080f;
    private float scaleFactor = 1.0f;

    public GameScreen() {
        setLayout(new BorderLayout());
        imgGrass = UIHelper.loadRawImage("grass_decoration.png");
        loadEpilogueBackground();
        initUI();
        
        // --- LISTENER RESIZE ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // 1. Hitung ulang bound layer
                int w = getWidth();
                int h = getHeight();
                layeredPane.setBounds(0, 0, w, h);
                gameContentPanel.setBounds(0, 0, w, h);
                storyPanel.setBounds(0, 0, w, h);
                
                // 2. Hitung Skala & Update Layout
                calculateScaleFactor();
                updateResponsiveLayout();
                
                revalidate(); 
                repaint();
            }
        });
    }
    
    // --- [SCALABLE] HITUNG FAKTOR SKALA ---
    private void calculateScaleFactor() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        float sW = (float) getWidth() / BASE_W;
        float sH = (float) getHeight() / BASE_H;
        this.scaleFactor = Math.min(sW, sH); // Ambil rasio terkecil agar proporsional
        if (this.scaleFactor < 0.5f) this.scaleFactor = 0.5f; // Batas minimum
    }

    // --- [SCALABLE] UPDATE UKURAN KOMPONEN ---
    private void updateResponsiveLayout() {
        // 1. Update Ukuran Font Instruksi
        if (lblInstruction != null) {
            int newFontSize = Math.max(24, (int)(48 * scaleFactor));
            lblInstruction.setFont(new Font("Comic Sans MS", Font.BOLD, newFontSize));
        }

        // 2. Update Padding Header
        if (headerPanel != null) {
            int topPad = (int)(20 * scaleFactor);
            int sidePad = (int)(30 * scaleFactor);
            headerPanel.setBorder(new EmptyBorder(topPad, sidePad, 5, sidePad));
        }

        // 3. Update Ukuran HUD Level
        if (levelHUD != null) {
            levelHUD.updateScale(scaleFactor); 
        }
        
        // 4. Update Skala Profile HUD
        if (userProfileHUD != null) {
            userProfileHUD.updateScale(scaleFactor);
        }

        // 5. Update Skala Score HUD
        if (gameScoreHUD != null) {
            gameScoreHUD.updateScale(scaleFactor);
        }
        
        // 6. Update Area Soal (Padding)
        if (questionPanel != null) {
             int qPad = (int)(30 * scaleFactor);
             questionPanel.setBorder(new EmptyBorder(qPad, qPad, qPad, qPad));
        }
        
        // [CRITICAL FIX] Force the layout manager to re-layout the components
        if (headerPanel != null) {
            headerPanel.revalidate(); // Calculates layout based on new preferred sizes
            headerPanel.repaint();    // Redraws the components
        }
        
        // Optional: Trigger visual container refresh if needed
        if (visualContainer != null && visualContainer.getComponentCount() > 0) {
             visualContainer.revalidate();
             visualContainer.repaint();
        }
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
    }

    private void setupGameContentUI() {
        // --- HEADER PANEL ---
        headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 20, 5, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        
        // [TAMBAHAN PENTING] Agar layout tidak kolaps (tinggi 0) saat resize
        gbc.weighty = 1.0; 
        // Kita pakai NONE agar HUD tidak melar (stretch) secara paksa, tetap sesuai ukuran aslinya
        gbc.fill = GridBagConstraints.NONE; 
        
        // --- 1. LEFT: Profile HUD ---
        userProfileHUD = new UserProfileHUD();
        
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.weightx = 0.33; 
        gbc.anchor = GridBagConstraints.NORTHWEST; // Rata Kiri Atas
        headerPanel.add(userProfileHUD, gbc);

        // --- 2. CENTER: Level Info ---
        levelHUD = new GameLevelHUD();
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.33;
        gbc.anchor = GridBagConstraints.NORTH; // Rata Tengah Atas
        headerPanel.add(levelHUD, gbc);

        // --- 3. RIGHT: Score HUD ---
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        gameScoreHUD = new GameScoreHUD();
        btnPause = new BouncyPauseButton();
        btnPause.addActionListener(e -> showPauseMenu());

        rightPanel.add(gameScoreHUD);
        rightPanel.add(btnPause);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.33;
        gbc.anchor = GridBagConstraints.NORTHEAST; // Rata Kanan Atas
        headerPanel.add(rightPanel, gbc);

        // Masukkan header ke panel utama
        gameContentPanel.add(headerPanel, BorderLayout.NORTH);

        // --- GAME AREA (Sama seperti kode Anda) ---
        JPanel gameContainer = new JPanel(new BorderLayout());
        gameContainer.setOpaque(false);
        gameContainer.setBorder(new EmptyBorder(0, 80, 10, 80));

        questionPanel = new ModernBoardPanel();
        questionPanel.setLayout(new GridBagLayout());
        questionPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        lblInstruction = new JLabel("Instruksi");
        lblInstruction.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        lblInstruction.setForeground(new Color(80, 80, 80));
        lblInstruction.setHorizontalAlignment(SwingConstants.CENTER);

        visualContainer = new JPanel();
        visualContainer.setOpaque(false);

        GridBagConstraints gbcQ = new GridBagConstraints();
        gbcQ.gridx = 0; gbcQ.gridy = 0; gbcQ.insets = new Insets(0, 0, 20, 0);
        questionPanel.add(lblInstruction, gbcQ);
        gbcQ.gridy = 1; gbcQ.insets = new Insets(0, 0, 0, 0);
        questionPanel.add(visualContainer, gbcQ);

        JPanel boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setOpaque(false);
        GridBagConstraints gbcW = new GridBagConstraints();
        gbcW.weightx = 1; gbcW.weighty = 1;
        gbcW.fill = GridBagConstraints.BOTH;
        gbcW.insets = new Insets(10, 0, 10, 0);
        boardWrapper.add(questionPanel, gbcW);
        gameContainer.add(boardWrapper, BorderLayout.CENTER);

        answerAreaPanel = new JPanel();
        answerAreaPanel.setOpaque(false);
        answerAreaPanel.setBorder(new EmptyBorder(15, 0, 25, 0));
        gameContainer.add(answerAreaPanel, BorderLayout.SOUTH);

        gameContentPanel.add(gameContainer, BorderLayout.CENTER);
        
        SwingUtilities.invokeLater(() -> {
            calculateScaleFactor();
            updateResponsiveLayout();
        });
    }

    public void startGame(ModuleModel module, int level) {
        this.currentModule = module;
        this.currentLevel = level;
        
        // 1. Terapkan Tema Warna (Dynamic Theme)
        applyTheme(module.getId());
        loadModuleBackground(module.getId());

        this.score = 0;
        this.currentQuestionIndex = 0;
        this.pointsPerQuestion = (level <= 0) ? 10 : level * 10;

        UserModel u = GameState.getCurrentUser();
        if (u != null) {
            this.baseTotalXP = xpRepo.getTotalScoreByUserId(u.getId());
        } else {
            this.baseTotalXP = 0;
        }

        // 2. Update Informasi di HUD
        userProfileHUD.updateProfile(baseTotalXP + score);
        gameScoreHUD.updateScore(score);
        levelHUD.setInfo(module.getName(), level);

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

    // --- LOGIC DYNAMIC THEME (Warna HUD berubah sesuai modul) ---
    private void applyTheme(int moduleId) {
        Color[] profileColors;
        Color levelTop, levelBottom, levelOutline, levelTextColor;
        Color scoreTop, scoreBottom;

        switch (moduleId) {
            case 1: // MODUL ANGKA
                themePrimaryColor = new Color(46, 139, 87);
                themeBgTopColor = new Color(200, 255, 200);
                themeBgBottomColor = new Color(240, 255, 240);
                themeAccentColor = new Color(34, 139, 34, 50);
                
                profileColors = new Color[]{ new Color(255, 215, 0), new Color(154, 205, 50), new Color(210, 180, 140) };
                
                levelTop = new Color(255, 140, 0); 
                levelBottom = new Color(205, 92, 92); 
                levelOutline = new Color(139, 69, 19); 
                levelTextColor = Color.WHITE;
                
                scoreTop = new Color(255, 99, 71); scoreBottom = new Color(178, 34, 34);
                break;

            case 2: // MODUL HURUF
                themePrimaryColor = new Color(199, 21, 133); 
                themeBgTopColor = new Color(255, 228, 238);    
                themeBgBottomColor = new Color(255, 250, 255); 
                themeAccentColor = new Color(255, 105, 180, 70); 

                profileColors = new Color[]{ 
                    new Color(0, 139, 139),    
                    new Color(32, 178, 170),  
                    new Color(72, 209, 204)    
                };
                
                levelTop = new Color(50, 111, 168);    
                levelBottom = new Color(0, 128, 128);  
                levelOutline = new Color(0, 51, 51);
                levelTextColor = Color.WHITE;             
                
                scoreTop = new Color(102, 255, 0); 
                scoreBottom = new Color(50, 168, 115);
                break;

            case 3: // MODUL WARNA
                themePrimaryColor = new Color(255, 112, 67);
                themeBgTopColor = new Color(255, 224, 178);
                themeBgBottomColor = new Color(255, 243, 224);
                themeAccentColor = new Color(255, 255, 255, 100);
                
                profileColors = new Color[]{ Color.MAGENTA, new Color(255, 105, 180), Color.CYAN };
                
                levelTop = new Color(218, 112, 214);    
                levelBottom = new Color(153, 50, 204); 
                levelOutline = new Color(75, 0, 130);  
                levelTextColor = Color.WHITE;
                
                scoreTop = new Color(0, 255, 255); scoreBottom = new Color(0, 128, 128);
                break;
                
            case 4: // MODUL BENTUK
                themePrimaryColor = new Color(255, 165, 0);
                themeBgTopColor = new Color(255, 250, 205);
                themeBgBottomColor = new Color(255, 228, 181);
                themeAccentColor = new Color(255, 140, 0, 50);
                
                profileColors = new Color[]{ new Color(100, 149, 237), new Color(65, 105, 225) }; 
                
                levelTop = new Color(255, 69, 0); 
                levelBottom = new Color(139, 0, 0); 
                levelOutline = new Color(100, 0, 0); 
                levelTextColor = Color.WHITE;
                
                scoreTop = new Color(50, 205, 50); scoreBottom = new Color(0, 100, 0);
                break;

            default: 
                themePrimaryColor = new Color(70, 130, 180);
                themeBgTopColor = new Color(200, 230, 255);
                themeBgBottomColor = Color.WHITE;
                themeAccentColor = new Color(0,0,0,20);
                
                profileColors = new Color[]{Color.LIGHT_GRAY, Color.GRAY};
                levelTop = Color.LIGHT_GRAY; levelBottom = Color.GRAY; 
                levelOutline = Color.BLACK; levelTextColor = Color.WHITE;
                scoreTop = Color.ORANGE; scoreBottom = Color.RED;
        }
        
        if (userProfileHUD != null) userProfileHUD.setProfileTheme(profileColors);
        if (levelHUD != null) levelHUD.setTheme(levelTop, levelBottom, levelOutline, levelTextColor);
        if (gameScoreHUD != null) gameScoreHUD.setScoreTheme(scoreTop, scoreBottom);
        
        repaint();
    }

    private void updateHUD() {
        gameScoreHUD.updateScore(score);
        userProfileHUD.updateProfile(baseTotalXP + score);
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
                int grassW = (int)(200 * scaleFactor); int grassH = (int)(120 * scaleFactor); 
                for (int x = 0; x < w; x += grassW) g2.drawImage(imgGrass, x, h - grassH, grassW, grassH, null); 
            }
            
            g2.setColor(themePrimaryColor); 
            GeneralPath path = new GeneralPath(); path.moveTo(0, 0); path.lineTo(0, (int)(90 * scaleFactor));
            int curveW = (int)(100 * scaleFactor); int curveH = (int)(105 * scaleFactor); int curveEnd = (int)(90 * scaleFactor);
            for (int x = 0; x < w; x += curveW) path.quadTo(x + (curveW/2), curveH, x + curveW, curveEnd);
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
        
        // [FIX] Pass scaleFactor to GameVisualizer
        if ("CLICK".equalsIgnoreCase(type)) {
            lblInstruction.setText("<html><center>" + q.getQuestionText() + "</center></html>");
        } else if ("SEQUENCE_MULTI".equalsIgnoreCase(type)) {
            String[] parts = q.getQuestionText().split("##");
            this.currentDisplayPattern = parts[0] + " ## " + parts[1];
            this.answerQueue = new LinkedList<>();
            String[] ans = parts[2].trim().split(",");
            for(String a : ans) answerQueue.add(a.trim());
            
            GameVisualizer.renderSequenceMulti(visualContainer, lblInstruction, currentDisplayPattern, scaleFactor);
        } else {
            GameVisualizer.render(visualContainer, lblInstruction, q, currentModule.getId(), currentLevel, scaleFactor);
        }
        
        if (q.getQuestionAudio() != null) soundPlayer.playSFX(q.getQuestionAudio());
        // Tambahkan scaleFactor di akhir
        GameInputManager.setupInput(q, answerAreaPanel, visualContainer, currentModule.getId(), currentDisplayPattern, 
            (answer) -> handleAnswer(q, answer), scaleFactor);
        
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
            if (isFirstAttempt) { 
                earned = pointsPerQuestion; 
                score += earned; 
                msgText = "Kamu dapat +" + earned + " Poin!"; 
                updateHUD(); 
            } 
            else { msgText = "Hebat! Tapi +0 Poin karena tadi sempat salah."; }
            
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
            
            // [FIX] Pass scaleFactor
            GameVisualizer.renderSequenceMulti(visualContainer, lblInstruction, currentDisplayPattern, scaleFactor);
            
            if (answerQueue.isEmpty()) {
                int earned = 0; String msgText = ""; String name = getPlayerName();
                if (isFirstAttempt) { 
                    earned = pointsPerQuestion; 
                    score += earned; 
                    msgText = "Kamu dapat +" + earned + " Poin!"; 
                    updateHUD(); 
                } 
                else { msgText = "Lengkap! Tapi +0 Poin karena tadi ada yang salah."; }
                
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
                    // [MODIFIKASI] Gunakan scaleFactor untuk ukuran gambar
                    // Perbesar dari base 200 ke 300 agar terlihat jelas
                    int imgSize = (int)(300 * scaleFactor); 
                    
                    ImageIcon colorIcon = UIHelper.loadIcon(realFile, imgSize, imgSize);
                    if (colorIcon != null) { 
                        ((JLabel) c).setIcon(colorIcon); 
                        visualContainer.repaint(); 
                        return true; 
                    }
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

    class ModernBoardPanel extends JPanel {
        public ModernBoardPanel(){setOpaque(false);}
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(),h=getHeight();
            // Skala radius round rect
            int rad = (int)(50 * scaleFactor);
            g2.setColor(new Color(0,0,0,40));g2.fillRoundRect(8,8,w-16,h-16,rad,rad);
            g2.setColor(Color.WHITE);g2.fillRoundRect(0,0,w-8,h-8,rad,rad);
            g2.setColor(themePrimaryColor);g2.setStroke(new BasicStroke(8));g2.drawRoundRect(4,4,w-16,h-16,rad,rad);
            g2.setColor(Color.WHITE);g2.setStroke(new BasicStroke(2));g2.drawRoundRect(6,6,w-20,h-20,rad-5,rad-5);
            g2.dispose();
        }
    }
}