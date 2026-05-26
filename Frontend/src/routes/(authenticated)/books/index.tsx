import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/(authenticated)/books/')({
  component: BooksPage,
})

function BooksPage() {
  return (
    <>
      <h1 className="font-display font-semibold text-3xl text-text">
        Books
      </h1>
    </>
  )
}
