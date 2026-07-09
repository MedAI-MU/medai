"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useSidebar } from "@/contexts/SidebarContext";

function SidebarNavItem({ href, icon, text, matchPrefix = false }) {
  const { closeSidebar } = useSidebar();

  const pathname = usePathname();
  const isActive = matchPrefix ? pathname.startsWith(href) : pathname === href;

  return (
    <Link
      href={href}
      onClick={closeSidebar}
      className={`flex items-center gap-3 rounded-lg px-3 py-2 transition-colors ${
        isActive
          ? "bg-primary/10 text-primary font-medium"
          : "text-text-muted hover:bg-surface-overlay"
      }`}
    >
      {icon}
      <span className="text-sm capitalize">{text}</span>
    </Link>
  );
}

export default SidebarNavItem;
