package com.petualanganbelajar.model;

public class QuestionModel {
    private int id;
    private int moduleId;
    private int level;
    private String questionText;
    private String questionImage;
    private String questionAudio;
    private String optionA;
    private String optionB;
    private String optionC;
    private String correctAnswer;

    // Constructor Update
    public QuestionModel(int id, int moduleId, int level, String questionText, 
                         String questionImage, String questionAudio, 
                         String optionA, String optionB, String optionC, 
                         String correctAnswer) {
        this.id = id;
        this.moduleId = moduleId;
        this.level = level;
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.questionAudio = questionAudio; // [BARU] Simpan data audio
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correctAnswer = correctAnswer;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public int getModuleId() { return moduleId; }
    public int getLevel() { return level; }
    public String getQuestionText() { return questionText; }
    public String getQuestionImage() { return questionImage; }
    
    // [BARU] Ini Method yang tadi Error (Missing Symbol)
    public String getQuestionAudio() { return questionAudio; }

    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getCorrectAnswer() { return correctAnswer; }

    // Logic Cek Jawaban
    public boolean checkAnswer(String answer) {
        return correctAnswer != null && correctAnswer.equalsIgnoreCase(answer);
    }
}