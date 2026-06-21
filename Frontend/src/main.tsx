import ReactDOM from 'react-dom/client';
import { RouterProvider } from '@tanstack/react-router';
import { StrictMode } from "react";
import { QueryClientProvider } from '@tanstack/react-query';
import { getRouter, queryClient } from './router';
import { ThemeProvider } from '#/context/ThemeContext.tsx';
import { AuthProvider } from '#/context/AuthContext.tsx';

const router = getRouter();

// Prevent iOS rubber-band overscroll globally, while allowing overflow-y/x scroll containers.
// Non-passive so we can call preventDefault(); CSS overscroll-behavior covers iOS 16+ but
// this handles older versions too.
document.addEventListener('touchmove', (e: TouchEvent) => {
  let target = e.target as Element | null;
  while (target && target !== document.documentElement) {
    const { overflowY, overflowX } = window.getComputedStyle(target);
    if ((overflowY === 'scroll' || overflowY === 'auto') && target.scrollHeight > target.clientHeight) return;
    if ((overflowX === 'scroll' || overflowX === 'auto') && target.scrollWidth > target.clientWidth) return;
    target = target.parentElement;
  }
  e.preventDefault();
}, { passive: false });

const rootElement = document.getElementById('app')!

if (!rootElement.innerHTML) {
  const root = ReactDOM.createRoot(rootElement)
  root.render(
    <ThemeProvider>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <StrictMode>
            <RouterProvider router={router} />
          </StrictMode>
        </AuthProvider>
      </QueryClientProvider>
    </ThemeProvider>
  )
}
