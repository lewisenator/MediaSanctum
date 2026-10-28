import { describe, it, expect, vi, beforeEach } from 'vitest';
import '@testing-library/jest-dom/vitest';
import { screen } from '@testing-library/react';
import { Route } from './index';
import { renderWithRouter } from '#/test/renderWithRouter.tsx';
import { getSeries } from '#/client/seriesClient.ts';

const SeriesPage = Route.options.component!;

vi.mock('#/client/seriesClient.ts', () => ({
  getSeries: vi.fn(),
}));

const mockGetSeries = vi.mocked(getSeries);

describe('SeriesPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('shows a loading state while the request is pending', async () => {
    mockGetSeries.mockReturnValue(new Promise(() => {}));
    await renderWithRouter(<SeriesPage />);

    expect(await screen.findByText('Loading series...')).toBeInTheDocument();
  });

  it('renders the series returned by the API', async () => {
    mockGetSeries.mockResolvedValue([
      { hardcoverId: 1, name: 'Jurassic Park', slug: 'jp', isCompleted: true, booksCount: 2, libraryBooksCount: 1 },
      { hardcoverId: 2, name: 'Dune', slug: 'dune', isCompleted: null, booksCount: null, libraryBooksCount: 3 },
    ]);
    await renderWithRouter(<SeriesPage />);

    expect(await screen.findByText('Jurassic Park')).toBeInTheDocument();
    expect(screen.getByText('1 of 2 books in library')).toBeInTheDocument();
    expect(screen.getByText('Completed')).toBeInTheDocument();
    expect(screen.getByText('Dune')).toBeInTheDocument();
    expect(screen.getByText('3 books in library')).toBeInTheDocument();
  });

  it('shows an empty state when there are no series', async () => {
    mockGetSeries.mockResolvedValue([]);
    await renderWithRouter(<SeriesPage />);

    expect(await screen.findByText('No series yet.')).toBeInTheDocument();
  });

  it('shows the error message when the request fails', async () => {
    mockGetSeries.mockRejectedValue(new Error('Failed to fetch series'));
    await renderWithRouter(<SeriesPage />);

    expect(await screen.findByRole('alert')).toHaveTextContent('Failed to fetch series');
  });
});
