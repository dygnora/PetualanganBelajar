package com.petualanganbelajar.util;

public class DialogScene {
    private String characterName;
    private String dialogText;
    private String poseImage;
    private boolean isTutorial;
    private String side; //

    // Constructor Diupdate
    public DialogScene(String characterName, String dialogText, String poseImage, boolean isTutorial, String side) {
        this.characterName = characterName;
        this.dialogText = dialogText;
        this.poseImage = poseImage;
        this.isTutorial = isTutorial;
        this.side = side;
    }

    public String getCharacterName() { return characterName; }
    public String getDialogText() { return dialogText; }
    public String getPoseImage() { return poseImage; }
    public boolean isTutorial() { return isTutorial; }
    public String getSide() { return side; } //
}