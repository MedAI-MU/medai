import Logo from "@/components/ui/Logo";
import Overlay from "@/components/ui/Overlay";
import Heading from "@/components/ui/Heading";
import LogoutButton from "../ui/LogoutButton";
import SidebarContainer from "./SidebarContainer";
import CloseButton from "./CloseButton";
import { getUserFromToken } from "@/lib/session";
import SidebarLinks from "./SidebarLinks";

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
        <SidebarLinks role={role} />

        {/* Logout */}
        <div className="border-border border-t p-4">
          <LogoutButton />
        </div>
      </SidebarContainer>
    </>
  );
}

export default Sidebar;
