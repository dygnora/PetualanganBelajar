package com.petualanganbelajar.core;

import com.petualanganbelajar.model.ModuleModel;
import com.petualanganbelajar.model.QuestionModel;
import com.petualanganbelajar.repository.ProgressRepository;
import com.petualanganbelajar.repository.QuestionRepository;
import com.petualanganbelajar.repository.UserRepository;
import com.petualanganbelajar.util.LevelManager;
import java.util.*;

public class GameSession {
    // Data Game
    private ModuleModel module;
    private int level;
    private int score = 0;
    private int maxScore = 0;
    private int pointsPerQuestion;
    private int currentQuestionIndex = 0;
    private boolean isFirstAttempt = true;
    
    private Set<Integer> answeredQuestionIds = new HashSet<>(); 
    private List<QuestionModel> questions;
    private Queue<String> answerQueue; 
    
    private final QuestionRepository questionRepo = new QuestionRepository();
    private final ProgressRepository progressRepo = new ProgressRepository();
    
    // [BARU] Variable Level Up
    private boolean isLeveledUp = false;
    private int newCharacterLevel = 0;

    public GameSession(ModuleModel module, int level) {
        this.module = module;
        this.level = level;
        this.pointsPerQuestion = (level <= 0) ? 10 : level * 10;
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

    public int checkStandardAnswer(String input) {
        QuestionModel q = getCurrentQuestion();
        if (q != null && input.trim().equalsIgnoreCase(q.getCorrectAnswer())) {
            if (answeredQuestionIds.contains(q.getId())) return 0;
            int earned = 0;
            if (isFirstAttempt) {
                earned = pointsPerQuestion;
                score += earned;
                answeredQuestionIds.add(q.getId());
                isFirstAttempt = false;
            }
            return earned; 
        }
        isFirstAttempt = false;
        return -1; 
    }

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
                QuestionModel q = getCurrentQuestion();
                if (answeredQuestionIds.contains(q.getId())) return 0;
                int earned = 0;
                if (isFirstAttempt) {
                    earned = pointsPerQuestion;
                    score += earned;
                    answeredQuestionIds.add(q.getId());
                    isFirstAttempt = false;
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
        return new UserRepository().getUserXP(userId);
    }

    // [FIXED] Save Progress dengan Deteksi Level Up
    public boolean saveProgress(int userId) {
        // 1. Simpan Highscore
        progressRepo.saveScore(userId, module.getId(), level, score);
        
        // 2. Logic Level Up Karakter
        UserRepository userRepo = new UserRepository();
        
        // A. Ambil XP Lama & Hitung Level Lama
        int oldXP = userRepo.getUserXP(userId);
        int oldLvl = LevelManager.calculateLevelFromScore(oldXP);
        
        // B. Tambah XP Baru
        progressRepo.addPlayerXP(userId, score); 
        
        // C. Hitung Level Baru
        int newXP = oldXP + score;
        int newLvl = LevelManager.calculateLevelFromScore(newXP);
        
        // D. Cek Kenaikan & Update DB
        if (newLvl > oldLvl) {
            userRepo.updateUserLevel(userId, newLvl);
            this.isLeveledUp = true;
            this.newCharacterLevel = newLvl;
            System.out.println("LOG: LEVEL UP DETECTED! " + oldLvl + " -> " + newLvl);
        } else {
            this.isLeveledUp = false;
        }

        // 3. Unlock Next Game Level
        if (score >= (maxScore * 0.6)) {
            progressRepo.unlockNextLevel(userId, module.getId(), level);
            return true; 
        }
        return false; 
    }

    // [BARU] Getter Penting untuk Level Up Dialog
    public boolean isLeveledUp() { return isLeveledUp; }
    public int getNewCharacterLevel() { return newCharacterLevel; }

    public boolean isPassedHalf() {
        return score >= (maxScore * 0.5);
    }

    public boolean checkIfGameFullyCompleted(int userId) {
        return progressRepo.isGameFullyCompleted(userId);
    }

    public int getScore() { return score; }
    public int getMaxScore() { return maxScore; }
    public ModuleModel getModule() { return module; }
    public int getLevel() { return level; }
}