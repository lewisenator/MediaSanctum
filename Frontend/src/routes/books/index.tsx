import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/books/')({
  component: BooksPage,
})

function BooksPage() {
  return (
    <>
      <h1 className="font-serif font-semibold text-3xl text-text">
        Books
      </h1>
    </>
  )
}
