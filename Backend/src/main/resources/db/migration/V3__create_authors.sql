CREATE TABLE authors (
    id               TEXT NOT NULL PRIMARY KEY,
    hardcover_id     INTEGER,
    image_id         TEXT,
    name             TEXT NOT NULL,
    title            TEXT,
    alternate_names  TEXT,
    slug             TEXT,
    bio              TEXT,
    born_year        INTEGER,
    death_year       INTEGER,
    books_count      INTEGER,
    links            TEXT,
    created_at       TEXT NOT NULL,
    updated_at       TEXT NOT NULL,
    CONSTRAINT uq_authors_hardcover_id UNIQUE (hardcover_id),
    CONSTRAINT fk_authors_image_id FOREIGN KEY (image_id) REFERENCES images(id)
);