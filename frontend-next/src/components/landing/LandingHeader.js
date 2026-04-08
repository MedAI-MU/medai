"use client";

import { useState } from "react";
import Logo from "../ui/Logo";
import ActionButtons from "./ActionButtons";
import Navbar from "./Navbar";
import MobileMenu from "./MobileMenu";
import Container from "../ui/Container";
import MenuToggler from "../ui/MenuToggler";
import DarkmodeToggler from "../ui/DarkmodeToggler";
import HeaderShell from "../ui/HeaderShell";

function LandingHeader() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <HeaderShell className="sticky top-0">
      <Container className="flex items-center justify-between">
        <Logo />
        <Navbar />

        {/* Right side: action buttons + dark mode toggle */}
        <div className="flex items-center gap-2">
          <DarkmodeToggler />
          <ActionButtons />
          <MenuToggler
            menuOpen={mobileMenuOpen}
            onClick={() => setMobileMenuOpen((prev) => !prev)}
          />
        </div>

        {/* Mobile Menu */}
        {mobileMenuOpen && <MobileMenu navLinks={NavLinks} />}
      </Container>
    </HeaderShell>
  );
}

export default LandingHeader;
