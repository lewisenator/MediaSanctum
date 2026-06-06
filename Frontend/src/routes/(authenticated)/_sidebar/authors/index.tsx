import { createFileRoute, Link } from '@tanstack/react-router';
import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';
import { getAuthors } from '#/client/authorClient.ts';
import { useState } from 'react';
import { IoIosSearch } from 'react-icons/io';
import Main from '#/components/layout/Main.tsx';

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

      <div className="flex flex-wrap items-center gap-2 mt-6">
        <div className="card p-4 w-full">
          <label className="card-label">
            Search your followed authors
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
        <div className="mt-6">
          { authors.map((author) => (
            <div key={author.id}>
              <div>{author.name}</div>
              {author.image && (
                <img src={author.image?.url} alt={`${author.name} Mugshot`} className="object-cover h-40 max-w-30" />
              )}

              <div>{JSON.stringify(author)}</div>
            </div>
          ))}
        </div>
      ) : (
        <div>No authors found.</div>
      )}
    </Main>
  )
}
