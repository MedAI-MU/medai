"use client";

import { useState, useEffect } from "react";
import Logo from "../ui/Logo";
import AuthActions from "./AuthActions";
import Navbar from "./Navbar";
import MobileMenu from "./MobileMenu";
import Container from "../ui/Container";
import MenuToggler from "../ui/MenuToggler";
import DarkmodeToggler from "../ui/DarkmodeToggler";
import HeaderShell from "../ui/HeaderShell";

const NAV_LINKS = [
  { text: "Home", to: "/#" },
  { text: "Services", to: "/#services" },
  { text: "Doctors", to: "/#doctors" },
  { text: "About", to: "/#about" },
  { text: "Contact", to: "/#contact" },
  { text: "Mobile App", to: "/download" },
];

function LandingHeader() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    if (mobileMenuOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }
    return () => {
      document.body.style.overflow = "";
    };
  }, [mobileMenuOpen]);

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
          <MobileMenu
            navLinks={NAV_LINKS}
            isOpen={mobileMenuOpen}
            onClose={() => setMobileMenuOpen(false)}
          />
        </div>
      </Container>
    </HeaderShell>
  );
}

export default LandingHeader;
