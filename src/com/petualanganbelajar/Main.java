/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.petualanganbelajar;
import com.petualanganbelajar.ui.screen.TitleScreen;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.db.DatabaseInitializer;
import com.petualanganbelajar.db.DatabaseConnection;
import javax.swing.SwingUtilities;
/**
 *
 * @author DD
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Init Database
                DatabaseInitializer.createTables();

                // 2. Init UI Manager
                ScreenManager.init();
                ScreenManager sm = ScreenManager.getInstance();

                // 3. DAFTARKAN LAYAR UTAMA
                sm.addScreen("SPLASH", new com.petualanganbelajar.ui.screen.SplashScreen());
                sm.addScreen("TITLE", new TitleScreen());
                sm.addScreen("MAIN_MENU", new com.petualanganbelajar.ui.screen.MainMenuScreen());
                sm.addScreen("SETTINGS", new com.petualanganbelajar.ui.screen.SettingsScreen());
                sm.addScreen("LEADERBOARD", new com.petualanganbelajar.ui.screen.LeaderboardScreen());
                sm.addScreen("PROFILE_CREATE", new com.petualanganbelajar.ui.screen.ProfileCreateScreen());
                sm.addScreen("PROFILE_SELECT", new com.petualanganbelajar.ui.screen.ProfileSelectionScreen());
                sm.addScreen("MODULE_SELECT", new com.petualanganbelajar.ui.screen.ModuleSelectionScreen());
                
                // 4. Tampilkan Layar Judul
                sm.showScreen("SPLASH");
                sm.showWindow();

                System.out.println("--- APLIKASI DIMULAI ---");
                
                // Shutdown Hook
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    DatabaseConnection.close();
                }));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
