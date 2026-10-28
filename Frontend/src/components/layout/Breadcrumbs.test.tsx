import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event/dist/cjs/index.js';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import Breadcrumbs from './Breadcrumbs.tsx';

describe('Breadcrumbs', () => {
  it('Renders every item', () => {
    render(<Breadcrumbs items={[<a href="/a">Books</a>, <span>Dune</span>]} />);
    expect(screen.getByText('Books')).toBeInTheDocument();
    expect(screen.getByText('Dune')).toBeInTheDocument();
  });

  it('Items remain clickable', async () => {
    const onClick = vi.fn();
    render(<Breadcrumbs items={[<button onClick={onClick}>Books</button>]} />);
    await userEvent.click(screen.getByText('Books'));
    expect(onClick).toHaveBeenCalledOnce();
  });
});
