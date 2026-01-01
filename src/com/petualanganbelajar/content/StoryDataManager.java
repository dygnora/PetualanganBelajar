package com.petualanganbelajar.content;

import com.petualanganbelajar.util.DialogScene;
import java.util.ArrayList;
import java.util.List;

public class StoryDataManager {

    public static List<DialogScene> getIntroStory(int moduleId, int level) {
        List<DialogScene> story = new ArrayList<>();

        switch (moduleId) {
            // ... (MODUL 1 & 2 TETAP SAMA SEPERTI SEBELUMNYA) ...
            case 1: // Angka
                if (level == 1) {
                    story.add(new DialogScene("Bobo", "Aduh, aku mau buat kue apel. Tapi aku bingung menghitung jumlah apelnya.", "bobo_confused.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", "Tenang Bobo! Sahabat Pintar di sini pasti bisa membantumu menghitung.", "otto_normal.png", false, "LEFT"));
                    story.add(new DialogScene("Prof. Otto", "Sahabat, lihat jumlah gambarnya, lalu klik tombol Angka yang sesuai ya!", "otto_teach.png", true, "LEFT"));
                } else if (level == 2) {
                    story.add(new DialogScene("Bobo", "Bahan makananku tercampur! Aku harus mengurutkannya dari yang kecil ke besar.", "bobo_confused.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", "Ayo kita rapikan urutan angkanya agar Bobo tidak pusing.", "otto_normal.png", false, "LEFT"));
                    story.add(new DialogScene("Prof. Otto", "Perhatikan deretan angkanya. Isi kotak yang kosong dengan angka yang benar!", "otto_teach.png", true, "LEFT"));
                } else if (level == 3) {
                    story.add(new DialogScene("Bobo", "Saatnya memasak! Aku perlu menambah dan mengurangi bahan agar rasanya pas.", "bobo_happy.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", "Benar! Kadang kita harus menjumlahkan, kadang harus mengurangi.", "otto_normal.png", false, "LEFT"));
                    story.add(new DialogScene("Prof. Otto", "Sahabat, perhatikan tandanya ya! Jika (+) berarti ditambah, jika (-) berarti dikurang.", "otto_teach.png", true, "LEFT"));
                }
                break;

            case 2: // Huruf
                if (level == 1) {
                    story.add(new DialogScene("Cici", "Gawat! Kartu undangan pestanya berantakan. Aku lupa nama hewan-hewan ini.", "cici_worried.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", "Jangan panik Cici. Sahabat Pintar sangat jago mengenal huruf depan.", "otto_normal.png", false, "LEFT"));
                    story.add(new DialogScene("Prof. Otto", "Lihat gambar hewannya, lalu pilih Huruf Pertama dari nama hewan tersebut!", "otto_teach.png", true, "LEFT"));
                } else if (level == 2) {
                    story.add(new DialogScene("Cici", "Angin kencang mengacak-acak kartu abjadku! Mana huruf yang hilang?", "cici_worried.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", "Ayo kita susun kembali urutan hurufnya.", "otto_normal.png", false, "LEFT"));
                    story.add(new DialogScene("Prof. Otto", "Perhatikan urutan hurufnya (A, B, C...). Klik huruf yang hilang dari barisan!", "otto_teach.png", true, "LEFT"));
                } else if (level == 3) {
                    story.add(new DialogScene("Cici", "Tinta di undangannya luntur! Ada huruf yang hilang dari nama tamu.", "cici_worried.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", "Kita harus menulis ulang huruf yang hilang itu.", "otto_normal.png", false, "LEFT"));
                    story.add(new DialogScene("Prof. Otto", "Sahabat, ketik huruf yang hilang untuk melengkapi nama hewannya ya!", "otto_teach.png", true, "LEFT"));
                }
                break;

            // ============================================================
            // MODUL 3: WARNA (MOLI) - [REVISI: Aset Hewan/Buah]
            // ============================================================
            case 3:
                if (level == 1) { // L1: Tebak Warna
                    story.add(new DialogScene("Moli", 
                        "Aku ingin menghias panggung. Tapi aku bingung membedakan warna balon dan hewan!", 
                        "moli_sad.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", 
                        "Wah, Moli butuh bantuan. Sahabat Pintar, ayo bantu Moli!", 
                        "otto_normal.png", false, "LEFT"));
                    // [REVISI] Menggunakan kata "gambar" agar netral (bisa hewan/buah)
                    story.add(new DialogScene("Prof. Otto", 
                        "Dengarkan warna yang diminta, lalu klik GAMBAR yang memiliki warna sama.", 
                        "otto_teach.png", true, "LEFT"));
                } 
                else if (level == 2) { // L2: Pilih Satu (Single Choice)
                    story.add(new DialogScene("Moli", 
                        // [REVISI] Moli mencari teman hewan atau buah, bukan barang
                        "Teman-teman hewan dan buah-buahanku tercampur! Aku harus menemukan yang warnanya pas.", 
                        "moli_sad.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", 
                        "Ayo kita bantu Moli menemukan apa yang dia cari.", 
                        "otto_normal.png", false, "LEFT"));
                    story.add(new DialogScene("Prof. Otto", 
                        // [REVISI] Instruksi jelas memilih gambar
                        "Perhatikan pertanyaan Moli, lalu klik SATU gambar hewan atau buah yang warnanya sesuai ya!", 
                        "otto_teach.png", true, "LEFT"));
                } 
                else if (level == 3) { // L3: Campur Warna (Tetap Cat)
                    story.add(new DialogScene("Moli", 
                        "Aku kehabisan cat warna Oranye! Aku cuma punya Merah dan Kuning.", 
                        "moli_sad.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", 
                        "Itu namanya eksperimen sains! Kita bisa mencampur warna.", 
                        "otto_normal.png", false, "LEFT"));
                    story.add(new DialogScene("Prof. Otto", 
                        "Jika dua warna dicampur, akan jadi warna apa ya? Pilih hasil campurannya!", 
                        "otto_teach.png", true, "LEFT"));
                }
                break;

            // ... (MODUL 4 & OUTRO/EPILOG TETAP SAMA) ...
            case 4: // Bentuk
                if (level == 1) {
                    story.add(new DialogScene("Tobi", "Dinding panggung berlubang! Aku butuh balok kayu untuk menambalnya.", "tobi_confused.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", "Kita harus mencari bentuk yang pas agar lubangnya tertutup rapat.", "otto_normal.png", false, "LEFT"));
                    story.add(new DialogScene("Prof. Otto", "Lihat bentuk lubangnya, lalu cari potongan kayu yang bentuknya SAMA!", "otto_teach.png", true, "LEFT"));
                } else if (level == 2) {
                    story.add(new DialogScene("Tobi", "Pak Tukang bertanya nama bentuk ini, tapi aku lupa namanya.", "tobi_confused.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", "Sahabat Pintar pasti tahu nama-nama bangun datar.", "otto_normal.png", false, "LEFT"));
                    story.add(new DialogScene("Prof. Otto", "Ketik nama bentuk yang muncul di layar. Misal: PERSEGI.", "otto_teach.png", true, "LEFT"));
                } else if (level == 3) {
                    story.add(new DialogScene("Tobi", "Aku sedang menyusun lantai dansa, tapi aku bingung urutan motifnya.", "tobi_confused.png", false, "RIGHT"));
                    story.add(new DialogScene("Prof. Otto", "Lantai dansa harus punya pola yang teratur biar indah.", "otto_normal.png", false, "LEFT"));
                    story.add(new DialogScene("Prof. Otto", "Lihat pola urutan gambarnya. Bentuk apa yang harusnya muncul selanjutnya?", "otto_teach.png", true, "LEFT"));
                }
                break;
        }
        
        return story;
    }

    public static List<DialogScene> getOutroStory(int moduleId, int level) {
        List<DialogScene> story = new ArrayList<>();
        if (level != 3) return story;
        
        switch (moduleId) {
            case 1:
                story.add(new DialogScene("Bobo", "Hore! Kue apelnya sudah jadi dan rasanya enak sekali!", "bobo_happy.png", false, "RIGHT"));
                story.add(new DialogScene("Prof. Otto", "Selamat Bobo! Dan terima kasih Sahabat, kamu sudah menamatkan pelajaran Angka!", "otto_thumbsup.png", false, "LEFT"));
                break;
            case 2:
                story.add(new DialogScene("Cici", "Semua undangan sudah terkirim dengan nama yang benar. Pestanya akan ramai!", "cici_happy.png", false, "RIGHT"));
                story.add(new DialogScene("Prof. Otto", "Kerja bagus! Kemampuan membacamu semakin hebat, Sahabat!", "otto_thumbsup.png", false, "LEFT"));
                break;
            case 3:
                story.add(new DialogScene("Moli", "Lihat! Panggungnya warna-warni dan indah sekali. Terima kasih!", "moli_happy.png", false, "RIGHT"));
                story.add(new DialogScene("Prof. Otto", "Luar biasa! Kamu sudah menguasai semua jenis warna.", "otto_thumbsup.png", false, "LEFT"));
                break;
            case 4:
                story.add(new DialogScene("Tobi", "Panggungnya kokoh dan lantainya indah. Pesta hutan siap dimulai!", "tobi_happy.png", false, "RIGHT"));
                story.add(new DialogScene("Prof. Otto", "Sempurna! Kamu adalah ahli bangunan dan bentuk yang cerdas!", "otto_thumbsup.png", false, "LEFT"));
                break;
        }
        return story;
    }
    
    public static List<DialogScene> getPrologueStory() {
        List<DialogScene> story = new ArrayList<>();
        story.add(new DialogScene("Prof. Otto", "Halo! Selamat datang di Hutan Pintar. Aku Profesor Otto.", "otto_normal.png", false, "LEFT"));
        story.add(new DialogScene("Prof. Otto", "Di sini, banyak teman-teman hewan yang butuh bantuanmu.", "otto_normal.png", false, "LEFT"));
        story.add(new DialogScene("Prof. Otto", "Ayo kita bertualang sambil belajar Angka, Huruf, Warna, dan Bentuk!", "otto_teach.png", true, "LEFT"));
        return story;
    }

    // --- EPILOG (ENDING GAME - Muncul setelah SEMUA level tamat) ---
    public static List<DialogScene> getEpilogueStory() {
        List<DialogScene> story = new ArrayList<>();
        
        // Pembuka oleh Prof Otto
        story.add(new DialogScene("Prof. Otto", 
            "Wah, lihatlah! Pesta Hutan Pintar akhirnya dimulai dengan sangat meriah!", 
            "otto_thumbsup.png", false, "LEFT"));

        // Bobo: Masalah Masak & Hitungan (Modul 1)
        story.add(new DialogScene("Bobo", 
            "Kue Apel buatanku rasanya pas sekali! Terima kasih sudah membantuku menghitung jumlah bahannya dengan tepat.", 
            "bobo_happy.png", false, "RIGHT"));
            
        // Cici: Masalah Undangan & Huruf (Modul 2)
        story.add(new DialogScene("Cici", 
            "Lihat, semua teman hewan berhasil datang! Itu karena kita sudah memperbaiki huruf nama mereka di kartu undangan.", 
            "cici_happy.png", false, "RIGHT"));
            
        // Moli: Masalah Dekorasi & Campur Warna (Modul 3)
        story.add(new DialogScene("Moli", 
            "Panggungnya jadi indah! Campuran warna cat yang kamu buat tadi membuat dekorasinya terlihat ajaib.", 
            "moli_happy.png", false, "RIGHT"));
            
        // Tobi: Masalah Konstruksi & Bentuk (Modul 4)
        story.add(new DialogScene("Tobi", 
            "Dan lantai dansanya sangat aman! Polanya rapi dan lubang dindingnya sudah tertutup bentuk yang pas.", 
            "tobi_happy.png", false, "RIGHT"));
            
        // Penutup (Graduation)
        story.add(new DialogScene("Prof. Otto", 
            "Semua masalah teratasi berkat kecerdasanmu. Kamu resmi menjadi Pahlawan Hutan Pintar, Yeayy.. Selamat!", 
            "otto_thumbsup.png", false, "LEFT"));
            
        return story;
    }
}