import ReactDOM from 'react-dom/client';
import { RouterProvider } from '@tanstack/react-router';
import { StrictMode } from "react";
import { QueryClientProvider } from '@tanstack/react-query';
import { getRouter, queryClient } from './router';
import { ThemeProvider } from '#/context/ThemeContext.tsx';

const router = getRouter();

const rootElement = document.getElementById('app')!

if (!rootElement.innerHTML) {
  const root = ReactDOM.createRoot(rootElement)
  root.render(
    <ThemeProvider>
      <QueryClientProvider client={queryClient}>
        <StrictMode>
          <RouterProvider router={router} />
        </StrictMode>
      </QueryClientProvider>
    </ThemeProvider>
  )
}
