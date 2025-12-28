package com.petualanganbelajar.core;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer {
    private static SoundPlayer instance;
    private Clip bgmClip;
    private boolean isMuted = false;
    
    // Simpan volume terakhir (0.0f - 1.0f)
    private float currentBGMVolume = 0.8f;
    private float currentSFXVolume = 1.0f;

    public static SoundPlayer getInstance() {
        if (instance == null) {
            instance = new SoundPlayer();
        }
        return instance;
    }

    // --- LOGIKA VOLUME CONTROL (GAIN) ---
    private void setClipVolume(Clip clip, float volume) {
        if (clip == null) return;
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // Konversi skala 0-1 ke Decibel (-80db s/d 6db)
            float db = (float) (Math.log(Math.max(volume, 0.0001f)) / Math.log(10.0) * 20.0);
            gainControl.setValue(db);
        } catch (Exception e) {
            // Beberapa klip audio pendek mungkin tidak support control, abaikan saja
        }
    }

    public void setBGMVolume(int volumePercent) {
        this.currentBGMVolume = volumePercent / 100.0f;
        if (bgmClip != null && bgmClip.isRunning() && !isMuted) {
            setClipVolume(bgmClip, currentBGMVolume);
        }
    }

    public void setSFXVolume(int volumePercent) {
        this.currentSFXVolume = volumePercent / 100.0f;
    }

    // --- PLAY SFX ---
    public void playSFX(String filename) {
        if (isMuted) return;
        try {
            File soundFile = new File("resources/audio/" + filename);
            if (!soundFile.exists()) return;

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            
            // Set Volume SFX sebelum main
            setClipVolume(clip, currentSFXVolume);
            
            clip.start();
        } catch (Exception e) {
            System.err.println("Gagal SFX: " + e.getMessage());
        }
    }

    // --- PLAY BGM ---
    public void playBGM(String filename) {
        stopBGM();
        if (isMuted) return;
        try {
            File soundFile = new File("resources/audio/" + filename);
            if (!soundFile.exists()) return;

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioIn);
            
            // Set Volume BGM
            setClipVolume(bgmClip, currentBGMVolume);
            
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
        } catch (Exception e) {
            System.err.println("Gagal BGM: " + e.getMessage());
        }
    }

    public void stopBGM() {
        if (bgmClip != null) {
            if (bgmClip.isRunning()) bgmClip.stop();
            bgmClip.close();
        }
    }

    public void setMute(boolean mute) {
        this.isMuted = mute;
        if (mute) {
            stopBGM();
        } else {
            // Jika punya lagu tema utama, bisa dipanggil ulang di sini
            // playBGM("theme.wav"); 
        }
    }
}