"use client";

import {
  CalendarCheck,
  CalendarPlus,
  FileText,
  LayoutDashboard,
  LogOut,
  MessageCircle,
  Settings,
  User,
  X,
} from "lucide-react";
import Link from "next/link";
import Logo from "./Logo";
import { usePathname } from "next/navigation";
import { useSidebar } from "@/contexts/SidebarContext";
import Overlay from "./Overlay";
import ButtonIcon from "./ButtonIcon";

const LINKS = {
  patient: [
    { text: "dashboard", href: "/patient", icon: LayoutDashboard },
    { text: "book appointment", href: "/", icon: CalendarPlus },
    { text: "my appointments", href: "#", icon: CalendarCheck },
    {
      text: "medical records",
      href: "/patient/medical-records",
      icon: FileText,
    },
    { text: "messages", href: "#", icon: MessageCircle },
  ],
};

function Sidebar() {
  const { isSidebarOpen, closeSidebar } = useSidebar();
  const pathname = usePathname();

  return (
    <>
      {isSidebarOpen && (
        <Overlay className="lg:hidden" onClick={closeSidebar} />
      )}

      <aside
        className={`border-border bg-surface fixed inset-y-0 z-50 row-span-2 flex w-(--sidebar-width) flex-col border-r transition-all duration-300 lg:relative lg:translate-0 ${isSidebarOpen ? "translate-x-0" : "-translate-x-full"}`}
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
          <div>
            <h1 className="text-text-base text-lg leading-none font-bold">
              MedAI
            </h1>
            <p className="text-text-subtle mt-1 text-xs">Patient Portal</p>
          </div>
        </div>

        {/* Navigation Links */}
        <nav className="flex-1 space-y-1 px-4 py-4">
          {LINKS["patient"].map(({ text, href, icon: Icon }) => {
            const isActive = pathname === href;
            return (
              <Link
                key={text}
                className={`flex items-center gap-3 rounded-lg px-3 py-2.5 transition-colors ${
                  isActive
                    ? "bg-primary/10 text-primary font-medium"
                    : "text-text-muted hover:bg-surface-overlay"
                }`}
                href={href}
                onClick={closeSidebar}
              >
                <Icon size={18} />
                <span className="text-sm capitalize">{text}</span>
              </Link>
            );
          })}

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
          <button className="text-danger hover:bg-danger-muted flex w-full items-center gap-3 rounded-lg px-3 py-2 transition-colors">
            <LogOut size={18} />
            <span className="text-sm font-medium">Log out</span>
          </button>
        </div>
      </aside>
    </>
  );
}

export default Sidebar;
