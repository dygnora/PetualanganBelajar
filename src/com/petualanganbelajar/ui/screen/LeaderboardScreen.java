/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.ui.screen;
import com.petualanganbelajar.core.GameConfig;
import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.model.LeaderboardEntry;
import com.petualanganbelajar.repository.LeaderboardRepository;
import javax.swing.*;
import java.awt.*;
import java.util.List;
/**
 *
 * @author DD
 */
public class LeaderboardScreen extends JPanel {
    private final LeaderboardRepository repo;
    private final JPanel listPanel;

    public LeaderboardScreen() {
        this.repo = new LeaderboardRepository();
        
        setLayout(new BorderLayout());
        setBackground(GameConfig.COLOR_BG);
        
        // 1. Header
        JLabel title = new JLabel("PAPAN JUARA", SwingConstants.CENTER);
        title.setFont(GameConfig.FONT_TITLE);
        title.setForeground(GameConfig.COLOR_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);
        
        // 2. List Container (Scrollable)
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // 3. Tombol Kembali
        JButton btnBack = new JButton("KEMBALI KE MENU");
        btnBack.setFont(GameConfig.FONT_BODY);
        btnBack.addActionListener(e -> ScreenManager.getInstance().showScreen("MAIN_MENU"));
        
        JPanel footer = new JPanel();
        footer.setBackground(GameConfig.COLOR_BG);
        footer.add(btnBack);
        add(footer, BorderLayout.SOUTH);
    }
    
    // Dipanggil setiap kali layar dibuka agar data selalu update
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            refreshLeaderboard();
        }
    }
    
    private void refreshLeaderboard() {
        listPanel.removeAll();
        List<LeaderboardEntry> entries = repo.getTopScores();
        
        if (entries.isEmpty()) {
            JLabel emptyLabel = new JLabel("Belum ada juara. Ayo mainkan game!");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setFont(GameConfig.FONT_SUBTITLE);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
            listPanel.add(emptyLabel);
        } else {
            int rank = 1;
            for (LeaderboardEntry entry : entries) {
                listPanel.add(createRow(rank++, entry));
                listPanel.add(Box.createVerticalStrut(10)); // Spasi antar baris
            }
        }
        
        listPanel.revalidate();
        listPanel.repaint();
    }
    
    private JPanel createRow(int rank, LeaderboardEntry entry) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(500, 60)); // Lebar max agar rapi
        row.setBackground(new Color(240, 240, 255));
        row.setBorder(BorderFactory.createLineBorder(GameConfig.COLOR_ACCENT, 1));
        
        // RANK (Kiri)
        JLabel lblRank = new JLabel("  #" + rank + "  ");
        lblRank.setFont(new Font("Arial", Font.BOLD, 20));
        lblRank.setForeground(GameConfig.COLOR_PRIMARY);
        
        // NAMA & MODUL (Tengah)
        JLabel lblName = new JLabel(entry.getPlayerName() + " (" + entry.getModuleName() + ")");
        lblName.setFont(new Font("Arial", Font.BOLD, 16));
        
        // SKOR (Kanan)
        JLabel lblScore = new JLabel(entry.getScore() + " Poin  ");
        lblScore.setFont(new Font("Arial", Font.BOLD, 16));
        lblScore.setForeground(new Color(255, 140, 0)); // Orange
        
        row.add(lblRank, BorderLayout.WEST);
        row.add(lblName, BorderLayout.CENTER);
        row.add(lblScore, BorderLayout.EAST);
        
        return row;
    }
}
