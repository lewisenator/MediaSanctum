import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event/dist/cjs/index.js';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import '@testing-library/jest-dom/vitest';
import Modal from './Modal.tsx';

describe('Modal', () => {
  beforeEach(() => {
    document.body.innerHTML = '<div id="portal"></div>';
  });

  it('Renders title and children into the portal when open', () => {
    render(<Modal isOpen title="Settings"><p>Body</p></Modal>);
    expect(screen.getByRole('heading', { name: 'Settings' })).toBeInTheDocument();
    expect(screen.getByText('Body')).toBeInTheDocument();
  });

  it('Renders nothing when closed', () => {
    render(<Modal isOpen={false} title="Settings"><p>Body</p></Modal>);
    expect(screen.queryByText('Settings')).not.toBeInTheDocument();
  });

  it('Calls onClose when the close button is clicked', async () => {
    const onClose = vi.fn();
    render(<Modal isOpen title="Settings" onClose={onClose}><p>Body</p></Modal>);
    await userEvent.click(screen.getByRole('button'));
    expect(onClose).toHaveBeenCalledOnce();
  });
});
