package com.petualanganbelajar;

import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.db.DatabaseInitializer; 
import com.petualanganbelajar.ui.screen.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Taskbar; // Import Taskbar
import java.net.URL;

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
        
        // [FIX] SET LOGO APLIKASI (AMAN UNTUK SEMUA OS)
        try {
            URL iconUrl = Main.class.getResource("/images/logo.png");
            if (iconUrl != null) {
                Image icon = new ImageIcon(iconUrl).getImage();
                
                // 1. Set Icon Utama (Wajib untuk Windows/Linux)
                // Ini saja sudah cukup untuk Windows menampilkan icon di Taskbar & Title Bar
                frame.setIconImage(icon);
                
                // 2. Set Icon Taskbar Khusus (Opsional - Terutama untuk macOS)
                // Kita bungkus try-catch & if-check agar tidak crash di Windows
                try {
                    if (Taskbar.isTaskbarSupported()) {
                        Taskbar taskbar = Taskbar.getTaskbar();
                        if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                            taskbar.setIconImage(icon);
                        }
                    }
                } catch (UnsupportedOperationException e) {
                    // Abaikan error jika OS tidak support fitur ini (Windows sering throw ini)
                    System.out.println("Info: Taskbar API tidak didukung di OS ini (Aman, pakai frame icon).");
                } catch (SecurityException e) {
                    // Abaikan security restriction
                }
                
            } else {
                System.err.println("Warning: Logo tidak ditemukan di /images/logo.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.setUndecorated(true); 
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setLocationRelativeTo(null); 
        
        frame.add(sm.getMainPanel());

        sm.showScreen("TITLE"); 

        frame.setVisible(true);
    }
}