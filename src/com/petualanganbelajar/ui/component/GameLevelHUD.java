package com.petualanganbelajar.ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class GameLevelHUD extends JPanel {
    private String moduleName = "";
    private String levelText = "";
    
    // Warna Default
    private Color bgTop = new Color(135, 206, 250);
    private Color bgBottom = new Color(0, 191, 255);
    private Color titleOutline = new Color(25, 25, 112); 
    private Color titleColor = Color.WHITE; // [BARU] Variabel warna teks

    public GameLevelHUD() {
        setOpaque(false);
        setPreferredSize(new Dimension(320, 90));
    }

    // [UPDATE] Menambahkan parameter 'textColor'
    public void setTheme(Color top, Color bottom, Color outline, Color textColor) {
        this.bgTop = top;
        this.bgBottom = bottom;
        this.titleOutline = outline;
        this.titleColor = textColor;
        repaint();
    }

    public void setInfo(String name, int lvl) {
        this.moduleName = (name != null) ? name.toUpperCase() : "";
        this.levelText = "Level " + lvl;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int centerX = w / 2;

        // 1. Background Plaque
        int plaqueW = 280; int plaqueH = 75; int plaqueX = (w - plaqueW) / 2; int plaqueY = 5;
        RoundRectangle2D plaqueShape = new RoundRectangle2D.Float(plaqueX, plaqueY, plaqueW, plaqueH, plaqueH, plaqueH);

        g2.setColor(new Color(0,0,0,40));
        g2.fillRoundRect(plaqueX+4, plaqueY+4, plaqueW, plaqueH, plaqueH, plaqueH);

        GradientPaint bgGrad = new GradientPaint(0, plaqueY, bgTop, 0, plaqueY+plaqueH, bgBottom);
        g2.setPaint(bgGrad); g2.fill(plaqueShape);

        g2.setPaint(new GradientPaint(0, plaqueY, new Color(255,255,255,150), 0, plaqueY + plaqueH/2, new Color(255,255,255,0)));
        g2.fillOval(plaqueX+10, plaqueY+5, plaqueW-20, plaqueH/2);

        g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(4f)); g2.draw(plaqueShape);

        // 2. Title Text
        if (!moduleName.isEmpty()) {
            Font titleFont = new Font("Comic Sans MS", Font.BOLD, 36);
            g2.setFont(titleFont);
            FontRenderContext frc = g2.getFontRenderContext();
            TextLayout titleLayout = new TextLayout(moduleName, titleFont, frc);
            Shape titleShape = titleLayout.getOutline(null);
            Rectangle2D titleBounds = titleShape.getBounds2D();
            
            double titleX = centerX - (titleBounds.getWidth() / 2);
            double titleY = plaqueY + 45;

            AffineTransform transform = AffineTransform.getTranslateInstance(titleX, titleY);
            Shape transformedTitle = transform.createTransformedShape(titleShape);

            // Text Shadow
            g2.translate(3, 3); g2.setColor(new Color(0, 0, 0, 50)); g2.fill(transformedTitle); g2.translate(-3, -3);

            // Text Outline
            g2.setColor(titleOutline);
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(transformedTitle);

            // Text Fill [UPDATE] Menggunakan variable titleColor
            g2.setColor(titleColor);
            g2.fill(transformedTitle);
        }

        // 3. Subtitle Badge
        Font subFont = new Font("Comic Sans MS", Font.BOLD, 18);
        FontMetrics fmSub = g2.getFontMetrics(subFont);
        int subW = fmSub.stringWidth(levelText) + 30; int subH = 28;
        int subX = (w - subW) / 2; int subY = plaqueY + 45 + 10;

        RoundRectangle2D badgeShape = new RoundRectangle2D.Float(subX, subY, subW, subH, 15, 15);
        GradientPaint goldGrad = new GradientPaint(0, subY, new Color(255, 215, 0), 0, subY+subH, new Color(218, 165, 32));
        g2.setPaint(goldGrad); g2.fill(badgeShape);
        g2.setColor(new Color(184, 134, 11)); g2.setStroke(new BasicStroke(2f)); g2.draw(badgeShape);

        g2.setFont(subFont); g2.setColor(new Color(101, 67, 33));
        int textX = subX + (subW - fmSub.stringWidth(levelText)) / 2;
        int textY = subY + ((subH - fmSub.getHeight()) / 2) + fmSub.getAscent();
        g2.drawString(levelText, textX, textY);

        // 4. Sparkles
        g2.setColor(Color.WHITE);
        drawSparkle(g2, plaqueX - 10, plaqueY + 10, 8);
        drawSparkle(g2, plaqueX + plaqueW + 5, plaqueY + 40, 6);

        g2.dispose();
    }

    private void drawSparkle(Graphics2D g2, int x, int y, int size) {
        g2.fillOval(x, y, size, size);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(x - size, y + size/2, x + size*2, y + size/2);
        g2.drawLine(x + size/2, y - size, x + size/2, y + size*2);
    }
}