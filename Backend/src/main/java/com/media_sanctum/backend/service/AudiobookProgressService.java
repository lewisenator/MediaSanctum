package com.media_sanctum.backend.service;

import com.media_sanctum.backend.entity.AudiobookProgress;
import com.media_sanctum.backend.repository.AudiobookProgressRepository;
import com.media_sanctum.backend.resource.AudiobookProgressResponse;
import com.media_sanctum.backend.resource.EbookProgressResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AudiobookProgressService {

    @Autowired
    private AudiobookProgressRepository audiobookProgressRepository;

    public AudiobookProgressService(AudiobookProgressRepository audiobookProgressRepository) {
        this.audiobookProgressRepository = audiobookProgressRepository;
    }

    public AudiobookProgress save(AudiobookProgress audiobookProgress) {
        return audiobookProgressRepository.save(audiobookProgress);
    }

    public AudiobookProgress findByBookIdAndUserId(String bookId, String userId) {
        return audiobookProgressRepository.findByBookIdAndUserId(bookId, userId).orElse(null);
    }

    public AudiobookProgress getProgress(String id) {
        return audiobookProgressRepository.findById(id).orElse(null);
    }

    public AudiobookProgressResponse getProgressResponse(String id) {
        return toResponse(getProgress(id));
    }

    public static AudiobookProgressResponse toResponse(AudiobookProgress audiobookProgress) {
        if (audiobookProgress == null) {
            return null;
        }
        return AudiobookProgressResponse.builder()
                .id(audiobookProgress.getId())
                .percent(audiobookProgress.getPercent())
                .currentChapter(audiobookProgress.getCurrentChapter())
                .totalChapters(audiobookProgress.getTotalChapters())
                .seconds(audiobookProgress.getSeconds())
                .duration(audiobookProgress.getDuration())
                .createdAt(audiobookProgress.getCreatedAt().toString())
                .updatedAt(audiobookProgress.getUpdatedAt().toString())
                .build();
    }
}
