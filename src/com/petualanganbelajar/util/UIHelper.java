package com.petualanganbelajar.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.net.URL;

public class UIHelper {

    // --- LOAD ICON ---
    public static ImageIcon loadIcon(String f, int w, int h) {
        try {
            URL u = UIHelper.class.getResource("/images/" + f);
            if (u != null) return new ImageIcon(new ImageIcon(u).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        return null;
    }

    // --- LOAD RAW IMAGE ---
    public static Image loadRawImage(String f) {
        try {
            URL u = UIHelper.class.getResource("/images/" + f);
            if (u != null) return new ImageIcon(u).getImage();
        } catch (Exception e) {}
        return null;
    }

    // --- PALET WARNA CERAH (KID FRIENDLY) ---
    public static Color getColorByName(String name) {
        switch(name) {
            case "RED":    return new Color(255, 89, 94);   // Merah Coral
            case "BLUE":   return new Color(25, 130, 196);  // Biru Laut
            case "YELLOW": return new Color(255, 202, 58);  // Kuning Emas
            case "GREEN":  return new Color(138, 201, 38);  // Hijau Apel
            case "ORANGE": return new Color(255, 159, 28);  // Oranye
            case "PURPLE": return new Color(106, 76, 147);  // Ungu
            case "PINK":   return new Color(255, 112, 166); // Pink
            case "BROWN":  return new Color(109, 89, 122);  // Cokelat
            case "BLACK":  return new Color(53, 53, 53);    // Hitam
            case "WHITE":  return Color.WHITE;
            default:       return Color.GRAY;
        }
    }

    // --- GENERATOR BENTUK (DENGAN OUTLINE PUTIH TEBAL) ---
    public static ImageIcon generateShapeIcon(String code, int size) {
        String[] parts = code.split(":"); 
        String shapeType = parts.length > 1 ? parts[1] : "RECT";
        String colorName = parts.length > 2 ? parts[2] : "RED";
        Color c = getColorByName(colorName);
        
        // Buat kanvas gambar transparan
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        
        // Setting render kualitas tinggi (Anti-aliasing)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Padding agar outline tebal tidak terpotong
        int pad = 12; 
        int w = size - (pad*2);
        int h = size - (pad*2);
        int x = pad; 
        int y = pad;
        int cx = size / 2; // Center X
        int cy = size / 2; // Center Y

        Shape shape = null; // Kita tampung bentuknya disini

        switch (shapeType) {
            case "CIRCLE": 
                shape = new java.awt.geom.Ellipse2D.Double(x, y, w, h);
                break;
            case "OVAL":
                shape = new java.awt.geom.Ellipse2D.Double(x, y + h/4, w, h/2);
                break;
            case "RECT": // Persegi
                shape = new java.awt.geom.RoundRectangle2D.Double(x, y, w, h, 15, 15);
                break;
            case "RECTANGLE": // Persegi Panjang
                shape = new java.awt.geom.RoundRectangle2D.Double(x, y + h/4, w, h/2, 10, 10);
                break;
            case "TRIANGLE":
                Polygon tri = new Polygon();
                tri.addPoint(cx, y);          // Atas Tengah
                tri.addPoint(x, y + h);       // Kiri Bawah
                tri.addPoint(x + w, y + h);   // Kanan Bawah
                shape = tri;
                break;
            case "RHOMBUS": // Belah Ketupat
                Polygon rhomb = new Polygon();
                rhomb.addPoint(cx, y);        // Atas
                rhomb.addPoint(x + w, cy);    // Kanan
                rhomb.addPoint(cx, y + h);    // Bawah
                rhomb.addPoint(x, cy);        // Kiri
                shape = rhomb;
                break;
            case "TRAPEZOID": // Trapesium
                Polygon trap = new Polygon();
                trap.addPoint(x + w/4, y + h/4);  // Kiri Atas (Mulai agak bawah biar center)
                trap.addPoint(x + w*3/4, y + h/4);// Kanan Atas
                trap.addPoint(x + w, y + h*3/4);  // Kanan Bawah
                trap.addPoint(x, y + h*3/4);      // Kiri Bawah
                shape = trap;
                break;
            case "PARALLELOGRAM": // Jajar Genjang
                Polygon para = new Polygon();
                para.addPoint(x + w/4, y + h/4);
                para.addPoint(x + w, y + h/4);
                para.addPoint(x + w*3/4, y + h*3/4);
                para.addPoint(x, y + h*3/4);
                shape = para;
                break;
            case "SEMICIRCLE": // Setengah Lingkaran
                shape = new java.awt.geom.Arc2D.Double(x, y + h/4, w, h, 0, 180, java.awt.geom.Arc2D.CHORD);
                break;
            case "HEART":
                shape = createHeartShape(x, y, w, h);
                break;
            case "STAR":
                shape = createStarShape(cx, cy, w/2, w/4, 5);
                break;
            case "PENTAGON":
                shape = createPolygonShape(cx, cy, w/2, 5);
                break;
            case "HEXAGON":
                shape = createPolygonShape(cx, cy, w/2, 6);
                break;
            case "OCTAGON":
                shape = createPolygonShape(cx, cy, w/2, 8);
                break;
            
            // --- 3D Fallback (Digambar manual jika shape null) ---
            case "CUBE": drawCube(g2, x, y, w, h, c); break;
            case "CUBOID": drawCube(g2, x, y + h/4, w, h/2, c); break;
            case "CYLINDER": drawCylinder(g2, x + w/4, y, w/2, h, c); break;
            
            default: // Fallback ke Kotak
                shape = new Rectangle(x, y, w, h);
        }

        // --- TEKNIK STIKER: OUTLINE PUTIH TEBAL ---
        if (shape != null) {
            // 1. Gambar Outline Putih Tebal (Sebagai pemisah kontras)
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(shape);

            // 2. Isi Warna Bentuk
            g2.setColor(c);
            g2.fill(shape);

            // 3. Gambar Garis Tepi Tipis (Opsional, agar lebih rapi)
            g2.setColor(new Color(0,0,0,30)); // Hitam transparan tipis
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(shape);
        }
        
        g2.dispose();
        return new ImageIcon(img);
    }

    // --- HELPER SHAPE GEOMETRI (PATH2D) ---

    private static Shape createStarShape(double centerX, double centerY, double outerRadius, double innerRadius, int numRays) {
        Path2D path = new Path2D.Double();
        double angleStep = Math.PI / numRays;
        for (int i = 0; i < numRays * 2; i++) {
            double angle = i * angleStep - Math.PI / 2; // Mulai dari atas (-90 derajat)
            double r = (i % 2 == 0) ? outerRadius : innerRadius;
            double x = centerX + Math.cos(angle) * r;
            double y = centerY + Math.sin(angle) * r;
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        path.closePath();
        return path;
    }

    private static Shape createPolygonShape(double centerX, double centerY, double radius, int sides) {
        Path2D path = new Path2D.Double();
        for (int i = 0; i < sides; i++) {
            // -Math.PI / 2 agar titik pertama di atas (jam 12), bukan di kanan
            double angle = i * 2 * Math.PI / sides - Math.PI / 2; 
            double x = centerX + Math.cos(angle) * radius;
            double y = centerY + Math.sin(angle) * radius;
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        path.closePath();
        return path;
    }

    private static Shape createHeartShape(int x, int y, int w, int h) {
        Path2D path = new Path2D.Double();
        // Kurva Bezier untuk hati yang mulus
        path.moveTo(x + w / 2.0, y + h / 3.0); // Titik tengah atas lembah
        // Lengkungan kiri atas
        path.curveTo(x + w / 2.0, y, x, y, x, y + h / 3.0); 
        // Garis ke bawah
        path.curveTo(x, y + h * 0.6, x + w / 2.0, y + h * 0.9, x + w / 2.0, y + h); 
        // Garis ke kanan atas
        path.curveTo(x + w / 2.0, y + h * 0.9, x + w, y + h * 0.6, x + w, y + h / 3.0); 
        // Lengkungan kanan atas
        path.curveTo(x + w, y, x + w / 2.0, y, x + w / 2.0, y + h / 3.0); 
        path.closePath();
        return path;
    }

    // --- HELPER 3D (DIGAMBAR LANGSUNG) ---
    // 3D juga kita kasih outline putih manual biar konsisten
    private static void drawCube(Graphics2D g, int x, int y, int w, int h, Color c) {
        // Outline Putih Background
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Color.WHITE);
        g.drawRect(x + w/4, y + h/4, w - w/4, h - h/4); // Kotak Depan dummy outline
        
        // Gambar Asli
        int depth = w / 4;
        int fw = w - depth; int fh = h - depth;
        g.setStroke(new BasicStroke(1.5f));
        
        // Belakang
        g.setColor(c.darker()); g.fillRect(x + depth, y, fw, fh);
        // Depan
        g.setColor(c); g.fillRect(x, y + depth, fw, fh);
        // Garis
        g.setColor(new Color(0,0,0,50));
        g.drawRect(x + depth, y, fw, fh);
        g.drawRect(x, y + depth, fw, fh);
        g.drawLine(x, y + depth, x + depth, y);
        g.drawLine(x + fw, y + depth, x + w, y);
        g.drawLine(x + fw, y + depth + fh, x + w, y + fh);
        g.drawLine(x, y + depth + fh, x + depth, y + fh);
    }

    private static void drawCylinder(Graphics2D g, int x, int y, int w, int h, Color c) {
        // Outline Putih Background (Simple block)
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(6f));
        g.drawRect(x, y + h/8, w, h - h/4);

        int ovalH = h / 4;
        g.setColor(c); g.fillRect(x, y + ovalH/2, w, h - ovalH);
        g.setColor(c); g.fillOval(x, y + h - ovalH, w, ovalH); // Bawah
        g.setColor(c.brighter()); g.fillOval(x, y, w, ovalH); // Atas
        
        // Garis
        g.setColor(new Color(0,0,0,50)); g.setStroke(new BasicStroke(1.5f));
        g.drawLine(x, y + ovalH/2, x, y + h - ovalH/2);
        g.drawLine(x + w, y + ovalH/2, x + w, y + h - ovalH/2);
        g.drawOval(x, y, w, ovalH);
        g.drawArc(x, y + h - ovalH, w, ovalH, 180, 180);
    }
}