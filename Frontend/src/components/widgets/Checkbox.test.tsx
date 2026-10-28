import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event/dist/cjs/index.js';
import { describe, it, expect, vi } from 'vitest';
import '@testing-library/jest-dom/vitest';
import Checkbox from './Checkbox.tsx';

describe('Checkbox', () => {
  it('Renders the label and checked state', () => {
    render(<Checkbox label="Dark mode" value={true} setValue={vi.fn()} />);
    expect(screen.getByLabelText('Dark mode')).toBeChecked();
  });

  it('Calls setValue with the new value when clicked', async () => {
    const setValue = vi.fn();
    render(<Checkbox label="Dark mode" value={false} setValue={setValue} />);
    await userEvent.click(screen.getByLabelText('Dark mode'));
    expect(setValue).toHaveBeenCalledWith(true);
  });
});
