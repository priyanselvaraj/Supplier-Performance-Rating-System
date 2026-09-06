import React from 'react';
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Badge } from '../components/common/Badge';

describe('Notification and Improvement Action UI Tests', () => {
  it('renders Critical notification priority badge', () => {
    render(<Badge variant="danger">CRITICAL</Badge>);
    const badge = screen.getByText('CRITICAL');
    expect(badge).toBeInTheDocument();
    expect(badge.className).toContain('rose');
  });

  it('renders In Progress improvement action badge', () => {
    render(<Badge variant="warning">In Progress</Badge>);
    const badge = screen.getByText('In Progress');
    expect(badge).toBeInTheDocument();
    expect(badge.className).toContain('amber');
  });

  it('renders Completed improvement action badge', () => {
    render(<Badge variant="success">Completed</Badge>);
    const badge = screen.getByText('Completed');
    expect(badge).toBeInTheDocument();
    expect(badge.className).toContain('emerald');
  });

  it('renders Open status badge', () => {
    render(<Badge variant="info">Open</Badge>);
    const badge = screen.getByText('Open');
    expect(badge).toBeInTheDocument();
    expect(badge.className).toContain('blue');
  });
});
