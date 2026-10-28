import { createFileRoute } from '@tanstack/react-router';
import { health, type HealthResponse } from '#/client/healthClient.ts';
import { queryOptions, useQuery } from '@tanstack/react-query';

const statusQueryOptions = () => queryOptions({
  queryKey: ['status'],
  queryFn: () => health()
});

export const Route = createFileRoute('/(authenticated)/_sidebar/status/')({
  component: StatusPage,
  loader: ({context: {queryClient}}) => {
    // Prefetch only; loading and error states render in the page itself.
    void queryClient.prefetchQuery(statusQueryOptions());
  }
});

function StatusItem({label, value, up}: {label: string; value: string; up: boolean}) {
  return (
    <li className="flex items-center justify-between gap-4 py-2" data-testid="status-item">
      <span className="text-text">{label}</span>
      <span className={up ? 'text-green-600' : 'text-red-600'}>
        <span aria-hidden="true">{up ? '●' : '○'}</span> {value}
      </span>
    </li>
  );
}

export function StatusView({status}: {status: HealthResponse}) {
  const up = status.status === 'UP';
  return (
    <ul className="divide-y divide-current/10">
      <StatusItem label="Service" value={up ? 'Up' : 'Down'} up={up}/>
      {(status.groups ?? []).map(group => (
        <StatusItem key={group} label={`Group: ${group}`} value="Available" up={up}/>
      ))}
    </ul>
  );
}

function StatusPage() {
  const {data: status, isPending, isError} = useQuery(statusQueryOptions());

  return (
    <>
      <h1 className="font-display font-semibold text-3xl text-text">Status</h1>
      {isPending && <p role="status">Loading status…</p>}
      {isError && <p role="alert">Could not reach the health endpoint.</p>}
      {status && <StatusView status={status}/>}
    </>
  );
}
