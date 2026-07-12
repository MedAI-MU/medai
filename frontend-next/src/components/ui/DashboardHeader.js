import { Bell } from "lucide-react";
import ButtonIcon from "./ButtonIcon";
import SidebarToggler from "../sidebar/SidebarToggler";
import DarkmodeToggler from "./DarkmodeToggler";
import HeaderShell from "./HeaderShell";
import Container from "./Container";
import UserInfo from "./UserInfo";
import { Suspense } from "react";
import UserInfoSkeleton from "./UserInfoSkeleton";

function DashboardHeader() {
  return (
    <HeaderShell>
      <Container className="flex items-center justify-between">
        <SidebarToggler />

        {/* ── Right: Actions + User ── */}
        <div className="ml-4 flex w-full items-center justify-end gap-2 md:gap-4">
          {/* Theme Toggle */}
          <DarkmodeToggler />

          {/* Divider */}
          <div className="bg-border mx-1 hidden h-8 w-px sm:block" />

          {/* User Info */}
          <Suspense fallback={<UserInfoSkeleton />}>
            <UserInfo />
          </Suspense>
        </div>
      </Container>
    </HeaderShell>
  );
}

export default DashboardHeader;
