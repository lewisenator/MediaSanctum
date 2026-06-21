package com.media_sanctum.backend.service;

import com.media_sanctum.backend.entity.EbookProgress;
import com.media_sanctum.backend.repository.EbookProgressRepository;
import com.media_sanctum.backend.resource.EbookProgressResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EbookProgressService {

    @Autowired
    private EbookProgressRepository ebookProgressRepository;

    public EbookProgressService(EbookProgressRepository ebookProgressRepository) {
        this.ebookProgressRepository = ebookProgressRepository;
    }

    public EbookProgress save(EbookProgress ebookProgress) {
        return ebookProgressRepository.save(ebookProgress);
    }

    public EbookProgress findByBookIdAndUserId(String bookId, String userId) {
        return ebookProgressRepository.findByBookIdAndUserId(bookId, userId).orElse(null);
    }

    public EbookProgress getProgress(String id) {
        return ebookProgressRepository.findById(id).orElse(null);
    }

    public EbookProgressResponse getProgressResponse(String id) {
        return toResponse(getProgress(id));
    }

    public static EbookProgressResponse toResponse(EbookProgress ebookProgress) {
        if (ebookProgress == null) {
            return null;
        }
        return EbookProgressResponse.builder()
                .id(ebookProgress.getId())
                .epubcfi(ebookProgress.getEpubcfi())
                .percent(ebookProgress.getPercent())
                .currentChapter(ebookProgress.getCurrentChapter())
                .totalChapters(ebookProgress.getTotalChapters())
                .currentPage(ebookProgress.getCurrentPage())
                .totalPages(ebookProgress.getTotalPages())
                .createdAt(ebookProgress.getCreatedAt().toString())
                .updatedAt(ebookProgress.getUpdatedAt().toString())
                .build();
    }
}
