"use client";

import { usePathname } from "next/navigation";
import SidebarNavItem from "./SidebarNavItem";

function SidebarNavGroup({ icon, text, onClose, items }) {
  const pathname = usePathname();
  const isGroupActive = items.some((item) => pathname.startsWith(item.href));

  return (
    <>
      {/* Group label */}
      <div
        className={`flex items-center gap-3 rounded-lg px-3 py-2.5 ${isGroupActive ? "text-primary font-medium" : "text-text-muted"}`}
      >
        {icon}
        <span className="text-sm capitalize">{text}</span>
      </div>

      {/* Subitems */}
      <div className="border-border mt-1 ml-4 space-y-1 border-l pl-3">
        {items.map((item) => {
          return (
            <SidebarNavItem
              key={item.text}
              text={item.text}
              href={item.href}
              icon={<item.icon size={16} />}
              onClick={onClose}
            />
          );
        })}
      </div>
    </>
  );
}

export default SidebarNavGroup;
