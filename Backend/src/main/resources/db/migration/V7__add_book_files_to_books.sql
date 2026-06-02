alter table books
    add column ebook_file_id text
    references book_files(id)
    default null;

alter table books
    add column audiobook_file_id text
    references book_files(id)
    default null;
