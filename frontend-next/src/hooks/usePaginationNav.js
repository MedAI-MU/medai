"use client";

import { usePathname, useSearchParams } from "next/navigation";

function usePaginationNav() {
  const pathname = usePathname();
  const searchParams = useSearchParams();

  function navigateTo(newPage) {
    const params = new URLSearchParams(searchParams);
    params.set("pageNo", newPage);
    return `${pathname}?${params.toString()}`;
  }

  return navigateTo;
}

export { usePaginationNav };
