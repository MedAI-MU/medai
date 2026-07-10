"use client";

import { X } from "lucide-react";
import ButtonIcon from "../ui/ButtonIcon";
import { useSidebar } from "@/contexts/SidebarContext";

function CloseButton() {
  const { closeSidebar } = useSidebar();

  return (
    <ButtonIcon
      className="absolute top-[20px] right-[15px] lg:hidden"
      onClick={closeSidebar}
    >
      <X />
    </ButtonIcon>
  );
}

export default CloseButton;
