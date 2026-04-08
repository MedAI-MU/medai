"use client";

import { Moon, Sun } from "lucide-react";
import ButtonIcon from "./ButtonIcon";
import { useTheme } from "next-themes";

function DarkmodeToggler({ className }) {
  const { resolvedTheme, setTheme } = useTheme();

  return (
    <ButtonIcon
      className={className}
      onClick={() => setTheme(resolvedTheme === "dark" ? "light" : "dark")}
    >
      <Sun size={20} className="dark:hidden" />
      <Moon size={20} className="hidden dark:block" />
    </ButtonIcon>
  );
}

export default DarkmodeToggler;
