import { createRouter as createTanStackRouter } from '@tanstack/react-router';
import { routeTree } from './routeTree.gen';
import { QueryClient, QueryCache } from '@tanstack/react-query';
import { ApiError } from '#/client/mediaSanctumClient.ts';

// Forward reference populated by getRouter() — queryCache.onError only fires
// at runtime so the router is guaranteed to exist by then.
let router: ReturnType<typeof getRouter> | undefined;

export const queryClient = new QueryClient({
  queryCache: new QueryCache({
    onError: (error) => {
      if (error instanceof ApiError
        && error.status === 401
        && error.url.includes('/auth/refresh')) {
        router?.navigate({ to: '/login' });
      }
    },
  }),
  defaultOptions: {
    queries: {
      retry: false,
    }
  }
});

export function getRouter() {
  const _router = createTanStackRouter({
    routeTree,
    context: {
      queryClient
    },
    scrollRestoration: true,
    defaultPreload: 'intent',
    defaultPreloadStaleTime: 0,
  });

  router = _router;
  return _router;
}

declare module '@tanstack/react-router' {
  interface Register {
    router: ReturnType<typeof getRouter>
  }
}
