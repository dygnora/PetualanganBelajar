package com.petualanganbelajar.content;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneratorUtils {
    
    public static final Random rand = new Random();
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Fungsi Insert ke Database (Shared)
    public static void addQ(PreparedStatement pstmt, int modId, int lvl, String type, String text,
                            String img, String audio, String op1, String op2, String op3, String ans) throws SQLException {
        List<String> ops = new ArrayList<>();
        if (op1 != null) { ops.add(op1); ops.add(op2); ops.add(op3); Collections.shuffle(ops); }
        else { ops.add(null); ops.add(null); ops.add(null); }

        pstmt.setInt(1, modId); pstmt.setInt(2, lvl); pstmt.setString(3, type);
        pstmt.setString(4, text); pstmt.setString(5, img); pstmt.setString(6, audio);
        pstmt.setString(7, ops.get(0)); pstmt.setString(8, ops.get(1)); pstmt.setString(9, ops.get(2));
        pstmt.setString(10, ans);
        pstmt.addBatch();
    }

    public static List<String> createBalancedDeck(String[] items, int totalQuestions) {
        List<String> deck = new ArrayList<>();
        int copies = (int) Math.ceil((double) totalQuestions / items.length);
        for (int i = 0; i < copies; i++) {
            for (String item : items) deck.add(item);
        }
        Collections.shuffle(deck);
        return deck.subList(0, totalQuestions);
    }
    
    public static int[] getWrongInts(int ans, int min, int max) {
        int w1, w2;
        do { w1 = rand.nextInt(max - min + 1) + min; } while (w1 == ans);
        do { w2 = rand.nextInt(max - min + 1) + min; } while (w2 == ans || w2 == w1);
        return new int[]{w1, w2};
    }

    public static String[] getWrongChars(String ansStr) {
        char ans = ansStr.toUpperCase().charAt(0);
        char w1, w2;
        do { w1 = ALPHABET.charAt(rand.nextInt(ALPHABET.length())); } while (w1 == ans);
        do { w2 = ALPHABET.charAt(rand.nextInt(ALPHABET.length())); } while (w2 == ans || w2 == w1);
        return new String[]{String.valueOf(w1), String.valueOf(w2)};
    }
}