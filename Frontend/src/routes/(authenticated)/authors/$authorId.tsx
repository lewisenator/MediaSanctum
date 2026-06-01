import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/(authenticated)/authors/$authorId')({
  component: AuthorDetailsPage,
})

function AuthorDetailsPage() {
  return <div>Hello "/(authenticated)/authors/$authorId"!</div>
}
