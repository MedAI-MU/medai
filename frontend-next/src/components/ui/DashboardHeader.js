import { Bell } from "lucide-react";
import ButtonIcon from "./ButtonIcon";
import SidebarToggler from "../sidebar/SidebarToggler";
import DarkmodeToggler from "./DarkmodeToggler";
import HeaderShell from "./HeaderShell";
import Container from "./Container";
import UserInfo from "./UserInfo";

function DashboardHeader() {
  return (
    <HeaderShell>
      <Container className="flex items-center justify-between">
        <SidebarToggler />

        {/* ── Right: Actions + User ── */}
        <div className="ml-4 flex w-full items-center justify-end gap-2 md:gap-4">
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
          <UserInfo />
        </div>
      </Container>
    </HeaderShell>
  );
}

export default DashboardHeader;
