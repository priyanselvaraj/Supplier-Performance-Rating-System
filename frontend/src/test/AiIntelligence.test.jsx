import React from 'react';
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Badge } from '../components/common/Badge';

describe('AI Intelligence Component & Badge Tests', () => {
  it('renders CRITICAL risk badge styling correctly', () => {
    render(<Badge variant="danger">CRITICAL</Badge>);
    const badge = screen.getByText('CRITICAL');
    expect(badge).toBeInTheDocument();
    expect(badge.className).toContain('rose');
  });

  it('renders HIGH risk badge styling correctly', () => {
    render(<Badge variant="danger">HIGH</Badge>);
    const badge = screen.getByText('HIGH');
    expect(badge).toBeInTheDocument();
  });

  it('renders MEDIUM risk badge styling correctly', () => {
    render(<Badge variant="warning">MEDIUM</Badge>);
    const badge = screen.getByText('MEDIUM');
    expect(badge).toBeInTheDocument();
    expect(badge.className).toContain('amber');
  });

  it('renders LOW risk badge styling correctly', () => {
    render(<Badge variant="success">LOW</Badge>);
    const badge = screen.getByText('LOW');
    expect(badge).toBeInTheDocument();
    expect(badge.className).toContain('emerald');
  });
});
