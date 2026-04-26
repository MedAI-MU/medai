"use client";

import { useSidebar } from "@/contexts/SidebarContext";
import MenuToggler from "./MenuToggler";

function SidebarToggler() {
  const { toggleSidebar } = useSidebar();

  return <MenuToggler onClick={toggleSidebar} />;
}

export default SidebarToggler;
