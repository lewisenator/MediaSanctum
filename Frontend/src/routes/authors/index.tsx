import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/authors/')({
  component: AuthorsPage,
})

function AuthorsPage() {
  return <h1>Hello "/authors/"!</h1>
}
