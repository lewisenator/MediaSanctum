import { createFileRoute } from '@tanstack/react-router'
import Main from '#/components/layout/Main.tsx';

export const Route = createFileRoute('/(authenticated)/_sidebar/authors/search')({
  component: RouteComponent,
})

function RouteComponent() {
  return (
    <Main>
      <h1>Hello "/(authenticated)/authors/search"!</h1>
    </Main>
  );
}
