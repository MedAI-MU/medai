"use client";

import { useSidebar } from "@/contexts/SidebarContext";

function Overlay({ className }) {
  const { isSidebarOpen, closeSidebar } = useSidebar();

  if (!isSidebarOpen) return null;

  return (
    <div
      onClick={closeSidebar}
      className={`fixed inset-0 z-40 bg-black/20 backdrop-blur-sm ${className}`}
    />
  );
}

export default Overlay;
