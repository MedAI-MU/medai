"use client";

import { Menu, X } from "lucide-react";
import ButtonIcon from "./ButtonIcon";

function MenuToggler({ menuOpen = false, onClick }) {
  return (
    <ButtonIcon className="lg:hidden" onClick={onClick}>
      {menuOpen ? <X size={24} /> : <Menu size={24} />}
    </ButtonIcon>
  );
}

export default MenuToggler;
