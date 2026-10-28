import { describe, it, expect, vi, beforeEach } from 'vitest';
import '@testing-library/jest-dom/vitest';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Route } from './search';
import { renderWithRouter } from '#/test/renderWithRouter.tsx';
import { searchAuthors } from '#/client/searchClient.ts';
import { addAuthor } from '#/client/authorClient.ts';

vi.mock('#/client/searchClient.ts', () => ({ searchAuthors: vi.fn() }));
vi.mock('#/client/authorClient.ts', () => ({ addAuthor: vi.fn() }));

const Page = Route.options.component!;
const hit = { hardcoverId: 'h1', name: 'Ursula Le Guin', booksCount: 40, imageUrl: '', books: [], seriesNames: [] };

describe('AuthorSearchPage', () => {
  beforeEach(() => vi.clearAllMocks());

  it('searches and lists results', async () => {
    vi.mocked(searchAuthors).mockResolvedValue({ found: 1, page: 1, perPage: 10, hits: [hit] } as any);
    const user = userEvent.setup();
    await renderWithRouter(<Page />);

    await user.type(screen.getByPlaceholderText('Search for an author...'), 'le guin');

    expect(await screen.findByText('Ursula Le Guin')).toBeInTheDocument();
    expect(searchAuthors).toHaveBeenCalledWith('le guin');
  });

  it('shows an empty state for no hits', async () => {
    vi.mocked(searchAuthors).mockResolvedValue({ found: 0, page: 1, perPage: 10, hits: [] } as any);
    const user = userEvent.setup();
    await renderWithRouter(<Page />);
    await user.type(screen.getByPlaceholderText('Search for an author...'), 'zzz');
    expect(await screen.findByText('No authors found.')).toBeInTheDocument();
  });

  it('shows the search error', async () => {
    vi.mocked(searchAuthors).mockRejectedValue(new Error('Search failed'));
    const user = userEvent.setup();
    await renderWithRouter(<Page />);
    await user.type(screen.getByPlaceholderText('Search for an author...'), 'zzz');
    expect(await screen.findByText('Search failed')).toBeInTheDocument();
  });

  it('adds the chosen author and navigates to its detail page', async () => {
    vi.mocked(searchAuthors).mockResolvedValue({ found: 1, page: 1, perPage: 10, hits: [hit] } as any);
    vi.mocked(addAuthor).mockResolvedValue({ id: 'a1' } as any);
    const user = userEvent.setup();
    const { router } = await renderWithRouter(<Page />);
    await user.type(screen.getByPlaceholderText('Search for an author...'), 'le guin');
    await user.click(await screen.findByText('Ursula Le Guin'));

    await waitFor(() => expect(addAuthor).toHaveBeenCalledWith('h1'));
    await waitFor(() => expect(router.state.location.pathname).toBe('/authors/a1'));
  });
});
