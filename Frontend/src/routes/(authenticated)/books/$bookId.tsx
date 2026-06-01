import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/(authenticated)/books/$bookId')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/(authenticated)/books/$bookId"!</div>
}
