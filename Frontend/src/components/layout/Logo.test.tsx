import { render } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom/vitest';
import Logo from './Logo';

describe('Logo', () => {
  it('Renders at size 7 by default', () => {
    const { container } = render(<Logo />);
    const svg = container.querySelector('svg');

    expect(svg).toBeInTheDocument();
    expect(svg).toHaveClass('h-7', 'w-7');
  });

  it('Renders at correct size when passed as a prop', () => {
    const { container } = render(<Logo size={12}/>);
    const svg = container.querySelector('svg');

    expect(svg).toBeInTheDocument();
    expect(svg).toHaveClass('h-12', 'w-12');
  });
});