package com.petualanganbelajar.content;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class ColorContent {

    public static void generate(PreparedStatement pstmt) throws SQLException {
        genL1(pstmt);
        genL2(pstmt);
        genL3(pstmt);
    }

    // --- HELPER KHUSUS MODUL WARNA ---
    
    private static Map<String, List<String>> getAssetMap() {
        Map<String, List<String>> assetMap = new HashMap<>();
        assetMap.put("RED", Collections.singletonList("item_red_apple.png"));
        assetMap.put("YELLOW", Collections.singletonList("item_yellow_banana.png"));
        assetMap.put("BLUE", Collections.singletonList("item_blue_whale.png"));
        assetMap.put("GREEN", Collections.singletonList("item_green_frog.png"));
        assetMap.put("ORANGE", Collections.singletonList("item_orange_fish.png"));
        assetMap.put("PURPLE", Collections.singletonList("item_purple_grape.png"));
        assetMap.put("PINK", Collections.singletonList("item_pink_donut.png"));
        
        // [PENTING] Mapping Beruang ke Cokelat & Kucing ke Hitam
        assetMap.put("BROWN", Collections.singletonList("item_brown_bear.png"));
        assetMap.put("BLACK", Collections.singletonList("item_black_cat.png"));
        
        assetMap.put("WHITE", Collections.singletonList("item_white_rabbit.png"));
        return assetMap;
    }

    private static String getRandomColorCodeExcluding(String[] allColors, String... excludes) {
        String c;
        do {
            c = allColors[GeneratorUtils.rand.nextInt(allColors.length)];
        } while (contains(excludes, c));
        return c;
    }

    private static String getAssetFromOtherColor(Map<String, List<String>> map, String[] allColors, String... excludes) {
        String chosenColor;
        do {
            chosenColor = allColors[GeneratorUtils.rand.nextInt(allColors.length)];
        } while (contains(excludes, chosenColor));
        return map.get(chosenColor).get(0);
    }
    
    private static boolean contains(String[] arr, String target) {
        for (String s : arr) if (s.equals(target)) return true;
        return false;
    }

    // --- LEVEL 1: PENGENALAN WARNA ---
    private static void genL1(PreparedStatement pstmt) throws SQLException {
        String[] colorCodes = {"RED", "YELLOW", "BLUE", "GREEN", "ORANGE", "PURPLE", "PINK", "BROWN", "BLACK", "WHITE"};
        String[] colorNames = {"MERAH", "KUNING", "BIRU", "HIJAU", "ORANYE", "UNGU", "MERAH MUDA", "COKELAT", "HITAM", "PUTIH"};
        Map<String, List<String>> assetMap = getAssetMap();

        List<String> targetDeck = GeneratorUtils.createBalancedDeck(colorCodes, 20);
        
        for (String targetColor : targetDeck) {
            int idx = -1; for(int k=0; k<colorCodes.length; k++) if(colorCodes[k].equals(targetColor)) idx = k;
            String targetName = colorNames[idx];
            
            int type = GeneratorUtils.rand.nextInt(3);
            String narrative, visualQuestion = null, correctAns, dist1, dist2;

            if (type == 0) { // Mencocokkan Benda
                String[] tpls = {"Moli mencari gambar <b>%s</b>. Mana yang benar?", "Benda mana yang berwarna <b>%s</b>?", "Tunjukkan padaku warna <b>%s</b>!"};
                narrative = String.format(tpls[GeneratorUtils.rand.nextInt(tpls.length)], targetName);
                
                correctAns = assetMap.get(targetColor).get(0);
                dist1 = getAssetFromOtherColor(assetMap, colorCodes, targetColor);
                dist2 = getAssetFromOtherColor(assetMap, colorCodes, targetColor, getColorFromAsset(dist1, assetMap));
            
            } else if (type == 1) { // Odd One Out
                String[] tpls = {"Ups, ada yang salah! Mana yang warnanya <b>BEDA</b>?", "Cari gambar yang warnanya <b>LAIN</b> sendiri!", "Yang mana yang beda <b>SENDIRI</b>?"};
                narrative = tpls[GeneratorUtils.rand.nextInt(tpls.length)];
                
                String maj = assetMap.get(targetColor).get(0);
                dist1 = maj; dist2 = maj; 
                String uniqColor = getRandomColorCodeExcluding(colorCodes, targetColor);
                correctAns = assetMap.get(uniqColor).get(0);
            
            } else { // Mewarnai (Siluet)
                String chosenAsset = assetMap.get(targetColor).get(0);
                String objName = "GAMBAR";
                try { objName = chosenAsset.split("_")[2].replace(".png","").toUpperCase(); } catch(Exception e){}
                
                // [FIXED] Menggunakan %2$s untuk mengambil Nama Warna (parameter ke-2)
                String[] tpls = {
                    "%s ini pucat! Warnai menjadi <b>%s</b>!",    
                    "Berikan warna <b>%2$s</b> pada gambar ini!", 
                    "Ayo warnai %s dengan warna <b>%s</b>!"       
                };
                
                narrative = String.format(tpls[GeneratorUtils.rand.nextInt(tpls.length)], objName, targetName);
                
                visualQuestion = "SILHOUETTE:" + chosenAsset;
                correctAns = "SHAPE:CIRCLE:" + targetColor;
                
                String c1 = getRandomColorCodeExcluding(colorCodes, targetColor);
                String c2 = getRandomColorCodeExcluding(colorCodes, targetColor, c1);
                dist1 = "SHAPE:CIRCLE:" + c1; 
                dist2 = "SHAPE:CIRCLE:" + c2;
            }
            
            GeneratorUtils.addQ(pstmt, 3, 1, "CHOICE", narrative, visualQuestion, null, correctAns, dist1, dist2, correctAns);
        }
    }

    // --- LEVEL 2: SORTIR WARNA (DIPERBAIKI: PILIH SATU) ---
    private static void genL2(PreparedStatement pstmt) throws SQLException {
        Map<String, List<String>> assetMap = getAssetMap();
        String[] colorCodes = {"RED", "YELLOW", "BLUE", "GREEN", "ORANGE", "PURPLE", "PINK", "BROWN", "BLACK", "WHITE"};
        String[] colorNames = {"MERAH", "KUNING", "BIRU", "HIJAU", "ORANYE", "UNGU", "MERAH MUDA", "COKELAT", "HITAM", "PUTIH"};
        
        List<String> targetDeck = GeneratorUtils.createBalancedDeck(colorCodes, 20);

        for (String targetColor : targetDeck) {
            int idx = -1; for(int k=0; k<colorCodes.length; k++) if(colorCodes[k].equals(targetColor)) idx = k;
            String targetName = colorNames[idx];
            
            // [REVISI NARASI] Menggunakan kalimat tunggal/pilih satu
            String[] tpls = {
                "Mana gambar yang warnanya <b>%s</b>?", 
                "Ayo klik benda berwarna <b>%s</b>!", 
                "Coba tunjuk, yang mana warna <b>%s</b>?"
            };
            
            String narrative = String.format(tpls[GeneratorUtils.rand.nextInt(tpls.length)], targetName);
            
            String correctImg = assetMap.get(targetColor).get(0);
            
            // Siapkan Distractor
            List<String> avail = new ArrayList<>(Arrays.asList(colorCodes));
            avail.remove(targetColor); 
            Collections.shuffle(avail);
            
            List<String> dists = new ArrayList<>();
            for(int k=0; k<5; k++) {
                if (k < avail.size()) dists.add(assetMap.get(avail.get(k)).get(0));
            }
            
            if (dists.size() >= 5) {
                // Gameplay Click: OptA Benar, OptB/C Salah
                String optA = correctImg; 
                String optB = dists.get(0) + "," + dists.get(1);
                String optC = dists.get(2) + "," + dists.get(3) + "," + dists.get(4);
                GeneratorUtils.addQ(pstmt, 3, 2, "CLICK", narrative, "CLICK_MODE", null, optA, optB, optC, correctImg);
            }
        }
    }

    // --- LEVEL 3: CAMPUR WARNA (2 & 3 BAHAN) ---
    private static void genL3(PreparedStatement pstmt) throws SQLException {
        String[][] recipes = {
            {"RED", "YELLOW", "ORANGE"}, 
            {"BLUE", "YELLOW", "GREEN"}, 
            {"RED", "BLUE", "PURPLE"}, 
            {"RED", "WHITE", "PINK"}, 
            {"BLACK", "WHITE", "GRAY"},
            {"RED", "YELLOW", "BLUE", "BROWN"} 
        };
        
        List<Integer> recipeIndices = new ArrayList<>();
        for(int i=0; i<4; i++) { 
            for(int r=0; r<recipes.length; r++) recipeIndices.add(r); 
        } 
        Collections.shuffle(recipeIndices);

        Map<String, String> colorFiles = new HashMap<>();
        colorFiles.put("RED", "cat_merah.png");
        colorFiles.put("YELLOW", "cat_kuning.png");
        colorFiles.put("BLUE", "cat_biru.png");
        colorFiles.put("GREEN", "cat_hijau.png");
        colorFiles.put("ORANGE", "cat_orange.png");
        colorFiles.put("PURPLE", "cat_ungu.png");
        colorFiles.put("WHITE", "cat_putih.png");
        colorFiles.put("BLACK", "cat_hitam.png");
        colorFiles.put("PINK",  "cat_pink.png");
        colorFiles.put("GRAY",  "cat_abu.png");
        colorFiles.put("BROWN", "cat_coklat.png");

        String[] possibleDistractors = {
            "RED", "BLUE", "YELLOW", "GREEN", "ORANGE", "PURPLE", 
            "PINK", "BLACK", "WHITE", "GRAY", "BROWN"
        };

        for (int i = 0; i < 20; i++) { 
            int rIdx = recipeIndices.get(i % recipeIndices.size());
            String[] recipe = recipes[rIdx];
            
            String resultKey = recipe[recipe.length - 1];
            String ansAsset = colorFiles.get(resultKey);
            
            List<String> inputs = new ArrayList<>();
            for(int k=0; k<recipe.length-1; k++) {
                inputs.add(recipe[k]);
            }
            Collections.shuffle(inputs);
            
            String[] tpls = {
                "Jika cat ini dicampur, jadi warna apa?", 
                "Ayo campur warnanya! Hasilnya apa?", 
                "Apa hasil pencampuran warna ini?"
            };
            String narrative = tpls[GeneratorUtils.rand.nextInt(tpls.length)];
            
            StringBuilder visualBuilder = new StringBuilder();
            visualBuilder.append(narrative).append(" ## ");
            
            for (int k = 0; k < inputs.size(); k++) {
                visualBuilder.append(colorFiles.get(inputs.get(k)));
                if (k < inputs.size() - 1) {
                    visualBuilder.append(" ## + ## ");
                }
            }
            
            String dist1Key;
            do { dist1Key = possibleDistractors[GeneratorUtils.rand.nextInt(possibleDistractors.length)]; } 
            while (dist1Key.equals(resultKey));
            
            String dist2Key;
            do { dist2Key = possibleDistractors[GeneratorUtils.rand.nextInt(possibleDistractors.length)]; } 
            while (dist2Key.equals(resultKey) || dist2Key.equals(dist1Key));
            
            String assetDist1 = colorFiles.getOrDefault(dist1Key, "SHAPE:CIRCLE:" + dist1Key);
            String assetDist2 = colorFiles.getOrDefault(dist2Key, "SHAPE:CIRCLE:" + dist2Key);

            GeneratorUtils.addQ(pstmt, 3, 3, "CHOICE", visualBuilder.toString(), null, null, ansAsset, assetDist1, assetDist2, ansAsset);
        }
    }
    
    private static String getColorFromAsset(String filename, Map<String, List<String>> assetMap) {
        for (Map.Entry<String, List<String>> entry : assetMap.entrySet()) {
            if (entry.getValue().contains(filename)) return entry.getKey();
        }
        return "";
    }
}