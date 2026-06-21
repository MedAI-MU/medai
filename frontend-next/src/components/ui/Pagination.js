"use client";

import { ChevronLeft, ChevronRight } from "lucide-react";
import Button from "./Button";
import { cn } from "@/lib/utils";
import { usePaginationNav } from "@/hooks/usePaginationNav";

function Pagination({
  currentPage,
  totalCount,
  hasPrevious,
  hasNext,
  pageSize = 10,
  className,
}) {
  const navigateTo = usePaginationNav();

  const totalPages = Math.ceil(totalCount / pageSize);
  const startItem = (currentPage - 1) * pageSize + 1;
  const endItem = Math.min(currentPage * pageSize, totalCount);

  if (totalPages <= 1) return null;

  return (
    <div
      className={cn(
        "border-border bg-surface flex w-full items-center justify-center rounded-xl border-t px-6 py-4 sm:justify-between",
        className,
      )}
    >
      {/* Results info */}
      <p className="text-text-muted hidden text-[13px] sm:block">
        Showing <span className="text-text-base font-medium">{startItem}</span>–
        <span className="text-text-base font-medium">{endItem}</span> of{" "}
        <span className="text-text-base font-medium">{totalCount}</span> results
      </p>

      {/* Controls */}
      <nav aria-label="Pagination" className="flex items-center gap-2">
        {/* Previous */}
        <Button
          prefetch
          href={navigateTo(currentPage - 1)}
          variation="outline"
          size="sm"
          startIcon={<ChevronLeft size={16} />}
          data-disabled={!hasPrevious}
        >
          <span className="hidden sm:inline">Previous</span>
        </Button>

        {/* Page info on mobile */}
        <span className="text-text-muted text-sm sm:hidden">
          {currentPage} / {totalPages}
        </span>

        {/* Next */}
        <Button
          prefetch
          href={navigateTo(currentPage + 1)}
          variation="outline"
          size="sm"
          endIcon={<ChevronRight size={16} />}
          data-disabled={!hasNext}
        >
          <span className="hidden sm:inline">Next</span>
        </Button>
      </nav>
    </div>
  );
}

export default Pagination;
