package com.petualanganbelajar.content;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class LetterContent {

    public static void generate(PreparedStatement pstmt) throws SQLException {
        genL1(pstmt); genL2(pstmt); genL3(pstmt);
    }

    // --- LEVEL 1: HURUF AWAL (DIPERBAIKI NARASINYA) ---
    private static void genL1(PreparedStatement pstmt) throws SQLException {
        Map<String, String> guests = new HashMap<>();
        guests.put("guest_ayam.png", "A"); guests.put("guest_bebek.png", "B");
        guests.put("guest_capung.png", "C"); guests.put("guest_domba.png", "D");
        guests.put("guest_elang.png", "E"); guests.put("guest_gajah.png", "G");
        guests.put("guest_ikan.png", "I"); guests.put("guest_jerapah.png", "J");
        guests.put("guest_katak.png", "K"); guests.put("guest_kuda.png", "K");
        guests.put("guest_landak.png", "L"); guests.put("guest_monyet.png", "M");
        guests.put("guest_rusa.png", "R"); guests.put("guest_sapi.png", "S");
        guests.put("guest_ular.png", "U"); guests.put("guest_zebra.png", "Z");

        // [PERBAIKAN NARASI]
        // Menggunakan kalimat yang lebih personal, ceria, dan mudah dipahami anak.
        String[] templates = {
            "Hai! Aku <b>%s</b>. Apa huruf depanku?",       // Seolah hewan menyapa
            "Coba tebak, <b>%s</b> dimulai dengan huruf apa?", // Mengajak menebak
            "Ayo bantu Cici! Huruf awal <b>%s</b> adalah...",  // Mengingatkan pada karakter Cici
            "Huruf depan apa yang tepat untuk menulis <b>%s</b>?"      // Edukatif
        };
        
        List<String> keys = new ArrayList<>(guests.keySet()); 
        Collections.shuffle(keys);
        
        for (int i = 0; i < 20; i++) { 
            String img = keys.get(i % keys.size()); 
            String ans = guests.get(img); 
            String[] w = GeneratorUtils.getWrongChars(ans);
            
            // Bersihkan nama file untuk ditampilkan di teks (misal: "guest_ayam.png" -> "AYAM")
            String animalName = img.replace("guest_", "").replace(".png", "").toUpperCase();
            
            // Masukkan nama hewan ke dalam template (%s)
            String text = String.format(templates[GeneratorUtils.rand.nextInt(templates.length)], animalName);
            
            GeneratorUtils.addQ(pstmt, 2, 1, "CHOICE", text, img, null, ans, w[0], w[1], ans);
        }
    }

    private static void genL2(PreparedStatement pstmt) throws SQLException {
        String alpha = GeneratorUtils.ALPHABET;
        String[] templates = {"Lengkapi urutan huruf ini!", "Huruf apa yang hilang?", "Ayo isi kotak kosong!", "Lanjutkan abjadnya!"};
        for (int i = 0; i < 15; i++) {
            int len = 5; int start = GeneratorUtils.rand.nextInt(26-len); char[] seq = new char[len];
            for(int j=0; j<len; j++) seq[j] = alpha.charAt(start+j);
            int miss = GeneratorUtils.rand.nextInt(2)+1; List<Integer> mIdx = new ArrayList<>();
            while(mIdx.size()<miss) { int r=GeneratorUtils.rand.nextInt(len); if(!mIdx.contains(r)) mIdx.add(r); }
            Collections.sort(mIdx); StringBuilder pat=new StringBuilder(); StringBuilder ans=new StringBuilder();
            for(int j=0; j<len; j++) {
                if(mIdx.contains(j)) { pat.append("_"); ans.append(seq[j]); if(mIdx.indexOf(j)<miss-1) ans.append(","); }
                else pat.append(seq[j]); if(j<len-1) pat.append(" ");
            }
            String text = templates[GeneratorUtils.rand.nextInt(templates.length)] + " ## " + pat.toString() + " ## " + ans.toString();
            GeneratorUtils.addQ(pstmt, 2, 2, "SEQUENCE_MULTI", text, null, null, null, null, null, ans.toString());
        }
    }

    private static void genL3(PreparedStatement pstmt) throws SQLException {
        Map<String, String> items = new HashMap<>(); 
        items.put("guest_ayam.png", "AYAM"); 
        items.put("guest_bebek.png", "BEBEK");
        items.put("guest_sapi.png", "SAPI"); 
        items.put("guest_kuda.png", "KUDA");
        items.put("guest_rusa.png", "RUSA"); 
        items.put("guest_ikan.png", "IKAN");
        items.put("guest_zebra.png", "ZEBRA");
        items.put("guest_gajah.png", "GAJAH");
        
        String[] templates = {
            "Lengkapi kata ini!", "Huruf apa yang kurang?", 
            "Ayo eja nama hewan ini!", "Isi huruf yang kosong!"
        };
        
        // --- LOGIKA PENGACAKAN BARU (SHUFFLE DECK) ---
        List<String> keys = new ArrayList<>(items.keySet());
        
        // Buat Deck yang cukup besar (misal: ulang daftar hewan 3-4 kali agar cukup untuk 20 soal)
        List<String> deck = new ArrayList<>();
        int repeatCount = (int) Math.ceil(20.0 / keys.size()); // Hitung butuh berapa putaran
        for(int r=0; r<repeatCount; r++) {
            deck.addAll(keys);
        }
        
        // Kocok deck agar urutannya acak tapi merata
        Collections.shuffle(deck);
        
        for(int i=0; i<20; i++) {
            // Ambil gambar dari deck yang sudah dikocok
            String img = deck.get(i); 
            String w = items.get(img);
            
            // Sembunyikan huruf secara acak
            int hide = GeneratorUtils.rand.nextInt(w.length()); 
            String ans = String.valueOf(w.charAt(hide));
            
            StringBuilder puz = new StringBuilder(); 
            for(int j=0; j<w.length(); j++) {
                puz.append(j==hide ? "_" : w.charAt(j)).append(" ");
            }
            
            String text = templates[GeneratorUtils.rand.nextInt(templates.length)] + " ## " + img + " ## " + puz.toString();
            GeneratorUtils.addQ(pstmt, 2, 3, "TYPING", text, null, null, null, null, null, ans);
        }
    }
}