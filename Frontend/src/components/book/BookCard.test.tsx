import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event/dist/cjs/index.js';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import BookCard from './BookCard.tsx';
import { type Book } from '#/client/bookClient.ts';
import { renderWithRouter } from '#/test/renderWithRouter.tsx';

const book = {
  id: 'b1',
  title: 'Dune',
  releaseYear: 1965,
  author: { name: 'Frank Herbert' },
} as unknown as Book;

describe('BookCard', () => {
  it('Renders title, author and year with a link to the book', async () => {
    await renderWithRouter(<BookCard book={book} />);
    expect(await screen.findByText('Frank Herbert')).toBeInTheDocument();
    expect(screen.getByText('1965')).toBeInTheDocument();
    expect(screen.getByRole('link')).toHaveAttribute('href', '/books/b1');
  });

  it('Renders the cover when an ebook image exists', async () => {
    const withImage = { ...book, ebookEdition: { image: { url: 'http://x/c.png' } } } as unknown as Book;
    await renderWithRouter(<BookCard book={withImage} />);
    expect(await screen.findByAltText('Dune Cover')).toHaveAttribute('src', 'http://x/c.png');
  });

  it('Navigates when clicked', async () => {
    const { router } = await renderWithRouter(<BookCard book={book} />);
    await userEvent.click(await screen.findByRole('link'));
    expect(router.state.location.pathname).toBe('/books/b1');
  });
});
