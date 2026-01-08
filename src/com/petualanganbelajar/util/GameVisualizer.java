package com.petualanganbelajar.util;

import com.petualanganbelajar.model.QuestionModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GameVisualizer {

    // Font Statis
    private static final Font FONT_BIG_BASE = new Font("Comic Sans MS", Font.BOLD, 40);
    private static final Font FONT_MATH_BASE = new Font("Comic Sans MS", Font.BOLD, 60); 
    
    private static final Color[] BOX_COLORS = {
        new Color(255, 107, 107), new Color(78, 205, 196),
        new Color(255, 217, 61), new Color(84, 160, 255), new Color(255, 159, 67)
    };
    private static final Color COLOR_BOX_GRAY = new Color(220, 220, 220);

    public static void render(JPanel container, JLabel lblInstruction, QuestionModel q, int moduleId, int level, float scale) {
        container.removeAll();
        int gap = (int)(20 * scale);
        container.setLayout(new FlowLayout(FlowLayout.CENTER, gap, gap));

        if (moduleId == 1) { // ANGKA
            if (level == 1) renderMathLevel1(container, lblInstruction, q, scale);
            else if (level == 2) renderMathLevel2(container, lblInstruction, q, scale);
            else if (level == 3) renderMathLevel3(container, lblInstruction, q, scale);
            else renderStandard(container, lblInstruction, q, scale);
        }
        else if (moduleId == 2) { // HURUF
            if (level == 2) renderMathLevel2(container, lblInstruction, q, scale); 
            else if (level == 3) renderLetterLevel3(container, lblInstruction, q, scale);
            else renderStandard(container, lblInstruction, q, scale);
        }
        else if (moduleId == 3) { // WARNA
            if (level == 2) lblInstruction.setText("<html><center>" + q.getQuestionText() + "</center></html>");
            else if (level == 3) renderMathLevel3(container, lblInstruction, q, scale); 
            else renderStandard(container, lblInstruction, q, scale);
        }
        else if (moduleId == 4) { // BENTUK
            if (q.getQuestionText().contains("##")) {
                renderSequence(container, lblInstruction, q.getQuestionText(), scale);
            } else {
                renderStandard(container, lblInstruction, q, scale);
            }
        }
        else {
            renderStandard(container, lblInstruction, q, scale);
        }

        container.revalidate();
        container.repaint();
    }
    
    // --- RENDER SEQUENCE ---
    private static void renderSequence(JPanel container, JLabel lblInstruction, String rawText, float scale) {
        String[] parts = rawText.split("##");
        if (parts.length > 0) lblInstruction.setText("<html><center>" + parts[0].trim() + "</center></html>");
        
        if (parts.length > 1) {
            String[] items = parts[1].trim().split(",");
            int gapH = (int)(15 * scale);
            int gapV = (int)(10 * scale);
            container.setLayout(new FlowLayout(FlowLayout.CENTER, gapH, gapV));
            
            for (int i = 0; i < items.length; i++) {
                String item = items[i].trim();
                
                if (item.equals("?") || item.equals("_")) {
                    NumberBox box = new NumberBox("?", COLOR_BOX_GRAY, scale);
                    box.setTextColor(Color.GRAY);
                    container.add(box);
                } else {
                    Color bg = BOX_COLORS[i % BOX_COLORS.length];
                    NumberBox box = new NumberBox("", bg, scale);
                    
                    if (item.startsWith("SHAPE:") || item.endsWith(".png")) {
                        // [SIZE UP] Icon size untuk sequence (60 -> 100)
                        box.setIconItem(item, (int)(100 * scale)); 
                        // [SIZE UP] Box size (90 -> 140)
                        int boxSize = (int)(140 * scale);
                        box.setPreferredSize(new Dimension(boxSize, boxSize));
                    } else {
                        box.setText(item);
                    }
                    container.add(box);
                }
            }
        }
    }

    // --- RENDER SEQUENCE MULTI ---
    public static void renderSequenceMulti(JPanel container, JLabel lblInstruction, String rawText, float scale) {
        container.removeAll();
        String[] parts = rawText.split("##");
        if (parts.length >= 2) {
            lblInstruction.setText("<html><center>" + parts[0].trim() + "</center></html>");
            String pattern = parts[1].trim();
            String[] items = pattern.split("[,\\s]+"); 
            
            int gapH = (int)(15 * scale);
            int gapV = (int)(10 * scale);
            container.setLayout(new FlowLayout(FlowLayout.CENTER, gapH, gapV));
            
            for (int i = 0; i < items.length; i++) {
                String item = items[i].trim();
                if (item.equals("_") || item.equals("?")) {
                    NumberBox box = new NumberBox("?", Color.WHITE, scale);
                    box.setTextColor(Color.BLACK); 
                    container.add(box); 
                } else {
                    Color c = BOX_COLORS[i % BOX_COLORS.length];
                    container.add(new NumberBox(item, c, scale));
                }
            }
        }
        container.revalidate();
        container.repaint();
    }

    // --- RENDER STANDARD (Soal Gambar Tunggal) ---
    private static void renderStandard(JPanel container, JLabel lblInstruction, QuestionModel q, float scale) {
        lblInstruction.setText("<html><center>" + q.getQuestionText() + "</center></html>");
        
        String imgStr = q.getQuestionImage();
        if (imgStr != null && !imgStr.isEmpty()) {
            // [SIZE UP] Ukuran dasar diperbesar drastis (300 -> 500)
            int standardSize = (int)(500 * scale); 
            
            if (imgStr.startsWith("SHAPE:")) {
                 NumberBox box = new NumberBox("", Color.WHITE, scale); 
                 box.setPreferredSize(new Dimension(standardSize, standardSize)); 
                 box.setIconItem(imgStr, (int)(350 * scale)); // Icon shape di dalam box
                 container.add(box);
            } 
            else if (imgStr.startsWith("SILHOUETTE:")) {
                 String realFile = imgStr.replace("SILHOUETTE:", "");
                 ImageIcon icon = UIHelper.loadIcon(realFile, standardSize, standardSize);
                 if(icon != null) {
                     ImageIcon sil = createSilhouette(icon.getImage(), standardSize);
                     container.add(new JLabel(sil));
                 }
            }
            else {
                 ImageIcon icon = UIHelper.loadIcon(imgStr, standardSize, standardSize);
                 if(icon != null) container.add(new JLabel(icon));
            }
        }
    }

    // --- MATH LEVEL 1 ---
    private static void renderMathLevel1(JPanel container, JLabel lblInstruction, QuestionModel q, float scale) {
        lblInstruction.setText("<html><center>" + q.getQuestionText() + "</center></html>");
        int count = 1; try { count = Integer.parseInt(q.getCorrectAnswer()); } catch (Exception e) {}
        
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.insets = new Insets((int)(5*scale), (int)(10*scale), (int)(5*scale), (int)(10*scale));
        
        // [SIZE UP] Ukuran item hitungan (120 -> 150)
        int itemSize = (int)(150 * scale);
        ImageIcon icon = UIHelper.loadIcon(q.getQuestionImage(), itemSize, itemSize);
        
        for (int i = 0; i < count; i++) {
            JLabel label = (icon != null) ? new JLabel(icon) : createTextLabel("?", scale);
            gbc.gridx = i % 5; gbc.gridy = i / 5; container.add(label, gbc);
        }
    }

    // --- MATH LEVEL 2 ---
    private static void renderMathLevel2(JPanel container, JLabel lblInstruction, QuestionModel q, float scale) {
        String rawText = q.getQuestionText();
        if (rawText.contains(".png")) renderComparisonVisual(container, lblInstruction, rawText, scale);
        else if (q.getQuestionType().toString().equals("SEQUENCE_MULTI")) {
            String[] parts = rawText.split("##");
            renderSequenceMulti(container, lblInstruction, parts[0] + " ## " + parts[1], scale);
        }
        else if (rawText.contains("##")) renderSequence(container, lblInstruction, rawText, scale);
        else lblInstruction.setText("<html><center>" + rawText + "</center></html>");
    }

    private static void renderComparisonVisual(JPanel container, JLabel lblInstruction, String rawText, float scale) {
        String[] parts = rawText.split("##");
        lblInstruction.setText("<html><center>" + parts[0].trim() + "</center></html>");
        
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; 
        gbc.weightx = 0.5; gbc.weighty = 1.0; 
        gbc.insets = new Insets(0, (int)(20*scale), 0, (int)(20*scale)); // Margin antar panel diperlebar
        
        gbc.gridx = 0; container.add(createItemPanel(parts[1].trim(), new Color(230, 240, 255), scale), gbc);
        gbc.gridx = 1; container.add(createItemPanel(parts[2].trim(), new Color(255, 230, 230), scale), gbc);
    }

    // --- MATH LEVEL 3 ---
    private static void renderMathLevel3(JPanel container, JLabel lblInstruction, QuestionModel q, float scale) {
        String[] parts = q.getQuestionText().split("##");
        
        if (parts.length >= 2) {
            lblInstruction.setText("<html><center>" + parts[0].trim() + "</center></html>");
            container.setLayout(new FlowLayout(FlowLayout.CENTER, (int)(10*scale), (int)(10*scale)));
            
            for (int i = 1; i < parts.length; i++) {
                String p = parts[i].trim();
                
                if (p.equals("+") || p.equals("-")) {
                    JLabel lblOp = new JLabel(p); 
                    lblOp.setFont(FONT_MATH_BASE.deriveFont((float)(80 * scale))); // Font operator diperbesar
                    lblOp.setForeground(Color.DARK_GRAY);
                    container.add(lblOp);
                } else {
                    container.add(createMathItem(p, scale));
                }
            }
            
            JLabel lblEq = new JLabel("="); 
            lblEq.setFont(FONT_MATH_BASE.deriveFont((float)(80 * scale))); 
            lblEq.setForeground(Color.DARK_GRAY);
            container.add(lblEq); 
            
            NumberBox resBox = new NumberBox("?", Color.GRAY, scale); 
            resBox.setTextColor(Color.WHITE); 
            container.add(resBox); 
            
        } else {
            renderStandard(container, lblInstruction, q, scale);
        }
    }

    private static Component createMathItem(String code, float scale) {
        if (code.startsWith("SHAPE:")) {
            NumberBox box = new NumberBox("", Color.WHITE, scale);
            // [SIZE UP] Icon shape math (60 -> 100)
            box.setIconItem(code, (int)(100 * scale));
            return box;
        } 
        else if (code.toLowerCase().endsWith(".png") || code.toLowerCase().endsWith(".jpg")) {
            // [SIZE UP] Image math item (120 -> 180)
            ImageIcon icon = UIHelper.loadIcon(code, (int)(180*scale), (int)(180*scale)); 
            if (icon != null) return new JLabel(icon);
            else return createTextLabel("?", scale);
        } else {
            return createItemPanel(code, new Color(230, 255, 230), scale);
        }
    }

    // --- LETTER LEVEL 3 ---
    private static void renderLetterLevel3(JPanel container, JLabel lblInstruction, QuestionModel q, float scale) {
        String[] parts = q.getQuestionText().split("##");
        if (parts.length >= 3) {
            lblInstruction.setText("<html><center>" + parts[0].trim() + "</center></html>");
            container.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets((int)(10*scale), (int)(10*scale), (int)(10*scale), (int)(10*scale));
            
            gbc.gridx = 0; gbc.gridy = 0;
            // [SIZE UP] Clue Image (180 -> 300) -- Ini untuk soal Zebra
            ImageIcon icon = UIHelper.loadIcon(parts[1].trim(), (int)(300*scale), (int)(300*scale));
            if (icon != null) container.add(new JLabel(icon), gbc);
            
            gbc.gridy = 1;
            JPanel puzzlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, (int)(10*scale), 0));
            puzzlePanel.setOpaque(false);
            
            String[] chars = parts[2].trim().split(" ");
            for (int i = 0; i < chars.length; i++) {
                if (chars[i].equals("_")) {
                    NumberBox box = new NumberBox("?", Color.WHITE, scale); box.setTextColor(Color.BLACK); puzzlePanel.add(box); 
                } else {
                    puzzlePanel.add(new NumberBox(chars[i], BOX_COLORS[i % BOX_COLORS.length], scale)); 
                }
            }
            container.add(puzzlePanel, gbc);
        } else renderStandard(container, lblInstruction, q, scale);
    }

    // --- CREATE ITEM PANEL (GRID KECIL) ---
    private static JPanel createItemPanel(String data, Color bg, float scale) {
        String[] d = data.split(":"); 
        if (d.length < 2) return new JPanel(); 
        
        String imgFile = d[0];
        int count = Integer.parseInt(d[1]);
        
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setBackground(bg);
        int pad = (int)(20 * scale);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, (int)(6*scale)), 
            new EmptyBorder(pad, pad, pad, pad) 
        ));
        
        // Logika Layout Dinamis (Ukuran icon di-scale lebih besar)
        int iconSize;
        int gap = (int)(10 * scale);
        if (count <= 3) {
            p.setLayout(new FlowLayout(FlowLayout.CENTER, pad, pad));
            iconSize = (int)(130 * scale); // [SIZE UP] 90 -> 130
        } else if (count <= 6) {
            p.setLayout(new GridLayout(2, 3, gap, gap)); 
            iconSize = (int)(100 * scale); // [SIZE UP] 70 -> 100
        } else {
            p.setLayout(new GridLayout(2, 5, gap, gap)); 
            iconSize = (int)(80 * scale); // [SIZE UP] 55 -> 80
        }
        
        ImageIcon icon = UIHelper.loadIcon(imgFile, iconSize, iconSize);
        for(int i=0; i<count; i++) {
            if (icon != null) {
                JLabel l = new JLabel(icon);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                p.add(l);
            } else {
                p.add(createTextLabel("?", scale));
            }
        }
        
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

    private static JLabel createTextLabel(String txt, float scale) { 
        JLabel l = new JLabel(txt); 
        l.setFont(FONT_BIG_BASE.deriveFont((float)(50 * scale))); // Font text label lebih besar
        return l; 
    }
    
    // --- INNER CLASS: NUMBER BOX (Responsive) ---
    static class NumberBox extends JPanel {
        private String text; 
        private Color bgColor;
        private Color textColor = Color.WHITE;
        private ImageIcon iconItem = null;
        private float scale;

        public NumberBox(String text, Color bgColor, float scale) { 
            this.text = text; this.bgColor = bgColor; this.scale = scale;
            setOpaque(false); 
            // [SIZE UP] Ukuran box (90 -> 120)
            int boxSize = (int)(120 * scale);
            setPreferredSize(new Dimension(boxSize, boxSize)); 
        }
        
        public void setTextColor(Color c) { this.textColor = c; }
        public void setText(String t) { this.text = t; }
        
        public void setIconItem(String code, int size) {
            if (code.startsWith("SHAPE:")) this.iconItem = UIHelper.generateShapeIcon(code, size);
            else this.iconItem = UIHelper.loadIcon(code, size, size);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D)g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(); int h = getHeight();
            int arc = (int)(30 * scale);
            int border = (int)(4 * scale);
            int shadowOff = (int)(6 * scale);
            
            // Bayangan
            g2.setColor(new Color(0,0,0,30)); g2.fillRoundRect(shadowOff, shadowOff, w-shadowOff*2, h-shadowOff*2, arc, arc);
            // Kotak Utama
            g2.setColor(bgColor); g2.fillRoundRect(2,2,w-8,h-8,arc,arc);
            // Border Putih
            g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(border)); g2.drawRoundRect(2,2,w-8,h-8,arc,arc);
            
            if (iconItem != null) {
                int ix = (w - iconItem.getIconWidth()) / 2;
                int iy = (h - iconItem.getIconHeight()) / 2;
                iconItem.paintIcon(this, g2, ix, iy);
            } else if (text != null && !text.isEmpty()) {
                // Font di dalam box (40 -> 60)
                g2.setFont(FONT_BIG_BASE.deriveFont((float)(60 * scale))); 
                FontMetrics fm = g2.getFontMetrics();
                int x = (w-fm.stringWidth(text))/2; 
                int y = (h-fm.getHeight())/2 + fm.getAscent();
                g2.setColor(textColor); g2.drawString(text, x, y);
            }
        }
    }
}