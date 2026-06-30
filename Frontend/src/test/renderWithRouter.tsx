import React from 'react';
import {
  createRootRoute,
  createRoute,
  createRouter,
  RouterProvider,
  createMemoryHistory
} from '@tanstack/react-router';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render } from '@testing-library/react';
import { ThemeProvider } from '#/context/ThemeContext.tsx';

export async function renderWithRouter(ui: React.ReactElement, initialPath = '/') {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  const rootRoute = createRootRoute({
    notFoundComponent: () => (
      <>Not Found</>
    )
  });

  const testRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: initialPath,
    component: () => ui
  });

  const routeTree = rootRoute.addChildren([testRoute]);

  const history = createMemoryHistory({
    initialEntries: [initialPath],
  });

  const router = createRouter({
    routeTree,
    history,
    defaultPendingMinMs: 0,
  });

  await router.load();

  return {
    ...render(
      <ThemeProvider>
        <QueryClientProvider client={queryClient}>
          <RouterProvider router={router} />
        </QueryClientProvider>
      </ThemeProvider>
    ),
    router,
  };
}