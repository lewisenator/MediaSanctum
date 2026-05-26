import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/(authenticated)/series/')({
  component: SeriesPage,
})

function SeriesPage() {
  return <h1 className="font-display font-semibold text-3xl text-text">Hello "/series/"!</h1>
}
