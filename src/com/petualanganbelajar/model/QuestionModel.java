/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.model;

/**
 *
 * @author DD
 */
public class QuestionModel {
    private int id;
    private int moduleId;
    private int level;
    private String questionText;
    private String questionImage; // Nama file gambar
    private String questionAudio; // Nama file suara
    private String optionA;
    private String optionB;
    private String optionC;
    private String correctAnswer;

    // Constructor
    public QuestionModel(int id, int moduleId, int level, String questionText, String questionImage, String questionAudio, String optionA, String optionB, String optionC, String correctAnswer) {
        this.id = id;
        this.moduleId = moduleId;
        this.level = level;
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.questionAudio = questionAudio;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correctAnswer = correctAnswer;
    }

    // Getters
    public int getId() { return id; }
    public String getQuestionText() { return questionText; }
    public String getQuestionImage() { return questionImage; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    
    // Cek Jawaban
    public boolean checkAnswer(String answer) {
        return correctAnswer.equalsIgnoreCase(answer);
    }
}
