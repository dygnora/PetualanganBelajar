package com.petualanganbelajar.core;

import com.petualanganbelajar.content.StoryDataManager;
import com.petualanganbelajar.repository.StoryRepository;
import com.petualanganbelajar.util.DialogScene;
import java.util.Collections;
import java.util.List;

public class StoryController {
    
    private final StoryRepository storyRepo;

    public StoryController() {
        this.storyRepo = new StoryRepository();
    }

    // --- INTRO STORY ---
    public List<DialogScene> getIntroStoryIfNew(int userId, int moduleId, int level) {
        // Cek DB dulu, kalau sudah pernah lihat, return list kosong
        if (storyRepo.hasSeenStory(userId, moduleId, level, "START")) {
            return Collections.emptyList();
        }
        // Kalau belum, ambil datanya
        return StoryDataManager.getIntroStory(moduleId, level);
    }

    public void markIntroAsSeen(int userId, int moduleId, int level) {
        storyRepo.markStoryAsSeen(userId, moduleId, level, "START");
    }

    // --- OUTRO STORY ---
    public List<DialogScene> getOutroStoryIfNew(int userId, int moduleId, int level) {
        if (storyRepo.hasSeenStory(userId, moduleId, level, "SUCCESS")) {
            return Collections.emptyList();
        }
        return StoryDataManager.getOutroStory(moduleId, level);
    }

    public void markOutroAsSeen(int userId, int moduleId, int level) {
        storyRepo.markStoryAsSeen(userId, moduleId, level, "SUCCESS");
    }

    // --- EPILOGUE STORY ---
    public List<DialogScene> getEpilogueIfNew(int userId) {
        if (storyRepo.hasSeenStory(userId, 0, 0, "EPILOGUE")) {
            return Collections.emptyList();
        }
        return StoryDataManager.getEpilogueStory();
    }

    public void markEpilogueAsSeen(int userId) {
        storyRepo.markStoryAsSeen(userId, 0, 0, "EPILOGUE");
    }
}