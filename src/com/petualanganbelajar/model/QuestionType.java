package com.petualanganbelajar.model;

/**
 * Daftar tipe soal yang didukung.
 * Menggunakan Enum mencegah Typo string manual.
 */

public enum QuestionType {
    CHOICE,         // Pilihan Ganda Standard
    SEQUENCE,       // Urutan Angka/Huruf (1 Kotak Kosong)
    CLICK,          // Point & Click
    TYPING,         // Input Text/Angka
    SEQUENCE_MULTI, // Urutan Huruf (Banyak Kotak Kosong)
    COMPARISON      // Membandingkan 2 Gambar
}