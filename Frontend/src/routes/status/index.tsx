import { createFileRoute } from '@tanstack/react-router';
import { health, type HealthResponse } from '#/client/mediaSanctumClient.ts';
import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';

const statusQueryOptions = () => queryOptions({
  queryKey: ['status'],
  queryFn: () => health()
});

export const Route = createFileRoute('/status/')({
  component: StatusPage,
  loader: async ({context: {queryClient}}): Promise<HealthResponse> => {
    return queryClient.ensureQueryData(statusQueryOptions());
  }
});

function StatusPage() {
  const {data: status} = useSuspenseQuery(statusQueryOptions());

  return (
    <>
      <h1>Status</h1>
      <p>
        {status.status}
      </p>
    </>
  );
}
