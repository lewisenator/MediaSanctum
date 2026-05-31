CREATE TABLE books (
    id               TEXT NOT NULL PRIMARY KEY,
    hardcover_id     INTEGER,
    author_id        TEXT,
    headline         TEXT,
    title            TEXT,
    slug             TEXT,
    subtitle         TEXT,
    description      TEXT,
    release_year     INTEGER,
    pages            INTEGER,
    audio_seconds    INTEGER,
    created_at       TEXT NOT NULL,
    updated_at       TEXT NOT NULL,
    CONSTRAINT uq_books_hardcover_id UNIQUE (hardcover_id),
    CONSTRAINT fk_books_author_id FOREIGN KEY (author_id) REFERENCES authors(id)
);