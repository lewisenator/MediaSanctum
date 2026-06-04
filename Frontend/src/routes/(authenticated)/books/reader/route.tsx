import { createFileRoute } from '@tanstack/react-router'
import FullPageLayout from '#/layouts/FullPageLayout.tsx';

export const Route = createFileRoute('/(authenticated)/books/_reader')({
  component: FullPageLayout,
});
