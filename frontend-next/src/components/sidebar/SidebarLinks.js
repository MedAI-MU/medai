"use client";

import {
  CalendarCheck,
  CalendarDays,
  CalendarPlus,
  Clock,
  FileText,
  LayoutDashboard,
  MessageCircle,
  Plus,
  Scan,
  Stethoscope,
  Briefcase,
  User,
  Users,
} from "lucide-react";
import SidebarNavItem from "./SidebarNavItem";
import SidebarNavGroup from "./SidebarNavGroup";

const LINKS = {
  patient: [
    { text: "dashboard", href: "/patient", icon: LayoutDashboard },
    {
      text: "book appointment",
      href: "/patient/book-appointment",
      icon: CalendarPlus,
    },
    {
      text: "my appointments",
      href: "/patient/appointments",
      icon: CalendarCheck,
    },
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
  secretary: [
    { text: "dashboard", href: "/secretary", icon: LayoutDashboard },
    {
      text: "appointments",
      href: "/secretary/appointments",
      icon: CalendarCheck,
    },
    {
      text: "doctors",
      href: "/secretary/doctors",
      icon: Stethoscope,
      matchPrefix: true,
    },
    {
      text: "specialities",
      href: "/secretary/specialities",
      icon: Briefcase,
    },
    {
      text: "patients",
      href: "/secretary/patients",
      icon: Users,
    },
    {
      text: "scans & reports",
      href: "#",
      icon: Scan,
    },
    { text: "messages", href: "#", icon: MessageCircle },
  ],
  manager: [
    { text: "dashboard", href: "/manager", icon: LayoutDashboard },
    {
      text: "users",
      href: "/manager/users",
      icon: Users,
      matchPrefix: true,
    },
    {
      text: "patients",
      href: "/manager/patients",
      icon: User,
    },
  ],
};

function SidebarLinks({ role }) {
  return (
    <nav className="flex-1 space-y-1 px-4 py-4">
      {LINKS[role]?.map(({ text, href, items, icon: Icon, ...rest }) =>
        !items ? (
          <SidebarNavItem
            key={text}
            text={text}
            href={href}
            icon={<Icon size={18} />}
            {...rest}
          />
        ) : (
          <SidebarNavGroup
            key={text}
            text={text}
            icon={<Icon size={18} />}
            items={items}
          />
        ),
      )}

      {/* Account Section */}
      <div className="pt-4 pb-2">
        <p className="text-text-subtle px-3 text-[10px] font-bold tracking-wider uppercase">
          Account
        </p>
      </div>
      <SidebarNavItem
        href="/profile"
        text="profile"
        icon={<User size={18} />}
      />
    </nav>
  );
}

export default SidebarLinks;
