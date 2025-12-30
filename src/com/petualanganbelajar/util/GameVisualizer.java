package com.petualanganbelajar.util;

import com.petualanganbelajar.model.QuestionModel;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameVisualizer {

    // Helper Font & Warna
    private static final Font FONT_BIG = new Font("Comic Sans MS", Font.BOLD, 40);
    private static final Color COLOR_BOX_YELLOW = new Color(255, 204, 100);
    private static final Color COLOR_BOX_BLUE = new Color(135, 206, 250);
    private static final Color COLOR_BOX_GRAY = new Color(200, 200, 200);

    /**
     * Method Utama: GameScreen memanggil ini untuk menggambar soal.
     */
    public static void render(JPanel container, JLabel lblInstruction, QuestionModel q, int moduleId, int level) {
        container.removeAll(); // Bersihkan gambar lama
        
        // Default Layout (Flow) untuk sebagian besar level
        // Kita reset di sini agar tidak terbawa settingan level sebelumnya
        container.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        // ROUTING LOGIC
        if (moduleId == 1 && level == 1) {
            renderMathLevel1(container, lblInstruction, q); // [UPDATE] Grid Layout 5 kolom
        } 
        else if (moduleId == 1 && level == 2) {
            renderMathLevel2(container, lblInstruction, q);
        } 
        else {
            renderStandard(container, lblInstruction, q);
        }

        container.revalidate();
        container.repaint();
    }

    // --- RENDERER KHUSUS: MODUL 1 LEVEL 1 (COUNTING GRID 5x2) ---
    private static void renderMathLevel1(JPanel container, JLabel lblInstruction, QuestionModel q) {
        lblInstruction.setText("<html><center>" + q.getQuestionText() + "</center></html>");

        // 1. Ambil jumlah benda
        int count = 0;
        try {
            count = Integer.parseInt(q.getCorrectAnswer());
        } catch (NumberFormatException e) {
            count = 1;
        }

        // 2. [LOGIC BARU] Gunakan GridBagLayout untuk kontrol posisi X,Y
        // Agar tersusun: 5 di atas, sisanya di bawah
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Jarak antar benda

        // 3. Siapkan Icon
        ImageIcon icon = null;
        if (q.getQuestionImage() != null && !q.getQuestionImage().isEmpty()) {
            String path = "/images/" + q.getQuestionImage();
            java.net.URL imgURL = GameVisualizer.class.getResource(path);
            if (imgURL != null) {
                Image img = new ImageIcon(imgURL).getImage();
                Image newImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                icon = new ImageIcon(newImg);
            }
        }

        // 4. Loop & Posisikan dalam Grid (Max 5 per baris)
        for (int i = 0; i < count; i++) {
            JLabel label;
            if (icon != null) {
                label = new JLabel(icon);
            } else {
                label = new JLabel("?");
                label.setFont(FONT_BIG);
                label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                label.setPreferredSize(new Dimension(80, 80));
                label.setHorizontalAlignment(SwingConstants.CENTER);
            }

            // Rumus Grid 5 Kolom:
            // i = 0,1,2,3,4 -> Baris 0 (Atas)
            // i = 5,6,7,8,9 -> Baris 1 (Bawah)
            gbc.gridx = i % 5; // Kolom ke- (0 sampai 4)
            gbc.gridy = i / 5; // Baris ke- (0 atau 1)
            
            container.add(label, gbc);
        }
    }

    // --- RENDERER KHUSUS: MODUL 1 LEVEL 2 (VISUAL PUZZLE) ---
    private static void renderMathLevel2(JPanel container, JLabel lblInstruction, QuestionModel q) {
        String text = q.getQuestionText();
        
        List<String> numbers = new ArrayList<>();
        Matcher m = Pattern.compile("\\d+").matcher(text);
        while (m.find()) numbers.add(m.group());

        if (q.getQuestionType().equals("SEQUENCE")) {
            lblInstruction.setText("<html><center>Lengkapi Urutan Angka Ini:</center></html>");
            for (String num : numbers) {
                container.add(new NumberBox(num, COLOR_BOX_YELLOW));
            }
            container.add(new NumberBox("?", COLOR_BOX_GRAY));

        } else {
            // Comparison
            lblInstruction.setText("<html><center>" + text + "</center></html>");
            if (!numbers.isEmpty()) {
                String targetNum = numbers.get(numbers.size() - 1);
                container.add(new NumberBox(targetNum, COLOR_BOX_BLUE));
            }
        }
    }

    // --- RENDERER STANDARD (Level 3 / Default) ---
    private static void renderStandard(JPanel container, JLabel lblInstruction, QuestionModel q) {
        lblInstruction.setText("<html><center>" + q.getQuestionText() + "</center></html>");
        
        if (q.getQuestionImage() != null && !q.getQuestionImage().isEmpty()) {
            String path = "/images/" + q.getQuestionImage();
            java.net.URL imgURL = GameVisualizer.class.getResource(path);
            
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage();
                Image newImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                container.add(new JLabel(new ImageIcon(newImg)));
            }
        }
    }

    // --- INNER CLASS VISUAL COMPONENT ---
    static class NumberBox extends JPanel {
        private String text;
        private Color bgColor;

        public NumberBox(String text, Color bgColor) {
            this.text = text;
            this.bgColor = bgColor;
            setOpaque(false);
            setPreferredSize(new Dimension(100, 100));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0,0,0,30));
            g2.fillRoundRect(8, 8, 90, 90, 25, 25);

            g2.setColor(bgColor);
            g2.fillRoundRect(5, 5, 90, 90, 25, 25);
            
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(5, 5, 90, 90, 25, 25);

            g2.setColor(Color.WHITE);
            g2.setFont(FONT_BIG);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            
            g2.setColor(new Color(0,0,0,50));
            g2.drawString(text, x+1, y+1);
            g2.setColor(Color.WHITE);
            g2.drawString(text, x, y);
        }
    }
}