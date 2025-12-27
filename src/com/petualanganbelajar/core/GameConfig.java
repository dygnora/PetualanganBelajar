/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.core;
import java.awt.Color;
import java.awt.Font;
/**
 *
 * @author DD
 */
public class GameConfig {
    // 1. Konfigurasi Layar (Fixed Size)
    public static final String GAME_TITLE = "Petualangan Belajar - PAUD/TK";
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;
    
    // 2. Palet Warna (Ceria & Kontras Tinggi untuk Anak)
    public static final Color COLOR_PRIMARY = new Color(255, 193, 7);   // Kuning Emas
    public static final Color COLOR_ACCENT = new Color(33, 150, 243);   // Biru Cerah
    public static final Color COLOR_BG = new Color(240, 248, 255);      // Putih Kebiruan (AliceBlue)
    
    // 3. Font Standard (Besar & Jelas)
    public static final Font FONT_TITLE = new Font("Comic Sans MS", Font.BOLD, 48);
    public static final Font FONT_SUBTITLE = new Font("Arial", Font.BOLD, 24);
    public static final Font FONT_BODY = new Font("Arial", Font.PLAIN, 18);
}
