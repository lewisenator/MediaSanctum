package com.media_sanctum.backend.entity;

import com.media_sanctum.backend.client.hardcover.model.HardcoverLink;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "authors")
public class Author {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "hardcover_id")
    private Integer hardcoverId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "title")
    private String title;

    @Column(name = "alternate_names", columnDefinition = "TEXT")
    @JdbcTypeCode(SqlTypes.JSON_ARRAY)
    private List<String> alternateNames;

    @Column(name = "slug")
    private String slug;

    @Column(name = "bio")
    private String bio;

    @Column(name = "born_year")
    private Integer bornYear;

    @Column(name = "death_year")
    private Integer deathYear;

    @Column(name = "books_count")
    private Integer booksCount;

    @Column(name = "links", columnDefinition = "TEXT")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<HardcoverLink> links;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id")
    private Image image;

    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    @PrePersist
    private void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
