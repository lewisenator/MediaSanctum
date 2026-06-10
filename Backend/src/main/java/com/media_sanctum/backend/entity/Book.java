package com.media_sanctum.backend.entity;

import com.media_sanctum.backend.client.hardcover.model.HardcoverFeaturedSeriesSearchResult;
import com.media_sanctum.backend.client.hardcover.model.HardcoverLink;
import com.media_sanctum.backend.client.hardcover.model.HardcoverTag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "books")
public class Book {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "hardcover_id")
    private Integer hardcoverId;

    @Column(name = "headline")
    private String headline;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug")
    private String slug;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "description")
    private String description;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "pages")
    private Integer pages;

    @Column(name = "audio_seconds")
    private Integer audioSeconds;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ebook_edition_id")
    private Edition ebookEdition;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ebook_file_id")
    private BookFile ebookFile;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "audiobook_edition_id")
    private Edition audiobookEdition;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "audiobook_file_id")
    private BookFile audiobookFile;

    @Column(name = "rating")
    private Float rating;

    @Column(name = "ratings_count")
    private Integer ratingsCount;

    @Column(name = "tags", columnDefinition = "TEXT")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<HardcoverTag> tags;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "featured_series", columnDefinition = "TEXT")
    @JdbcTypeCode(SqlTypes.JSON)
    private HardcoverFeaturedSeriesSearchResult featuredSeries;

    public void setAuthor(Author author) {
        this.author = author;
        author.addBook(this);
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
