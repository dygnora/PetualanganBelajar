/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.ui.screen;
import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.GameState;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.repository.ProgressRepository;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author DD
 */
public class LevelSelectionScreen extends JPanel {
    private final ProgressRepository progressRepo;
    private final JPanel buttonPanel;
    private ModuleModel currentModule; // Modul apa yang sedang dipilih?
    private JLabel titleLabel;

    public LevelSelectionScreen() {
        this.progressRepo = new ProgressRepository();
        
        setLayout(new BorderLayout());
        setBackground(GameConfig.COLOR_BG);

        // 1. Header
        titleLabel = new JLabel("PILIH LEVEL", SwingConstants.CENTER);
        titleLabel.setFont(GameConfig.FONT_TITLE);
        titleLabel.setForeground(GameConfig.COLOR_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 2. Container Tombol Level
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0)); // Flow layout horizontal
        buttonPanel.setBackground(GameConfig.COLOR_BG);
        add(buttonPanel, BorderLayout.CENTER);

        // 3. Tombol Kembali
        JButton btnBack = new JButton("KEMBALI KE MODUL");
        btnBack.setFont(GameConfig.FONT_BODY);
        btnBack.addActionListener(e -> ScreenManager.getInstance().showScreen("MODULE_SELECT"));
        
        JPanel footer = new JPanel();
        footer.setBackground(GameConfig.COLOR_BG);
        footer.add(btnBack);
        add(footer, BorderLayout.SOUTH);
    }

    // METHOD PENTING: Dipanggil sebelum layar ditampilkan
    public void setModule(ModuleModel module) {
        this.currentModule = module;
        this.titleLabel.setText("MODUL: " + module.getName());
        refreshLevels();
    }

    private void refreshLevels() {
        buttonPanel.removeAll();
        
        int userId = GameState.getCurrentUser().getId();
        int highestLevel = progressRepo.getHighestLevelUnlocked(userId, currentModule.getId());

        // Buat 3 Tombol Level
        for (int i = 1; i <= 3; i++) {
            buttonPanel.add(createLevelButton(i, i <= highestLevel));
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private JButton createLevelButton(int level, boolean isUnlocked) {
        JButton btn = new JButton("LEVEL " + level);
        btn.setPreferredSize(new Dimension(200, 200));
        btn.setFont(new Font("Arial", Font.BOLD, 30));
        btn.setFocusPainted(false);
        
        if (isUnlocked) {
            btn.setBackground(GameConfig.COLOR_ACCENT);
            btn.setForeground(Color.WHITE);
            // Aksi Main
            btn.addActionListener(e -> {
                ScreenManager.getInstance().startGame(currentModule, level); // <-- Perhatikan parameter baru (level)
            });
        } else {
            btn.setBackground(Color.LIGHT_GRAY);
            btn.setForeground(Color.DARK_GRAY);
            btn.setText("<html><center>LEVEL " + level + "<br><small>(Terkunci)</small></center></html>");
            btn.setEnabled(false); // Matikan tombol
        }
        
        return btn;
    }
}
