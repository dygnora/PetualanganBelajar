package com.petualanganbelajar.core;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * SoundPlayer (Safe & Optimized Version)
 * - Support file JAR (menggunakan getResource)
 * - Mencegah Memory Leak (Auto-close SFX)
 * - Singleton Pattern
 */
public class SoundPlayer {

    private static SoundPlayer instance;
    private Clip bgmClip; // Untuk musik latar (Looping)
    
    private boolean isMuted = false;
    private float currentVolume = 0.0f; // Default Normal (0.0f = 100%)

    // Private constructor (Singleton)
    private SoundPlayer() {}

    public static SoundPlayer getInstance() {
        if (instance == null) {
            instance = new SoundPlayer();
        }
        return instance;
    }

    // --- PLAY BACKGROUND MUSIC (BGM) ---
    public void playBGM(String filename) {
        // 1. Stop musik sebelumnya biar gak tumpuk
        stopBGM();

        if (isMuted) return;

        try {
            // 2. Load File dengan cara aman untuk JAR
            URL url = getClass().getResource("/audio/" + filename);
            if (url == null) {
                System.err.println("Audio tidak ditemukan: " + filename);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioIn);
            
            // Atur Volume
            setClipVolume(bgmClip);

            // Loop selamanya
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Gagal main BGM: " + e.getMessage());
        }
    }

    public void stopBGM() {
        if (bgmClip != null) {
            if (bgmClip.isRunning()) bgmClip.stop();
            bgmClip.close(); // Penting: Lepas resource
            bgmClip = null;
        }
    }

    // --- PLAY SOUND EFFECT (SFX) ---
    public void playSFX(String filename) {
        if (isMuted) return;

        try {
            URL url = getClass().getResource("/audio/" + filename);
            if (url == null) {
                // Silent fail agar game tidak crash cuma gara-gara suara hilang
                System.err.println("SFX missing: " + filename);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            
            setClipVolume(clip);

            // 3. RESOURCE MANAGEMENT: Auto-close saat selesai
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close(); // Buang dari memori
                }
            });

            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- VOLUME CONTROL ---
    private void setClipVolume(Clip clip) {
        if (clip == null) return;
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // Mengurangi volume (angka negatif = lebih kecil)
            // Range biasanya -80.0f sampai 6.0f
            gainControl.setValue(currentVolume); 
        } catch (Exception e) {
            // Tidak semua audio support Master Gain, abaikan saja
        }
    }

    public void setMute(boolean mute) {
        this.isMuted = mute;
        if (mute) stopBGM();
    }
    
    // Set volume global (misal dari Settings)
    // val: -80.0f (Mute) sampai 6.0f (Max)
    public void setVolume(float val) {
        this.currentVolume = val;
        if (bgmClip != null && bgmClip.isOpen()) {
            setClipVolume(bgmClip);
        }
    }
}