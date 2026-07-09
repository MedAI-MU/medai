"use client";

import { useSidebar } from "@/contexts/SidebarContext";

function SidebarContainer({ children }) {
  const { isSidebarOpen } = useSidebar();

  return (
    <aside
      className={`border-border bg-surface fixed inset-y-0 z-50 row-span-2 flex w-(--sidebar-width) max-w-full flex-col border-r transition-all duration-300 lg:relative lg:translate-0 ${isSidebarOpen ? "translate-x-0" : "-translate-x-full"}`}
    >
      {children}
    </aside>
  );
}

export default SidebarContainer;
