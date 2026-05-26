import { Outlet, createRootRouteWithContext, Link } from '@tanstack/react-router';
import { TanStackRouterDevtoolsPanel } from '@tanstack/react-router-devtools';
import { TanStackDevtools } from '@tanstack/react-devtools';
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import type { QueryClient } from '@tanstack/react-query';

import '../styles.css';
import LeftHandMenu from '#/components/LeftHandMenu.tsx';

type RouterContext = {
  queryClient: QueryClient;
};

export const Route = createRootRouteWithContext<RouterContext>()({
  head: () => ({
    meta: [
      { name: 'description', content: 'Manage your ebook and audiobook libraries' },
      { title: 'Media Sanctum - Home' },
    ]
  }),
  component: RootLayout,
  notFoundComponent: NotFound,
})

function RootLayout() {
  return (
    <div className="min-h-screen flex flex-row">
      <LeftHandMenu />
      <main className='flex justify-center p-6'>
        <div className="w-full max-w-4xl">
          <Outlet />
        </div>
      </main>

      <TanStackDevtools
        config={{
          position: 'bottom-right',
        }}
        plugins={[
          {
            name: 'Router',
            render: <TanStackRouterDevtoolsPanel />,
          },
          {
            name: 'Query',
            render: <ReactQueryDevtools />
          },
        ]}
      />
    </div>
  )
}


function NotFound() {
  return (
    <div className="flex flex-col items-center justify-center py-20">
      <h1 className="text-4xl font-bold text-gray-800 mb-4">404</h1>
      <p className="text-lg text-gray-600 mb-6">Oops! The page you are looking for does not exist.</p>
      <Link
        to='/'
        className='px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition'
      >
        Go Back Home
      </Link>
    </div>
  );
}