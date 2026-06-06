package com.media_sanctum.backend.service;

import com.media_sanctum.backend.entity.Progress;
import com.media_sanctum.backend.repository.ProgressRepository;
import com.media_sanctum.backend.resource.ProgressResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgressService {

    @Autowired
    private ProgressRepository progressRepository;

    public ProgressService(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    public Progress save(Progress progress) {
        return progressRepository.save(progress);
    }

    public Progress findByBookIdAndUserId(String bookId, String userId) {
        return progressRepository.findByBookIdAndUserId(bookId, userId).orElse(null);
    }

    public Progress getProgress(String id) {
        return progressRepository.findById(id).orElse(null);
    }

    public ProgressResponse getProgressResponse(String id) {
        return toResponse(getProgress(id));
    }

    public static ProgressResponse toResponse(Progress progress) {
        if (progress == null) {
            return null;
        }
        return ProgressResponse.builder()
                .id(progress.getId())
                .editionType(progress.getEditionType())
                .epubcfi(progress.getEpubcfi())
                .percent(progress.getPercent())
                .currentChapter(progress.getCurrentChapter())
                .totalChapters(progress.getTotalChapters())
                .currentPage(progress.getCurrentPage())
                .totalPages(progress.getTotalPages())
                .createdAt(progress.getCreatedAt().toString())
                .updatedAt(progress.getUpdatedAt().toString())
                .build();
    }
}
