"use client";

import { createContext, useContext } from "react";

const TableContext = createContext();

function Table({ children, columns }) {
  return (
    <TableContext.Provider value={{ columns }}>
      <div className="scrollbar-thumb-surface-overlay border-border bg-surface scrollbar-thin overflow-x-auto rounded-xl border shadow-md">
        <div role="table" className="w-full min-w-max">
          {children}
        </div>
      </div>
    </TableContext.Provider>
  );
}

function Header({ children }) {
  const { columns } = useContext(TableContext);

  return (
    <div
      role="row"
      style={{ gridTemplateColumns: columns }}
      className="bg-surface-overlay border-border text-text-muted grid items-center gap-6 border-b px-6 py-4 text-xs font-semibold tracking-wider uppercase"
    >
      {children}
    </div>
  );
}

function Row({ children }) {
  const { columns } = useContext(TableContext);

  return (
    <div
      role="row"
      style={{ gridTemplateColumns: columns }}
      className="border-border *:wrap-break-words grid items-center gap-6 border-b px-6 py-4 transition-colors duration-200 last:border-b-0"
    >
      {children}
    </div>
  );
}

function Body({ data, render }) {
  if (!data?.length)
    return (
      <p className="text-text-muted py-10 text-center text-sm font-medium">
        No data available
      </p>
    );

  return <section className="my-1">{data?.map(render)}</section>;
}

function Footer({ children }) {
  if (!children) return null;

  return (
    <footer className="bg-surface-overlay border-border flex items-center justify-between border-t px-6 py-4">
      {children}
    </footer>
  );
}

Table.Header = Header;
Table.Row = Row;
Table.Body = Body;
Table.Footer = Footer;

export default Table;
