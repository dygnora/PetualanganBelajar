package com.petualanganbelajar.util;

public class LevelManager {

    // Konstanta: Faktor pengali kesulitan
    private static final int BASE_XP_FACTOR = 100; 

    /**
     * Menghitung Level saat ini berdasarkan Total Skor.
     * Rumus Baru: Level = Akar(TotalScore / 100) + 1
     * Contoh: 
     * - Skor 0   -> Level 1
     * - Skor 50  -> Level 1
     * - Skor 100 -> Level 2
     */
    public static int calculateLevelFromScore(int totalScore) {
        if (totalScore < 0) return 1;
        // Kita tambah +1 agar start dari Level 1 (bukan Level 0)
        return (int) Math.sqrt(totalScore / (double) BASE_XP_FACTOR) + 1;
    }

    /**
     * Menghitung batas bawah XP untuk level tertentu.
     * Rumus: XP = 100 * (Level - 1)^2
     * Contoh:
     * - Level 1 Start -> 100 * 0^2 = 0 XP
     * - Level 2 Start -> 100 * 1^2 = 100 XP
     * - Level 3 Start -> 100 * 2^2 = 400 XP
     */
    public static int getXPStartForLevel(int level) {
        if (level <= 1) return 0;
        return BASE_XP_FACTOR * (level - 1) * (level - 1);
    }

    /**
     * Menghitung progress (%) menuju level berikutnya.
     */
    public static float getProgressToNextLevel(int totalScore) {
        int currentLevel = calculateLevelFromScore(totalScore);
        int nextLevel = currentLevel + 1;

        int xpStartCurrent = getXPStartForLevel(currentLevel); // Misal Lvl 1 = 0
        int xpStartNext = getXPStartForLevel(nextLevel);       // Misal Lvl 2 = 100

        int xpGained = totalScore - xpStartCurrent;            // 50 - 0 = 50
        int xpNeeded = xpStartNext - xpStartCurrent;           // 100 - 0 = 100

        if (xpNeeded == 0) return 1.0f; // Prevent division by zero

        // Return float (0.0 - 1.0)
        return (float) xpGained / (float) xpNeeded;
    }
    
    /**
     * Helper untuk menampilkan teks di UI (Tooltip)
     */
    public static String getXPText(int totalScore) {
        int currentLevel = calculateLevelFromScore(totalScore);
        int xpStartCurrent = getXPStartForLevel(currentLevel);
        int xpStartNext = getXPStartForLevel(currentLevel + 1);
        
        int current = totalScore - xpStartCurrent;
        int target = xpStartNext - xpStartCurrent;
        
        return current + " / " + target + " XP";
    }
}