package com.petualanganbelajar.util;

import java.awt.*;

/**
 * STYLE CONSTANTS
 * Pusat konfigurasi visual agar tampilan game konsisten.
 * Ubah font atau warna di sini, dan seluruh game akan berubah otomatis.
 */
public class StyleConstants {
    // --- FONTS ---
    // Font Comic Sans dipilih karena ramah anak (alternatif: Arial Rounded MT)
    public static final Font FONT_TITLE  = new Font("Comic Sans MS", Font.BOLD, 48);
    public static final Font FONT_BUTTON = new Font("Comic Sans MS", Font.BOLD, 22);
    public static final Font FONT_BODY   = new Font("Comic Sans MS", Font.PLAIN, 18);

    // --- COLOR PALETTE ---
    public static final Color COL_PRIMARY = new Color(255, 193, 7);  // Kuning Emas (Warna Utama)
    public static final Color COL_ACCENT  = new Color(33, 150, 243); // Biru Cerah
    public static final Color COL_DANGER  = new Color(244, 67, 54);  // Merah (Tombol Keluar/Salah)
    public static final Color COL_SUCCESS = new Color(76, 175, 80);  // Hijau (Tombol Lanjut/Benar)
    
    // --- TECHNICAL COLORS ---
    public static final Color COL_SHADOW  = new Color(0, 0, 0, 40);  // Transparan Hitam untuk bayangan

    // --- ANIMATION & LAYOUT ---
    // Seberapa dalam tombol "turun" saat ditekan (dalam pixel)
    public static final int ANIM_PRESS_OFFSET = 3; 
    public static final int BTN_ARC_SIZE = 25; // Kelengkungan sudut tombol
}