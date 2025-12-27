/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.core;

import javax.swing.*;
import java.awt.*;
import com.petualanganbelajar.ui.screen.GameScreen;
import com.petualanganbelajar.ui.screen.LevelSelectionScreen;
import com.petualanganbelajar.ui.screen.StoryScreen; // [BARU] Import Story
import com.petualanganbelajar.ui.screen.ResultScreen;
import com.petualanganbelajar.model.ModuleModel;

/**
 *
 * @author DD
 */
public class ScreenManager extends JPanel {
    private static ScreenManager instance;
    private final JFrame window;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    
    // Referensi ke layar-layar dinamis
    private GameScreen gameScreen; 
    private LevelSelectionScreen levelScreen;
    private StoryScreen storyScreen; // [BARU] Referensi layar cerita
    private ResultScreen resultScreen;

    // Singleton
    public static ScreenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ScreenManager belum di-init di Main!");
        }
        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new ScreenManager();
        }
    }

    // Constructor
    public ScreenManager() {
        window = new JFrame(GameConfig.GAME_TITLE);
        window.setSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setLocationRelativeTo(null); 

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(GameConfig.COLOR_BG);
        
        window.add(mainPanel);
    }

    public void showWindow() {
        window.setVisible(true);
    }

    // Mendaftarkan layar statis (Menu, Title, Splash, Settings, dll)
    public void addScreen(String name, JPanel panel) {
        mainPanel.add(panel, name);
    }

    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }
    
    // --- LOGIKA UTAMA NAVIGASI GAME ---
    
    // 1. TAMPILKAN PILIHAN LEVEL
    public void showLevelSelect(ModuleModel module) {
        if (levelScreen == null) {
            levelScreen = new LevelSelectionScreen();
            addScreen("LEVEL_SELECT", levelScreen);
        }
        
        levelScreen.setModule(module); 
        showScreen("LEVEL_SELECT");
    }
    
    // 2. [BARU] TAMPILKAN STORY / TUTORIAL
    // Dipanggil saat level diklik. Ini jembatan sebelum masuk game.
    public void showStory(ModuleModel module, int level) {
        if (storyScreen == null) {
            storyScreen = new StoryScreen();
            addScreen("STORY", storyScreen);
        }
        
        storyScreen.setupStory(module, level); // Siapkan teks cerita & tutorial
        showScreen("STORY");
    }
    
    // 3. START GAME (GAMEPLAY)
    // Dipanggil oleh tombol "AYO MULAI" di StoryScreen
    public void showGame(ModuleModel module, int level) {
        if (gameScreen == null) {
            gameScreen = new GameScreen();
            addScreen("GAME", gameScreen);
        }
        
        // Reset skor dan load soal baru
        gameScreen.startGame(module, level);
        
        // Tampilkan layarnya
        showScreen("GAME");
    }
    
    // 4. TAMPILKAN HASIL (RESULT)
    public void showResult(ModuleModel module, int level, int score, int maxScore) {
        if (resultScreen == null) {
            resultScreen = new ResultScreen();
            addScreen("RESULT", resultScreen);
        }
        resultScreen.showResult(module, level, score, maxScore);
        showScreen("RESULT");
    }
    
}