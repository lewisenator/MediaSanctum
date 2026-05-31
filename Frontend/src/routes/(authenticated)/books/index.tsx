import { createFileRoute, Link } from '@tanstack/react-router';
import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';
import { getBooks } from '#/client/mediaSanctumClient.ts';
import { IoIosSearch } from 'react-icons/io';
import { useState } from 'react';

const bookQueryOptions = () => queryOptions({
  queryKey: ['books'],
  queryFn: () => getBooks()
});

export const Route = createFileRoute('/(authenticated)/books/')({
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
    <>
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

      <div className="flex flex-wrap items-center gap-2 mt-6">
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
        <div className="mt-6">
          { books.map((book) => (
            <div key={book.id}>
              <div>{book.title}</div>
              <div>{JSON.stringify(book)}</div>
            </div>
          ))}
        </div>
      ) : (
        <div>No books found.</div>
      )}
    </>
  )
}
