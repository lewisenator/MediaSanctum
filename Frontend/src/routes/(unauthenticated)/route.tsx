import { createFileRoute } from '@tanstack/react-router'
import AuthLayout from '#/layouts/AuthLayout.tsx';

export const Route = createFileRoute('/(unauthenticated)')({
  component: AuthLayout,
  head: () => ({
    meta: [
      { title: 'Media Sanctum - Authenticate' },
    ],
  })
})