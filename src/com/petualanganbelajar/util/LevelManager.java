package com.petualanganbelajar.util;

public class LevelManager {

    // Konstanta: Berapa XP dasar untuk mencapai Level 1?
    // Semakin besar angka ini, semakin susah naik level.
    private static final int BASE_XP_FACTOR = 100; 

    /**
     * Menghitung Level saat ini berdasarkan Total Skor.
     * Rumus: Level = Akar(TotalScore / 100)
     */
    public static int calculateLevelFromScore(int totalScore) {
        if (totalScore < BASE_XP_FACTOR) return 1; // Minimal Level 1
        
        // Math.sqrt adalah Akar Kuadrat
        int level = (int) Math.sqrt(totalScore / (double) BASE_XP_FACTOR);
        
        // Pastikan minimal level 1 (jika hasil perhitungan 0)
        return Math.max(1, level);
    }

    /**
     * Menghitung berapa Total XP yang dibutuhkan untuk mencapai level tertentu.
     * Rumus: XP = 100 * (Level^2)
     */
    public static int getXPRequiredForLevel(int targetLevel) {
        return BASE_XP_FACTOR * (targetLevel * targetLevel);
    }

    /**
     * Menghitung progress (%) menuju level berikutnya.
     * Berguna jika kamu ingin membuat Progress Bar Level (Exp Bar).
     */
    public static float getProgressToNextLevel(int totalScore) {
        int currentLevel = calculateLevelFromScore(totalScore);
        int nextLevel = currentLevel + 1;

        int xpCurrentLevel = getXPRequiredForLevel(currentLevel);
        int xpNextLevel = getXPRequiredForLevel(nextLevel);

        int xpGainedInThisLevel = totalScore - xpCurrentLevel;
        int xpNeededForNext = xpNextLevel - xpCurrentLevel;

        return (float) xpGainedInThisLevel / xpNeededForNext;
    }
}