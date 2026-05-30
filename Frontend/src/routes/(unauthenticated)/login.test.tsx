import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import { screen } from '@testing-library/react';
import { LoginPage } from './login';
import { renderWithRouter } from '#/test/renderWithRouter.tsx';

describe('LoginPage', () => {
  it('Should render the email and password fields and a sign-in button', async () => {
    const { container } = await renderWithRouter(<LoginPage />);

    expect(container.querySelector('#email')).toBeInTheDocument();
    expect(container.querySelector('#password')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Sign In' })).toBeInTheDocument();
  });
});
