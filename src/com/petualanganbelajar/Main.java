package com.petualanganbelajar;

import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.db.DatabaseInitializer; 
import com.petualanganbelajar.ui.screen.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    
    private static final boolean DEBUG_SKIP_SPLASH = false;


    public static void main(String[] args) {

        DatabaseInitializer.createTables();

        SwingUtilities.invokeLater(() -> {

            if (DEBUG_SKIP_SPLASH) {
                launchMainApp();
            } else {
                SplashScreen splash = new SplashScreen(() -> {
                    launchMainApp();
                });
                splash.setVisible(true);
            }

        });
    }


    private static void launchMainApp() {
        ScreenManager sm = ScreenManager.getInstance();

        // --- DAFTAR SCREEN ---
         sm.addScreen("TITLE", new TitleScreen());
        sm.addScreen("MAIN_MENU", new MainMenuScreen());
        sm.addScreen("PROFILE_SELECT", new ProfileSelectionScreen());
        sm.addScreen("PROFILE_CREATE", new ProfileCreateScreen());
        sm.addScreen("MODULE_SELECT", new ModuleSelectionScreen());
        sm.addScreen("LEADERBOARD", new LeaderboardScreen());
        sm.addScreen("GAME", new GameScreen());
        sm.addScreen("SETTINGS", new SettingsScreen()); 

        // --- SETUP FRAME ---
        JFrame frame = new JFrame("Petualangan Belajar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setUndecorated(true); 
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setLocationRelativeTo(null); 
        
        frame.add(sm.getMainPanel());

        // Mulai dari Title Screen (Pastikan TitleScreen sudah dibuat/di-uncomment jika ingin dipakai)
        // Jika TitleScreen belum siap, ganti ke "MAIN_MENU" atau "PROFILE_SELECT" sementara
        sm.showScreen("TITLE"); 

        frame.setVisible(true);
    }
}