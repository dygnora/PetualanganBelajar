package com.petualanganbelajar.ui.component;

import com.petualanganbelajar.model.UserModel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

public class UserProfileCard {

    private final UserModel user;
    
    // Variabel Visual
    private float scaleFactor = 1.0f;
    private float cardScale = 1.0f;
    private float alpha = 1.0f;

    // --- UKURAN KARTU ---
    private static final int BASE_W = 320;
    private static final int BASE_H = 450;

    // Warna-warni Tema Kartu
    private static final Color[] THEME_COLORS = {
        new Color(79, 195, 247), // Cyan Segar
        new Color(255, 179, 0),  // Amber/Gold
        new Color(102, 187, 106), // Hijau Soft
        new Color(171, 71, 188)   // Ungu
    };
    
    // --- ASET GAMBAR TOMBOL DELETE (Static Load) ---
    private static Image trashIconImg;
    static {
        try {
            // Memuat gambar hanya sekali saat kelas ini pertama kali diakses
            URL url = UserProfileCard.class.getResource("/images/btn_delete.png");
            if (url != null) {
                trashIconImg = new ImageIcon(url).getImage();
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat btn_delete.png: " + e.getMessage());
        }
    }

    public UserProfileCard(UserModel user) {
        this.user = user;
    }

    public void setVisualProperties(float globalScale, float cardScale, float alpha) {
        this.scaleFactor = globalScale;
        this.cardScale = cardScale;
        this.alpha = alpha;
    }

    // Mendapatkan Hitbox Kartu (Untuk deteksi klik)
    public Rectangle getCardBounds(int centerX, int centerY) {
        int dw = (int) (BASE_W * cardScale * scaleFactor);
        int dh = (int) (BASE_H * cardScale * scaleFactor);
        return new Rectangle(centerX - (dw / 2), centerY - (dh / 2), dw, dh);
    }

    // Mendapatkan Hitbox Tombol Sampah
    public Rectangle getTrashBounds(int centerX, int centerY) {
        Rectangle b = getCardBounds(centerX, centerY);
        int size = (int)(100 * scaleFactor); // Ukuran tombol agak besar
        int margin = (int)(0 * scaleFactor);
        // Pojok Kanan Atas
        return new Rectangle(b.x + b.width - size - margin, b.y + margin, size, size);
    }

    public void paint(Graphics2D g2, int centerX, int centerY, boolean isTrashHovered) {
        Rectangle bounds = getCardBounds(centerX, centerY);
        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;
        int arc = (int)(50 * scaleFactor);

        Composite oldComp = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        Color themeColor = THEME_COLORS[user.getId() % THEME_COLORS.length];

        // --- 1. SHADOW (Bayangan Bawah) ---
        g2.setColor(new Color(0,0,0,40));
        g2.fillRoundRect(x + (int)(10*scaleFactor), y + (int)(15*scaleFactor), w, h, arc, arc);

        // --- 2. CARD BASE (Background Putih) ---
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x, y, w, h, arc, arc);

        // --- 3. BORDER TEBAL BERWARNA ---
        g2.setColor(themeColor);
        g2.setStroke(new BasicStroke(6 * scaleFactor));
        g2.drawRoundRect(x, y, w, h, arc, arc);

        // --- 4. HEADER BACKGROUND (Lengkung di atas) ---
        Shape savedClip = g2.getClip();
        g2.setClip(new RoundRectangle2D.Float(x, y, w, h, arc, arc));
        
        g2.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 30)); // Transparan
        g2.fillRect(x, y, w, h/2); // Setengah atas berwarna pudar
        
        // Lingkaran dekorasi di header
        g2.setColor(themeColor);
        g2.fillOval(x + w/2 - (int)(100*scaleFactor), y - (int)(80*scaleFactor), (int)(200*scaleFactor), (int)(200*scaleFactor));
        
        g2.setClip(savedClip);

        // --- 5. AVATAR (Pop-out Circle) ---
        int avatarSize = (int)(180 * cardScale * scaleFactor);
        int cxAv = x + (w - avatarSize) / 2;
        int cyAv = y + (int)(50 * scaleFactor); // Posisi agak turun dari atas

        // Border Putih Avatar
        g2.setColor(Color.WHITE);
        g2.fillOval(cxAv, cyAv, avatarSize, avatarSize);
        // Stroke Avatar
        g2.setColor(themeColor);
        g2.setStroke(new BasicStroke(4 * scaleFactor));
        g2.drawOval(cxAv, cyAv, avatarSize, avatarSize);

        // Gambar Avatar
        try {
            String fName = (user.getAvatar() == null) ? "default.png" : user.getAvatar();
            URL url = getClass().getResource("/images/" + fName);
            if (url != null) {
                Image img = new ImageIcon(url).getImage();
                int pad = (int)(8 * scaleFactor);
                g2.setClip(new Ellipse2D.Float(cxAv+pad, cyAv+pad, avatarSize-(pad*2), avatarSize-(pad*2)));
                g2.drawImage(img, cxAv+pad, cyAv+pad, avatarSize-(pad*2), avatarSize-(pad*2), null);
                g2.setClip(savedClip);
            }
        } catch (Exception ignored) {}

        // --- 6. NAME & LABEL ---
        int textY = cyAv + avatarSize + (int)(50 * scaleFactor);
        
        g2.setColor(new Color(50, 50, 50));
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, (int)(36 * cardScale * scaleFactor)));
        FontMetrics fm = g2.getFontMetrics();
        
        String name = user.getName();
        if (fm.stringWidth(name) > w - 40) name = name.substring(0, Math.min(name.length(), 8)) + "..";
        
        int textX = x + (w - fm.stringWidth(name)) / 2;
        g2.drawString(name, textX, textY);

        // Subtitle "Level X"
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, (int)(18 * cardScale * scaleFactor)));
        String sub = "Petualang Sejati";
        textX = x + (w - g2.getFontMetrics().stringWidth(sub)) / 2;
        g2.drawString(sub, textX, textY + (int)(25 * scaleFactor));

        // --- 7. TOMBOL HAPUS (MENGGUNAKAN ASSET GAMBAR) ---
        // Hanya gambar jika ini kartu tengah (cardScale > 1.0f menandakan kartu aktif/tengah)
        if (cardScale > 1.0f) { 
            Rectangle tRect = getTrashBounds(centerX, centerY);
            
            if (trashIconImg != null) {
                int drawSize = tRect.width;
                int drawX = tRect.x;
                int drawY = tRect.y;

                // Efek Hover: Sedikit membesar
                if (isTrashHovered) {
                    int grow = (int)(6 * scaleFactor);
                    drawSize += grow;
                    drawX -= grow / 2;
                    drawY -= grow / 2;
                }
                
                // Gambar Aset Tombol Delete
                g2.drawImage(trashIconImg, drawX, drawY, drawSize, drawSize, null);

            } else {
                // Fallback jika gambar gagal dimuat (lingkaran merah sederhana)
                g2.setColor(Color.RED);
                g2.fillOval(tRect.x, tRect.y, tRect.width, tRect.height);
            }
        }

        g2.setComposite(oldComp);
    }
}