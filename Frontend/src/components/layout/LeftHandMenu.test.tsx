import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import LeftHandMenu from './LeftHandMenu.tsx';
import { renderWithRouter } from '#/test/renderWithRouter.tsx';
import userEvent from '@testing-library/user-event/dist/cjs/index.js';
import { screen, waitFor } from '@testing-library/react';
import { logout } from '#/client/authClient.ts';

const mockSetAccessToken = vi.fn();
const mockSetUser = vi.fn();

vi.mock('#/context/AuthContext.tsx', () => ({
  useAuth: () => ({
    setAccessToken: mockSetAccessToken,
    setUser: mockSetUser,
  }),
}));

vi.mock('#/client/authClient.ts', () => ({
  logout: vi.fn().mockResolvedValue(undefined),
}));

const mockLogout = vi.mocked(logout);

describe('LeftHandMenu', () => {
  it('Should render', async () => {
    const { container } = await renderWithRouter(<LeftHandMenu />);
    const aside = container.querySelector('aside');
    expect(aside).toBeInTheDocument();
  });

  it('Should logout when Sign Out button is clicked', async () => {
    const user = userEvent.setup();
    const { router } = await renderWithRouter(<LeftHandMenu />);
    await user.click(screen.getByRole('button', {name: /Sign Out/i}));
    await waitFor(() => {
      expect(mockLogout).toHaveBeenCalled();
      expect(mockSetAccessToken).toHaveBeenCalledWith(null);
      expect(mockSetUser).toHaveBeenCalledWith(null);
      expect(router.state.location.pathname).toBe('/login');
    });
  });
});
