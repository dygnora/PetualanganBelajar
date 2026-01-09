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
    private Color titleColor = Color.WHITE; 

    // [FIX UTAMA DI SINI] 
    // Jangan set ke 1.0f, set ke 0f agar updateScale(1.0f) pertama kali DIJALANKAN.
    private float currentScale = 0f; 
    
    private Font titleFont;
    private Font subFont;

    public GameLevelHUD() {
        setOpaque(false);
        // [FIX] Ini sekarang akan men-trigger setPreferredSize karena 0.0 != 1.0
        updateScale(1.0f); 
    }

    public void updateScale(float scale) {
        // Logika optimasi ini sekarang aman karena inisialisasi variabel sudah benar
        if (Math.abs(this.currentScale - scale) < 0.01f) return;
        
        this.currentScale = scale;

        // Hitung dimensi panel
        int w = (int)(320 * scale);
        int h = (int)(90 * scale);
        
        // Set ukuran agar Layout Manager (GridBagLayout) tahu
        setPreferredSize(new Dimension(w, h));
        setMinimumSize(new Dimension(w, h)); 

        // Cache Font
        titleFont = new Font("Comic Sans MS", Font.BOLD, (int)(36 * scale));
        subFont = new Font("Comic Sans MS", Font.BOLD, Math.max(12, (int)(18 * scale)));

        revalidate();
        repaint();
    }

    // ... (Sisa method setTheme, setInfo, paintComponent SAMA SEPERTI SEBELUMNYA) ...
    public void setTheme(Color top, Color bottom, Color outline, Color textColor) {
        this.bgTop = top; this.bgBottom = bottom; this.titleOutline = outline; this.titleColor = textColor; repaint();
    }
    public void setInfo(String name, int lvl) {
        this.moduleName = (name != null) ? name.toUpperCase() : ""; this.levelText = "Level " + lvl; repaint();
    }
    @Override protected void paintComponent(Graphics g) {
        // ... (Copy paste isi paintComponent yang sudah benar sebelumnya) ...
        // Kode paintComponent tidak perlu diubah, masalahnya hanya di inisialisasi size.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(); int h = getHeight(); 
        int plaqueW = (int)(280 * currentScale); int plaqueH = (int)(75 * currentScale); 
        int plaqueX = (w - plaqueW) / 2; int plaqueY = (int)(5 * currentScale);
        int arc = plaqueH; 
        
        RoundRectangle2D plaqueShape = new RoundRectangle2D.Float(plaqueX, plaqueY, plaqueW, plaqueH, arc, arc);
        g2.setColor(new Color(0,0,0,40));
        int shadowOff = (int)(4 * currentScale);
        g2.fillRoundRect(plaqueX + shadowOff, plaqueY + shadowOff, plaqueW, plaqueH, arc, arc);

        GradientPaint bgGrad = new GradientPaint(0, plaqueY, bgTop, 0, plaqueY+plaqueH, bgBottom);
        g2.setPaint(bgGrad); g2.fill(plaqueShape);

        g2.setPaint(new GradientPaint(0, plaqueY, new Color(255,255,255,150), 0, plaqueY + plaqueH/2, new Color(255,255,255,0)));
        int glossX = plaqueX + (int)(10 * currentScale); int glossW = plaqueW - (int)(20 * currentScale);
        g2.fillOval(glossX, plaqueY + (int)(5 * currentScale), glossW, plaqueH/2);

        g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(4f * currentScale)); g2.draw(plaqueShape);

        if (!moduleName.isEmpty() && titleFont != null) {
            g2.setFont(titleFont);
            FontRenderContext frc = g2.getFontRenderContext();
            TextLayout titleLayout = new TextLayout(moduleName, titleFont, frc);
            Shape titleShape = titleLayout.getOutline(null);
            Rectangle2D titleBounds = titleShape.getBounds2D();
            double titleX = (w / 2.0) - (titleBounds.getWidth() / 2);
            double titleY = plaqueY + (plaqueH * 0.65); 
            AffineTransform transform = AffineTransform.getTranslateInstance(titleX, titleY);
            Shape transformedTitle = transform.createTransformedShape(titleShape);
            int txtShadow = (int)(3 * currentScale);
            g2.translate(txtShadow, txtShadow); g2.setColor(new Color(0, 0, 0, 50)); g2.fill(transformedTitle); g2.translate(-txtShadow, -txtShadow);
            g2.setColor(titleOutline); g2.setStroke(new BasicStroke(5f * currentScale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.draw(transformedTitle);
            g2.setColor(titleColor); g2.fill(transformedTitle);
        }

        if (subFont != null) {
            g2.setFont(subFont);
            FontMetrics fmSub = g2.getFontMetrics(subFont);
            int subPadW = (int)(30 * currentScale);
            int subW = fmSub.stringWidth(levelText) + subPadW; int subH = (int)(28 * currentScale);
            int subX = (w - subW) / 2; int subY = plaqueY + plaqueH - (int)(15 * currentScale); 
            int subArc = (int)(15 * currentScale);
            RoundRectangle2D badgeShape = new RoundRectangle2D.Float(subX, subY, subW, subH, subArc, subArc);
            GradientPaint goldGrad = new GradientPaint(0, subY, new Color(255, 215, 0), 0, subY+subH, new Color(218, 165, 32));
            g2.setPaint(goldGrad); g2.fill(badgeShape);
            g2.setColor(new Color(184, 134, 11)); g2.setStroke(new BasicStroke(2f * currentScale)); g2.draw(badgeShape);
            g2.setColor(new Color(101, 67, 33));
            int textX = subX + (subW - fmSub.stringWidth(levelText)) / 2;
            int textY = subY + ((subH - fmSub.getHeight()) / 2) + fmSub.getAscent();
            g2.drawString(levelText, textX, textY);
        }

        g2.setColor(Color.WHITE);
        int spSizeBig = (int)(8 * currentScale); int spSizeSmall = (int)(6 * currentScale);
        drawSparkle(g2, plaqueX - (int)(10*currentScale), plaqueY + (int)(10*currentScale), spSizeBig);
        drawSparkle(g2, plaqueX + plaqueW + (int)(5*currentScale), plaqueY + (int)(40*currentScale), spSizeSmall);
        g2.dispose();
    }
    
    private void drawSparkle(Graphics2D g2, int x, int y, int size) {
        g2.fillOval(x, y, size, size);
        g2.setStroke(new BasicStroke(1.5f * currentScale));
        g2.drawLine(x - size, y + size/2, x + size*2, y + size/2);
        g2.drawLine(x + size/2, y - size, x + size/2, y + size*2);
    }
}