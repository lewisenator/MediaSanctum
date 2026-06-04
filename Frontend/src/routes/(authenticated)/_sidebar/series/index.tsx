import { createFileRoute } from '@tanstack/react-router'
import Main from '#/components/layout/Main.tsx';

export const Route = createFileRoute('/(authenticated)/_sidebar/series/')({
  component: SeriesPage,
})

function SeriesPage() {
  return (
    <Main>
      <h1 className="font-display font-semibold text-3xl text-text">Hello "/series/"!</h1>
    </Main>
  );
}
