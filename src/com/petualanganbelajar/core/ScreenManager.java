package com.petualanganbelajar.core;

import javax.swing.*;
import java.awt.*;
import com.petualanganbelajar.ui.screen.GameScreen;
import com.petualanganbelajar.ui.screen.StoryScreen;
import com.petualanganbelajar.ui.screen.ResultScreen;
import com.petualanganbelajar.model.ModuleModel;

public class ScreenManager {
    
    private static ScreenManager instance;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    
    // Referensi ke layar dinamis untuk memanggil method khususnya
    private GameScreen gameScreenRef; 
    private StoryScreen storyScreenRef; 
    private ResultScreen resultScreenRef;

    // Private Constructor (Singleton)
    private ScreenManager() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(GameConfig.COLOR_BG);
    }

    public static synchronized ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    // --- 1. METHOD WAJIB UNTUK MAIN.JAVA ---
    public JPanel getMainPanel() {
        return mainPanel;
    }

    // --- 2. MENDAFTARKAN LAYAR ---
    public void addScreen(String name, JPanel screen) {
        mainPanel.add(screen, name);
        
        // Auto-detect: Simpan referensi jika tipe layarnya khusus
        // Ini agar kita bisa memanggil method seperti .startGame() atau .setupStory() nanti
        if (screen instanceof GameScreen) {
            this.gameScreenRef = (GameScreen) screen;
        } else if (screen instanceof StoryScreen) {
            this.storyScreenRef = (StoryScreen) screen;
        } else if (screen instanceof ResultScreen) {
            this.resultScreenRef = (ResultScreen) screen;
        }
    }

    // --- 3. PINDAH LAYAR (DASAR) ---
    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }
    
    // --- 4. NAVIGASI KHUSUS GAMEPLAY ---

    // A. TAMPILKAN STORY (Jika Modul punya cerita)
    public void showStory(ModuleModel module, int level) {
        // Jika belum ada StoryScreen, buat baru (Lazy Loading)
        if (storyScreenRef == null) {
            storyScreenRef = new StoryScreen();
            addScreen("STORY", storyScreenRef);
        }
        
        storyScreenRef.setupStory(module, level); 
        showScreen("STORY");
    }
    
    // B. START GAME (Inti Permainan)
    public void showGame(ModuleModel module, int level) {
        // Cek apakah GameScreen sudah didaftarkan di Main.java
        if (gameScreenRef != null) {
            gameScreenRef.startGame(module, level);
            showScreen("GAME");
        } else {
            // Fallback jika lupa addScreen di Main
            gameScreenRef = new GameScreen();
            addScreen("GAME", gameScreenRef);
            gameScreenRef.startGame(module, level);
            showScreen("GAME");
        }
    }
    
    // C. TAMPILKAN HASIL (Result)
    public void showResult(ModuleModel module, int level, int score, int maxScore) {
        if (resultScreenRef == null) {
            resultScreenRef = new ResultScreen();
            addScreen("RESULT", resultScreenRef);
        }
        resultScreenRef.showResult(module, level, score, maxScore);
        showScreen("RESULT");
    }
}