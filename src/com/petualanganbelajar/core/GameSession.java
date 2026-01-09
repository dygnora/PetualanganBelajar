package com.petualanganbelajar.core;

import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.QuestionModel;
import com.petualanganbelajar.repository.LeaderboardRepository;
import com.petualanganbelajar.repository.ProgressRepository;
import com.petualanganbelajar.repository.QuestionRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class GameSession {
    // Data Game
    private ModuleModel module;
    private int level;
    private int score = 0;
    private int maxScore = 0;
    private int pointsPerQuestion;
    private int currentQuestionIndex = 0;
    private boolean isFirstAttempt = true;
    
    // [SECURE FIX] Set untuk menyimpan ID soal yang SUDAH memberi poin
    // Ini adalah pengaman mutlak agar skor tidak bisa masuk 2x untuk soal yang sama
    private Set<Integer> answeredQuestionIds = new HashSet<>(); 
    
    // Data Soal
    private List<QuestionModel> questions;
    private Queue<String> answerQueue; 
    
    // Repositories
    private final QuestionRepository questionRepo = new QuestionRepository();
    private final ProgressRepository progressRepo = new ProgressRepository();
    private final LeaderboardRepository xpRepo = new LeaderboardRepository();

    public GameSession(ModuleModel module, int level) {
        this.module = module;
        this.level = level;
        
        // --- DEBUG PRINT ---
        System.out.println("=== GAME SESSION STARTED ===");
        System.out.println("Module: " + module.getName());
        System.out.println("Level Input: " + level);
        
        // Cek Poin
        this.pointsPerQuestion = (level <= 0) ? 10 : level * 10;
        System.out.println("Points Per Question: " + pointsPerQuestion); 
        // Jika di console muncul '20', berarti Level terdeteksi sebagai 2.
        
        loadQuestions();
    }

    private void loadQuestions() {
        List<QuestionModel> all = questionRepo.getQuestionsByModule(module.getId(), level);
        if (!all.isEmpty()) {
            Collections.shuffle(all);
            int limit = Math.min(all.size(), 15);
            this.questions = new ArrayList<>(all.subList(0, limit));
            this.maxScore = this.questions.size() * pointsPerQuestion;
        } else {
            this.questions = new ArrayList<>();
        }
    }

    public QuestionModel getCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }

    public boolean isGameFinished() {
        return currentQuestionIndex >= questions.size();
    }

    public void nextQuestion() {
        currentQuestionIndex++;
        isFirstAttempt = true;
    }

    // --- LOGIKA JAWABAN STANDARD (SECURE) ---
    public int checkStandardAnswer(String input) {
        QuestionModel q = getCurrentQuestion();
        if (q != null && input.trim().equalsIgnoreCase(q.getCorrectAnswer())) {
            
            // [SECURE FIX] Cek apakah ID soal ini sudah pernah dikasih poin?
            if (answeredQuestionIds.contains(q.getId())) {
                System.out.println("⚠️ BLOCKED: Soal ID " + q.getId() + " sudah dinilai sebelumnya.");
                return 0; 
            }

            int earned = 0;
            if (isFirstAttempt) {
                earned = pointsPerQuestion;
                score += earned;
                
                // KUNCI ID SOAL
                answeredQuestionIds.add(q.getId());
                isFirstAttempt = false; 
                
                System.out.println("✅ CORRECT! Earned: " + earned + " | Total Score: " + score);
            } else {
                System.out.println("❌ CORRECT but 2nd attempt. Earned: 0");
            }
            
            return earned; 
        }
        
        isFirstAttempt = false;
        System.out.println("wrong answer...");
        return -1; 
    }

    // --- LOGIKA JAWABAN SEQUENCE (SECURE) ---
    public void initSequenceQueue(String fullAnswer) {
        this.answerQueue = new LinkedList<>();
        String[] ans = fullAnswer.trim().split(",");
        for (String a : ans) answerQueue.add(a.trim());
    }

    public int checkSequenceAnswer(String input) {
        if (answerQueue == null || answerQueue.isEmpty()) return -2; 
        
        String target = answerQueue.peek();
        if (input.equalsIgnoreCase(target)) {
            answerQueue.poll(); 
            
            if (answerQueue.isEmpty()) {
                // Sequence selesai
                QuestionModel q = getCurrentQuestion();
                
                // [SECURE FIX]
                if (answeredQuestionIds.contains(q.getId())) {
                    return 0;
                }

                int earned = 0;
                if (isFirstAttempt) {
                    earned = pointsPerQuestion;
                    score += earned;
                    
                    answeredQuestionIds.add(q.getId());
                    isFirstAttempt = false;
                    
                    System.out.println("✅ SEQUENCE COMPLETE! Earned: " + earned + " | Total: " + score);
                }
                return earned; 
            }
            return 0; 
        }
        
        isFirstAttempt = false;
        return -1; 
    }

    // --- DATABASE OPERATIONS ---
    public int getBaseXP(int userId) {
        return xpRepo.getTotalScoreByUserId(userId);
    }

    public boolean saveProgress(int userId) {
        System.out.println("💾 SAVING SCORE: " + score);
        progressRepo.saveScore(userId, module.getId(), level, score);
        if (score >= (maxScore * 0.6)) {
            progressRepo.unlockNextLevel(userId, module.getId(), level);
            return true; 
        }
        return false; 
    }

    public boolean isPassedHalf() {
        return score >= (maxScore * 0.5);
    }

    public boolean checkIfGameFullyCompleted(int userId) {
        return progressRepo.isGameFullyCompleted(userId);
    }

    // Getters
    public int getScore() { return score; }
    public int getMaxScore() { return maxScore; }
    public ModuleModel getModule() { return module; }
    public int getLevel() { return level; }
}