package com.petualanganbelajar.util;

public class StoryContent {

    public static String getStory(String moduleName, int level) {
        // Contoh sederhana logika cerita berdasarkan Modul & Level
        // Nanti Anda bisa ubah teksnya sesuka hati
        
        if (moduleName.equalsIgnoreCase("Angka")) {
            if (level == 1) return "Halo! Aku Kancil.\nHari ini Pak Tani panen apel.\nAyo bantu aku menghitung apel di kebun!";
            if (level == 2) return "Wah, panen semakin banyak!\nSekarang kita hitung buah yang lebih besar ya.\nKamu pasti bisa!";
            if (level == 3) return "Hebat! Sekarang tantangan terakhir.\nBantu aku menghitung semua stok gudang ya!";
        }
        
        if (moduleName.equalsIgnoreCase("Huruf")) {
            if (level == 1) return "Lihat! Ada buku ajaib terbuka.\nHuruf-huruf berterbangan keluar.\nAyo tangkap huruf yang tepat!";
            if (level == 2) return "Sekarang kita masuk ke hutan kata.\nCari huruf depan dari nama hewan ya!";
        }
        
        if (moduleName.equalsIgnoreCase("Warna")) {
            return "Dunia menjadi hitam putih!\nAyo kita warnai lagi dunia ini dengan memilih warna yang benar.";
        }

        // Default jika modul lain
        return "Petualangan baru dimulai!\nAyo selesaikan tantangan di level ini.";
    }

    public static String getTutorial(String moduleName, int level) {
        if (moduleName.equalsIgnoreCase("Angka")) {
            return "CARA MAIN: Hitung gambar yang muncul, lalu klik angka jawabannya.";
        }
        if (moduleName.equalsIgnoreCase("Huruf")) {
            return "CARA MAIN: Lihat soalnya, lalu pilih huruf yang sesuai.";
        }
        return "CARA MAIN: Klik jawaban yang menurutmu benar!";
    }
}