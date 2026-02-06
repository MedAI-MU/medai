import { Menu, X } from "lucide-react";

function MenuToggler({ menuOpen, onClick }) {
  return (
    <button className="text-primary-dark p-2 md:hidden" onClick={onClick}>
      {menuOpen ? <X size={24} /> : <Menu size={24} />}
    </button>
  );
}

export default MenuToggler;
