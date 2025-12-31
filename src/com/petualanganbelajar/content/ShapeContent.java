package com.petualanganbelajar.content;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShapeContent {

    public static void generate(PreparedStatement pstmt) throws SQLException {
        genL1(pstmt);
        genL2(pstmt);
        genL3(pstmt);
    }

    // --- LEVEL 1: TEBAK BENTUK (PILIHAN GANDA) ---
    private static void genL1(PreparedStatement pstmt) throws SQLException {
        String[] shapes = {"RECT", "RECTANGLE", "HEXAGON", "OCTAGON", "HEART", "STAR", "TRIANGLE", "CIRCLE", "PENTAGON", "RHOMBUS", "OVAL", "SEMICIRCLE"};
        String[] names = {"PERSEGI", "PERSEGI PANJANG", "SEGI ENAM", "SEGI DELAPAN", "HATI", "BINTANG", "SEGITIGA", "LINGKARAN", "SEGI LIMA", "BELAH KETUPAT", "OVAL", "SETENGAH LINGKARAN"};
        String[] colors = {"RED", "BLUE", "GREEN", "ORANGE", "PURPLE"};

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < shapes.length; i++) indices.add(i);
        for (int i = 0; i < 8; i++) indices.add(GeneratorUtils.rand.nextInt(shapes.length));
        Collections.shuffle(indices);

        for (int idx : indices) {
            String targetShape = shapes[idx];
            String targetName = names[idx];
            String color = colors[GeneratorUtils.rand.nextInt(colors.length)];
            
            String[] tpls = {
                "Aku mencari bentuk <b>%s</b>. Bisakah kamu menemukannya?", 
                "Mana gambar <b>%s</b>?", 
                "Tunjukkan padaku bentuk <b>%s</b>!", 
                "Yang manakah <b>%s</b>?"
            };
            String narrative = String.format(tpls[GeneratorUtils.rand.nextInt(tpls.length)], targetName);
            String ans = "SHAPE:" + targetShape + ":" + color;
            
            // Distractor Acak
            int distIdx1; do { distIdx1 = GeneratorUtils.rand.nextInt(shapes.length); } while (distIdx1 == idx);
            int distIdx2; do { distIdx2 = GeneratorUtils.rand.nextInt(shapes.length); } while (distIdx2 == idx || distIdx2 == distIdx1);
            
            String dist1 = "SHAPE:" + shapes[distIdx1] + ":" + color;
            String dist2 = "SHAPE:" + shapes[distIdx2] + ":" + color;
            
            GeneratorUtils.addQ(pstmt, 4, 1, "CHOICE", narrative, null, null, ans, dist1, dist2, ans);
        }
    }

    // --- LEVEL 2: MENGETIK NAMA BENTUK ---
    private static void genL2(PreparedStatement pstmt) throws SQLException {
        String[] shapes = {"RECT", "RECTANGLE", "HEXAGON", "OCTAGON", "HEART", "STAR", "TRIANGLE", "CIRCLE", "PENTAGON", "RHOMBUS", "OVAL", "SEMICIRCLE"};
        String[] names = {"PERSEGI", "PERSEGI PANJANG", "SEGI ENAM", "SEGI DELAPAN", "HATI", "BINTANG", "SEGITIGA", "LINGKARAN", "SEGI LIMA", "BELAH KETUPAT", "OVAL", "SETENGAH LINGKARAN"};
        String[] colors = {"RED", "BLUE", "GREEN", "ORANGE", "PURPLE"};
        
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < shapes.length; i++) indices.add(i);
        for (int i = 0; i < 3; i++) indices.add(GeneratorUtils.rand.nextInt(shapes.length));
        Collections.shuffle(indices);
        
        for (int idx : indices) {
            String shapeCode = shapes[idx];
            String correctName = names[idx];
            String color = colors[GeneratorUtils.rand.nextInt(colors.length)];
            
            String visualShape = "SHAPE:" + shapeCode + ":" + color;
            String[] tpls = {"Apa nama bentuk ini?", "Bentuk apakah aku?", "Ketik nama bangun datar ini!", "Siapakah aku?"};
            String narrative = tpls[GeneratorUtils.rand.nextInt(tpls.length)] + " (Ketik jawabannya)";
            
            GeneratorUtils.addQ(pstmt, 4, 2, "TYPING", narrative, visualShape, null, null, null, null, correctName);
        }
    }

    // --- LEVEL 3: POLA GAMBAR (5 VARIASI) ---
    private static void genL3(PreparedStatement pstmt) throws SQLException {
        String[] shapes = {"CIRCLE", "RECT", "TRIANGLE", "STAR", "HEART", "HEXAGON", "RHOMBUS"};
        String[] colors = {"RED", "BLUE", "GREEN", "YELLOW"};
        String[] tpls = {"Lengkapi Pola ini!", "Apa selanjutnya?", "Teruskan urutannya!", "Isi kotak yang kosong!"};
        
        // Deck untuk Pasangan (A, B)
        List<String> deckPairs = new ArrayList<>();
        for(int i=0; i<shapes.length; i++) {
            for(int j=i+1; j<shapes.length; j++) {
                deckPairs.add(shapes[i] + ":" + shapes[j]);
            }
        }
        
        // Deck untuk Trio (A, B, C)
        List<String> deckTrios = new ArrayList<>();
        for(int i=0; i<shapes.length; i++) {
            int next1 = (i + 1) % shapes.length;
            int next2 = (i + 2) % shapes.length;
            deckTrios.add(shapes[i] + ":" + shapes[next1] + ":" + shapes[next2]);
        }

        // Kita ingin 20 Soal. Ada 5 Tipe Pola. Jadi masing-masing tipe muncul 4 kali.
        // List tipe pola: 0,0,0,0, 1,1,1,1, dst...
        List<Integer> patternTypes = new ArrayList<>();
        for(int type=0; type<5; type++) {
            for(int k=0; k<4; k++) patternTypes.add(type);
        }
        Collections.shuffle(patternTypes); // Acak urutan tipe soal
        
        // Acak deck aset juga
        Collections.shuffle(deckPairs);
        Collections.shuffle(deckTrios);

        for (int i = 0; i < 20; i++) {
            int type = patternTypes.get(i);
            String narrative = tpls[GeneratorUtils.rand.nextInt(tpls.length)];
            String color = colors[GeneratorUtils.rand.nextInt(colors.length)];
            
            String sequenceText = "";
            String ansItem = "";
            String itemA, itemB, itemC;
            
            if (type == 2) { 
                // TIPE C: A - B - C - A - B - ? (Butuh 3 bentuk)
                String[] trio = deckTrios.get(i % deckTrios.size()).split(":");
                itemA = "SHAPE:" + trio[0] + ":" + color;
                itemB = "SHAPE:" + trio[1] + ":" + color;
                itemC = "SHAPE:" + trio[2] + ":" + color;
                
                sequenceText = itemA + "," + itemB + "," + itemC + "," + itemA + "," + itemB + ",?";
                ansItem = itemC;
                
            } else {
                // TIPE LAIN (Butuh 2 bentuk: A & B)
                String[] pair = deckPairs.get(i % deckPairs.size()).split(":");
                String sA = pair[0]; String sB = pair[1];
                if(GeneratorUtils.rand.nextBoolean()) { String t=sA; sA=sB; sB=t; } // Swap acak
                
                itemA = "SHAPE:" + sA + ":" + color;
                itemB = "SHAPE:" + sB + ":" + color;
                
                switch(type) {
                    case 0: // Tipe A: A - B - A - B - A - ? (Seling-seling)
                        sequenceText = itemA + "," + itemB + "," + itemA + "," + itemB + "," + itemA + ",?";
                        ansItem = itemB;
                        break;
                    case 1: // Tipe B: A - A - B - B - A - ? (Ganda)
                        sequenceText = itemA + "," + itemA + "," + itemB + "," + itemB + "," + itemA + ",?";
                        ansItem = itemA; // Polanya AA BB AA..
                        break;
                    case 3: // Tipe D: A - A - B - A - A - ? (Dominan A)
                        sequenceText = itemA + "," + itemA + "," + itemB + "," + itemA + "," + itemA + ",?";
                        ansItem = itemB; // Polanya AAB AAB
                        break;
                    case 4: // Tipe E: A - B - B - A - B - ? (Dominan B)
                        sequenceText = itemA + "," + itemB + "," + itemB + "," + itemA + "," + itemB + ",?";
                        ansItem = itemB; // Polanya ABB ABB
                        break;
                }
            }
            
            // Pengecoh (Distractor)
            // Dist1: Item yang salah (Jika jawaban A, maka B. Jika jawaban B, maka A)
            // Dist2: Item benar tapi warnanya Hitam (Siluet)
            String dist1 = ansItem.equals(itemA) ? (type==2 ? "SHAPE:STAR:BLACK" : itemB) : itemA;
            if(type==2 && ansItem.equals(itemA)) { 
                // Khusus tipe C, distractornya harus bentuk lain dari set itu
                 String[] trio = deckTrios.get(i % deckTrios.size()).split(":");
                 dist1 = "SHAPE:" + trio[1] + ":" + color; 
            }
            
            String shapeCode = ansItem.split(":")[1];
            String dist2 = "SHAPE:" + shapeCode + ":BLACK";

            GeneratorUtils.addQ(pstmt, 4, 3, "SEQUENCE", narrative + " ## " + sequenceText, null, null, ansItem, dist1, dist2, ansItem);
        }
    }
}