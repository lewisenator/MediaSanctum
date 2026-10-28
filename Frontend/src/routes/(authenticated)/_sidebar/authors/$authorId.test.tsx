import { describe, it, expect, vi, beforeEach } from 'vitest';
import '@testing-library/jest-dom/vitest';
import { screen } from '@testing-library/react';
import { Route } from './$authorId';
import { renderWithRouter } from '#/test/renderWithRouter.tsx';
import { getAuthor } from '#/client/authorClient.ts';
import { getBooks } from '#/client/bookClient.ts';

vi.mock('#/client/authorClient.ts', () => ({ getAuthor: vi.fn() }));
vi.mock('#/client/bookClient.ts', () => ({ getBooks: vi.fn() }));

const Page = Route.options.component!;
const author = { id: 'a1', name: 'Ursula Le Guin', bio: 'Wrote things.', bornYear: 1929, deathYear: 2018 };
const book = (id: string, title: string, authorId: string) => ({ id, title, releaseYear: 1969, author: { id: authorId, name: 'x' } });

vi.spyOn(Route, 'useParams').mockReturnValue({ authorId: 'a1' });

describe('AuthorDetailsPage', () => {
  beforeEach(() => vi.clearAllMocks());

  it('shows the author and only their books', async () => {
    vi.mocked(getAuthor).mockResolvedValue(author as any);
    vi.mocked(getBooks).mockResolvedValue([book('b1', 'Left Hand', 'a1'), book('b2', 'Other', 'a2')] as any);
    await renderWithRouter(<Page />);

    expect(await screen.findByRole('heading', { name: 'Ursula Le Guin' })).toBeInTheDocument();
    expect(await screen.findByText('Left Hand')).toBeInTheDocument();
    expect(screen.queryByText('Other')).not.toBeInTheDocument();
  });

  it('shows a loading state', async () => {
    vi.mocked(getAuthor).mockReturnValue(new Promise(() => {}));
    vi.mocked(getBooks).mockReturnValue(new Promise(() => {}));
    await renderWithRouter(<Page />);
    expect(screen.getByText('Loading...')).toBeInTheDocument();
  });

  it('shows an empty state when the author has no books', async () => {
    vi.mocked(getAuthor).mockResolvedValue(author as any);
    vi.mocked(getBooks).mockResolvedValue([]);
    await renderWithRouter(<Page />);
    expect(await screen.findByText('No books yet.')).toBeInTheDocument();
  });

  it('shows the error when the author fails to load', async () => {
    vi.mocked(getAuthor).mockRejectedValue(new Error('Author with id a1 not found'));
    vi.mocked(getBooks).mockResolvedValue([]);
    await renderWithRouter(<Page />);
    expect(await screen.findByText('Author with id a1 not found')).toBeInTheDocument();
  });
});
