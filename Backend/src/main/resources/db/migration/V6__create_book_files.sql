CREATE TABLE book_files (
    id               TEXT NOT NULL PRIMARY KEY,
    size             INTEGER NOT NULL,
    hash             TEXT NOT NULL,
    directory        TEXT NOT NULL,
    filename         TEXT NOT NULL,
    content_type     TEXT NOT NULL,
    extension        TEXT NOT NULL,
    edition_type     TEXT NOT NULL,
    book_id          TEXT NOT NULL,
    ffprobe          TEXT,
    created_at       TEXT NOT NULL,
    updated_at       TEXT NOT NULL,
    CONSTRAINT fk_book_files_book_id FOREIGN KEY (book_id) REFERENCES books(id)
);