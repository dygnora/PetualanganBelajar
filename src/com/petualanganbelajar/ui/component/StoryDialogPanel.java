package com.petualanganbelajar.ui.component;

import com.petualanganbelajar.util.DialogScene;
import com.petualanganbelajar.util.StyleConstants;
import com.petualanganbelajar.util.UIHelper;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class StoryDialogPanel extends JPanel {

    private JLabel lblCharImage;
    private JLabel lblNameTag;
    private JTextArea txtDialog;
    private JoyBtn btnNext;
    private JPanel dialogBox;

    private List<DialogScene> currentScenes;
    private int sceneIndex = 0;
    private Runnable onFinishedCallback;
    
    private String currentSide = "LEFT"; 
    
    // [PENTING] Variabel untuk menyimpan nama file gambar saat ini
    // Kita butuh ini agar saat layar di-resize, kita bisa memuat ulang gambar dengan ukuran baru
    private String currentPoseImage = ""; 

    // ==========================================================
    // --- 1. RESOLUSI DESAIN DASAR (BASE REFERENCE) ---
    // [PENTING] Kita tentukan desain ini dibuat berdasarkan layar Full HD (1920x1080).
    // Semua perhitungan skala akan mengacu ke angka ini.
    // ==========================================================
    private final float BASE_SCREEN_W = 1920f;
    private final float BASE_SCREEN_H = 1080f;

    // Ukuran elemen pada resolusi ideal 1920x1080 (Ukuran Besar/HD)
    private final int BASE_CHAR_W = 600;
    private final int BASE_CHAR_H = 750;
    private final int BASE_BOX_H  = 300;
    
    // Ukuran Font Dasar (Akan membesar/mengecil otomatis)
    private final float BASE_FONT_DIALOG = 36f;
    private final float BASE_FONT_TAG = 32f;
    private final float BASE_FONT_BTN = 28f;

    // [PENTING] Faktor Skala (Multiplier)
    // 1.0 = Layar 1080p, 0.7 = Layar Laptop Kecil, 2.0 = Layar 4K
    private float scaleFactor = 1.0f;

    public StoryDialogPanel() {
        setLayout(null);
        setOpaque(false);
        initUI();
        
        // [PENTING] Listener Resize
        // Kode ini akan dijalankan setiap kali ukuran window berubah
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (isVisible()) {
                    // 1. Hitung ulang faktor skala berdasarkan ukuran layar baru
                    calculateScaleFactor();
                    // 2. Tata ulang layout (posisi & ukuran komponen)
                    updateLayout(currentSide);
                    // 3. Muat ulang gambar agar tidak pecah/kekecilan
                    reloadCharacterImage(); 
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Background hitam transparan (Dimming efek)
        g2.setColor(new Color(0, 0, 0, 180)); 
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    private void initUI() {
        // Inisialisasi komponen (belum diatur posisinya, nanti diatur oleh updateLayout)
        
        lblCharImage = new JLabel();
        lblCharImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblCharImage.setVerticalAlignment(SwingConstants.BOTTOM);
        add(lblCharImage); 

        dialogBox = new JPanel();
        dialogBox.setBackground(Color.WHITE);
        dialogBox.setLayout(null); 
        add(dialogBox); 

        lblNameTag = new JLabel("Prof. Otto", SwingConstants.CENTER);
        lblNameTag.setOpaque(true);
        lblNameTag.setBackground(StyleConstants.COL_ACCENT);
        lblNameTag.setForeground(Color.WHITE);
        add(lblNameTag);

        txtDialog = new JTextArea();
        txtDialog.setForeground(Color.BLACK);
        txtDialog.setWrapStyleWord(true);
        txtDialog.setLineWrap(true);
        txtDialog.setEditable(false);
        txtDialog.setOpaque(false);
        dialogBox.add(txtDialog);

        btnNext = new JoyBtn("LANJUT >", StyleConstants.COL_SUCCESS);
        btnNext.addActionListener(e -> nextScene());
        dialogBox.add(btnNext);
        
        // Atur tumpukan layer (Z-Order)
        setComponentZOrder(lblNameTag, 0);   // Paling Atas
        setComponentZOrder(dialogBox, 1);    // Tengah
        setComponentZOrder(lblCharImage, 2); // Paling Bawah
    }

    // --- [LOGIKA PENTING] MENGHITUNG SKALA ---
    private void calculateScaleFactor() {
        int w = getWidth();
        int h = getHeight();
        
        // Mencegah error division by zero saat inisialisasi awal
        if (w <= 0 || h <= 0) return;

        // Rumus: Ukuran Layar Saat Ini / Ukuran Layar Dasar (1920x1080)
        // Kita ambil nilai terkecil (Math.min) agar tampilan muat dan proporsional
        float scaleW = (float) w / BASE_SCREEN_W;
        float scaleH = (float) h / BASE_SCREEN_H;
        
        this.scaleFactor = Math.min(scaleW, scaleH);
        
        // [Optional] Batasi skala minimal agar tidak terlalu mikroskopis di HP
        if (this.scaleFactor < 0.5f) this.scaleFactor = 0.5f;
    }

    public void startStory(List<DialogScene> scenes, Runnable onFinished) {
        if (scenes == null || scenes.isEmpty()) {
            onFinished.run();
            return;
        }
        this.currentScenes = scenes;
        this.onFinishedCallback = onFinished;
        this.sceneIndex = 0;
        
        setVisible(true);
        // Hitung skala layar sebelum menampilkan apapun
        calculateScaleFactor();
        loadCurrentScene();
    }

    private void loadCurrentScene() {
        if (sceneIndex >= currentScenes.size()) {
            setVisible(false);
            if (onFinishedCallback != null) onFinishedCallback.run();
            return;
        }

        DialogScene scene = currentScenes.get(sceneIndex);
        
        lblNameTag.setText(scene.getCharacterName());
        txtDialog.setText(scene.getDialogText());
        
        // Simpan nama file gambar ke variabel global agar bisa di-reload saat resize
        this.currentPoseImage = "char/" + scene.getPoseImage();
        reloadCharacterImage();
        
        if (scene.isTutorial()) lblNameTag.setBackground(StyleConstants.COL_DANGER);
        else lblNameTag.setBackground(StyleConstants.COL_ACCENT);

        this.currentSide = scene.getSide();
        updateLayout(currentSide);
    }

    // --- [LOGIKA PENTING] RELOAD GAMBAR SESUAI SKALA ---
    private void reloadCharacterImage() {
        if (currentPoseImage == null || currentPoseImage.isEmpty()) return;
        
        // Hitung target ukuran gambar berdasarkan Scale Factor
        // Contoh: Jika scale 0.5 (layar kecil), gambar jadi setengah ukuran aslinya
        int targetW = (int) (BASE_CHAR_W * scaleFactor);
        int targetH = (int) (BASE_CHAR_H * scaleFactor);
        
        // Load icon baru dengan ukuran yang pas
        ImageIcon icon = UIHelper.loadIcon(currentPoseImage, targetW, targetH);
        if(icon != null) lblCharImage.setIcon(icon);
    }
    
    // --- [LOGIKA PENTING] LAYOUT RESPONSIF ---
    private void updateLayout(String side) {
        int screenW = getWidth();
        int screenH = getHeight();
        if (screenW == 0 || screenH == 0) return;

        // 1. Konversi Ukuran Dasar ke Ukuran Skala (Responsif)
        int charW = (int) (BASE_CHAR_W * scaleFactor);
        int charH = (int) (BASE_CHAR_H * scaleFactor);
        int boxH  = (int) (BASE_BOX_H * scaleFactor);
        int marginBtm = (int) (30 * scaleFactor); 
        
        // 2. Update Font Size secara Dinamis (Font Dasar * Skala)
        lblNameTag.setFont(StyleConstants.FONT_BUTTON.deriveFont(BASE_FONT_TAG * scaleFactor));
        txtDialog.setFont(StyleConstants.FONT_BODY.deriveFont(BASE_FONT_DIALOG * scaleFactor));
        btnNext.setFont(StyleConstants.FONT_BUTTON.deriveFont(BASE_FONT_BTN * scaleFactor));
        
        // Update Ketebalan Border Kotak
        int borderThick = Math.max(2, (int) (8 * scaleFactor));
        dialogBox.setBorder(new LineBorder(StyleConstants.COL_PRIMARY, borderThick, true));
        
        // Update Padding/Margin dalam Teks Area
        int padTop = (int)(35 * scaleFactor);
        int padX   = (int)(30 * scaleFactor);
        txtDialog.setMargin(new Insets(padTop, padX, padX, padX));

        // 3. Hitung Koordinat (X, Y)
        int charY = screenH - charH; // Karakter selalu menempel bawah
        int boxY = screenH - boxH - marginBtm; // Kotak dialog di atas margin bawah
        
        int nameTagH = (int) (60 * scaleFactor); 
        int nameTagY = boxY - (nameTagH / 2); // Nametag nangkring di tengah garis atas kotak

        int sideMargin = (int) (-50 * scaleFactor); // Efek sinematik (sedikit keluar layar)
        int textMargin = (int) (50 * scaleFactor); 
        int gap = (int) (20 * scaleFactor);        
        
        // Lebar kotak dialog menyesuaikan sisa layar
        int boxW = screenW - charW - sideMargin - gap - textMargin;
        if (boxW < 300) boxW = 300; 

        // 4. Set Bounds (Terapkan posisi ke komponen)
        if ("LEFT".equalsIgnoreCase(side)) {
            // Mode Kiri
            lblCharImage.setBounds(sideMargin, charY, charW, charH); 
            int boxX = sideMargin + charW + gap;
            dialogBox.setBounds(boxX, boxY, boxW, boxH);
            lblNameTag.setBounds(boxX + (int)(40*scaleFactor), nameTagY, (int)(300*scaleFactor), nameTagH);
        } else {
            // Mode Kanan
            int charX = screenW - charW - sideMargin;
            lblCharImage.setBounds(charX, charY, charW, charH); 
            int boxX = textMargin;
            dialogBox.setBounds(boxX, boxY, boxW, boxH);
            lblNameTag.setBounds(textMargin + (int)(40*scaleFactor), nameTagY, (int)(300*scaleFactor), nameTagH);
        }
        
        // 5. Update Komponen Internal Box Dialog
        int txtW = boxW - (int)(40*scaleFactor);
        int txtH = boxH - (int)(90*scaleFactor);
        txtDialog.setBounds((int)(10*scaleFactor), (int)(10*scaleFactor), txtW, txtH); 
        
        // Tombol Next menyesuaikan skala
        int btnW = (int)(200 * scaleFactor);
        int btnH = (int)(65 * scaleFactor);
        btnNext.setBounds(boxW - btnW - (int)(20*scaleFactor), boxH - btnH - (int)(15*scaleFactor), btnW, btnH);
        
        revalidate();
        repaint();
    }

    private void nextScene() {
        sceneIndex++;
        loadCurrentScene();
    }
}