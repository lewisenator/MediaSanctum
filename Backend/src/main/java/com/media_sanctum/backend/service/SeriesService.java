package com.media_sanctum.backend.service;

import com.media_sanctum.backend.client.hardcover.model.HardcoverSeries;
import com.media_sanctum.backend.entity.Book;
import com.media_sanctum.backend.repository.BookRepository;
import com.media_sanctum.backend.resource.SeriesResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Service
public class SeriesService {

    private final BookRepository bookRepository;

    public SeriesService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<SeriesResponse> getSeries() {
        var grouped = new LinkedHashMap<Integer, SeriesResponse>();
        bookRepository.findAll().stream()
                .map(Book::getFeaturedSeries)
                .filter(Objects::nonNull)
                .map(featured -> featured.getSeries())
                .filter(series -> series != null && series.getId() != null)
                .forEach(series -> grouped.merge(series.getId(), toResponse(series), (existing, _) -> {
                    existing.setLibraryBooksCount(existing.getLibraryBooksCount() + 1);
                    return existing;
                }));
        return grouped.values().stream()
                .sorted(Comparator.comparing(SeriesResponse::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    private static SeriesResponse toResponse(HardcoverSeries series) {
        return SeriesResponse.builder()
                .hardcoverId(series.getId())
                .name(series.getName())
                .slug(series.getSlug())
                .isCompleted(series.getIsCompleted())
                .booksCount(series.getBooksCount())
                .libraryBooksCount(1L)
                .build();
    }
}
