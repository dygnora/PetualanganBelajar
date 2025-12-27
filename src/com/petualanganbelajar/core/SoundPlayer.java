/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.core;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author DD
 */
public class SoundPlayer {
    
    private static SoundPlayer instance;
    private Clip bgmClip; // Untuk Musik Latar (Looping)
    private boolean isMuted = false;
    
    // Singleton Pattern
    public static SoundPlayer getInstance() {
        if (instance == null) {
            instance = new SoundPlayer();
        }
        return instance;
    }
    
    // Putar SFX (Sound Effect) - Sekali main (Contoh: Klik, Benar, Salah)
    public void playSFX(String filename) {
        if (isMuted) return;
        
        try {
            // Asumsi file ada di folder resources/audio/
            File soundFile = new File("resources/audio/" + filename);
            if (!soundFile.exists()) return; // Kalau file gak ada, diam aja (jangan error)
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start(); // Mainkan
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Gagal putar SFX: " + e.getMessage());
        }
    }
    
    // Putar BGM (Background Music) - Berulang-ulang (Looping)
    public void playBGM(String filename) {
        stopBGM(); // Matikan lagu sebelumnya jika ada
        
        if (isMuted) return;
        
        try {
            File soundFile = new File("resources/audio/" + filename);
            if (!soundFile.exists()) {
                System.out.println("Info: File BGM tidak ditemukan (" + filename + ")");
                return;
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioIn);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // Ulang terus
            bgmClip.start();
            
        } catch (Exception e) {
            System.err.println("Gagal putar BGM: " + e.getMessage());
        }
    }
    
    public void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }
    
    // Fitur Mute dari Settings
    public void setMute(boolean mute) {
        this.isMuted = mute;
        if (mute) {
            stopBGM();
        } else {
            // Jika unmute, idealnya restart BGM (Opsional: playBGM("main_theme.wav"))
        }
    }
}