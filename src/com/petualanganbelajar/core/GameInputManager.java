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

    // Method utama untuk setup input area
    public static void setupInput(QuestionModel q, JPanel answerPanel, JPanel visualPanel, int moduleId, 
                                  String currentPattern, AnswerCallback callback) {
        answerPanel.removeAll();
        String type = q.getQuestionType().toString();

        if ("TYPING".equalsIgnoreCase(type)) {
            setupTyping(q, answerPanel, moduleId, callback);
        } 
        else if ("SEQUENCE_MULTI".equalsIgnoreCase(type)) {
            setupKeypad(q, answerPanel, currentPattern, visualPanel, callback);
        } 
        else if ("CLICK".equalsIgnoreCase(type)) {
            // Click mode merender objek interaktif di visualPanel (tengah layar)
            setupClickableScene(q, visualPanel, callback);
        } 
        else {
            setupChoice(q, answerPanel, callback);
        }
        answerPanel.revalidate(); answerPanel.repaint();
    }

    // --- [UPDATED] LOGIC: CHOICE (TOMBOL BESAR 160px) ---
    private static void setupChoice(QuestionModel q, JPanel panel, AnswerCallback callback) {
        // Layout: Center, Gap Horizontal 40 (Lebih lega), Gap Vertical 10
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 10));
        
        String[] options = {q.getOptionA(), q.getOptionB(), q.getOptionC()};
        Color[] colors = { new Color(100, 181, 246), new Color(129, 199, 132), new Color(255, 183, 77) };
        
        // --- KONFIGURASI UKURAN ---
        int btnSize = 160;   // Ukuran Kotak Tombol
        int iconSize = 120;  // Ukuran Gambar di dalam
        
        for (int i = 0; i < options.length; i++) {
            if (options[i] == null) continue;
            String val = options[i];
            ModernAnswerButton btn;
            
            // Cek apakah opsi adalah SHAPE atau GAMBAR (.jpg / .png)
            boolean isVisual = val.startsWith("SHAPE:") || val.toLowerCase().endsWith(".jpg") || val.toLowerCase().endsWith(".png");
            
            if (isVisual) {
                // Tombol Putih untuk Gambar
                btn = new ModernAnswerButton("", Color.WHITE);
                btn.setPreferredSize(new Dimension(btnSize, btnSize)); // Paksa ukuran besar
                
                if (val.startsWith("SHAPE:")) {
                    btn.setIcon(UIHelper.generateShapeIcon(val, iconSize));
                } else {
                    // Load gambar (support .png dan .jpg) dengan ukuran besar
                    btn.setIcon(UIHelper.loadIcon(val, iconSize, iconSize));
                }
            } else {
                // Tombol Teks Biasa (Angka/Huruf)
                btn = new ModernAnswerButton(val, colors[i % 3]);
                // Jika teks, ukurannya persegi panjang agar muat teks panjang
                btn.setPreferredSize(new Dimension(140, 80)); 
            }
            
            btn.addActionListener(e -> callback.onAnswer(val));
            panel.add(btn);
        }
    }

    // --- LOGIC: TYPING (INPUT TEKS) ---
    // --- LOGIC: TYPING (INPUT TEKS - UPDATED LEBAR) ---
    private static void setupTyping(QuestionModel q, JPanel panel, int moduleId, AnswerCallback callback) {
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JLabel lblHint = new JLabel("Ketik jawaban:"); 
        lblHint.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        
        // [UPDATE] Perbesar kolom dan dimensi lebar
        JTextField txtInput = new JTextField(20); // Tambah jumlah kolom karakter
        txtInput.setFont(new Font("Comic Sans MS", Font.BOLD, 30)); // Font sedikit dikecilkan agar muat banyak
        txtInput.setHorizontalAlignment(JTextField.CENTER);
        
        // Lebar diubah dari 120 menjadi 450 agar muat "SETENGAH LINGKARAN"
        txtInput.setPreferredSize(new Dimension(450, 60)); 
        
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
        btnSubmit.addActionListener(e -> callback.onAnswer(txtInput.getText()));

        panel.add(lblHint); 
        panel.add(txtInput); 
        panel.add(btnSubmit);
        
        SwingUtilities.invokeLater(txtInput::requestFocusInWindow);
    }

    // --- LOGIC: CLICK (HIDDEN OBJECT - 6 ITEMS) ---
    private static void setupClickableScene(QuestionModel q, JPanel visualPanel, AnswerCallback callback) {
        visualPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        List<String> images = new ArrayList<>();
        
        // Ambil data opsi A, B, C (yang mungkin berisi koma)
        String[] rawOptions = {q.getOptionA(), q.getOptionB(), q.getOptionC()};
        
        for (String raw : rawOptions) {
            if (raw == null || raw.isEmpty()) continue;
            
            // Split berdasarkan koma (untuk menangani packing multiple images)
            String[] files = raw.split(",");
            for (String f : files) {
                images.add(f.trim());
            }
        }
        
        Collections.shuffle(images); // Acak posisi
        
        for (String imgFile : images) {
            if (imgFile.isEmpty()) continue;
            
            JButton itemBtn = new JButton();
            itemBtn.setContentAreaFilled(false); 
            itemBtn.setBorderPainted(false); 
            itemBtn.setFocusPainted(false);
            itemBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Load Icon (Ukuran 100x100 agar muat banyak di tengah layar)
            ImageIcon icon = UIHelper.loadIcon(imgFile, 100, 100);
            itemBtn.setIcon(icon);
            
            // Efek Hover (Membesar sedikit)
            ImageIcon hoverIcon = UIHelper.loadIcon(imgFile, 110, 110);
            if (hoverIcon != null) itemBtn.setRolloverIcon(hoverIcon);
            
            itemBtn.addActionListener(e -> callback.onAnswer(imgFile));
            visualPanel.add(itemBtn);
        }
    }

    // --- LOGIC: KEYPAD (SEQUENCE MULTI) ---
    private static void setupKeypad(QuestionModel q, JPanel panel, String pattern, JPanel visualPanel, AnswerCallback callback) {
        panel.setLayout(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(3, 3, 3, 3);
        
        String[] rows = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};
        for (int r = 0; r < rows.length; r++) {
            String row = rows[r];
            for (int i = 0; i < row.length(); i++) {
                gbc.gridy = r; 
                gbc.gridx = (r == 2) ? i + 1 : i; 
                if (r == 1 && i == 0) gbc.insets = new Insets(3, 20, 3, 3); 
                else gbc.insets = new Insets(3, 3, 3, 3);
                
                String letter = String.valueOf(row.charAt(i));
                GlassKeypadButton btn = new GlassKeypadButton(letter);
                btn.addActionListener(e -> callback.onAnswer(letter));
                panel.add(btn, gbc);
            }
        }
    }
}