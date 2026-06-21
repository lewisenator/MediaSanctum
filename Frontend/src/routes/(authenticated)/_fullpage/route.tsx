import { createFileRoute } from '@tanstack/react-router'
import FullPageLayout from '#/layouts/FullPageLayout.tsx';

export const Route = createFileRoute('/(authenticated)/_fullpage')({
  component: FullPageLayout,
});
