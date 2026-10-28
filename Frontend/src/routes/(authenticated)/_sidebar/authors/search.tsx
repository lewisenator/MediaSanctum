import { createFileRoute, Link, useNavigate } from '@tanstack/react-router';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useDebounce } from 'use-debounce';
import { FaLongArrowAltLeft } from 'react-icons/fa';
import { IoIosSearch } from 'react-icons/io';
import { RxPerson } from 'react-icons/rx';
import { addAuthor } from '#/client/authorClient.ts';
import { searchAuthors, type AuthorResult } from '#/client/searchClient.ts';
import Main from '#/components/layout/Main.tsx';

export const Route = createFileRoute('/(authenticated)/_sidebar/authors/search')({
  component: AuthorSearchPage,
})

function AuthorSearchResult({ author }: { author: AuthorResult }) {
  const navigate = useNavigate();
  const { mutate, isPending, isError, error } = useMutation({
    mutationFn: () => addAuthor(author.hardcoverId),
    onSuccess: (added) => navigate({ to: '/authors/$authorId', params: { authorId: added.id } }),
  });

  return (
    <button
      type="button"
      className="flex items-center gap-3 px-3 py-2 hover:bg-surfaceAlt w-full text-left"
      onClick={() => mutate()}
      disabled={isPending}
    >
      { author.imageUrl ? (
        <img className="object-cover h-16 w-12 shrink-0" src={author.imageUrl} alt={author.name} />
      ) : (
        <div className="flex items-center justify-center h-16 w-12 shrink-0 bg-surfaceAlt text-xl border border-border">
          <RxPerson />
        </div>
      )}
      <div>
        <p className="font-display font-medium text-text">{author.name}</p>
        <p className="text-sm italic font-light text-textDim">
          {`${author.booksCount} ${author.booksCount === 1 ? 'book' : 'books'}`}
        </p>
        { isError && <p className="text-sm text-danger">{error.message}</p> }
      </div>
    </button>
  );
}

function AuthorSearchPage() {
  const [query, setQuery] = useState('');
  const [debouncedQuery] = useDebounce(query, 300);

  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['authorSearch', debouncedQuery],
    queryFn: () => searchAuthors(debouncedQuery),
    enabled: debouncedQuery.length > 1,
  });

  return (
    <Main>
      <div className="flex flex-col flex-1 overflow-y-hidden max-w-6xl mx-auto">
        <div className="w-full flex flex-row flex-wrap justify-between mb-6">
          <h1 className="font-display font-semibold text-3xl text-text">Author Search</h1>
          <Link to="/authors" className="btn btn-secondary text-sm">
            <FaLongArrowAltLeft /> Go Back
          </Link>
        </div>
        <div className="card p-4 w-full">
          <label className="card-label" htmlFor="author-search">Add a new author by name</label>
          <div className="search-bar relative flex-1 min-w-[16rem] w-full">
            <IoIosSearch />
            <input
              id="author-search"
              type="search"
              placeholder="Search for an author..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </div>

          { isLoading && <p className="status mt-3">Loading...</p> }
          { isError && <p className="status error mt-3 text-danger">{error.message}</p> }
          { data && data.hits.length === 0 && <p className="status mt-3">No authors found.</p> }
          { data && data.hits.length > 0 && (
            <div className="flex flex-col rounded-md mt-3 border border-border bg-surface overflow-x-hidden max-h-screen divide-y divide-surfaceAlt">
              { data.hits.map((author) => (
                <AuthorSearchResult author={author} key={author.hardcoverId} />
              ))}
            </div>
          )}
        </div>
      </div>
    </Main>
  );
}
