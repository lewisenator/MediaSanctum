import { createFileRoute, Link } from '@tanstack/react-router';
import { getBook } from '#/client/mediaSanctumClient.ts';
import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';
import { BsBook } from 'react-icons/bs';
import { FiUpload } from "react-icons/fi";

const bookQueryOptions = (bookId: string) => queryOptions({
  queryKey: ['book', bookId],
  queryFn: () => getBook(bookId),
});

export const Route = createFileRoute('/(authenticated)/books/$bookId')({
  component: BookDetailsPage,
  loader: async ({params: {bookId}, context: {queryClient}}) => {
    return queryClient.ensureQueryData(bookQueryOptions(bookId));
  }
})

function BookDetailsPage() {
  const { bookId } = Route.useParams();
  const { data: book } = useSuspenseQuery(bookQueryOptions(bookId));

  return (
    <div className="flex flex-col flex-1 overflow-y-hidden max-w-6xl mx-auto">
      <div className="flex flex-row">
        { book.ebookEdition && book.ebookEdition.image && book.ebookEdition.image.url ? (
          <img
            src={book.ebookEdition?.image?.url}
            alt={book.title}
            className="max-w-40 rounded-md h-55 w-45 object-contain rounded-md"
          />
        ) : (
          <div className="shrink-0 grow-0 rounded-md border border-border bg-surfaceAlt flex items-center justify-center h-50 w-40 text-center text-4xl">
            <BsBook />
          </div>
        )}

        <div className="flex flex-col ml-5">
          <span className="mb-2 text-[11px] uppercase tracking-[0.16em] font-mono text-accent">Book</span>
          <h1 className="font-display font-semibold text-4xl text-text">{book.title}</h1>
          <p className="mt-3 text-sm text-textDim font-ui">
            by
            <Link
              to="/authors/$authorId"
              params={{authorId: book.author.id}}
              className="hover:underline ml-1 text-text"
            >
              {book.author.name}
            </Link>
          </p>
          { book.featuredSeries && book.featuredSeries.series && book.featuredSeries.series.name && (
            <p className="mt-1 text-xs text-textDim font-ui tracking-wide">
              <Link to="/books/$bookId" params={{bookId: book.id}} className="hover:underline text-text">
                {book.featuredSeries.series.name}
              </Link>
              <span className="ml-1 tabular-nums font-mono">#{book.featuredSeries.position}</span>
            </p>
          )}
          { book.releaseYear && (
            <span className="mt-1 text-xs font-mono text-textMute">
              Released
              <span className="ml-1 tabular-nums">{book.releaseYear}</span>
            </span>
          )}
          { book.description && (
            <div className="mt-4 text-sm leading-relaxed max-w-none text-text font-ui">
              {book.description}
            </div>
          )}
        </div>
      </div>

      <div className="mt-8">
        <h2 className="font-display font-semibold text-lg text-text tracking-tight">Formats</h2>
        <div className="card mt-3 divide-y divide-surfaceAlt">
          <div className="py-4 px-5 flex flex-col">
            <div className="flex flex-row justify-between items-center flex-wrap">
              <div className="flex flex-row">
                <p className="library-card-format">
                  ebook
                </p>
                <div className="ml-5 text-sm text-textDim gap-1 flex flex-row flex-wrap items-center">
                  <span className="tabular-nums">305</span>
                  Pages
                </div>
              </div>
              <div className="">
                <button className="btn btn-secondary text-xs! px-3 py-2 font-ui">
                  <FiUpload /> Upload
                </button>
              </div>
            </div>
            <div className="basis-full text-xs italic text-textMute">
              Files missing - drop a file anywhere on the page or use the buttons above.
            </div>
          </div>
          <div className="p-6">
            Audiobook
          </div>
        </div>
      </div>

      <hr className="mt-5" />
      <div>
        {JSON.stringify(book)}
      </div>
    </div>
  )
}
