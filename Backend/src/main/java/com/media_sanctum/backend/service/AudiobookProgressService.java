package com.media_sanctum.backend.service;

import com.media_sanctum.backend.entity.AudiobookProgress;
import com.media_sanctum.backend.entity.Book;
import com.media_sanctum.backend.entity.User;
import com.media_sanctum.backend.repository.AudiobookProgressRepository;
import com.media_sanctum.backend.resource.AudiobookProgressResponse;
import com.media_sanctum.backend.resource.UpsertAudiobookProgressRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AudiobookProgressService {

    @Autowired
    private final AudiobookProgressRepository audiobookProgressRepository;

    public AudiobookProgressService(AudiobookProgressRepository audiobookProgressRepository) {
        this.audiobookProgressRepository = audiobookProgressRepository;
    }

    public AudiobookProgressResponse upsert(Book book, User user, UpsertAudiobookProgressRequest body) {
        var progressBuilder = Optional.ofNullable(findByBookIdAndUserId(book.getId(), user.getId()))
                .map(AudiobookProgress::toBuilder)
                .orElse(null);
        if (progressBuilder == null) {
            progressBuilder = AudiobookProgress.builder()
                    .user(user)
                    .book(book);
        }
        var progress = progressBuilder
                .percent(body.getPercent())
                .seconds(body.getSeconds())
                .duration(body.getDuration())
                .currentChapter(body.getCurrentChapter())
                .totalChapters(body.getTotalChapters())
                .build();
        return toResponse(save(progress));
    }

    public AudiobookProgress save(AudiobookProgress audiobookProgress) {
        return audiobookProgressRepository.save(audiobookProgress);
    }

    public AudiobookProgress findByBookIdAndUserId(String bookId, String userId) {
        return audiobookProgressRepository.findFirstByBookIdAndUserId(bookId, userId).orElse(null);
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
