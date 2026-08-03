// TICKET-ADV114 — Compound DataTable.
// TICKET-ADV117 — useDebouncedSearch.
import React, { useEffect, useState } from 'react';
import { withAuth } from '@components/withAuth.jsx';
import DataTable from '@components/DataTable.jsx';
import { useDebouncedSearch } from '@hooks/useDebouncedSearch.js';
import { api } from '@services/apiService.js';

function Trades() {
  const [search, setSearch] = useState('');
  const debounced = useDebouncedSearch(search, 300);
  const [page, setPage] = useState(0);
  const [data, setData] = useState({ items: [], totalPages: 0 });

  useEffect(() => {
    async function loadTrades() {
      try {
        const params = new URLSearchParams();
        params.set('page', page);

        if (debounced) {
          params.set('status', debounced);
        }

        const result = await api.listTrades(`?${params.toString()}`);

        setData({
          items: result?.items ?? [],
          totalPages: result?.totalPages ?? 0,
        });
      } catch (error) {
        console.error('Failed to load trades:', error);
        setData({ items: [], totalPages: 0 });
      }
    }

    loadTrades();
  }, [page, debounced]);

  return (
    <section>
      <h2>Trades</h2>

      <input
        aria-label="Filter by status"
        placeholder="status filter (PENDING/MATCHED/…)"
        value={search}
        onChange={(e) => setSearch(e.target.value.toUpperCase())}
      />

      <DataTable>
        <DataTable.Header
          columns={[
            { key: 'tradeRef', label: 'Ref' },
            { key: 'symbol', label: 'Symbol' },
            { key: 'qty', label: 'Qty' },
            { key: 'price', label: 'Price' },
            { key: 'status', label: 'Status' },
          ]}
        />

        <DataTable.Body
          rows={data.items}
          render={(trade) => (
            <>
              <span>{trade.tradeRef}</span>
              <span>{trade.symbol}</span>
              <span>{trade.qty}</span>
              <span>{trade.price}</span>
              <span>{trade.status}</span>
            </>
          )}
        />

        <DataTable.Pagination
          page={page}
          totalPages={Math.max(1, data.totalPages)}
          onChange={setPage}
        />
      </DataTable>
    </section>
  );
}

export default withAuth(Trades);