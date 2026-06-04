import { createFileRoute, Link } from '@tanstack/react-router';
import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';
import { getBooks } from '#/client/mediaSanctumClient.ts';
import { IoIosSearch } from 'react-icons/io';
import { useState } from 'react';
import { BsBook } from "react-icons/bs";
import BookCard from '#/components/book/BookCard.tsx';
import Main from '#/components/layout/Main.tsx';

const bookQueryOptions = () => queryOptions({
  queryKey: ['books'],
  queryFn: () => getBooks()
});

export const Route = createFileRoute('/(authenticated)/_sidebar/books/')({
  component: BooksPage,
  loader: async ({ context: { queryClient } }) => {
    return queryClient.ensureQueryData(bookQueryOptions());
  }
})

function BooksPage() {
  const { data: allBooks } = useSuspenseQuery(bookQueryOptions());
  const [query, setQuery] = useState('');

  let books = allBooks /*.filter((book) => {
    if (!query || query.trim() === '') return true;
    return false;
  }); */

  return (
    <Main>
      <div className="flex flex-col flex-1 overflow-y-hidden max-w-6xl mx-auto">
        <div className="w-full flex flex-row flex-wrap justify-between">
          <h1 className="font-display font-semibold text-3xl text-text">
            Books
            { books && (
              <span className="text-xs text-textMute ml-2">
                {books.length > 1 ? `${books.length} titles` : '1 title'}
              </span>
            )}
          </h1>
          <Link
            to="/books/search"
            className="btn btn-secondary text-sm"
          >
            Add New Book
          </Link>
        </div>

        <div className="w-full flex flex-wrap items-center gap-2 mt-6">
          <div className="card p-4 w-full">
            <label className="card-label">
              Search your catalogue
            </label>
            <div className="search-bar relative flex-1 min-w-[16rem] w-full">
              <IoIosSearch />
              <input
                type="search"
                placeholder="Title, Author, ISBN, etc..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
              />
            </div>
          </div>
        </div>

        { books && books.length > 0 ? (
          <div className="mt-6 flex flex-wrap items-center overflow-y-scroll w-full">
            <div className="grid gap-4 grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 2xl:grid-cols-7 overflow-y-scroll">
              { books.map((book) => (
                <BookCard book={book} key={book.id} />
              ))}
            </div>
          </div>
        ) : (
          <div className="mt-6 card p-6 w-full flex flex-col justify-center items-center gap-2 flex-1">
            <div className="text-4xl">
              <BsBook />
            </div>
            <div className="text-lg font-display text-text">No books yet.</div>
            <div className="text-sm font-display font-medium text-textDim">Add a book to your catalogue by clicking the "Add New Book" at button.</div>
          </div>
        )}
      </div>
    </Main>
  )
}
