/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.core;
import javax.swing.*;
import java.awt.*;
import com.petualanganbelajar.ui.screen.GameScreen;
import com.petualanganbelajar.ui.screen.LevelSelectionScreen; // [UPDATE] Import baru
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.ui.screen.ResultScreen;
/**
 *
 * @author DD
 */
public class ScreenManager extends JPanel {
    private static ScreenManager instance;
    private final JFrame window;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    
    // Simpan referensi layar penting agar bisa diakses langsung
    private GameScreen gameScreen; 
    private LevelSelectionScreen levelScreen; // [UPDATE] Tambah referensi layar level
    
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

    public void addScreen(String name, JPanel panel) {
        mainPanel.add(panel, name);
    }

    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }
    
    // --- LOGIKA UTAMA NAVIGASI GAME ---
    
    // [UPDATE] Method baru untuk menampilkan Pilihan Level
    public void showLevelSelect(ModuleModel module) {
        // Lazy Loading: Layar baru dibuat saat pertama kali dibutuhkan
        if (levelScreen == null) {
            levelScreen = new LevelSelectionScreen();
            addScreen("LEVEL_SELECT", levelScreen);
        }
        
        // Set modul agar layar level tahu judulnya (Angka/Huruf/dll)
        levelScreen.setModule(module); 
        showScreen("LEVEL_SELECT");
    }
    
    // [UPDATE] Method Start Game sekarang menerima parameter Level
    public void startGame(ModuleModel module, int level) {
        if (gameScreen == null) {
            gameScreen = new GameScreen();
            addScreen("GAME", gameScreen);
        }
        
        // Reset skor dan set modul serta level
        gameScreen.startGame(module, level);
        
        // Tampilkan layarnya
        showScreen("GAME");
    }
    
    public void showResult(ModuleModel module, int level, int score, int maxScore) {
        if (resultScreen == null) {
            resultScreen = new ResultScreen();
            addScreen("RESULT", resultScreen);
        }
        resultScreen.showResult(module, level, score, maxScore);
        showScreen("RESULT");
    }
    
}
