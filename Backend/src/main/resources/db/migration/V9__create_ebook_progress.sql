CREATE TABLE ebook_progress (
    id               TEXT NOT NULL PRIMARY KEY,
    user_id          TEXT NOT NULL,
    book_id          TEXT NOT NULL,
    epubcfi          TEXT NOT NULL,
    percent          INTEGER,
    current_chapter  INTEGER,
    total_chapters   INTEGER,
    current_page     INTEGER,
    total_pages      INTEGER,
    created_at       TEXT NOT NULL,
    updated_at       TEXT NOT NULL,
    CONSTRAINT fk_ebook_progress_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ebook_progress_book_id FOREIGN KEY (book_id) REFERENCES books(id)
);