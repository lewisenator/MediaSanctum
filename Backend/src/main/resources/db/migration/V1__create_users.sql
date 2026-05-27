CREATE TABLE users (
   id               TEXT NOT NULL PRIMARY KEY,
   email            TEXT NOT NULL,
   first_name       TEXT,
   last_name        TEXT,
   password_hash    TEXT NOT NULL,
   is_active        INTEGER NOT NULL DEFAULT 1,
   created_at       TEXT NOT NULL,
   updated_at       TEXT NOT NULL,
   CONSTRAINT uq_users_email UNIQUE (email)
);