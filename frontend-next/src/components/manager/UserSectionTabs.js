"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const tabs = [
  { label: "Secretaries", href: "/manager/users", slug: "secretaries" },
  { label: "Doctors", href: "/manager/users/doctors", slug: "doctors" },
  { label: "Managers", href: "/manager/users/managers", slug: "managers" },
];

export default function UserSectionTabs() {
  const pathname = usePathname();

  return (
    <div className="border-border no-scrollbar flex overflow-x-auto border-b">
      {tabs.map(({ label, href, slug }) => {
        const isActive = pathname === href;
        return (
          <Link
            key={slug}
            href={href}
            className={`${isActive ? "border-primary text-primary " : "text-text-muted hover:text-text-base border-b-transparent"} border-b-2 px-6 py-3 text-sm font-medium whitespace-nowrap capitalize transition-all`}
          >
            {label}
          </Link>
        );
      })}
    </div>
  );
}
