import { createFileRoute } from '@tanstack/react-router'
import FullPageLayout from '#/layouts/FullPageLayout.tsx';

export const Route = createFileRoute('/(authenticated)/books/$bookId')({
  component: FullPageLayout,
});
