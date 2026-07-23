package com.media_sanctum.backend.service;

import com.media_sanctum.backend.client.hardcover.model.HardcoverAuthor;
import com.media_sanctum.backend.client.hardcover.model.HardcoverLink;
import com.media_sanctum.backend.entity.Author;
import com.media_sanctum.backend.entity.Image;
import com.media_sanctum.backend.repository.AuthorRepository;
import com.media_sanctum.backend.resource.AuthorResponse;
import com.media_sanctum.backend.resource.LinkResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorResponse> getAuthorsResponse() {
        return getAuthors().stream().map(AuthorService::toResponse).toList();
    }

    public List<Author> getAuthors() {
        var sortByCreatedAt = Sort.by("createdAt").descending();
        var pageRequest = PageRequest.of(0, 100, sortByCreatedAt);
        var entities = authorRepository.findAll(pageRequest);
        return entities.stream().toList();
    }

    public AuthorResponse getAuthorResponse(String id) {
        var author = getAuthor(id);
        return toResponse(author);
    }

    public Author getAuthor(String id) {
        return authorRepository.findById(id).orElse(null);
    }

    public Optional<Author> getAuthorByHardcoverId(Integer hardcoverId) {
        return authorRepository.findByHardcoverId(hardcoverId);
    }

    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Author addAuthor(HardcoverAuthor hardcoverAuthor, Image image) {
        var author = Author.builder()
                .hardcoverId(hardcoverAuthor.getId())
                .name(hardcoverAuthor.getName())
                .title(hardcoverAuthor.getTitle())
                .alternateNames(hardcoverAuthor.getAlternateNames())
                .slug(hardcoverAuthor.getSlug())
                .bio(hardcoverAuthor.getBio())
                .bornYear(hardcoverAuthor.getBornYear())
                .deathYear(hardcoverAuthor.getDeathYear())
                .booksCount(hardcoverAuthor.getBooksCount())
                .links(hardcoverAuthor.getLinks())
                .build();
        author.setImage(image);
        return saveAuthor(author);
    }

    public static AuthorResponse toResponse(Author author) {
        if (author == null) {
            return null;
        }
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .title(author.getTitle())
                .alternateNames(author.getAlternateNames())
                .slug(author.getSlug())
                .bio(author.getBio())
                .bornYear(author.getBornYear())
                .deathYear(author.getDeathYear())
                .booksCount(author.getBooksCount())
                .libraryBooksCount(author.getLibraryBooksCount())
                .links(author.getLinks().stream().map(AuthorService::toLinkResponse).toList())
                .image(ImageService.toResponse(author.getImage()))
                .createdAt(author.getCreatedAt().toString())
                .updatedAt(author.getUpdatedAt().toString())
                .build();
    }

    public static LinkResponse toLinkResponse(HardcoverLink hardcoverLink) {
        return LinkResponse.builder()
                .url(hardcoverLink.getUrl())
                .title(hardcoverLink.getTitle())
                .build();
    }
}
