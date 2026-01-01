package com.petualanganbelajar.core;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundPlayer {

    private static SoundPlayer instance;
    private Clip bgmClip;
    private String currentBgmFile = ""; 
    
    private boolean isMuted = false;
    
    // [BARU] Memori untuk menyimpan posisi slider (0-100)
    private int volumePercentBGM = 80; // Default
    private int volumePercentSFX = 100; // Default

    private SoundPlayer() {}

    public static SoundPlayer getInstance() {
        if (instance == null) {
            instance = new SoundPlayer();
        }
        return instance;
    }

    // --- GETTER & SETTER UNTUK SLIDER ---
    public int getVolumeBGM() { return volumePercentBGM; }
    public int getVolumeSFX() { return volumePercentSFX; }

    public void setVolumeBGM(int percent) {
        this.volumePercentBGM = percent;
        updateClipVolume(bgmClip, percent); // Update langsung BGM yang sedang main
    }

    public void setVolumeSFX(int percent) {
        this.volumePercentSFX = percent;
        // SFX biasanya sekali main, jadi tidak perlu update clip yang sedang jalan,
        // tapi clip berikutnya akan memakai nilai ini.
    }

    public boolean isMuted() { return isMuted; }

    public void setMute(boolean mute) {
        this.isMuted = mute;
        if (mute) {
            if (bgmClip != null && bgmClip.isRunning()) bgmClip.stop(); // Pause
        } else {
            // Kalau unmute, cek apakah harus resume BGM?
            // Untuk simplisitas, kalau unmute biasanya user ingin dengar musik lagi
            if (bgmClip != null && !bgmClip.isRunning()) bgmClip.start();
            
            // Restore volume
            updateClipVolume(bgmClip, volumePercentBGM);
        }
    }

    // --- LOGIKA AUDIO ---
    
    public void playBGM(String filename) {
        if (isMuted) return; // Jangan main kalau mute, tapi currentBgmFile jangan diubah biar bisa resume
        
        if (bgmClip != null && bgmClip.isOpen() && filename.equals(currentBgmFile)) {
            return; // Lagu sama, jangan restart
        }

        stopBGM();
        currentBgmFile = filename; 

        new Thread(() -> {
            try {
                InputStream is = getClass().getResourceAsStream("/audio/" + filename);
                if (is == null) return;

                BufferedInputStream bis = new BufferedInputStream(is);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bis);
                
                bgmClip = AudioSystem.getClip();
                bgmClip.open(audioIn);
                
                updateClipVolume(bgmClip, volumePercentBGM); // Set volume awal

                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                bgmClip.start();
            } catch (Exception e) {
                System.err.println("Error BGM: " + e.getMessage());
            }
        }).start();
    }

    public void playSFX(String filename) {
        if (isMuted) return;
        new Thread(() -> {
            try {
                InputStream is = getClass().getResourceAsStream("/audio/" + filename);
                if (is == null) return;
                BufferedInputStream bis = new BufferedInputStream(is);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bis);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                
                updateClipVolume(clip, volumePercentSFX); // Pakai volume SFX

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) clip.close();
                });
                clip.start();
            } catch (Exception e) {}
        }).start();
    }

    public void stopBGM() {
        if (bgmClip != null) {
            if (bgmClip.isRunning()) bgmClip.stop();
            bgmClip.close();
            bgmClip = null;
            currentBgmFile = ""; 
        }
    }

    // Helper Konversi Persen ke Decibel
    private void updateClipVolume(Clip clip, int percent) {
        if (clip == null) return;
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                
                // Rumus Konversi Logaritmik (Agar smooth)
                float db;
                if (percent <= 0) db = -80.0f; // Mute total
                else db = (float) (20.0 * Math.log10(percent / 100.0));
                
                // Clamp nilai agar aman
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                gainControl.setValue(Math.max(min, Math.min(db, max)));
            }
        } catch (Exception e) {}
    }
}