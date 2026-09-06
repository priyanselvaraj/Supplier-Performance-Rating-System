import React from 'react';
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Badge } from '../components/common/Badge';

describe('Badge Component Tests', () => {
  it('renders EXCELLENT badge with emerald styling', () => {
    render(<Badge variant="EXCELLENT" />);
    const badge = screen.getByText('EXCELLENT');
    expect(badge).toBeInTheDocument();
    expect(badge.className).toContain('emerald');
  });

  it('renders POOR badge with rose styling', () => {
    render(<Badge variant="POOR" />);
    const badge = screen.getByText('POOR');
    expect(badge).toBeInTheDocument();
    expect(badge.className).toContain('rose');
  });

  it('renders custom children when passed', () => {
    render(<Badge variant="ACTIVE">Vendor Active</Badge>);
    expect(screen.getByText('Vendor Active')).toBeInTheDocument();
  });
});
