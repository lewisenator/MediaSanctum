package com.media_sanctum.backend.manager;

import com.media_sanctum.backend.config.RequestContext;
import com.media_sanctum.backend.resource.BookResponse;
import com.media_sanctum.backend.service.AudiobookProgressService;
import com.media_sanctum.backend.service.BookService;
import com.media_sanctum.backend.service.EbookProgressService;
import org.springframework.stereotype.Service;

@Service
public class CatalogueManager {

    private final BookService bookService;
    private final EbookProgressService ebookProgressService;
    private final AudiobookProgressService audiobookProgressService;
    private final RequestContext requestContext;

    public CatalogueManager(
            BookService bookService,
            EbookProgressService ebookProgressService,
            AudiobookProgressService audiobookProgressService,
            RequestContext requestContext
    ) {
        this.bookService = bookService;
        this.ebookProgressService = ebookProgressService;
        this.audiobookProgressService = audiobookProgressService;
        this.requestContext = requestContext;
    }

    public BookResponse getBookResponse(String id) {
        var result = bookService.getBookResponse(id);
        var userId = requestContext.getUser().getId();

        var ebookProgress = ebookProgressService.findByBookIdAndUserId(id, userId);
        if (ebookProgress != null) {
            result.setEbookProgress(EbookProgressService.toResponse(ebookProgress));
        }

        var audiobookProgress = audiobookProgressService.findByBookIdAndUserId(id, userId);
        if (audiobookProgress != null) {
            result.setAudiobookProgress(AudiobookProgressService.toResponse(audiobookProgress));
        }

        return result;
    }
}
