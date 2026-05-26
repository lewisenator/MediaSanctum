import { createFileRoute } from '@tanstack/react-router'
import SidebarLayout from '#/layouts/SidebarLayout.tsx';

export const Route = createFileRoute('/(authenticated)')({
  component: SidebarLayout,
  head: () => ({
    meta: [
      { title: 'Media Sanctum' },
    ],
  })
})