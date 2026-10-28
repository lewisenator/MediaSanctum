import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event/dist/cjs/index.js';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import Dropdown from './Dropdown.tsx';

const options: Array<[string, string]> = [['Small', 's'], ['Large', 'l']];

describe('Dropdown', () => {
  it('Renders the label and selected option', () => {
    render(<Dropdown label="Size" options={options} value="s" setValue={vi.fn()} />);
    expect(screen.getByText('Size')).toBeInTheDocument();
    expect(screen.getByRole('button')).toHaveTextContent('Small');
    expect(screen.queryByRole('list')).not.toBeInTheDocument();
  });

  it('Opens the list and selects an option', async () => {
    const setValue = vi.fn();
    render(<Dropdown label="Size" options={options} value="s" setValue={setValue} />);
    await userEvent.click(screen.getByRole('button'));
    await userEvent.click(screen.getByText('Large'));
    expect(setValue).toHaveBeenCalledWith('l');
    expect(screen.queryByRole('list')).not.toBeInTheDocument();
  });
});
