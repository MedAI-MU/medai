"use client";

import { useState } from "react";
import Logo from "../ui/Logo";
import ActionButtons from "./ActionButtons";
import Navbar from "./Navbar";
import MenuToggler from "./MenuToggler";
import MobileMenu from "./MobileMenu";
import Container from "../ui/Container";

const NavLinks = [
  { text: "Home", to: "#home" },
  { text: "Services", to: "#services" },
  { text: "Doctors", to: "#doctors" },
  { text: "About", to: "#about" },
  { text: "Contact", to: "#contact" },
];

function Header() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <nav
      className="border-border-gray sticky top-0 z-50 border-b bg-white"
      style={{ boxShadow: "0 1px 3px rgba(0,0,0,0.1)" }}
    >
      <Container className="px-6 py-4">
        <div className="flex items-center justify-between">
          <Logo />
          <Navbar navLinks={NavLinks} />
          <ActionButtons />

          {/* Mobile Menu Button */}
          <MenuToggler
            menuOpen={mobileMenuOpen}
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          />
        </div>

        {/* Mobile Menu */}
        {mobileMenuOpen && <MobileMenu navLinks={NavLinks} />}
      </Container>
    </nav>
  );
}

export default Header;
