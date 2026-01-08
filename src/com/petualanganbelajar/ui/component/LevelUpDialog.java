package com.petualanganbelajar.ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LevelUpDialog extends JDialog {

    public LevelUpDialog(Frame parent, int newLevel) {
        super(parent, true); // Modal = true (memblokir input ke belakang)
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // Transparan

        // Panel Utama dengan Custom Painting (Gradient & Border)
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background Emas Gradien
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 215, 0), 0, getHeight(), new Color(255, 140, 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                // Border Putih
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(5));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 40, 40);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Label Judul
        JLabel lblTitle = new JLabel("LEVEL UP!");
        lblTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label Pesan
        JLabel lblMsg = new JLabel("Selamat! Kamu naik ke Level " + newLevel);
        lblMsg.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        lblMsg.setForeground(new Color(255, 255, 224));
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tombol OK
        JButton btnOk = new JButton("HEBAT!");
        btnOk.setFont(new Font("Arial", Font.BOLD, 20));
        btnOk.setBackground(Color.WHITE);
        btnOk.setForeground(new Color(255, 140, 0));
        btnOk.setFocusPainted(false);
        btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.addActionListener(e -> dispose());

        // Susun Komponen
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblMsg);
        panel.add(Box.createVerticalStrut(30));
        panel.add(btnOk);

        setContentPane(panel);
        setSize(450, 320); // Sedikit diperbesar agar lega
        setLocationRelativeTo(parent);
    }
}