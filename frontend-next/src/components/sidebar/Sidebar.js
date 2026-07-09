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
  Settings,
  Stethoscope,
  Briefcase,
  User,
  Users,
  X,
} from "lucide-react";
import Link from "next/link";
import Logo from "@/components/ui/Logo";
import Overlay from "@/components/ui/Overlay";
import Heading from "@/components/ui/Heading";
import SidebarNavItem from "@/components/sidebar/SidebarNavItem";
import SidebarNavGroup from "@/components/sidebar/SidebarNavGroup";
import LogoutButton from "../ui/LogoutButton";
import SidebarContainer from "./SidebarContainer";
import CloseButton from "./CloseButton";
import { getUserFromToken } from "@/lib/session";

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
      href: "/secretary/scans",
      icon: Scan,
    },
    { text: "messages", href: "#", icon: MessageCircle },
  ],
};

async function Sidebar() {
  const user = await getUserFromToken();
  const role = user?.role;

  return (
    <>
      <Overlay className="lg:hidden" />

      <SidebarContainer>
        <CloseButton />
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
      </SidebarContainer>
    </>
  );
}

export default Sidebar;
