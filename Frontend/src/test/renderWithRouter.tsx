import React from 'react';
import {
  createRootRoute,
  createRoute,
  createRouter,
  RouterProvider,
  createMemoryHistory
} from '@tanstack/react-router';
import { render } from '@testing-library/react';

export async function renderWithRouter(ui: React.ReactElement, initialPath = '/') {
  // 1. Create a dummy root route
  const rootRoute = createRootRoute();

  // 2. Create a test route at the exact initialPath so TanStack Router matches it
  const testRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: initialPath,
    component: () => ui,
  });

  // 3. Assemble the temporary route tree
  const routeTree = rootRoute.addChildren([testRoute]);

  // 4. Instantiate a memory history targeting your starting path
  const history = createMemoryHistory({
    initialEntries: [initialPath],
  });

  const router = createRouter({
    routeTree,
    history,
    defaultPendingMinMs: 0, // Prevents artificial delays during tests
  });

  // 5. Resolve the initial navigation before rendering so the route component
  //    is mounted synchronously when render() is called
  await router.load();

  return {
    ...render(<RouterProvider router={router} />),
    router,
  };
}