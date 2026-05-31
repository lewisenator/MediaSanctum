import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/(authenticated)/authors/search')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/(authenticated)/authors/search"!</div>
}
