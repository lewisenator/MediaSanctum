import { createFileRoute, Link } from '@tanstack/react-router';
import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';
import { getAuthors } from '#/client/authorClient.ts';
import { useState } from 'react';
import { IoIosSearch } from 'react-icons/io';
import Main from '#/components/layout/Main.tsx';
import { RxPerson } from "react-icons/rx";
import AuthorCard from '#/components/author/AuthorCard.tsx';

const authorsQueryOptions = () => queryOptions({
  queryKey: ['authors'],
  queryFn: () => getAuthors()
});

export const Route = createFileRoute('/(authenticated)/_sidebar/authors/')({
  component: AuthorsPage,
  loader: async ({ context: { queryClient } }) => {
    return queryClient.ensureQueryData(authorsQueryOptions());
  }
});

function AuthorsPage() {
  const { data: authors } = useSuspenseQuery(authorsQueryOptions());
  const [query, setQuery] = useState('');

  return (
    <Main>
      <div className="flex flex-col flex-1 overflow-y-hidden max-w-6xl mx-auto">
        <div className="w-full flex flex-row flex-wrap justify-between">
          <h1 className="font-display font-semibold text-3xl text-text">
            Authors
            { authors && (
              <span className="text-xs text-textMute ml-2">
                {authors.length > 1 ? `${authors.length} authors` : '1 author'}
              </span>
            )}
          </h1>
          <Link
            to="/authors/search"
            className="btn btn-secondary text-sm"
          >
            Add New Author
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
                placeholder="First Name, Last Name, etc..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
              />
            </div>
          </div>
        </div>

        { authors && authors.length > 0 ? (
          <div className="mt-6 flex flex-wrap items-center overflow-y-scroll w-full">
            <div className="grid gap-4 grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 2xl:grid-cols-7 overflow-y-scroll">
              { authors.map((author) => (
                <AuthorCard author={author} key={author.id} />
              ))}
            </div>
          </div>
        ) : (
          <div className="mt-6 card p-6 w-full flex flex-col justify-center items-center gap-2 flex-1">
            <div className="text-4xl">
              <RxPerson />
            </div>
            <div className="text-lg font-display text-text">No books yet.</div>
            <div className="text-sm font-display font-medium text-textDim">Add a book to your catalogue by clicking the "Add New Book" at button.</div>
          </div>
        )}
      </div>
    </Main>
  )
}
