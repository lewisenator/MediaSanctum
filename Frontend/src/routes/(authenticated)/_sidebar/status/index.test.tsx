import { describe, it, expect, vi, beforeEach } from 'vitest';
import '@testing-library/jest-dom/vitest';
import { screen } from '@testing-library/react';
import { Route } from './index';
import { renderWithRouter } from '#/test/renderWithRouter.tsx';
import { health } from '#/client/healthClient.ts';

const StatusPage = Route.options.component!;

vi.mock('#/client/healthClient.ts', () => ({
  health: vi.fn(),
}));

const mockHealth = vi.mocked(health);

describe('StatusPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('shows loading while the request is pending', async () => {
    mockHealth.mockReturnValue(new Promise(() => {}));
    await renderWithRouter(<StatusPage />);
    expect(await screen.findByRole('status')).toHaveTextContent('Loading');
  });

  it('shows the service and groups as up', async () => {
    mockHealth.mockResolvedValue({status: 'UP', groups: ['liveness', 'readiness']});
    await renderWithRouter(<StatusPage />);
    expect(await screen.findByText(/Up/)).toBeInTheDocument();
    expect(screen.getByText('Group: liveness')).toBeInTheDocument();
    expect(screen.getAllByTestId('status-item')).toHaveLength(3);
  });

  it('shows the service as down', async () => {
    mockHealth.mockResolvedValue({status: 'DOWN', groups: []});
    await renderWithRouter(<StatusPage />);
    expect(await screen.findByText(/Down/)).toBeInTheDocument();
  });

  it('shows an error when the request fails', async () => {
    mockHealth.mockRejectedValue(new Error('boom'));
    await renderWithRouter(<StatusPage />);
    expect(await screen.findByRole('alert')).toHaveTextContent('Could not reach');
  });
});
