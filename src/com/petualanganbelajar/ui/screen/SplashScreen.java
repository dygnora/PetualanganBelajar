package com.petualanganbelajar.ui.screen;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class SplashScreen extends JWindow {

    private int progress = 0;
    private String currentStatus = "Menyiapkan petualangan...";
    private Image bgImage;
    private final Runnable onLoaded;
    
    // ID Tema Terpilih (1-5)
    // 1: Otto, 2: Bobo, 3: Cici, 4: Moli, 5: Tobi
    private int themeId; 

    public SplashScreen(Runnable onLoaded) {
        this.onLoaded = onLoaded;
        
        // 1. Tentukan Karakter/Tema secara Acak saat Splash Screen dibuat
        this.themeId = new Random().nextInt(5) + 1; // Menghasilkan angka 1 sampai 5
        
        loadAssets();
        
        setSize(800, 500); 
        setLocationRelativeTo(null);
        setLayout(null);
        
        // Opsional: Sudut membulat
        // setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        startLoadingSimulation();
    }

    private void loadAssets() {
        try {
            // 2. Load Gambar Dinamis Berdasarkan themeId
            // Pastikan Anda punya file: splash_1.png, splash_2.png, ..., splash_5.png di folder src/images/
            String imgName = "splash_" + themeId + ".png";
            
            URL bgUrl = getClass().getResource("/images/" + imgName);
            if (bgUrl != null) {
                bgImage = new ImageIcon(bgUrl).getImage();
            } else {
                System.err.println("ERROR: " + imgName + " tidak ditemukan! Menggunakan fallback warna.");
            }
            
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
                    // Sedikit variasi kecepatan
                    Thread.sleep(25 + rand.nextInt(30)); 
                    publish(i);
                    
                    // 3. Logika Teks Berbeda Tiap Karakter
                    updateStatusText(i);
                }
                return null;
            }

            // Helper untuk mengupdate teks berdasarkan progress dan karakter
            private void updateStatusText(int i) {
                if (i == 15 || i == 35 || i == 55 || i == 75 || i == 95) {
                    switch (themeId) {
                        case 1: // Prof. Otto (Owl - Tema Umum/Sekolah)
                            if (i == 15) currentStatus = "Membersihkan papan tulis...";
                            if (i == 35) currentStatus = "Merapikan buku pelajaran...";
                            if (i == 55) currentStatus = "Prof. Otto menyiapkan materi...";
                            if (i == 75) currentStatus = "Mengecek daftar hadir siswa...";
                            if (i == 95) currentStatus = "Kelas siap dimulai!";
                            break;
                            
                        case 2: // Bobo (Beruang - Tema Makanan/Angka)
                            if (i == 15) currentStatus = "Bobo sedang mencuci tangan...";
                            if (i == 35) currentStatus = "Menghitung jumlah apel...";
                            if (i == 55) currentStatus = "Menyiapkan piring dan gelas...";
                            if (i == 75) currentStatus = "Mencium aroma kue lezat...";
                            if (i == 95) currentStatus = "Waktunya makan... eh, belajar!";
                            break;
                            
                        case 3: // Cici (Tupai - Tema Huruf/Lincah)
                            if (i == 15) currentStatus = "Cici berlari mengantar surat...";
                            if (i == 35) currentStatus = "Mengumpulkan huruf yang tercecer...";
                            if (i == 55) currentStatus = "Melompat dari dahan ke dahan...";
                            if (i == 75) currentStatus = "Mengeja kata-kata baru...";
                            if (i == 95) currentStatus = "Undangan siap dibagikan!";
                            break;
                            
                        case 4: // Moli (Bunglon - Tema Warna/Seni)
                            if (i == 15) currentStatus = "Moli sedang bersembunyi...";
                            if (i == 35) currentStatus = "Mencampur warna merah dan biru...";
                            if (i == 55) currentStatus = "Menyiapkan kuas dan kanvas...";
                            if (i == 75) currentStatus = "Menghias ruangan pesta...";
                            if (i == 95) currentStatus = "Dunia menjadi penuh warna!";
                            break;
                            
                        case 5: // Tobi (Kura-kura - Tema Bentuk/Bangunan)
                            if (i == 15) currentStatus = "Tobi berjalan pelan tapi pasti...";
                            if (i == 35) currentStatus = "Menyusun balok-balok kayu...";
                            if (i == 55) currentStatus = "Mengukur sisi segitiga...";
                            if (i == 75) currentStatus = "Memasang atap bangunan...";
                            if (i == 95) currentStatus = "Sepertinya pondasinya sudah kuat!";
                            break;
                    }
                }
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Background Image
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, w, h, this);
        } else {
            // Fallback warna berbeda tiap karakter jika gambar gagal load
            Color[] fallbackColors = {
                Color.BLACK, // Dummy index 0
                new Color(34, 139, 34),  // 1. Hijau Hutan (Otto)
                new Color(210, 105, 30), // 2. Coklat (Bobo)
                new Color(255, 165, 0),  // 3. Oranye (Cici)
                new Color(60, 179, 113), // 4. Hijau Laut (Moli)
                new Color(70, 130, 180)  // 5. Biru Baja (Tobi)
            };
            g2.setColor(fallbackColors[themeId]);
            g2.fillRect(0, 0, w, h);
        }

        // --- AREA LOADING BAR ---
        int barW = 500;
        int barH = 24;
        int barX = (w - barW) / 2;
        int barY = h - 80;
        int cornerRadius = barH;

        // 2. Wadah Progress Bar
        g2.setColor(new Color(20, 60, 20, 160)); 
        g2.fillRoundRect(barX, barY, barW, barH, cornerRadius, cornerRadius);
        
        g2.setColor(new Color(255, 255, 200, 50));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(barX, barY, barW, barH, cornerRadius, cornerRadius);

        // 3. Isi Progress
        if (progress > 0) {
            int padding = 3;
            int fillW = (int) ((barW - (padding*2)) * (progress / 100.0));
            int fillH = barH - (padding*2);
            
            GradientPaint goldenLight = new GradientPaint(
                    barX, barY, new Color(255, 223, 0),    
                    barX, barY + barH, new Color(255, 165, 0) 
            );
            g2.setPaint(goldenLight);
            g2.fillRoundRect(barX + padding, barY + padding, fillW, fillH, fillH, fillH);
        }

        // 4. Teks Status
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 17));
        FontMetrics fm = g2.getFontMetrics();
        
        int textY = barY - 12;

        // A. Bayangan Teks
        g2.setColor(new Color(50, 30, 10, 200));
        g2.drawString(currentStatus, barX + 2, textY + 2);
        String pctText = progress + "%";
        g2.drawString(pctText, barX + barW - fm.stringWidth(pctText) - 2, textY + 2);
        
        // B. Teks Utama
        g2.setColor(new Color(255, 250, 240)); 
        g2.drawString(currentStatus, barX, textY);
        g2.drawString(pctText, barX + barW - fm.stringWidth(pctText), textY);
    }
}