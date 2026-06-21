"use client";

import { useSidebar } from "@/contexts/SidebarContext";
import {
  CalendarCheck,
  CalendarDays,
  CalendarPlus,
  Clock,
  FileText,
  LayoutDashboard,
  LogOut,
  MessageCircle,
  Plus,
  Settings,
  User,
  X,
} from "lucide-react";
import Link from "next/link";
import Logo from "@/components/ui/Logo";
import Overlay from "@/components/ui/Overlay";
import ButtonIcon from "@/components/ui/ButtonIcon";
import Heading from "@/components/ui/Heading";
import SidebarNavItem from "@/components/sidebar/SidebarNavItem";
import SidebarNavGroup from "@/components/sidebar/SidebarNavGroup";
import LogoutButton from "../ui/LogoutButton";

const LINKS = {
  patient: [
    { text: "dashboard", href: "/patient", icon: LayoutDashboard },
    {
      text: "book appointment",
      href: "/patient/book-appointment",
      icon: CalendarPlus,
    },
    { text: "my appointments", href: "#", icon: CalendarCheck },
    {
      text: "medical records",
      href: "/patient/medical-records",
      icon: FileText,
    },
    { text: "messages", href: "#", icon: MessageCircle },
  ],
  doctor: [
    { text: "dashboard", href: "/doctor", icon: LayoutDashboard },
    {
      text: "schedule",
      icon: CalendarCheck,
      items: [
        {
          text: "appointments",
          href: "/doctor/appointments",
          icon: CalendarDays,
        },
        {
          text: "availability",
          href: "/doctor/availability",
          icon: Plus,
        },
        {
          text: "working hours",
          href: "/doctor/working-hours",
          icon: Clock,
        },
      ],
    },
  ],
};

function Sidebar({ role }) {
  const { isSidebarOpen, closeSidebar } = useSidebar();

  return (
    <>
      {isSidebarOpen && (
        <Overlay className="lg:hidden" onClick={closeSidebar} />
      )}

      <aside
        className={`border-border bg-surface fixed inset-y-0 z-50 row-span-2 flex w-(--sidebar-width) max-w-full flex-col border-r transition-all duration-300 lg:relative lg:translate-0 ${isSidebarOpen ? "translate-x-0" : "-translate-x-full"}`}
      >
        <ButtonIcon
          className="absolute top-[20px] right-[15px] lg:hidden"
          onClick={closeSidebar}
        >
          <X />
        </ButtonIcon>
        {/* Logo + App Name */}
        <div className="flex items-center gap-3 p-6">
          <Logo />
          <Heading
            Tag="h3"
            size="sm"
            title="MedAI"
            subtitle={`${role} Portal`}
            capitalizeSubtitle
          />
        </div>

        {/* Navigation Links */}
        <nav className="flex-1 space-y-1 px-4 py-4">
          {LINKS[role]?.map(({ text, href, items, icon: Icon }) =>
            !items ? (
              <SidebarNavItem
                key={text}
                text={text}
                href={href}
                icon={<Icon size={18} />}
                onClick={closeSidebar}
              />
            ) : (
              <SidebarNavGroup
                key={text}
                text={text}
                icon={<Icon size={18} />}
                items={items}
                onClose={closeSidebar}
              />
            ),
          )}

          {/* Account Section */}
          <div className="pt-4 pb-2">
            <p className="text-text-subtle px-3 text-[10px] font-bold tracking-wider uppercase">
              Account
            </p>
          </div>
          <Link
            className="text-text-muted hover:bg-surface-overlay flex items-center gap-3 rounded-lg px-3 py-2.5 transition-colors"
            href="#"
          >
            <User size={18} />
            <span className="text-sm">Profile</span>
          </Link>
          <Link
            className="text-text-muted hover:bg-surface-overlay flex items-center gap-3 rounded-lg px-3 py-2.5 transition-colors"
            href="#"
          >
            <Settings size={18} />
            <span className="text-sm">Settings</span>
          </Link>
        </nav>

        {/* Logout */}
        <div className="border-border border-t p-4">
          <LogoutButton />
        </div>
      </aside>
    </>
  );
}

export default Sidebar;
