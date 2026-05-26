import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/(authenticated)/authors/')({
  component: AuthorsPage,
})

function AuthorsPage() {
  return <h1 className="font-display font-semibold text-3xl text-text">Hello "/authors/"!</h1>
}
