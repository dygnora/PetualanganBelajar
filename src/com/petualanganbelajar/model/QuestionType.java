package com.petualanganbelajar.model;

/**
 * Daftar tipe soal yang didukung.
 * Menggunakan Enum mencegah Typo string manual.
 */
public enum QuestionType {
    CHOICE,     // Pilihan Ganda (Default)
    COUNTING,   // Menghitung Grid Gambar
    MATH,       // Matematika (Penjumlahan visual)
    SEQUENCE,   // Urutan Angka
    TYPING,     // Mengetik Kata
    KEYPAD,     // Melengkapi Huruf
    CLICK       // Klik Objek (Visual)
}