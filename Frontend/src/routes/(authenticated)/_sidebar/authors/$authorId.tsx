import { createFileRoute } from '@tanstack/react-router'
import Main from '#/components/layout/Main.tsx';

export const Route = createFileRoute('/(authenticated)/_sidebar/authors/$authorId')({
  component: AuthorDetailsPage,
})

function AuthorDetailsPage() {
  return (
    <Main>
      <div>Hello "/(authenticated)/authors/$authorId"!</div>
    </Main>
  );
}
