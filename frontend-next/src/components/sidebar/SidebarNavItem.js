"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

function SidebarNavItem({ href, onClick, icon, text, matchPrefix = false }) {
  const pathname = usePathname();
  const isActive = matchPrefix
    ? pathname.startsWith(href)
    : pathname === href;

  return (
    <Link
      href={href}
      onClick={onClick}
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
