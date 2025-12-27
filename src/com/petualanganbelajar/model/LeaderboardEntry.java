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
    private String moduleName;
    private int score;

    public LeaderboardEntry(String playerName, String avatar, String moduleName, int score) {
        this.playerName = playerName;
        this.avatar = avatar;
        this.moduleName = moduleName;
        this.score = score;
    }

    public String getPlayerName() { return playerName; }
    public String getAvatar() { return avatar; }
    public String getModuleName() { return moduleName; }
    public int getScore() { return score; }
}
