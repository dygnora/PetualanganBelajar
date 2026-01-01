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
    
    // Simpan side terakhir untuk update saat resize
    private String currentSide = "LEFT"; 

    // ==========================================================
    // --- 1. SETTING UKURAN KARAKTER (GLOBAL) ---
    // Ubah angka di sini, maka Gambar DAN Wadah akan berubah otomatis
    // ==========================================================
    private final int CHAR_WIDTH = 420;  // Lebar (coba 400 atau 350)
    private final int CHAR_HEIGHT = 500; // Tinggi (coba 500 atau 550)

    public StoryDialogPanel() {
        setLayout(null);
        setOpaque(false);
        initUI();
        
        // LISTENER RESIZE
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (isVisible()) {
                    updateLayout(currentSide);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Background hitam transparan
        g2.setColor(new Color(0, 0, 0, 180)); 
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    private void initUI() {
        // SETUP KARAKTER
        lblCharImage = new JLabel();
        lblCharImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblCharImage.setVerticalAlignment(SwingConstants.BOTTOM);
        add(lblCharImage); 

        // SETUP KOTAK DIALOG
        dialogBox = new JPanel();
        dialogBox.setBackground(Color.WHITE);
        dialogBox.setBorder(new LineBorder(StyleConstants.COL_PRIMARY, 6, true));
        dialogBox.setLayout(null); 
        add(dialogBox); 

        // SETUP NAMA TAG
        lblNameTag = new JLabel("Prof. Otto", SwingConstants.CENTER);
        lblNameTag.setOpaque(true);
        lblNameTag.setBackground(StyleConstants.COL_ACCENT);
        lblNameTag.setForeground(Color.WHITE);
        lblNameTag.setFont(StyleConstants.FONT_BUTTON);
        add(lblNameTag);

        // ISI DIALOG
        txtDialog = new JTextArea();
        txtDialog.setFont(StyleConstants.FONT_BODY.deriveFont(24f));
        txtDialog.setForeground(Color.BLACK);
        txtDialog.setWrapStyleWord(true);
        txtDialog.setLineWrap(true);
        txtDialog.setEditable(false);
        txtDialog.setOpaque(false);
        dialogBox.add(txtDialog);

        btnNext = new JoyBtn("LANJUT >", StyleConstants.COL_SUCCESS);
        btnNext.addActionListener(e -> nextScene());
        dialogBox.add(btnNext);
        
        // ATUR Z-ORDER
        setComponentZOrder(lblNameTag, 0);   
        setComponentZOrder(dialogBox, 1);    
        setComponentZOrder(lblCharImage, 2); 
    }

    // --- LOGIKA CERITA ---
    public void startStory(List<DialogScene> scenes, Runnable onFinished) {
        if (scenes == null || scenes.isEmpty()) {
            onFinished.run();
            return;
        }
        this.currentScenes = scenes;
        this.onFinishedCallback = onFinished;
        this.sceneIndex = 0;
        
        setVisible(true);
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
        
        // [PENTING] Gunakan variabel global CHAR_WIDTH & CHAR_HEIGHT
        // Ini yang membuat gambar di-RESIZE (skala), bukan di-CROP.
        ImageIcon icon = UIHelper.loadIcon("char/" + scene.getPoseImage(), CHAR_WIDTH, CHAR_HEIGHT);
        
        if(icon != null) lblCharImage.setIcon(icon);
        else lblCharImage.setIcon(null);
        
        if (scene.isTutorial()) lblNameTag.setBackground(StyleConstants.COL_DANGER);
        else lblNameTag.setBackground(StyleConstants.COL_ACCENT);

        this.currentSide = scene.getSide();
        updateLayout(currentSide);
    }
    
    // --- LOGIKA POSISI DINAMIS ---
    private void updateLayout(String side) {
        // [PENTING] Ambil ukuran dari variabel global juga
        int charW = CHAR_WIDTH;     
        int charH = CHAR_HEIGHT;
        
        int screenW = getWidth() > 0 ? getWidth() : 1024; 
        int screenH = getHeight() > 0 ? getHeight() : 768; 
        
        // Hitung Y agar kaki menempel di bawah
        int charY = screenH - charH; 
        
        int sideMargin = 0;  
        int textMargin = 40; 
        int gap = 10;        
        
        int boxW = screenW - charW - sideMargin - gap - textMargin;
        if (boxW < 300) boxW = 300; 

        if ("LEFT".equalsIgnoreCase(side)) {
            // KIRI
            lblCharImage.setBounds(sideMargin, charY, charW, charH); 
            
            int boxX = sideMargin + charW + gap;
            dialogBox.setBounds(boxX, 520, boxW, 200);
            lblNameTag.setBounds(boxX + 30, 495, 200, 50);
            
        } else {
            // KANAN
            int charX = screenW - charW - sideMargin;
            lblCharImage.setBounds(charX, charY, charW, charH); 
            
            int boxX = textMargin;
            dialogBox.setBounds(boxX, 520, boxW, 200);
            lblNameTag.setBounds(textMargin + 30, 495, 200, 50);
        }
        
        // Update Komponen Internal
        txtDialog.setBounds(30, 40, boxW - 60, 110);
        btnNext.setBounds(boxW - 170, 130, 140, 50);
        
        revalidate();
        repaint();
    }

    private void nextScene() {
        sceneIndex++;
        loadCurrentScene();
    }
}