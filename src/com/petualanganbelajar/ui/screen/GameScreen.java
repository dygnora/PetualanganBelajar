package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.content.StoryDataManager;
import com.petualanganbelajar.core.GameInputManager;
import com.petualanganbelajar.core.GameSession;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.core.StoryController;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.QuestionModel;
import com.petualanganbelajar.model.UserModel;
import com.petualanganbelajar.ui.component.BouncyPauseButton;
import com.petualanganbelajar.ui.component.GameFeedbackDialog;
import com.petualanganbelajar.ui.component.GameLevelHUD;
import com.petualanganbelajar.ui.component.GameScoreHUD;
import com.petualanganbelajar.ui.component.PauseMenuDialog;
import com.petualanganbelajar.ui.component.StoryDialogPanel;
import com.petualanganbelajar.ui.component.UserProfileHUD;
import com.petualanganbelajar.ui.component.LevelUpDialog;
import com.petualanganbelajar.util.DialogScene;
import com.petualanganbelajar.util.GameThemeManager;
import com.petualanganbelajar.util.GameVisualizer;
import com.petualanganbelajar.util.ModuleTheme;
import com.petualanganbelajar.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.GeneralPath;
import java.net.URL;
import java.util.List;

public class GameScreen extends JPanel {

    // --- LOGIC & STATE ---
    private GameSession session;
    private final StoryController storyController = new StoryController(); 
    private int cachedBaseXP = 0;
    
    // [FIX DEBUG] Variabel Pengaman
    private boolean inputLocked = false;   // Mencegah klik ganda pada jawaban
    private boolean isGameEnded = false;   // Mencegah finishGame() dipanggil 2x

    // --- UI STATE ---
    private boolean isStoryMode = false;
    private Image moduleBgImage;
    private boolean isEpilogueMode = false;
    private Image epilogueBgImage;
    private String currentDisplayPattern;

    // --- UI COMPONENTS ---
    private JLayeredPane layeredPane;
    private JPanel gameContentPanel;
    private StoryDialogPanel storyPanel;

    private UserProfileHUD userProfileHUD;
    private GameScoreHUD gameScoreHUD;
    private GameLevelHUD levelHUD;
    private BouncyPauseButton btnPause;

    private JLabel lblInstruction;
    private JPanel visualContainer, answerAreaPanel;
    private ModernBoardPanel questionPanel;
    private JPanel headerPanel;

    // --- HELPERS ---
    private final SoundPlayer soundPlayer = SoundPlayer.getInstance();
    
    // Theme Cache
    private Color themePrimaryColor, themeBgTopColor, themeBgBottomColor, themeAccentColor;
    private Image imgGrass;

    // --- SCALING ---
    private final float BASE_W = 1920f;
    private final float BASE_H = 1080f;
    private float scaleFactor = 1.0f;

    public GameScreen() {
        setLayout(new BorderLayout());
        imgGrass = UIHelper.loadRawImage("grass_decoration.png");
        loadEpilogueBackground();
        initUI();
        
        // --- RESIZE LISTENER ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                layeredPane.setBounds(0, 0, w, h);
                gameContentPanel.setBounds(0, 0, w, h);
                storyPanel.setBounds(0, 0, w, h);
                
                calculateScaleFactor();
                updateResponsiveLayout();
                
                revalidate(); 
                repaint();
            }
        });
    }
    
    private void calculateScaleFactor() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        float sW = (float) getWidth() / BASE_W;
        float sH = (float) getHeight() / BASE_H;
        this.scaleFactor = Math.min(sW, sH);
        if (this.scaleFactor < 0.5f) this.scaleFactor = 0.5f;
    }

    private void updateResponsiveLayout() {
        if (lblInstruction != null) {
            int newFontSize = Math.max(24, (int)(48 * scaleFactor));
            lblInstruction.setFont(new Font("Comic Sans MS", Font.BOLD, newFontSize));
        }

        if (headerPanel != null) {
            int topPad = (int)(20 * scaleFactor);
            int sidePad = (int)(30 * scaleFactor);
            headerPanel.setBorder(new EmptyBorder(topPad, sidePad, 5, sidePad));
        }

        if (levelHUD != null) levelHUD.updateScale(scaleFactor);
        if (userProfileHUD != null) userProfileHUD.updateScale(scaleFactor);
        if (gameScoreHUD != null) gameScoreHUD.updateScale(scaleFactor);
        
        if (questionPanel != null) {
             int qPad = (int)(30 * scaleFactor);
             questionPanel.setBorder(new EmptyBorder(qPad, qPad, qPad, qPad));
        }
        
        if (headerPanel != null) {
            headerPanel.revalidate();
            headerPanel.repaint();
        }
        
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
        // --- HEADER ---
        headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 20, 5, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.NONE; 
        
        // 1. LEFT: Profile
        userProfileHUD = new UserProfileHUD();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.33;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        headerPanel.add(userProfileHUD, gbc);

        // 2. CENTER: Level Info
        levelHUD = new GameLevelHUD();
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 0.33;
        gbc.anchor = GridBagConstraints.NORTH;
        headerPanel.add(levelHUD, gbc);

        // 3. RIGHT: Score
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        gameScoreHUD = new GameScoreHUD();
        btnPause = new BouncyPauseButton();
        btnPause.addActionListener(e -> showPauseMenu());
        rightPanel.add(gameScoreHUD);
        rightPanel.add(btnPause);

        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 0.33;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        headerPanel.add(rightPanel, gbc);

        gameContentPanel.add(headerPanel, BorderLayout.NORTH);

        // --- GAME BOARD ---
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
        // [DEBUG] Reset State
        this.isGameEnded = false;
        this.inputLocked = false;
        
        System.out.println("\n[DEBUG] === START GAME INITIATED ===");
        System.out.println("[DEBUG] Target: Modul " + module.getName() + ", Level " + level);

        // 1. Inisialisasi Session Baru
        this.session = new GameSession(module, level);
        
        // 2. Load Visual
        applyTheme(module.getId());
        loadModuleBackground(module.getId());

        // 3. Ambil User & XP Terbaru (PENTING)
        UserModel u = GameState.getCurrentUser();
        
        if (u != null) {
            // [FIX] Kita ambil XP fresh dari Database
            int totalXpFromDB = session.getBaseXP(u.getId());
            this.cachedBaseXP = totalXpFromDB;
            
            System.out.println("[DEBUG] User ID: " + u.getId());
            System.out.println("[DEBUG] Total XP di Database (Awal Main): " + totalXpFromDB);
        } else {
            this.cachedBaseXP = 0;
            System.out.println("[DEBUG] User NULL! XP set ke 0.");
        }

        // 4. Update HUD
        // [FIX] Total saat ini = XP Lama (DB) + Skor Sesi Ini (0)
        userProfileHUD.updateProfile(cachedBaseXP + session.getScore());
        gameScoreHUD.updateScore(session.getScore());
        levelHUD.setInfo(module.getName(), level);

        // 5. Cek Ketersediaan Soal
        if (session.isGameFinished()) { 
            JOptionPane.showMessageDialog(this, "Soal belum tersedia.");
            ScreenManager.getInstance().showScreen("MODULE_SELECT");
            return;
        }

        playModuleBGM();
        checkAndPlayIntroStory();
    }

    private void applyTheme(int moduleId) {
        ModuleTheme theme = GameThemeManager.getThemeForModule(moduleId);

        this.themePrimaryColor = theme.primary;
        this.themeBgTopColor = theme.bgTop;
        this.themeBgBottomColor = theme.bgBottom;
        this.themeAccentColor = theme.accent;
        
        if (userProfileHUD != null) userProfileHUD.setProfileTheme(theme.profileColors);
        if (levelHUD != null) levelHUD.setTheme(theme.levelTop, theme.levelBottom, theme.levelOutline, theme.levelText);
        if (gameScoreHUD != null) gameScoreHUD.setScoreTheme(theme.scoreTop, theme.scoreBottom);
        
        repaint();
    }

    private void updateHUD() {
        gameScoreHUD.updateScore(session.getScore());
        userProfileHUD.updateProfile(cachedBaseXP + session.getScore());
    }

    private void playModuleBGM() {
        String bgmFile = "bgm_menu.wav";
        if (session.getModule() != null) {
            switch (session.getModule().getId()) {
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

    // --- LOGIKA STORY (Delegasi ke Controller) ---
    private void checkAndPlayIntroStory() {
        UserModel u = GameState.getCurrentUser();
        int userId = (u != null) ? u.getId() : 0;
        
        List<DialogScene> scenes = storyController.getIntroStoryIfNew(userId, session.getModule().getId(), session.getLevel());
        
        if (!scenes.isEmpty()) {
            setStoryMode(true);
            storyPanel.startStory(scenes, () -> {
                storyController.markIntroAsSeen(userId, session.getModule().getId(), session.getLevel());
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

    private void showQuestion() {
        // [DEBUG & FIX] Buka kunci input saat soal baru muncul
        inputLocked = false;
        
        if (session.isGameFinished()) { finishGame(); return; }
        
        QuestionModel q = session.getCurrentQuestion();
        String type = q.getQuestionType().toString();
        
        visualContainer.removeAll(); answerAreaPanel.removeAll();
        
        if ("CLICK".equalsIgnoreCase(type)) {
            lblInstruction.setText("<html><center>" + q.getQuestionText() + "</center></html>");
        } else if ("SEQUENCE_MULTI".equalsIgnoreCase(type)) {
            String[] parts = q.getQuestionText().split("##");
            this.currentDisplayPattern = parts[0] + " ## " + parts[1];
            
            session.initSequenceQueue(parts[2]);
            
            GameVisualizer.renderSequenceMulti(visualContainer, lblInstruction, currentDisplayPattern, scaleFactor);
        } else {
            GameVisualizer.render(visualContainer, lblInstruction, q, session.getModule().getId(), session.getLevel(), scaleFactor);
        }
        
        if (q.getQuestionAudio() != null) soundPlayer.playSFX(q.getQuestionAudio());
        
        GameInputManager.setupInput(q, answerAreaPanel, visualContainer, session.getModule().getId(), currentDisplayPattern, 
            (answer) -> handleAnswer(q, answer), scaleFactor);
        
        visualContainer.revalidate(); visualContainer.repaint();
        answerAreaPanel.revalidate(); answerAreaPanel.repaint();
    }

    private void handleAnswer(QuestionModel q, String answer) {
        // [FIX] Jika input terkunci (sedang animasi jawaban), abaikan klik
        if (inputLocked) {
            System.out.println("[DEBUG] Klik diabaikan karena sedang proses jawaban.");
            return;
        }
        
        String type = q.getQuestionType().toString();
        if ("SEQUENCE_MULTI".equalsIgnoreCase(type)) handleMultiStepAnswer(q, answer);
        else handleStandardAnswer(q, answer);
    }

    private void handleStandardAnswer(QuestionModel q, String answer) {
        int points = session.checkStandardAnswer(answer);
        String name = getPlayerName();

        if (points >= 0) { // Benar
            inputLocked = true; // [FIX] Kunci input segera
            
            soundPlayer.playSFX("correct.wav");
            revealRealImage(q);
            
            String msgText = (points > 0) ? "Kamu dapat +" + points + " Poin!" : "Hebat! Tapi +0 Poin karena tadi sempat salah.";
            if (points > 0) updateHUD();

            showFeedback(name, msgText, GameFeedbackDialog.TYPE_SUCCESS, true);
        } else { // Salah
            soundPlayer.playSFX("wrong.wav"); 
            showFeedback(name, "Ups Salah " + name + "!\nAyo coba lagi ya...", GameFeedbackDialog.TYPE_ERROR, false);
        }
    }

    private void handleMultiStepAnswer(QuestionModel q, String input) {
        int result = session.checkSequenceAnswer(input);
        String name = getPlayerName();

        if (result == -2) return;

        if (result >= 0) { // Benar
            soundPlayer.playSFX("correct.wav");
            
            String[] parts = currentDisplayPattern.split("##");
            String pola = parts[1].replaceFirst("_", input);
            this.currentDisplayPattern = parts[0] + "##" + pola;
            GameVisualizer.renderSequenceMulti(visualContainer, lblInstruction, currentDisplayPattern, scaleFactor);

            if (result > 0) { // Selesai
                inputLocked = true; // [FIX] Kunci input
                updateHUD();
                showFeedback(name, "Kamu dapat +" + result + " Poin!", GameFeedbackDialog.TYPE_SUCCESS, true);
            }
        } else { // Salah
            soundPlayer.playSFX("wrong.wav");
            showFeedback(name, "Salah " + name + "!\nAyo coba lagi.", GameFeedbackDialog.TYPE_ERROR, false);
        }
    }
    
    private void showFeedback(String title, String message, int type, boolean nextQuestion) {
        Window window = SwingUtilities.getWindowAncestor(this);
        new GameFeedbackDialog(window, title, message, type).setVisible(true);
        if (nextQuestion) {
            Timer t = new Timer(500, e -> { 
                session.nextQuestion();
                showQuestion(); 
            });
            t.setRepeats(false); t.start();
        }
    }

    private boolean revealRealImage(QuestionModel q) {
        String qImg = q.getQuestionImage();
        if (qImg != null && qImg.startsWith("SILHOUETTE:")) {
            String realFile = qImg.replace("SILHOUETTE:", "");
            for (Component c : visualContainer.getComponents()) {
                if (c instanceof JLabel) {
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
        if (isGameEnded) return;
        isGameEnded = true;

        UserModel user = GameState.getCurrentUser();
        boolean passed = false;
        
        // Variabel lokal untuk menyimpan status Level Up
        boolean levelUpOccurred = false;
        int newLevelChar = 0;

        if (user != null) {
            // 1. Simpan Progress ke Database
            passed = session.saveProgress(user.getId());
            
            // 2. [FIX] Ambil Status Level Up DARI SESSION (Setelah saveProgress dipanggil)
            levelUpOccurred = session.isLeveledUp();
            newLevelChar = session.getNewCharacterLevel();
            
            if (passed) soundPlayer.playSFX("level_complete.wav");
            else soundPlayer.playSFX("level_failed.wav");
        }

        // 3. Logika Urutan Tampilan: Dialog Level Up -> Outro Story
        if (levelUpOccurred) {
            final int levelToShow = newLevelChar;
            SwingUtilities.invokeLater(() -> {
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                // Tampilkan Dialog
                LevelUpDialog dialog = new LevelUpDialog(parentFrame, levelToShow);
                dialog.setVisible(true); // Program akan berhenti di sini sampai dialog ditutup (Modal)
                
                // Setelah dialog ditutup, lanjut ke Outro
                checkAndPlayOutroStoryAndFinish();
            });
        } else {
            // Jika TIDAK naik level, langsung lanjut
            checkAndPlayOutroStoryAndFinish();
        }
    }

    // --- OUTRO & EPILOGUE ---
    private void checkAndPlayOutroStoryAndFinish() {
        UserModel u = GameState.getCurrentUser();
        int userId = (u != null) ? u.getId() : 0;

        if (session.isPassedHalf()) {
            List<DialogScene> scenes = storyController.getOutroStoryIfNew(userId, session.getModule().getId(), session.getLevel());

            if (!scenes.isEmpty()) {
                String epilogBgm = "bgm_menu.wav";
                if (session.getModule() != null) {
                    switch(session.getModule().getId()) {
                        case 1: epilogBgm = "bgm_epilog_angka.wav"; break;
                        case 2: epilogBgm = "bgm_epilog_huruf.wav"; break;
                        case 3: epilogBgm = "bgm_epilog_warna.wav"; break;
                        case 4: epilogBgm = "bgm_epilog_bentuk.wav"; break;
                    }
                }
                soundPlayer.playBGM(epilogBgm);

                setStoryMode(true);
                storyPanel.startStory(scenes, () -> {
                    storyController.markOutroAsSeen(userId, session.getModule().getId(), session.getLevel());
                    checkEpilogue(userId); 
                });
                return; 
            }
        }
        
        if (session.isPassedHalf()) {
             checkEpilogue(userId);
        } else {
             goToResultScreen();
        }
    }

    private void checkEpilogue(int userId) {
        if (session.checkIfGameFullyCompleted(userId)) { 
            List<DialogScene> epilogue = storyController.getEpilogueIfNew(userId);
            
            if (!epilogue.isEmpty()) {
                soundPlayer.playBGM("bgm_ending.wav");
                isEpilogueMode = true;
                setStoryMode(true); 
                repaint(); 
                
                storyPanel.startStory(epilogue, () -> {
                    storyController.markEpilogueAsSeen(userId);
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
        ScreenManager.getInstance().showResult(session.getModule(), session.getLevel(), session.getScore(), session.getMaxScore());
    }

    private String getPlayerName() {
        UserModel user = GameState.getCurrentUser();
        return (user != null && user.getName() != null) ? user.getName() : "Teman";
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
            int rad = (int)(50 * scaleFactor);
            g2.setColor(new Color(0,0,0,40));g2.fillRoundRect(8,8,w-16,h-16,rad,rad);
            g2.setColor(Color.WHITE);g2.fillRoundRect(0,0,w-8,h-8,rad,rad);
            g2.setColor(themePrimaryColor);g2.setStroke(new BasicStroke(8));g2.drawRoundRect(4,4,w-16,h-16,rad,rad);
            g2.setColor(Color.WHITE);g2.setStroke(new BasicStroke(2));g2.drawRoundRect(6,6,w-20,h-20,rad-5,rad-5);
            g2.dispose();
        }
    }
}