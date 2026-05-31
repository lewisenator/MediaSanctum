CREATE TABLE editions (
    id               TEXT NOT NULL PRIMARY KEY,
    hardcover_id     INTEGER,
    image_id         TEXT,
    asin             TEXT,
    isbn10           TEXT,
    isbn13           TEXT,
    language         TEXT,
    country          TEXT,
    edition_type     TEXT NOT NULL,
    created_at       TEXT NOT NULL,
    updated_at       TEXT NOT NULL,
    CONSTRAINT uq_editions_hardcover_id UNIQUE (hardcover_id)
);