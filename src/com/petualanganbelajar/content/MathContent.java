package com.petualanganbelajar.content;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MathContent {

    public static void generate(PreparedStatement pstmt) throws SQLException {
        genL1(pstmt);
        genL2(pstmt);
        genL3(pstmt);
    }

    private static String getFileForMathItem(String itemName) {
        switch(itemName) {
            case "Apel":   return "item_red_apple.png";
            case "Pisang": return "item_yellow_banana.png";
            case "Jeruk":  return "item_orange_fruit.png";
            case "Donat":  return "item_pink_donut.png";
            case "Kue":    return "party_kue.png";
            case "Roti":   return "party_roti.png";
            case "Susu":   return "party_susu.png";
            default:       return "item_red_apple.png";
        }
    }

    // --- LEVEL 1: BERHITUNG (SUDAH BAGUS) ---
    private static void genL1(PreparedStatement pstmt) throws SQLException {
        String[] foods = {"Apel", "Pisang", "Jeruk", "Donat", "Kue", "Roti", "Susu"};
        String[] templates = {
            "Perut Bobo lapar! Ada berapa <b>%s</b>?", "Bantu Bobo menghitung <b>%s</b>!", 
            "Coba hitung, jumlah <b>%s</b> ada berapa?", "Total <b>%s</b> di meja makan adalah..."
        };
        
        List<String> deck = new ArrayList<>();
        // Buat kombinasi unik makanan & angka (7 makanan x 10 angka = 70 variasi)
        for (String f : foods) for (int n = 1; n <= 10; n++) deck.add(f + ":" + n);
        Collections.shuffle(deck);
        
        for (int i = 0; i < 20; i++) {
            String[] parts = deck.get(i % deck.size()).split(":");
            String item = parts[0]; int ans = Integer.parseInt(parts[1]);
            String text = String.format(templates[GeneratorUtils.rand.nextInt(templates.length)], item);
            String imgFile = getFileForMathItem(item);
            int[] wrongs = GeneratorUtils.getWrongInts(ans, 1, 10);
            GeneratorUtils.addQ(pstmt, 1, 1, "CHOICE", text, imgFile, null, ""+ans, ""+wrongs[0], ""+wrongs[1], ""+ans);
        }
    }

    // --- LEVEL 2: URUTAN & PERBANDINGAN (DIPERBAIKI) ---
    private static void genL2(PreparedStatement pstmt) throws SQLException {
        String[] textVariations = {"Lengkapi urutan angka ini!", "Angka berapa yang hilang?", "Ayo isi kotak yang kosong!", "Setelah itu angka berapa ya?"};
        
        // 1. SEQUENCE (Deck Angka Awal)
        // Kita acak angka awal (1, 2, 3, 4, 5) dan posisi hilang (0, 1, 2, 3)
        // Total kombinasi = 5 x 4 = 20 unik. Kita butuh 15 soal.
        List<String> seqDeck = new ArrayList<>();
        for(int s=1; s<=5; s++) { // Start number 1-5
            for(int m=0; m<4; m++) { // Missing pos 0-3
                seqDeck.add(s + ":" + m);
            }
        }
        Collections.shuffle(seqDeck);

        for (int i = 0; i < 15; i++) {
            String[] parts = seqDeck.get(i).split(":");
            int start = Integer.parseInt(parts[0]);
            int missingPos = Integer.parseInt(parts[1]);
            
            int[] nums = {start, start+1, start+2, start+3};
            StringBuilder sb = new StringBuilder();
            for(int j=0; j<4; j++) sb.append(j==missingPos ? "?" : nums[j]).append(j<3?",":"");
            
            String text = textVariations[GeneratorUtils.rand.nextInt(textVariations.length)] + " ## " + sb.toString();
            GeneratorUtils.addQ(pstmt, 1, 2, "SEQUENCE", text, null, null, ""+nums[missingPos], ""+(nums[missingPos]+2), ""+(nums[missingPos]-1), ""+nums[missingPos]);
        }
        
        // 2. COMPARISON (Deck Pasangan Benda)
        // Kita hindari benda yang sama muncul terus
        String[] itemNames = {"Apel", "Pisang", "Jeruk", "Donat", "Kue", "Roti", "Susu"};
        List<String> compDeck = new ArrayList<>();
        // Buat pasangan unik (misal: Apel vs Pisang, Jeruk vs Donat)
        for(int i=0; i<itemNames.length; i++) {
            for(int j=i+1; j<itemNames.length; j++) {
                compDeck.add(itemNames[i] + ":" + itemNames[j]);
            }
        }
        Collections.shuffle(compDeck);
        
        for (int i = 0; i < 8; i++) {
             String[] items = compDeck.get(i % compDeck.size()).split(":");
             String name1 = items[0]; String name2 = items[1];
             
             // Acak posisi kiri/kanan setiap kali
             if(GeneratorUtils.rand.nextBoolean()) { String t=name1; name1=name2; name2=t; }
             
             String file1 = getFileForMathItem(name1); String file2 = getFileForMathItem(name2);
             int c1 = GeneratorUtils.rand.nextInt(5) + 2; int c2 = GeneratorUtils.rand.nextInt(5) + 2; if (c1 == c2) c2++;
             boolean askMore = GeneratorUtils.rand.nextBoolean();
             
             String qText = askMore ? "Mana yang lebih <b>BANYAK</b>?" : "Mana yang lebih <b>SEDIKIT</b>?";
             String ans = askMore ? (c1 > c2 ? name1 : name2) : (c1 < c2 ? name1 : name2);
             GeneratorUtils.addQ(pstmt, 1, 2, "COMPARISON", qText + " ## " + file1 + ":" + c1 + " ## " + file2 + ":" + c2, null, null, name1, name2, null, ans);
        }
    }

    // --- LEVEL 3: PENJUMLAHAN (DIPERBAIKI) ---
    private static void genL3(PreparedStatement pstmt) throws SQLException {
        String[] itemNames = {"Apel", "Pisang", "Jeruk", "Donat", "Kue"};
        String[] templates = {"Berapa hasil penjumlahannya?", "Ayo hitung semuanya!", "Jumlahkan makanan di bawah ini!", "Ada berapa totalnya?"};
        
        // Buat Deck Soal Penjumlahan Unik (Agar tidak ada 2+3 dua kali)
        // Range A (1-4) dan B (1-4) -> 16 Kombinasi Unik. Kita butuh 15 soal.
        List<String> mathDeck = new ArrayList<>();
        for(int a=1; a<=4; a++) {
            for(int b=1; b<=4; b++) {
                mathDeck.add(a + ":" + b);
            }
        }
        Collections.shuffle(mathDeck);
        
        for (int i = 0; i < 15; i++) {
            String[] nums = mathDeck.get(i).split(":");
            int a = Integer.parseInt(nums[0]);
            int b = Integer.parseInt(nums[1]);
            
            // Ambil benda acak (ini boleh acak karena angkanya sudah unik)
            String name = itemNames[GeneratorUtils.rand.nextInt(itemNames.length)];
            String file = getFileForMathItem(name);
            String text = templates[GeneratorUtils.rand.nextInt(templates.length)];
            
            GeneratorUtils.addQ(pstmt, 1, 3, "TYPING", text + " ## " + file + ":" + a + " ## + ## " + file + ":" + b, null, null, null, null, null, String.valueOf(a+b));
        }
    }
}