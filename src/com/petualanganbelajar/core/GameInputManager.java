package com.petualanganbelajar.core;

import com.petualanganbelajar.model.QuestionModel;
import com.petualanganbelajar.ui.component.*;
import com.petualanganbelajar.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameInputManager {

    // Interface callback untuk mengirim jawaban kembali ke GameScreen
    public interface AnswerCallback {
        void onAnswer(String answer);
    }

    // [MODIFIKASI] Tambahkan parameter scale
    public static void setupInput(QuestionModel q, JPanel answerPanel, JPanel visualPanel, int moduleId, 
                                  String currentPattern, AnswerCallback callback, float scale) {
        answerPanel.removeAll();
        String type = q.getQuestionType().toString();

        if ("TYPING".equalsIgnoreCase(type)) {
            setupTyping(q, answerPanel, moduleId, callback, scale);
        } 
        else if ("SEQUENCE_MULTI".equalsIgnoreCase(type)) {
            setupKeypad(q, answerPanel, currentPattern, visualPanel, callback, scale);
        } 
        else if ("CLICK".equalsIgnoreCase(type)) {
            // Click mode merender objek interaktif di visualPanel (tengah layar)
            setupClickableScene(q, visualPanel, callback, scale);
        } 
        else {
            setupChoice(q, answerPanel, callback, scale);
        }
        answerPanel.revalidate(); answerPanel.repaint();
    }

    // --- [SCALABLE] LOGIC: CHOICE (TOMBOL BESAR RESPONSIF) ---
    private static void setupChoice(QuestionModel q, JPanel panel, AnswerCallback callback, float scale) {
        // Layout: Center, Gap Horizontal Scaled, Gap Vertical Scaled
        int gapH = (int)(40 * scale);
        int gapV = (int)(10 * scale);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, gapH, gapV));
        
        String[] options = {q.getOptionA(), q.getOptionB(), q.getOptionC()};
        Color[] colors = { new Color(100, 181, 246), new Color(129, 199, 132), new Color(255, 183, 77) };
        
        // --- KONFIGURASI UKURAN (SCALED) ---
        // Basis ukuran diperbesar sedikit agar lega
        int btnSize = (int)(180 * scale);   // Ukuran Kotak Tombol Gambar
        int iconSize = (int)(130 * scale);  // Ukuran Gambar di dalam
        
        // Ukuran tombol teks (Persegi Panjang)
        int txtBtnW = (int)(160 * scale);
        int txtBtnH = (int)(90 * scale);
        
        // Ukuran Font Tombol Teks
        int fontSize = (int)(32 * scale);
        
        for (int i = 0; i < options.length; i++) {
            if (options[i] == null) continue;
            String val = options[i];
            ModernAnswerButton btn;
            
            // Cek apakah opsi adalah SHAPE atau GAMBAR (.jpg / .png)
            boolean isVisual = val.startsWith("SHAPE:") || val.toLowerCase().endsWith(".jpg") || val.toLowerCase().endsWith(".png");
            
            if (isVisual) {
                // Tombol Putih untuk Gambar
                btn = new ModernAnswerButton("", Color.WHITE);
                btn.setPreferredSize(new Dimension(btnSize, btnSize)); // Paksa ukuran bujur sangkar
                
                if (val.startsWith("SHAPE:")) {
                    btn.setIcon(UIHelper.generateShapeIcon(val, iconSize));
                } else {
                    // Load gambar (support .png dan .jpg) dengan ukuran besar
                    btn.setIcon(UIHelper.loadIcon(val, iconSize, iconSize));
                }
            } else {
                // Tombol Teks Biasa (Angka/Huruf)
                btn = new ModernAnswerButton(val, colors[i % 3]);
                // Set font responsif
                btn.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
                // Jika teks, ukurannya persegi panjang agar muat teks panjang
                btn.setPreferredSize(new Dimension(txtBtnW, txtBtnH)); 
            }
            
            btn.addActionListener(e -> callback.onAnswer(val));
            panel.add(btn);
        }
    }

    // --- [SCALABLE] LOGIC: TYPING (INPUT TEKS) ---
    private static void setupTyping(QuestionModel q, JPanel panel, int moduleId, AnswerCallback callback, float scale) {
        int gap = (int)(15 * scale);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, gap, (int)(10*scale)));
        
        JLabel lblHint = new JLabel("Ketik jawaban:"); 
        lblHint.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(24 * scale))); // 20 -> 24 scaled
        
        // [UPDATE] Perbesar kolom dan dimensi lebar
        JTextField txtInput = new JTextField(20); 
        txtInput.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(36 * scale))); // 30 -> 36 scaled
        txtInput.setHorizontalAlignment(JTextField.CENTER);
        
        // Lebar diubah agar muat teks panjang
        int inputW = (int)(500 * scale); // 450 -> 500 scaled
        int inputH = (int)(70 * scale);  // 60 -> 70 scaled
        txtInput.setPreferredSize(new Dimension(inputW, inputH)); 
        
        txtInput.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Hanya Modul Angka (ID 1) yang dibatasi angka. Modul Bentuk (ID 4) bebas huruf.
                if (moduleId == 1 && !Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
            public void keyPressed(KeyEvent e) { 
                if (e.getKeyCode() == KeyEvent.VK_ENTER) callback.onAnswer(txtInput.getText()); 
            }
        });

        ActionAnswerButton btnSubmit = new ActionAnswerButton("JAWAB");
        // Update scale tombol submit manual atau via method jika class support
        btnSubmit.setFont(new Font("Arial", Font.BOLD, (int)(24 * scale)));
        btnSubmit.setPreferredSize(new Dimension((int)(140*scale), (int)(60*scale)));
        
        btnSubmit.addActionListener(e -> callback.onAnswer(txtInput.getText()));

        panel.add(lblHint); 
        panel.add(txtInput); 
        panel.add(btnSubmit);
        
        SwingUtilities.invokeLater(txtInput::requestFocusInWindow);
    }

    // --- [SCALABLE] LOGIC: CLICK (HIDDEN OBJECT - 6 ITEMS) ---
    private static void setupClickableScene(QuestionModel q, JPanel visualPanel, AnswerCallback callback, float scale) {
        int gap = (int)(30 * scale);
        visualPanel.setLayout(new FlowLayout(FlowLayout.CENTER, gap, gap));
        
        List<String> images = new ArrayList<>();
        
        String[] rawOptions = {q.getOptionA(), q.getOptionB(), q.getOptionC()};
        
        for (String raw : rawOptions) {
            if (raw == null || raw.isEmpty()) continue;
            String[] files = raw.split(",");
            for (String f : files) {
                images.add(f.trim());
            }
        }
        
        Collections.shuffle(images); // Acak posisi
        
        // Ukuran icon click mode
        int iconSize = (int)(150 * scale); // 100 -> 120 scaled
        int hoverSize = (int)(165 * scale); // 110 -> 130 scaled
        
        for (String imgFile : images) {
            if (imgFile.isEmpty()) continue;
            
            JButton itemBtn = new JButton();
            itemBtn.setContentAreaFilled(false); 
            itemBtn.setBorderPainted(false); 
            itemBtn.setFocusPainted(false);
            itemBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            ImageIcon icon = UIHelper.loadIcon(imgFile, iconSize, iconSize);
            itemBtn.setIcon(icon);
            
            ImageIcon hoverIcon = UIHelper.loadIcon(imgFile, hoverSize, hoverSize);
            if (hoverIcon != null) itemBtn.setRolloverIcon(hoverIcon);
            
            itemBtn.addActionListener(e -> callback.onAnswer(imgFile));
            visualPanel.add(itemBtn);
        }
    }

    // --- [SCALABLE] LOGIC: KEYPAD (SEQUENCE MULTI) ---
    private static void setupKeypad(QuestionModel q, JPanel panel, String pattern, JPanel visualPanel, AnswerCallback callback, float scale) {
        panel.setLayout(new GridBagLayout()); 
        int pad = (int)(4 * scale); // Padding antar tombol
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.insets = new Insets(pad, pad, pad, pad);
        
        String[] rows = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};
        
        // Ukuran tombol keypad
        int keyW = (int)(60 * scale);
        int keyH = (int)(60 * scale);
        Font keyFont = new Font("Arial", Font.BOLD, (int)(24 * scale));
        
        for (int r = 0; r < rows.length; r++) {
            String row = rows[r];
            for (int i = 0; i < row.length(); i++) {
                gbc.gridy = r; 
                gbc.gridx = (r == 2) ? i + 1 : i; 
                
                // Indent baris kedua
                int leftPad = (r == 1 && i == 0) ? (int)(25 * scale) : pad;
                gbc.insets = new Insets(pad, leftPad, pad, pad);
                
                String letter = String.valueOf(row.charAt(i));
                GlassKeypadButton btn = new GlassKeypadButton(letter);
                
                // Manual scaling untuk Keypad Button
                btn.setPreferredSize(new Dimension(keyW, keyH));
                btn.setFont(keyFont);
                
                btn.addActionListener(e -> callback.onAnswer(letter));
                panel.add(btn, gbc);
            }
        }
    }
}