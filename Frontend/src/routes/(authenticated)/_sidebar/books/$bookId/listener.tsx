import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react';
import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';
import { getBook } from '#/client/bookClient.ts';

const bookQueryOptions = (bookId: string) => queryOptions({
  queryKey: ['book', bookId],
  queryFn: async () => {
    console.log('Fetching book ', bookId);
    const res = await getBook(bookId)
    console.log('Fetched book book ', bookId);
    return res;
  }
});

export const Route = createFileRoute(
  '/(authenticated)/_sidebar/books/$bookId/listener',
)({
  component: ListenerPage,
  loader: async ({params: {bookId}, context: {queryClient}}) => {
    return queryClient.ensureQueryData(bookQueryOptions(bookId));
  }
});

function ListenerPage() {
  const { bookId } = Route.useParams();
  const { data: book } = useSuspenseQuery(bookQueryOptions(bookId));

  const [playing, setPlaying] = useState(false);

  return (
    <div
      className=""
    >
      <audio src={book.audiobookFile!.url} controls>

      </audio>
    </div>
  );
}
