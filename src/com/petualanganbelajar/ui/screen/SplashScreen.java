package com.petualanganbelajar.ui.screen;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Random;
import java.util.List;

public class SplashScreen extends JWindow {

    private int progress = 0;
    private String currentStatus = "Menyiapkan petualangan...";
    private Image bgImage;
    // private Image logoImage; // DIHAPUS: Tidak lagi menggunakan logo title
    private final Runnable onLoaded;

    public SplashScreen(Runnable onLoaded) {
        this.onLoaded = onLoaded;
        
        loadAssets();
        
        // Ukuran Splash Screen (Ukuran ini cukup standar untuk rasio gambar tersebut)
        setSize(800, 500); 
        setLocationRelativeTo(null);
        setLayout(null);
        
        // Opsional: Jika ingin sudut window membulat (hanya bekerja di beberapa OS/L&F)
        // setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        startLoadingSimulation();
    }

    private void loadAssets() {
        try {
            // HANYA LOAD BACKGROUND
            // Pastikan file bg_splash.png (gambar burung hantu) ada di folder src/images/
            URL bgUrl = getClass().getResource("/images/bg_splash.png");
            if (bgUrl != null) {
                bgImage = new ImageIcon(bgUrl).getImage();
            } else {
                System.err.println("ERROR: bg_splash.png tidak ditemukan!");
                // Fallback darurat jika gambar tidak ada (opsional)
                // bgImage = new java.awt.image.BufferedImage(800, 500, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            }
            
            // BAGIAN LOAD LOGO DIHAPUS
            // URL logoUrl = getClass().getResource("/images/title_papan_juara.png"); 
            // if (logoUrl != null) logoImage = new ImageIcon(logoUrl).getImage();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startLoadingSimulation() {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                Random rand = new Random();
                for (int i = 0; i <= 100; i++) {
                    // Sedikit dipercepat agar tidak terlalu lama menunggu
                    Thread.sleep(25 + rand.nextInt(30)); 
                    publish(i);
                    
                    // Teks status yang relevan dengan tema sekolah hutan
                    if (i == 15) currentStatus = "Membersihkan papan tulis...";
                    if (i == 35) currentStatus = "Merapikan buku pelajaran...";
                    if (i == 55) currentStatus = "Profesor Owl menyiapkan materi...";
                    if (i == 75) currentStatus = "Mengumpulkan krayon ajaib...";
                    if (i == 95) currentStatus = "Siap memulai pelajaran!";
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                progress = chunks.get(chunks.size() - 1);
                repaint();
            }

            @Override
            protected void done() {
                dispose();
                if (onLoaded != null) {
                    onLoaded.run();
                }
            }
        };
        worker.execute();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // Mengaktifkan anti-aliasing untuk grafis dan teks yang halus
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Background Image (Full Screen pada Window)
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, w, h, this);
        } else {
            // Fallback jika gambar gagal diload
            g2.setColor(new Color(34, 139, 34)); // Forest Green
            g2.fillRect(0, 0, w, h);
        }

        // BAGIAN GAMBAR LOGO DIHAPUS

        // --- AREA LOADING BAR ---
        // Posisi di bagian bawah, di area rumput
        int barW = 500;
        int barH = 24; // Sedikit lebih ramping
        int barX = (w - barW) / 2;
        int barY = h - 80;
        int cornerRadius = barH; // Membuat sudut benar-benar bulat (kapsul)

        // 2. Wadah Progress Bar (Latar Belakang Bar)
        // Menggunakan warna hijau gelap transparan agar menyatu dengan rumput
        g2.setColor(new Color(20, 60, 20, 160)); 
        g2.fillRoundRect(barX, barY, barW, barH, cornerRadius, cornerRadius);
        
        // Border halus untuk wadah
        g2.setColor(new Color(255, 255, 200, 50)); // Kuning pucat sangat transparan
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(barX, barY, barW, barH, cornerRadius, cornerRadius);

        // 3. Isi Progress (Efek Cahaya Emas)
        if (progress > 0) {
            // Menghitung lebar isi, dikurangi padding kecil (3px)
            int padding = 3;
            int fillW = (int) ((barW - (padding*2)) * (progress / 100.0));
            int fillH = barH - (padding*2);
            
            // Gradasi Emas ke Kuning Terang (Memberi kesan cahaya magis)
            GradientPaint goldenLight = new GradientPaint(
                    barX, barY, new Color(255, 223, 0),    // Emas Terang
                    barX, barY + barH, new Color(255, 165, 0) // Oranye Emas
            );
            g2.setPaint(goldenLight);
            // Menggambar isi dengan sudut membulat di dalam wadah
            g2.fillRoundRect(barX + padding, barY + padding, fillW, fillH, fillH, fillH);
        }

        // 4. Teks Status dan Persentase
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 17));
        FontMetrics fm = g2.getFontMetrics();
        
        int textY = barY - 12; // Posisi teks di atas bar

        // A. Bayangan Teks (Coklat Tua untuk kontras)
        g2.setColor(new Color(50, 30, 10, 200));
        g2.drawString(currentStatus, barX + 2, textY + 2);
        String pctText = progress + "%";
        g2.drawString(pctText, barX + barW - fm.stringWidth(pctText) - 2, textY + 2);
        
        // B. Teks Utama (Krem Terang / Putih Gading)
        g2.setColor(new Color(255, 250, 240)); 
        g2.drawString(currentStatus, barX, textY);
        g2.drawString(pctText, barX + barW - fm.stringWidth(pctText), textY);
    }
}