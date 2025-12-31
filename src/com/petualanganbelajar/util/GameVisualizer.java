package com.petualanganbelajar.util;

import com.petualanganbelajar.model.QuestionModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class GameVisualizer {

    private static final Font FONT_BIG = new Font("Comic Sans MS", Font.BOLD, 40);
    private static final Font FONT_MATH = new Font("Comic Sans MS", Font.BOLD, 60); 
    
    // Warna Background default untuk Angka/Huruf/Bentuk (Warni-Warni Ceria)
    private static final Color[] BOX_COLORS = {
        new Color(255, 107, 107), new Color(78, 205, 196),
        new Color(255, 217, 61), new Color(84, 160, 255), new Color(255, 159, 67)
    };
    private static final Color COLOR_BOX_GRAY = new Color(220, 220, 220);

    public static void render(JPanel container, JLabel lblInstruction, QuestionModel q, int moduleId, int level) {
        container.removeAll();
        container.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        if (moduleId == 1) { // ANGKA
            if (level == 1) renderMathLevel1(container, lblInstruction, q);
            else if (level == 2) renderMathLevel2(container, lblInstruction, q);
            else if (level == 3) renderMathLevel3(container, lblInstruction, q);
            else renderStandard(container, lblInstruction, q);
        }
        else if (moduleId == 2) { // HURUF
            if (level == 2) renderMathLevel2(container, lblInstruction, q); 
            else if (level == 3) renderLetterLevel3(container, lblInstruction, q);
            else renderStandard(container, lblInstruction, q);
        }
        else if (moduleId == 3) { // WARNA
            if (level == 2) lblInstruction.setText("<html><center>" + q.getQuestionText() + "</center></html>");
            else if (level == 3) renderMathLevel3(container, lblInstruction, q); 
            else renderStandard(container, lblInstruction, q);
        }
        else if (moduleId == 4) { // BENTUK
            // Cek jika ini soal Sequence/Pola (ada '##')
            if (q.getQuestionText().contains("##")) {
                renderSequence(container, lblInstruction, q.getQuestionText());
            } else {
                renderStandard(container, lblInstruction, q);
            }
        }
        else {
            renderStandard(container, lblInstruction, q);
        }

        container.revalidate();
        container.repaint();
    }
    
    // --- RENDER SEQUENCE (MODUL 1 & 4) ---
    private static void renderSequence(JPanel container, JLabel lblInstruction, String rawText) {
        String[] parts = rawText.split("##");
        if (parts.length > 0) lblInstruction.setText("<html><center>" + parts[0].trim() + "</center></html>");
        
        if (parts.length > 1) {
            String[] items = parts[1].trim().split(",");
            container.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
            
            for (int i = 0; i < items.length; i++) {
                String item = items[i].trim();
                
                // Item Kosong / Pertanyaan
                if (item.equals("?") || item.equals("_")) {
                    NumberBox box = new NumberBox("?", COLOR_BOX_GRAY);
                    box.setTextColor(Color.GRAY);
                    container.add(box);
                } else {
                    // Item Isi (Kembali ke Warni-Warni Ceria)
                    Color bg = BOX_COLORS[i % BOX_COLORS.length];
                    NumberBox box = new NumberBox("", bg);
                    
                    if (item.startsWith("SHAPE:") || item.endsWith(".png")) {
                        box.setIconItem(item); 
                    } else {
                        box.setText(item);
                    }
                    container.add(box);
                }
            }
        }
    }

    // --- RENDER SEQUENCE MULTI (KHUSUS HURUF) ---
    public static void renderSequenceMulti(JPanel container, JLabel lblInstruction, String rawText) {
        container.removeAll();
        String[] parts = rawText.split("##");
        if (parts.length >= 2) {
            lblInstruction.setText("<html><center>" + parts[0].trim() + "</center></html>");
            String pattern = parts[1].trim();
            String[] items = pattern.split("[,\\s]+"); 
            container.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
            for (int i = 0; i < items.length; i++) {
                String item = items[i].trim();
                if (item.equals("_") || item.equals("?")) {
                    NumberBox box = new NumberBox("?", Color.WHITE);
                    box.setTextColor(Color.BLACK); 
                    container.add(box); 
                } else {
                    Color c = BOX_COLORS[i % BOX_COLORS.length];
                    container.add(new NumberBox(item, c));
                }
            }
        }
        container.revalidate();
        container.repaint();
    }

    // --- RENDER STANDARD (Level 1 / Level 2 Awal) ---
    private static void renderStandard(JPanel container, JLabel lblInstruction, QuestionModel q) {
        lblInstruction.setText("<html><center>" + q.getQuestionText() + "</center></html>");
        
        String imgStr = q.getQuestionImage();
        if (imgStr != null && !imgStr.isEmpty()) {
            if (imgStr.startsWith("SHAPE:")) {
                 // Kotak Besar untuk Level 1 / Level 2
                 NumberBox box = new NumberBox("", Color.WHITE); 
                 box.setPreferredSize(new Dimension(200, 200)); // Ukuran Besar
                 box.setIconItem(imgStr, 120); // Icon Besar
                 container.add(box);
            } 
            else if (imgStr.startsWith("SILHOUETTE:")) {
                 String realFile = imgStr.replace("SILHOUETTE:", "");
                 ImageIcon icon = UIHelper.loadIcon(realFile, 200, 200);
                 if(icon != null) {
                     ImageIcon sil = createSilhouette(icon.getImage(), 200);
                     container.add(new JLabel(sil));
                 }
            }
            else {
                 ImageIcon icon = UIHelper.loadIcon(imgStr, 200, 200);
                 if(icon != null) container.add(new JLabel(icon));
            }
        }
    }

    // --- MATH & LAINNYA ---
    private static void renderMathLevel1(JPanel container, JLabel lblInstruction, QuestionModel q) {
        lblInstruction.setText("<html><center>" + q.getQuestionText() + "</center></html>");
        int count = 1; try { count = Integer.parseInt(q.getCorrectAnswer()); } catch (Exception e) {}
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5, 10, 5, 10);
        ImageIcon icon = UIHelper.loadIcon(q.getQuestionImage(), 120, 120);
        for (int i = 0; i < count; i++) {
            JLabel label = (icon != null) ? new JLabel(icon) : createTextLabel("?");
            gbc.gridx = i % 5; gbc.gridy = i / 5; container.add(label, gbc);
        }
    }

    private static void renderMathLevel2(JPanel container, JLabel lblInstruction, QuestionModel q) {
        String rawText = q.getQuestionText();
        if (rawText.contains(".png")) renderComparisonVisual(container, lblInstruction, rawText);
        else if (q.getQuestionType().toString().equals("SEQUENCE_MULTI")) {
            String[] parts = rawText.split("##");
            renderSequenceMulti(container, lblInstruction, parts[0] + " ## " + parts[1]);
        }
        else if (rawText.contains("##")) renderSequence(container, lblInstruction, rawText);
        else lblInstruction.setText("<html><center>" + rawText + "</center></html>");
    }

    private static void renderComparisonVisual(JPanel container, JLabel lblInstruction, String rawText) {
        String[] parts = rawText.split("##");
        lblInstruction.setText("<html><center>" + parts[0].trim() + "</center></html>");
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 0.5; gbc.weighty = 1.0; gbc.insets = new Insets(0, 10, 0, 10);
        
        // Panggil helper yang sudah di-update untuk visual balance
        gbc.gridx = 0; container.add(createItemPanel(parts[1].trim(), new Color(230, 240, 255)), gbc);
        gbc.gridx = 1; container.add(createItemPanel(parts[2].trim(), new Color(255, 230, 230)), gbc);
    }

    // [UPDATED] Render Math Level 3 & Color Level 3 (Support 3 Bahan)
    private static void renderMathLevel3(JPanel container, JLabel lblInstruction, QuestionModel q) {
        String[] parts = q.getQuestionText().split("##");
        
        if (parts.length >= 2) {
            lblInstruction.setText("<html><center>" + parts[0].trim() + "</center></html>");
            container.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            
            // Loop untuk membaca semua input (Bisa 2 atau 3 bahan)
            // Format: Narasi ## Item1 ## + ## Item2 ## + ## Item3
            for (int i = 1; i < parts.length; i++) {
                String p = parts[i].trim();
                
                if (p.equals("+")) {
                    JLabel lblOp = new JLabel("+"); 
                    lblOp.setFont(FONT_MATH); 
                    lblOp.setForeground(Color.DARK_GRAY);
                    container.add(lblOp);
                } else {
                    container.add(createMathItem(p));
                }
            }
            
            JLabel lblEq = new JLabel("="); 
            lblEq.setFont(FONT_MATH); 
            lblEq.setForeground(Color.DARK_GRAY);
            container.add(lblEq); 
            
            NumberBox resBox = new NumberBox("?", Color.GRAY); 
            resBox.setTextColor(Color.WHITE); 
            container.add(resBox); 
            
        } else {
            renderStandard(container, lblInstruction, q);
        }
    }

    private static Component createMathItem(String code) {
        if (code.startsWith("SHAPE:")) {
            NumberBox box = new NumberBox("", Color.WHITE);
            box.setIconItem(code);
            return box;
        } 
        else if (code.toLowerCase().endsWith(".png") || code.toLowerCase().endsWith(".jpg")) {
            ImageIcon icon = UIHelper.loadIcon(code, 120, 120); 
            if (icon != null) return new JLabel(icon);
            else return createTextLabel("?");
        } else {
            // Jika formatnya "file.png:angka", gunakan createItemPanel
            return createItemPanel(code, new Color(230, 255, 230));
        }
    }

    private static void renderLetterLevel3(JPanel container, JLabel lblInstruction, QuestionModel q) {
        String[] parts = q.getQuestionText().split("##");
        if (parts.length >= 3) {
            lblInstruction.setText("<html><center>" + parts[0].trim() + "</center></html>");
            container.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0; gbc.gridy = 0;
            ImageIcon icon = UIHelper.loadIcon(parts[1].trim(), 180, 180);
            if (icon != null) container.add(new JLabel(icon), gbc);
            gbc.gridy = 1;
            JPanel puzzlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            puzzlePanel.setOpaque(false);
            String[] chars = parts[2].trim().split(" ");
            for (int i = 0; i < chars.length; i++) {
                if (chars[i].equals("_")) {
                    NumberBox box = new NumberBox("?", Color.WHITE); box.setTextColor(Color.BLACK); puzzlePanel.add(box); 
                } else {
                    puzzlePanel.add(new NumberBox(chars[i], BOX_COLORS[i % BOX_COLORS.length])); 
                }
            }
            container.add(puzzlePanel, gbc);
        } else renderStandard(container, lblInstruction, q);
    }

    // [UPDATED] Create Item Panel (Visual Balance Logic)
    private static JPanel createItemPanel(String data, Color bg) {
        String[] d = data.split(":"); 
        if (d.length < 2) return new JPanel(); 
        
        String imgFile = d[0];
        int count = Integer.parseInt(d[1]);
        
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setBackground(bg);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 4), 
            new EmptyBorder(15, 15, 15, 15) // Padding lebih lega
        ));
        
        // Logika Layout Dinamis
        int iconSize;
        if (count <= 3) {
            p.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
            iconSize = 90; // Besar
        } else if (count <= 6) {
            p.setLayout(new GridLayout(2, 3, 5, 5)); // Grid 2 Baris
            iconSize = 70; // Sedang
        } else {
            p.setLayout(new GridLayout(2, 5, 5, 5)); // Grid Padat
            iconSize = 55; // Kecil
        }
        
        ImageIcon icon = UIHelper.loadIcon(imgFile, iconSize, iconSize);
        for(int i=0; i<count; i++) {
            if (icon != null) {
                JLabel l = new JLabel(icon);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                p.add(l);
            } else {
                p.add(createTextLabel("?"));
            }
        }
        
        // Isi kekosongan grid agar tidak melar
        if(count > 3) {
             int cells = (count <= 6) ? 6 : 10;
             for(int k=0; k < cells - count; k++) p.add(new JLabel(""));
        }
        
        return p;
    }

    private static ImageIcon createSilhouette(Image originalImage, int size) {
        BufferedImage dyed = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dyed.createGraphics();
        g.drawImage(originalImage, 0, 0, size, size, null);
        g.setComposite(AlphaComposite.SrcIn);
        g.setColor(new Color(220, 220, 220)); g.fillRect(0, 0, size, size);
        g.dispose(); return new ImageIcon(dyed);
    }

    private static JLabel createTextLabel(String txt) { JLabel l = new JLabel(txt); l.setFont(FONT_BIG); return l; }
    
    // --- INNER CLASS: NUMBER BOX (KOTAK WARNI-WARNI) ---
    static class NumberBox extends JPanel {
        private String text; 
        private Color bgColor;
        private Color textColor = Color.WHITE;
        private ImageIcon iconItem = null;

        public NumberBox(String text, Color bgColor) { 
            this.text = text; this.bgColor = bgColor; 
            setOpaque(false); setPreferredSize(new Dimension(90, 90)); 
        }
        
        public void setTextColor(Color c) { this.textColor = c; }
        public void setText(String t) { this.text = t; }
        
        public void setIconItem(String code) { setIconItem(code, 60); } // Default size
        
        public void setIconItem(String code, int size) {
            if (code.startsWith("SHAPE:")) this.iconItem = UIHelper.generateShapeIcon(code, size);
            else this.iconItem = UIHelper.loadIcon(code, size, size);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D)g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Bayangan
            g2.setColor(new Color(0,0,0,30)); g2.fillRoundRect(6,6,getWidth()-12,getHeight()-12,25,25);
            // Kotak Utama
            g2.setColor(bgColor); g2.fillRoundRect(2,2,getWidth()-8,getHeight()-8,25,25);
            // Border Putih Tebal (Agar menonjol)
            g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(3)); g2.drawRoundRect(2,2,getWidth()-8,getHeight()-8,25,25);
            
            if (iconItem != null) {
                int ix = (getWidth() - iconItem.getIconWidth()) / 2;
                int iy = (getHeight() - iconItem.getIconHeight()) / 2;
                iconItem.paintIcon(this, g2, ix, iy);
            } else if (text != null && !text.isEmpty()) {
                g2.setFont(FONT_BIG); FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()-fm.stringWidth(text))/2; int y = (getHeight()-fm.getHeight())/2 + fm.getAscent();
                g2.setColor(textColor); g2.drawString(text, x, y);
            }
        }
    }
}