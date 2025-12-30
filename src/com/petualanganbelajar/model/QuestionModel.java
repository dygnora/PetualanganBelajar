package com.petualanganbelajar.model;

public class QuestionModel {
    private int id;
    private int moduleId;
    private int level;
    private QuestionType questionType; // UBAH: String -> QuestionType
    private String questionText;
    private String questionImage;
    private String questionAudio;
    private String optionA;
    private String optionB;
    private String optionC;
    private String correctAnswer;

    public QuestionModel(int id, int moduleId, int level, QuestionType questionType, String questionText, 
                         String questionImage, String questionAudio, String optionA, String optionB, 
                         String optionC, String correctAnswer) {
        this.id = id;
        this.moduleId = moduleId;
        this.level = level;
        this.questionType = questionType;
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.questionAudio = questionAudio;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correctAnswer = correctAnswer;
    }

    // Getter & Setter
    public int getId() { return id; }
    public int getModuleId() { return moduleId; }
    public int getLevel() { return level; }
    
    // UBAH Getter ini
    public QuestionType getQuestionType() { return questionType; }

    public String getQuestionText() { return questionText; }
    public String getQuestionImage() { return questionImage; }
    public String getQuestionAudio() { return questionAudio; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getCorrectAnswer() { return correctAnswer; }

    // Logic Cek Jawaban sederhana
    public boolean checkAnswer(String answer) {
        return correctAnswer != null && correctAnswer.equalsIgnoreCase(answer);
    }
}