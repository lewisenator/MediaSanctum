import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/series/')({
  component: SeriesPage,
})

function SeriesPage() {
  return <h1>Hello "/series/"!</h1>
}
