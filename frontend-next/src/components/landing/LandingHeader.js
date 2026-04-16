"use client";

import { useState } from "react";
import Logo from "../ui/Logo";
import AuthActions from "./AuthActions";
import Navbar from "./Navbar";
import MobileMenu from "./MobileMenu";
import Container from "../ui/Container";
import MenuToggler from "../ui/MenuToggler";
import DarkmodeToggler from "../ui/DarkmodeToggler";
import HeaderShell from "../ui/HeaderShell";

const NAV_LINKS = [
  { text: "Home", to: "#" },
  { text: "Services", to: "#services" },
  { text: "Doctors", to: "#doctors" },
  { text: "About", to: "#about" },
  { text: "Contact", to: "#contact" },
];

function LandingHeader() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <HeaderShell className="sticky top-0">
      <Container>
        <div className="relative flex items-center justify-between">
          <Logo />
          <Navbar NAV_LINKS={NAV_LINKS} />

          {/* Right side: action buttons + dark mode toggle */}
          <div className="flex items-center gap-2">
            <DarkmodeToggler />
            <AuthActions />
            <MenuToggler
              menuOpen={mobileMenuOpen}
              onClick={() => setMobileMenuOpen((prev) => !prev)}
            />
          </div>
          {/* Mobile Menu */}
          {mobileMenuOpen && <MobileMenu navLinks={NAV_LINKS} />}
        </div>
      </Container>
    </HeaderShell>
  );
}

export default LandingHeader;
