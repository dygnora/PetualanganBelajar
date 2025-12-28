package com.petualanganbelajar.model;

/**
 * Model data untuk merepresentasikan satu soal.
 * Sekarang mendukung berbagai tipe gameplay (Pilihan Ganda, Typing, dll).
 */
public class QuestionModel {
    private int id;
    private int moduleId;
    private int level;
    private String questionType; // [BARU] Tipe Soal: CHOICE, TYPING, CLICK, KEYPAD, SEQUENCE
    private String questionText;
    private String questionImage;
    private String questionAudio;
    private String optionA;
    private String optionB;
    private String optionC;
    private String correctAnswer;

    // Constructor Lengkap
    public QuestionModel(int id, int moduleId, int level, String questionType, 
                         String questionText, String questionImage, String questionAudio,
                         String optionA, String optionB, String optionC, String correctAnswer) {
        this.id = id;
        this.moduleId = moduleId;
        this.level = level;
        this.questionType = questionType; // Simpan tipe soal
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.questionAudio = questionAudio;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correctAnswer = correctAnswer;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public int getModuleId() { return moduleId; }
    public int getLevel() { return level; }
    public String getQuestionType() { return questionType; } // Getter baru
    public String getQuestionText() { return questionText; }
    public String getQuestionImage() { return questionImage; }
    public String getQuestionAudio() { return questionAudio; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getCorrectAnswer() { return correctAnswer; }

    /**
     * Cek jawaban user. Mengabaikan huruf besar/kecil.
     */
    public boolean checkAnswer(String answer) {
        return correctAnswer != null && correctAnswer.equalsIgnoreCase(answer);
    }
}