package com.petualanganbelajar.core;

import javax.swing.*;
import java.awt.*;
import com.petualanganbelajar.ui.screen.GameScreen;
import com.petualanganbelajar.ui.screen.ResultScreen;
import com.petualanganbelajar.model.ModuleModel;

public class ScreenManager {
    
    private static ScreenManager instance;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    
    // Referensi layar dinamis
    private GameScreen gameScreenRef; 
    private ResultScreen resultScreenRef;
    // StoryScreen sudah dihapus dari sini

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

    public JPanel getMainPanel() {
        return mainPanel;
    }

    // --- MENDAFTARKAN LAYAR ---
    public void addScreen(String name, JPanel screen) {
        mainPanel.add(screen, name);
        
        // Auto-detect tipe layar untuk disimpan referensinya
        if (screen instanceof GameScreen) {
            this.gameScreenRef = (GameScreen) screen;
        } else if (screen instanceof ResultScreen) {
            this.resultScreenRef = (ResultScreen) screen;
        }
    }

    // --- PINDAH LAYAR ---
    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }
    
    // --- NAVIGASI KHUSUS ---

    // A. START GAME (Story akan otomatis dicek di dalam GameScreen)
    public void showGame(ModuleModel module, int level) {
        if (gameScreenRef != null) {
            gameScreenRef.startGame(module, level);
            showScreen("GAME");
        } else {
            // Fallback jika lupa addScreen di Main, buat baru
            gameScreenRef = new GameScreen();
            addScreen("GAME", gameScreenRef);
            gameScreenRef.startGame(module, level);
            showScreen("GAME");
        }
    }
    
    // B. TAMPILKAN HASIL
    public void showResult(ModuleModel module, int level, int score, int maxScore) {
        if (resultScreenRef == null) {
            resultScreenRef = new ResultScreen();
            addScreen("RESULT", resultScreenRef);
        }
        resultScreenRef.showResult(module, level, score, maxScore);
        showScreen("RESULT");
    }
}