import { describe, it, expect, vi, beforeEach } from 'vitest';
import '@testing-library/jest-dom/vitest';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { LoginPage } from './login';
import { renderWithRouter } from '#/test/renderWithRouter.tsx';
import { login } from '#/client/mediaSanctumClient.ts';

const mockSetAccessToken = vi.fn();
const mockSetUser = vi.fn();

vi.mock('#/context/AuthContext.tsx', () => ({
  useAuth: () => ({
    setAccessToken: mockSetAccessToken,
    setUser: mockSetUser,
  }),
}));

vi.mock('#/client/mediaSanctumClient.ts', () => ({
  login: vi.fn(),
}));

const mockLogin = vi.mocked(login);

const mockAuthResponse = {
  accessToken: 'test-token',
  user: { id: '1', email: 'test@example.com', firstName: 'Test', lastName: 'User' },
};

describe('LoginPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders the email and password fields and a sign-in button', async () => {
    const { container } = await renderWithRouter(<LoginPage />);

    expect(container.querySelector('#email')).toBeInTheDocument();
    expect(container.querySelector('#password')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Sign In' })).toBeInTheDocument();
  });

  it('shows a validation error when submitted with both fields empty', async () => {
    const user = userEvent.setup();
    await renderWithRouter(<LoginPage />);

    await user.click(screen.getByRole('button', { name: 'Sign In' }));

    expect(screen.getByText('Email and password are required')).toBeInTheDocument();
    expect(mockLogin).not.toHaveBeenCalled();
  });

  it('shows a validation error when only the email is filled', async () => {
    const user = userEvent.setup();
    await renderWithRouter(<LoginPage />);

    await user.type(screen.getByLabelText(/email/i), 'test@example.com');
    await user.click(screen.getByRole('button', { name: 'Sign In' }));

    expect(screen.getByText('Email and password are required')).toBeInTheDocument();
    expect(mockLogin).not.toHaveBeenCalled();
  });

  it('calls login with credentials and navigates to /books on success', async () => {
    mockLogin.mockResolvedValue(mockAuthResponse);
    const user = userEvent.setup();
    const { router } = await renderWithRouter(<LoginPage />);

    await user.type(screen.getByLabelText(/email/i), 'test@example.com');
    await user.type(screen.getByLabelText(/password/i), 'secret123');
    await user.click(screen.getByRole('button', { name: 'Sign In' }));

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith({ email: 'test@example.com', password: 'secret123' });
      expect(mockSetAccessToken).toHaveBeenCalledWith('test-token');
      expect(mockSetUser).toHaveBeenCalledWith(mockAuthResponse.user);
      expect(router.state.location.pathname).toBe('/books');
    });
  });

  it('displays the error message returned by the API on failure', async () => {
    mockLogin.mockRejectedValue(new Error('Invalid credentials'));
    const user = userEvent.setup();
    await renderWithRouter(<LoginPage />);

    await user.type(screen.getByLabelText(/email/i), 'test@example.com');
    await user.type(screen.getByLabelText(/password/i), 'wrongpassword');
    await user.click(screen.getByRole('button', { name: 'Sign In' }));

    await waitFor(() => {
      expect(screen.getByText('Invalid credentials')).toBeInTheDocument();
    });
    expect(mockSetAccessToken).not.toHaveBeenCalled();
  });

  it('shows "Signing in..." while the request is pending', async () => {
    let resolveLogin!: (value: typeof mockAuthResponse) => void;
    mockLogin.mockReturnValue(new Promise((res) => { resolveLogin = res; }));
    const user = userEvent.setup();
    await renderWithRouter(<LoginPage />);

    await user.type(screen.getByLabelText(/email/i), 'test@example.com');
    await user.type(screen.getByLabelText(/password/i), 'secret123');
    await user.click(screen.getByRole('button', { name: 'Sign In' }));

    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Signing in...' })).toBeInTheDocument();
    });

    resolveLogin(mockAuthResponse);
  });
});
