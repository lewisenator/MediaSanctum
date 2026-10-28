import { createFileRoute, Link } from '@tanstack/react-router';
import { useQuery } from '@tanstack/react-query';
import { FaLongArrowAltLeft } from 'react-icons/fa';
import { RxPerson } from 'react-icons/rx';
import { BsBook } from 'react-icons/bs';
import { getAuthor } from '#/client/authorClient.ts';
import { getBooks } from '#/client/bookClient.ts';
import BookCard from '#/components/book/BookCard.tsx';
import Main from '#/components/layout/Main.tsx';

export const Route = createFileRoute('/(authenticated)/_sidebar/authors/$authorId')({
  component: AuthorDetailsPage,
})

function AuthorDetailsPage() {
  const { authorId } = Route.useParams();

  const author = useQuery({
    queryKey: ['author', authorId],
    queryFn: () => getAuthor(authorId),
  });
  const books = useQuery({
    queryKey: ['books'],
    queryFn: () => getBooks(),
  });

  const authorBooks = (books.data ?? []).filter((book) => book.author?.id === authorId);

  return (
    <Main>
      <div className="flex flex-col flex-1 max-w-6xl mx-auto w-full">
        <div className="w-full flex flex-row flex-wrap justify-between mb-6">
          <h1 className="font-display font-semibold text-3xl text-text">
            {author.data?.name ?? 'Author'}
          </h1>
          <Link to="/authors" className="btn btn-secondary text-sm">
            <FaLongArrowAltLeft /> Go Back
          </Link>
        </div>

        { author.isLoading && <p className="status">Loading...</p> }
        { author.isError && <p className="status error text-danger">{author.error.message}</p> }

        { author.data && (
          <div className="card p-4 flex flex-row gap-4">
            { author.data.image ? (
              <img
                src={author.data.image.url}
                alt={`${author.data.name} Image`}
                className="cover-image w-32 shrink-0"
              />
            ) : (
              <div className="cover-image flex items-center justify-center h-40 w-32 shrink-0 text-4xl">
                <RxPerson />
              </div>
            )}
            <div>
              { author.data.bornYear && (
                <p className="font-mono text-xs text-textMute">
                  {author.data.bornYear} - {author.data.deathYear || 'Present'}
                </p>
              )}
              { author.data.bio && (
                <p className="text-sm text-textDim mt-2">{author.data.bio}</p>
              )}
            </div>
          </div>
        )}

        { author.data && (
          <>
            <h2 className="font-display font-semibold text-xl text-text mt-6 mb-3">Books</h2>
            { books.isLoading && <p className="status">Loading books...</p> }
            { books.isError && <p className="status error text-danger">{books.error.message}</p> }
            { books.data && authorBooks.length > 0 && (
              <div className="grid gap-4 grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6">
                { authorBooks.map((book) => (
                  <BookCard book={book} key={book.id} />
                ))}
              </div>
            )}
            { books.data && authorBooks.length === 0 && (
              <div className="card p-6 flex flex-col items-center gap-2">
                <div className="text-4xl"><BsBook /></div>
                <div className="text-lg font-display text-text">No books yet.</div>
              </div>
            )}
          </>
        )}
      </div>
    </Main>
  );
}
