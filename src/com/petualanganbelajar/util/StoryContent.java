package com.petualanganbelajar.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Menyimpan naskah cerita "Festival Hutan Ceria".
 * Mengembalikan daftar dialog (List<DialogScene>) berdasarkan modul & level.
 */
public class StoryContent {

    public static List<DialogScene> getScenes(String moduleName, int level) {
        List<DialogScene> scenes = new ArrayList<>();

        // --- PROLOG (AWAL GAME) ---
        if (moduleName.equalsIgnoreCase("PROLOGUE")) {
            scenes.add(new DialogScene("Prof. Otto", 
                "Hoo.. Hoo.. Selamat datang, Sahabat Pintar!\nBesok adalah 'Festival Hutan Ceria'.", 
                "otto_sapa.png", false));
            scenes.add(new DialogScene("Prof. Otto", 
                "Tapi lihat! Teman-teman hewan kewalahan menyiapkan pestanya.\nMaukah kamu membantu mereka?", 
                "otto_bingung.png", false));
            return scenes;
        }

        // --- EPILOG (TAMAT) ---
        if (moduleName.equalsIgnoreCase("EPILOGUE")) {
            scenes.add(new DialogScene("Prof. Otto", 
                "Luar Biasa! Dekorasi indah, makanan lezat, panggung megah.\nFestival Hutan Ceria sukses besar!", 
                "otto_senang.png", false));
            scenes.add(new DialogScene("Semua Hewan", 
                "Terima kasih Sahabat Pintar!\nKamu adalah Pahlawan Pesta kami!", 
                "party_all.png", false));
            return scenes;
        }

        // --- MODUL 1: ANGKA (BOBO SI BERUANG) ---
        if (moduleName.equalsIgnoreCase("Angka")) {
            if (level == 1) {
                // Scene 1: Masalah Bobo
                scenes.add(new DialogScene("Bobo si Beruang", 
                    "Hai sobat! Aku mau bikin salad buah buat pesta.\nTapi aku bingung, ada berapa apel ini ya?", 
                    "bobo_bingung.png", false));
                // Scene 2: Solusi Otto (Tutorial)
                scenes.add(new DialogScene("Prof. Otto", 
                    "Bobo butuh bantuanmu. Hitung jumlah apel di gambar,\nlalu tekan tombol angka yang benar.", 
                    "otto_tunjuk.png", true));
            } else if (level == 2) {
                scenes.add(new DialogScene("Bobo si Beruang", 
                    "Wah tamu makin banyak! Aku butuh wadah madu yang lebih BESAR.\nYang mana ya?", 
                    "bobo_panik.png", false));
                scenes.add(new DialogScene("Prof. Otto", 
                    "Urutkan! Cari angka yang nilainya LEBIH BESAR dari angka sebelumnya.", 
                    "otto_tunjuk.png", true));
            } else if (level == 3) {
                scenes.add(new DialogScene("Bobo si Beruang", 
                    "Di ember ada 2 ikan, di piring ada 2 ikan.\nKalau digabung jadi berapa ya? Perutku sudah lapar...", 
                    "bobo_lapar.png", false));
                scenes.add(new DialogScene("Prof. Otto", 
                    "Penjumlahan itu menggabungkan! Klik ikan di ember untuk memindahkannya ke piring.", 
                    "otto_tunjuk.png", true));
            }
        }

        // --- MODUL 2: HURUF (CICI SI TUPAI) ---
        if (moduleName.equalsIgnoreCase("Huruf")) {
            if (level == 1) {
                scenes.add(new DialogScene("Cici si Tupai", 
                    "Lari.. Lari..! Aku harus kirim undangan ini.\nTapi ini surat 'A' harus masuk ke kotak mana?", 
                    "cici_lari.png", false));
                scenes.add(new DialogScene("Prof. Otto", 
                    "Lihat bentuknya! Klik surat 'A', lalu klik Kotak Pos yang hurufnya SAMA.", 
                    "otto_tunjuk.png", true));
            } else if (level == 2) {
                scenes.add(new DialogScene("Cici si Tupai", 
                    "Ini undangan untuk RUSA. Rusa.. Rrr.. Rrr..\nHuruf depannya apa sih?", 
                    "cici_mikir.png", false));
                scenes.add(new DialogScene("Prof. Otto", 
                    "Dengarkan bunyinya. Rrr-Rusa dimulai dengan huruf R.\nCari tombol huruf R!", 
                    "otto_tunjuk.png", true));
            } else if (level == 3) {
                scenes.add(new DialogScene("Cici si Tupai", 
                    "Gawat! Tinta nama tamu luntur kena hujan.\nHarusnya tertulis 'KADO'. Huruf apa yang hilang?", 
                    "cici_sedih.png", false));
                scenes.add(new DialogScene("Prof. Otto", 
                    "Gunakan KEYBOARD laptopmu. Ketik huruf yang hilang, lalu tekan Enter.", 
                    "otto_tunjuk.png", true));
            }
        }

        // --- MODUL 3: WARNA (MOLI SI BUNGLON) ---
        if (moduleName.equalsIgnoreCase("Warna")) {
            if (level == 1) {
                scenes.add(new DialogScene("Moli si Bunglon", 
                    "Yuk hias panggung! Aku mau balon MERAH biar meriah.\nYang mana balon merah?", 
                    "moli_happy.png", false));
                scenes.add(new DialogScene("Prof. Otto", 
                    "Belajar Warna! Pilih gambar Balon yang warnanya Merah.", 
                    "otto_tunjuk.png", true));
            }
            // ... Tambahkan level 2 & 3 sesuai pola ...
        }

        // --- MODUL 4: BENTUK (TOBI SI KURA-KURA) ---
        if (moduleName.equalsIgnoreCase("Bentuk")) {
             if (level == 1) {
                scenes.add(new DialogScene("Tobi si Kura-kura", 
                    "Aku butuh hiasan yang bulat seperti Roda.\nBenda apa yang bentuknya bulat ya?", 
                    "tobi_mikir.png", false));
                scenes.add(new DialogScene("Prof. Otto", 
                    "Itu namanya Lingkaran. Cari benda yang bentuknya Lingkaran!", 
                    "otto_tunjuk.png", true));
            }
            // ... Tambahkan level 2 & 3 sesuai pola ...
        }

        // Fallback jika data belum ada
        if (scenes.isEmpty()) {
            scenes.add(new DialogScene("Prof. Otto", "Ayo langsung kita mulai belajar!", "otto_sapa.png", false));
        }

        return scenes;
    }
}