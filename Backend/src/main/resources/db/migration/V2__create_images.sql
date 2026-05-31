CREATE TABLE images (
    id               TEXT NOT NULL PRIMARY KEY,
    hardcover_id     INTEGER,
    filename         TEXT NOT NULL,
    directory        TEXT NOT NULL,
    extension        TEXT NOT NULL,
    color            TEXT,
    width            INTEGER,
    height           INTEGER,
    image_type             TEXT NOT NULL,
    created_at       TEXT NOT NULL,
    updated_at       TEXT NOT NULL,
    CONSTRAINT uq_images_hardcover_id UNIQUE (hardcover_id)
);