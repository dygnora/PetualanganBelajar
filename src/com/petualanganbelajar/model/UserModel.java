/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.model;

/**
 *
 * @author DD
 */
public class UserModel {
    private int id;
    private String name;
    private String avatar;       // Nama file gambar (misal: "avatar_1.png")
    private int level;           // [BARU] Level User
    private int bgmVolume;
    private int sfxVolume;
    private boolean isActive;

    // Constructor Kosong (Wajib ada)
    public UserModel() {}

    // Constructor Lengkap (Updated with Level)
    public UserModel(int id, String name, String avatar, int level, int bgmVolume, int sfxVolume, boolean isActive) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.level = level;
        this.bgmVolume = bgmVolume;
        this.sfxVolume = sfxVolume;
        this.isActive = isActive;
    }

    // Getter & Setter (Cara cepat akses data)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    // [BARU] Getter Setter Level
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    // Volume default 80/100 jika null
    public int getBgmVolume() { return bgmVolume; }
    public void setBgmVolume(int bgmVolume) { this.bgmVolume = bgmVolume; }

    public int getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(int sfxVolume) { this.sfxVolume = sfxVolume; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}