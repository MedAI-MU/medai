"use client";

import { ArrowUp, ArrowDown } from "lucide-react";
import ButtonIcon from "./ButtonIcon";

export default function SortToggle({ direction, onToggle, label = "Date" }) {
  const Icon = direction === "asc" ? ArrowUp : ArrowDown;

  return (
    <ButtonIcon
      onClick={onToggle}
      className="text-text-muted flex items-center gap-1.5 text-xs font-medium"
      title={`Sort by ${label} ${direction === "asc" ? "ascending" : "descending"}`}
    >
      <Icon size={14} />
      <span>{label}</span>
    </ButtonIcon>
  );
}
