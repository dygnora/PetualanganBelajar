/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.model;

/**
 *
 * @author DD
 */
public class LeaderboardEntry {
    private String playerName;
    private String avatar;
    private int level; // [BARU] Level User
    private int score; // Total Score

    // Constructor Baru (Tanpa ModuleName, Tambah Level)
    public LeaderboardEntry(String playerName, String avatar, int level, int score) {
        this.playerName = playerName;
        this.avatar = avatar;
        this.level = level;
        this.score = score;
    }

    public String getPlayerName() { return playerName; }
    public String getAvatar() { return avatar; }
    public int getLevel() { return level; } // [BARU]
    public int getScore() { return score; }
}