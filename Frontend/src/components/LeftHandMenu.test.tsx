import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import LeftHandMenu from './LeftHandMenu';
import { renderWithRouter } from '#/test/renderWithRouter.tsx';

describe('LeftHandMenu', () => {
  it('Should render', async () => {
    const { container } = await renderWithRouter(<LeftHandMenu />);
    const aside = container.querySelector('aside');

    expect(aside).toBeInTheDocument();
  });
});