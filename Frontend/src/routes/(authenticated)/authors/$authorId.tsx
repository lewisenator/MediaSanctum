import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/(authenticated)/authors/$authorId')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/(authenticated)/authors/$authorId"!</div>
}
