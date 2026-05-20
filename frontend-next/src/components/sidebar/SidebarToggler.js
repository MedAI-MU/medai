"use client";

import { useSidebar } from "@/contexts/SidebarContext";
import MenuToggler from "@/components/ui/MenuToggler";

function SidebarToggler() {
  const { toggleSidebar } = useSidebar();

  return <MenuToggler onClick={toggleSidebar} />;
}

export default SidebarToggler;
