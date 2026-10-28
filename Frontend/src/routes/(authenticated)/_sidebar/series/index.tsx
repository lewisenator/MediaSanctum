import { createFileRoute } from '@tanstack/react-router';
import { useQuery } from '@tanstack/react-query';
import { RxStack } from 'react-icons/rx';
import { getSeries } from '#/client/seriesClient.ts';
import Main from '#/components/layout/Main.tsx';

export const Route = createFileRoute('/(authenticated)/_sidebar/series/')({
  component: SeriesPage,
});

function SeriesPage() {
  const { data: series, isPending, isError, error } = useQuery({
    queryKey: ['series'],
    queryFn: () => getSeries(),
  });

  return (
    <Main>
      <div className="flex flex-col flex-1 max-w-6xl mx-auto w-full">
        <h1 className="font-display font-semibold text-3xl text-text">
          Series
          { series && series.length > 0 && (
            <span className="text-xs text-textMute ml-2">
              {series.length === 1 ? '1 series' : `${series.length} series`}
            </span>
          )}
        </h1>

        { isPending ? (
          <div className="mt-6 card p-6 text-sm text-textDim" role="status">Loading series...</div>
        ) : isError ? (
          <div className="mt-6 card p-6 text-sm text-textDim" role="alert">
            {error instanceof Error ? error.message : 'Failed to fetch series'}
          </div>
        ) : series.length === 0 ? (
          <div className="mt-6 card p-6 w-full flex flex-col justify-center items-center gap-2 flex-1">
            <div className="text-4xl"><RxStack /></div>
            <div className="text-lg font-display text-text">No series yet.</div>
            <div className="text-sm font-display font-medium text-textDim">
              Series appear here once you add books that belong to one.
            </div>
          </div>
        ) : (
          <ul className="mt-6 grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3">
            { series.map((s) => (
              <li key={s.hardcoverId} className="card p-4 flex flex-col gap-1">
                <span className="font-display font-semibold text-text">{s.name}</span>
                <span className="text-sm text-textDim">
                  {s.libraryBooksCount}{s.booksCount != null ? ` of ${s.booksCount}` : ''} {s.booksCount === 1 || (s.booksCount == null && s.libraryBooksCount === 1) ? 'book' : 'books'} in library
                </span>
                { s.isCompleted != null && (
                  <span className="text-xs text-textMute">{s.isCompleted ? 'Completed' : 'Ongoing'}</span>
                )}
              </li>
            ))}
          </ul>
        )}
      </div>
    </Main>
  );
}
