import { createFileRoute, Link } from '@tanstack/react-router';
import { type Book, getBook } from '#/client/bookClient.ts';
import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';
import { BsBook } from 'react-icons/bs';
import TimeAgo from '#/components/formatting/TimeAgo.tsx';
import { useState } from 'react';
import BookFormat from '#/components/book/BookFormat.tsx';
import Main from '#/components/layout/Main.tsx';
import LessMore from '#/components/formatting/LessMore.tsx';
import Breadcrumbs, { breadcrumbClassName } from '#/components/layout/Breadcrumbs.tsx';
import { CiHeadphones } from "react-icons/ci";
import { PiBookLight } from "react-icons/pi";

const bookQueryOptions = (bookId: string) => queryOptions({
  queryKey: ['book', bookId],
  queryFn: () => getBook(bookId),
});

export const Route = createFileRoute('/(authenticated)/_sidebar/books/$bookId')({
  component: BookDetailsPage,
  loader: async ({params: {bookId}, context: {queryClient}}) => {
    return queryClient.ensureQueryData(bookQueryOptions(bookId));
  }
})

function BookDetailsPage() {
  const { queryClient } = Route.useRouteContext();
  const { bookId } = Route.useParams();
  const { data: queriedBook } = useSuspenseQuery(bookQueryOptions(bookId));
  const [book, setBookUnwrapped] = useState<Book>(queriedBook);
  const [selectedFormat, setSelectedFormat] = useState<string>("ebook");

  const setBook = async (newBook: Book) => {
    setBookUnwrapped(newBook);
    await queryClient.invalidateQueries({ queryKey: ['book', bookId] });
  };

  const genres = [...(new Set(book.tags
    ?.filter((tag) => tag.category === 'Genre' )
    ?.sort((a, b) => b.count - a.count)
    ?.map((tag) => tag.tag)) || [])]
    .slice(0, 5)
    .join(', ') || '';

  return (
    <Main>
      <div className="flex flex-col flex-1 max-w-6xl mx-auto">
        <Breadcrumbs
          className="mb-4"
          items={[
            <Link to="/books" className={breadcrumbClassName}>Books</Link>,
            <Link to="/books/$bookId" className={breadcrumbClassName} params={{bookId: bookId}}>{book.title}</Link>
          ]}
        />
        <div className="flex flex-row">
          { book.ebookEdition && book.ebookEdition.image && book.ebookEdition.image.url ? (
            <img
              src={book.ebookEdition?.image?.url}
              alt={book.title}
              className="max-w-40 h-55 w-45 object-cover drop-shadow"
            />
          ) : (
            <div className="shrink-0 grow-0 rounded-md border border-border bg-surfaceAlt flex items-center justify-center h-50 w-40 text-center text-4xl drop-shadow">
              <BsBook />
            </div>
          )}

          <div className="flex flex-col ml-5">
            <h1 id="book-title" className="font-display font-semibold text-4xl text-text">{book.title}</h1>
            <p id="book-author" className="mt-1 text-md text-textDim font- italic">
              by
              <Link
                to="/authors/$authorId"
                params={{authorId: book.author.id}}
                className="hover:underline ml-1"
              >
                {book.author.name}
              </Link>
            </p>

            <div
              className="flex flex-row flex-wrap gap-6 mt-3"
            >
              {/* Metadata */}
              { book.releaseYear && (
                <div className="">
                  <div className="uppercase text-xs text-textMute font-ui">Year</div>
                  <div className="text-sm tabular-nums">{book.releaseYear}</div>
                </div>
              )}

              { book.createdAt && (
                <div className="">
                  <div className="uppercase text-xs text-textMute font-ui">Added</div>
                  <div className="text-sm"><TimeAgo date={new Date(book.createdAt)} /></div>
                </div>
              )}

              { book.featuredSeries && book.featuredSeries.series &&
                book.featuredSeries.series.name && (
                <div className="">
                  <div className="uppercase text-xs text-textMute font-ui">Series</div>
                  <div className="text-sm">
                    <Link to="/books/$bookId" params={{bookId: book.id}} className="hover:underline text-text">
                      {book.featuredSeries.series.name}
                    </Link>
                    <span className="ml-1 tabular-nums font-mono">#{book.featuredSeries.position}</span>
                  </div>
                </div>
              )}

              { genres.length > 0 && (
                <div className="">
                  <div className="uppercase text-xs text-textMute font-ui">Genres</div>
                  <div className="text-sm">{ genres }</div>
                </div>
              )}
            </div>
          </div>
        </div>

        <div
          className="uppercase text-xs font-ui text-textMute mt-8"
        >
          About
        </div>

        <div>
          { book.description && (
            <div className="mt-4 max-w-none">
              <LessMore
                className="text-sm leading-relaxed text-text font-ui"
                text={book.description}
                limit={350}
              />
            </div>
          )}
        </div>

        <div
          className="flex flex-row mt-8 bg-surfaceAlt border border-border p-1 rounded-lg max-w-53"
        >
          <span
            onClick={() => setSelectedFormat("ebook")}
            className={`inline-flex justify-center items-center py-2 px-3 m-0 text-sm hover:bg-surface rounded-md cursor-pointer text-textDim ${selectedFormat === 'ebook' && 'drop-shadow-sm text-text!'}`}
            style={{
              backgroundColor: selectedFormat === 'ebook' ? 'var(--c-surface)' : 'var(--c-surfaceAlt)',
            }}
          >
            <PiBookLight className={`mr-1 ${selectedFormat === 'ebook' && 'text-accent'}`} /> Ebook
          </span>
          <span
            onClick={() => setSelectedFormat("audiobook")}
            className={`inline-flex justify-center items-center py-2 px-3 m-0 text-sm hover:bg-surface rounded-md cursor-pointer text-textDim ${selectedFormat === 'audiobook' && 'drop-shadow-sm text-text!'}`}
            style={{
              backgroundColor: selectedFormat === 'audiobook' ? 'var(--c-surface)' : 'var(--c-surfaceAlt)',
            }}
          >
            <CiHeadphones className={`mr-1 ${selectedFormat === 'audiobook' && 'text-accent'}`} /> Audiobook
          </span>
        </div>

        { selectedFormat === 'ebook' && (
          <BookFormat
            bookId={book.id}
            setBook={setBook}
            format={"ebook"}
            metric={"Pages"}
            amount={book.pages}
            bookFile={book.ebookFile}
          />
        )}

        { selectedFormat === 'audiobook' && (
          <BookFormat
            bookId={book.id}
            setBook={setBook}
            format={"audiobook"}
            metric={"Seconds"}
            amount={book.ebookEdition?.audioSeconds}
            bookFile={book.audiobookFile}
          />
        )}

      </div>
    </Main>
  )
}
