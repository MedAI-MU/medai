import { Bell, Search, Sun } from "lucide-react";
import ButtonIcon from "./ButtonIcon";
import SidebarToggler from "./SidebarToggler";
import DarkmodeToggler from "./DarkmodeToggler";
import HeaderShell from "./HeaderShell";
import Container from "./Container";

function DashboardHeader() {
  return (
    <HeaderShell>
      <Container className="flex items-center justify-between">
        {/* ── Left: Hamburger + Search ── */}
        <div className="flex flex-1 items-center gap-4">
          {/* Mobile Hamburger */}
          <SidebarToggler />

          {/* Search Bar */}
          <div className="w-full max-w-md">
            <div className="group relative">
              <span className="text-text-subtle group-focus-within:text-primary absolute top-1/2 left-3 -translate-y-1/2 transition-colors">
                <Search size={16} />
              </span>
              <input
                className="bg-surface-overlay text-text-base placeholder:text-text-subtle focus:ring-primary/20 w-full rounded-lg border-none py-2 pr-4 pl-10 text-sm outline-none focus:ring-2"
                placeholder="Search medical history, tests, or records..."
                type="text"
              />
            </div>
          </div>
        </div>

        {/* ── Right: Actions + User ── */}
        <div className="ml-4 flex items-center gap-2 md:gap-4">
          {/* Theme Toggle */}
          <DarkmodeToggler />

          {/* Notifications */}
          <ButtonIcon className="relative">
            <Bell size={20} />
            <span className="border-surface bg-danger absolute top-2 right-2.5 h-2 w-2 rounded-full border-2"></span>
          </ButtonIcon>

          {/* Divider */}
          <div className="bg-border mx-1 hidden h-8 w-px sm:block" />

          {/* User Info */}
          <div className="group flex cursor-pointer items-center gap-3">
            <div className="hidden text-right md:block">
              <p className="text-text-base group-hover:text-primary text-sm font-semibold transition-colors">
                Alex Thompson
              </p>
              <p className="text-text-subtle text-xs">Patient ID: #22934</p>
            </div>

            {/* Avatar */}
            <div className="border-surface bg-primary/20 size-9 shrink-0 overflow-hidden rounded-full border-2 md:size-10">
              <img
                alt="User avatar"
                className="h-full w-full object-cover"
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuAOcWE1M30lFGq-GjI6ngcTxkRqQomwggB3oNi-WAZwrzRGkM02YAQEBShWWHPvoAYSzlyLA7a96eeDZlqr5D7pLCRXdvR3dbk8RcLZvIrgQ4iTy4lEt-tLgHaMfhTmqk4C4o-wEXMINTefQgYrCRFfHEvbHY6EHwqYR_S4cDg-zGkgF9grT_ds4MBR1jlKH3Nrv_ZI0N1yjGm5dJLqO_GlbVfECJO-4M8dV136Jq06XDCyjwyxIsUllUCeBNiSGI2yCi-qXDC0UoM"
              />
            </div>
          </div>
        </div>
      </Container>
    </HeaderShell>
  );
}

export default DashboardHeader;
