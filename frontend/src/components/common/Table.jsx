import React from 'react';
import { ArrowUpDown, ArrowUp, ArrowDown } from 'lucide-react';

export const Table = ({
  columns,
  data,
  loading,
  emptyMessage = 'No records found',
  sortBy,
  sortDirection = 'asc',
  onSort,
}) => {
  if (loading) {
    return (
      <div className="py-12 text-center text-slate-500">
        <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mb-2"></div>
        <p className="text-sm">Loading records...</p>
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <div className="py-12 text-center text-slate-500 bg-slate-50/50 rounded-xl border border-dashed border-slate-200">
        <p className="text-sm font-medium">{emptyMessage}</p>
      </div>
    );
  }

  const handleHeaderClick = (col) => {
    if (!onSort) return;
    const isSortable = col.sortable !== false && (col.accessor || col.sortKey);
    if (!isSortable) return;

    const sortKey = col.sortKey || col.accessor;
    if (sortBy === sortKey) {
      const nextDir = sortDirection === 'asc' ? 'desc' : 'asc';
      onSort(sortKey, nextDir);
    } else {
      // Default to 'desc' for ratings/scores/dates or 'asc' for names/codes
      const defaultDesc = ['overallRating', 'rating', 'totalEvaluations', 'evaluationDate', 'createdAt', 'score'].includes(sortKey);
      onSort(sortKey, defaultDesc ? 'desc' : 'asc');
    }
  };

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-slate-200 text-left text-sm">
        <thead className="bg-slate-50/75 text-xs uppercase font-semibold text-slate-600 tracking-wider">
          <tr>
            {columns.map((col, idx) => {
              const sortKey = col.sortKey || col.accessor;
              const isSortable = Boolean(onSort && col.sortable !== false && sortKey);
              const isSorted = isSortable && sortBy === sortKey;

              return (
                <th
                  key={idx}
                  scope="col"
                  onClick={() => isSortable && handleHeaderClick(col)}
                  className={`px-6 py-3.5 select-none ${col.className || ''} ${
                    isSortable ? 'cursor-pointer hover:bg-slate-100/80 transition-colors group' : ''
                  }`}
                >
                  <div className={`flex items-center gap-1.5 ${col.className?.includes('text-right') ? 'justify-end' : ''}`}>
                    <span>{col.header}</span>
                    {isSortable && (
                      <span className="inline-flex text-slate-400">
                        {isSorted ? (
                          sortDirection === 'asc' ? (
                            <ArrowUp className="h-3.5 w-3.5 text-blue-600 font-bold" />
                          ) : (
                            <ArrowDown className="h-3.5 w-3.5 text-blue-600 font-bold" />
                          )
                        ) : (
                          <ArrowUpDown className="h-3 w-3 opacity-0 group-hover:opacity-100 transition-opacity" />
                        )}
                      </span>
                    )}
                  </div>
                </th>
              );
            })}
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-200/70 bg-white">
          {data.map((row, rowIdx) => (
            <tr key={row.id || rowIdx} className="hover:bg-slate-50/80 transition-colors">
              {columns.map((col, colIdx) => (
                <td key={colIdx} className={`px-6 py-4 whitespace-nowrap text-slate-700 ${col.cellClassName || ''}`}>
                  {col.render ? col.render(row) : row[col.accessor]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Table;
