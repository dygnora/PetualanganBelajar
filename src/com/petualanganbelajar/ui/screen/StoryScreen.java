package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.util.DialogScene;
import com.petualanganbelajar.util.StoryContent;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Layar Cerita (Visual Novel Style).
 * Menampilkan dialog berurutan sebelum masuk ke gameplay.
 */
public class StoryScreen extends JPanel {

    // UI Components
    private JLabel lblImage;      // Gambar Karakter
    private JLabel lblName;       // Nama Pembicara
    private JTextArea txtDialog;  // Teks Dialog (Multi-line)
    private JButton btnNext;      // Tombol Lanjut/Mulai

    // Data Logic
    private List<DialogScene> currentScenes;
    private int sceneIndex = 0;
    private ModuleModel pendingModule;
    private int pendingLevel;

    public StoryScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. AREA GAMBAR (CENTER)
        lblImage = new JLabel("", SwingConstants.CENTER);
        // Placeholder Emoji jika gambar belum ada
        lblImage.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 150)); 
        add(lblImage, BorderLayout.CENTER);

        // 2. AREA DIALOG (SOUTH)
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setPreferredSize(new Dimension(800, 200));
        dialogPanel.setBackground(new Color(245, 245, 245)); // Abu sangat muda
        dialogPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, GameConfig.COLOR_PRIMARY));

        // Nama Pembicara
        lblName = new JLabel("Nama Karakter");
        lblName.setFont(new Font("Arial", Font.BOLD, 22));
        lblName.setForeground(GameConfig.COLOR_PRIMARY);
        lblName.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
        dialogPanel.add(lblName, BorderLayout.NORTH);

        // Teks Dialog
        txtDialog = new JTextArea();
        txtDialog.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        txtDialog.setWrapStyleWord(true);
        txtDialog.setLineWrap(true);
        txtDialog.setEditable(false);
        txtDialog.setOpaque(false); // Transparan agar ikut warna panel
        txtDialog.setMargin(new Insets(5, 20, 5, 20));
        dialogPanel.add(txtDialog, BorderLayout.CENTER);

        // Tombol Next (Pojok Kanan Bawah)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        
        btnNext = new JButton("LANJUT >>");
        btnNext.setFont(new Font("Arial", Font.BOLD, 18));
        btnNext.setBackground(GameConfig.COLOR_ACCENT);
        btnNext.setForeground(Color.WHITE);
        btnNext.setPreferredSize(new Dimension(180, 50));
        btnNext.addActionListener(e -> nextScene());
        
        btnPanel.add(btnNext);
        dialogPanel.add(btnPanel, BorderLayout.SOUTH);

        add(dialogPanel, BorderLayout.SOUTH);
    }

    // Dipanggil oleh ScreenManager
    public void setupStory(ModuleModel module, int level) {
        this.pendingModule = module;
        this.pendingLevel = level;
        
        // Ambil naskah cerita
        this.currentScenes = StoryContent.getScenes(module.getName(), level);
        this.sceneIndex = 0;

        // Tampilkan scene pertama
        showScene(0);
    }

    private void showScene(int index) {
        if (currentScenes == null || currentScenes.isEmpty()) return;

        DialogScene scene = currentScenes.get(index);

        // Update UI
        lblName.setText(scene.getCharacterName());
        txtDialog.setText(scene.getDialogText());

        // Update Gambar (Logic Placeholder Sederhana)
        // Nanti diganti dengan: new ImageIcon("resources/images/" + scene.getPoseImage())
        updatePlaceholderImage(scene.getPoseImage());

        // Ubah warna jika Tutorial (Visual Cue)
        if (scene.isTutorial()) {
             lblName.setForeground(new Color(200, 100, 0)); // Oranye Tua
        } else {
             lblName.setForeground(GameConfig.COLOR_PRIMARY); // Kuning Emas
        }

        // Cek tombol terakhir
        if (index == currentScenes.size() - 1) {
            btnNext.setText("AYO MAIN!");
            btnNext.setBackground(new Color(50, 205, 50)); // Hijau (LimeGreen)
        } else {
            btnNext.setText("LANJUT >>");
            btnNext.setBackground(GameConfig.COLOR_ACCENT); // Biru
        }
    }

    private void nextScene() {
        sceneIndex++;
        if (sceneIndex < currentScenes.size()) {
            showScene(sceneIndex);
        } else {
            // Cerita Selesai -> Pindah Layar
            if (pendingModule.getName().equalsIgnoreCase("PROLOGUE")) {
                ScreenManager.getInstance().showScreen("MAIN_MENU");
            } else if (pendingModule.getName().equalsIgnoreCase("EPILOGUE")) {
                ScreenManager.getInstance().showScreen("MAIN_MENU");
            } else {
                // Masuk ke Gameplay
                ScreenManager.getInstance().showGame(pendingModule, pendingLevel);
            }
        }
    }

    // Helper sementara (karena kamu belum punya aset gambar asli)
    private void updatePlaceholderImage(String imageName) {
        String emoji = "❓"; // Default
        if (imageName.contains("otto")) emoji = "🦉";
        else if (imageName.contains("bobo")) emoji = "🐻";
        else if (imageName.contains("cici")) emoji = "🐿️";
        else if (imageName.contains("moli")) emoji = "🦎";
        else if (imageName.contains("tobi")) emoji = "🐢";
        
        lblImage.setText(emoji);
        lblImage.setIcon(null); // Kosongkan icon dulu
        
        // NANTI: Aktifkan kode ini jika gambar sudah ada di resources/images/
        /*
        try {
            java.net.URL imgUrl = getClass().getClassLoader().getResource("images/" + imageName);
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                // Resize gambar agar pas
                Image img = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
                lblImage.setText(""); // Hapus emoji
            }
        } catch (Exception e) {
            System.err.println("Gagal load gambar: " + imageName);
        }
        */
    }
}