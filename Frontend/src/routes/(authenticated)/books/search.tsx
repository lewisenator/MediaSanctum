import { createFileRoute, Link } from '@tanstack/react-router';
import { FaLongArrowAltLeft } from "react-icons/fa";
import { IoIosSearch } from "react-icons/io";
import { useDebounce } from 'use-debounce';
import { useState } from 'react';
import { searchBooks } from '#/client/mediaSanctumClient.ts';
import BookSearchResult from '#/components/BookSearchResult.tsx';
import { useQuery } from '@tanstack/react-query';


export const Route = createFileRoute('/(authenticated)/books/search')({
  component: BookSearchPage,
});

function BookSearchPage() {
  const [query, setQuery] = useState('');
  const [debouncedQuery] = useDebounce(query, 300);

  const { data: searchResults, isLoading, isError, error } = useQuery({
    queryKey: ['books', debouncedQuery],
    queryFn: () => searchBooks(debouncedQuery),
    enabled: debouncedQuery?.length > 1
  });

  return (
    <div className="flex flex-col flex-1 overflow-y-hidden max-w-6xl mx-auto">
      <div className="w-full flex flex-row flex-wrap justify-between mb-6">
        <h1 className="font-display font-semibold text-3xl text-text">
          Book Search
        </h1>
        <Link
          to="/books"
          className="btn btn-secondary text-sm"
        >
          <FaLongArrowAltLeft /> Go Back
        </Link>
      </div>
      <div className="flex flex-wrap items-center gap-2">
        <div className="card p-4 w-full">
          <label className="card-label">
            Add a new book by title
          </label>
          <div className="search-bar relative flex-1 min-w-[16rem] w-full">
            <IoIosSearch />
            <input
              type="search"
              placeholder="Search for a title..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </div>

          { isLoading && (
            <p className="status mt-3">Loading...</p>
          )}

          { isError && (
            <p className="status error mt-3 text-danger">{error.message}</p>
          )}

          { searchResults && searchResults.hits && searchResults.hits.length > 0 && (
            <div className="flex flex-col rounded-md  mt-3 border border-border bg-surface overflow-x-hidden max-h-screen">
              <div className="grow-1 min-h-0 divide-y divide-surfaceAlt">
                { searchResults.hits.map((book) => (
                  <BookSearchResult book={book} key={book.hardcoverId} />
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
