package com.petualanganbelajar.util;

/**
 * Class sederhana untuk menyimpan data satu layar dialog (Scene).
 * Mirip seperti satu halaman pada buku cerita.
 */
public class DialogScene {
    private String characterName;   // Nama Tokoh (misal: "Prof. Otto")
    private String dialogText;      // Isi Bicara (misal: "Halo...")
    private String poseImage;       // Nama file gambar (misal: "otto_sapa.png")
    private boolean isTutorial;     // Penanda apakah ini sesi tutorial (untuk beda warna background text)

    public DialogScene(String characterName, String dialogText, String poseImage, boolean isTutorial) {
        this.characterName = characterName;
        this.dialogText = dialogText;
        this.poseImage = poseImage;
        this.isTutorial = isTutorial;
    }

    public String getCharacterName() { return characterName; }
    public String getDialogText() { return dialogText; }
    public String getPoseImage() { return poseImage; }
    public boolean isTutorial() { return isTutorial; }
}