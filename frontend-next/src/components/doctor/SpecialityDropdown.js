"use client";

import { useRouter, usePathname, useSearchParams } from "next/navigation";
import { ChevronDown, Stethoscope } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/shadcn/dropdown-menu";

function SpecialityDropdown({ specialities = [] }) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const selectedSpec = searchParams.get("speciality") || "";
  const selected = specialities.find((s) => s?.name === selectedSpec);

  function onSelect(speciality) {
    const params = new URLSearchParams(searchParams);
    if (speciality) {
      params.set("speciality", speciality);
    } else {
      params.delete("speciality");
    }
    params.delete("pageNo");
    router.replace(`${pathname}?${params}`);
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger className="border-border bg-surface-overlay text-text-base focus:ring-primary/90 data-[state=open]:ring-primary/90 flex cursor-pointer items-center gap-2 rounded-lg border py-3 pr-4 pl-3 capitalize transition-all outline-none focus:ring-2 data-[state=open]:ring-2">
        <Stethoscope size={18} className="text-text-subtle shrink-0" />
        <span className="flex-1 text-left">
          {selected ? selected?.name : "All Specialities"}
        </span>
        <ChevronDown size={16} className="text-text-subtle shrink-0" />
      </DropdownMenuTrigger>
      <DropdownMenuContent
        align="start"
        className="w-(--radix-dropdown-menu-trigger-width)"
      >
        <DropdownMenuItem onClick={() => onSelect("")}>
          All Specialities
        </DropdownMenuItem>
        {specialities?.map((spec) => (
          <DropdownMenuItem key={spec.id} onClick={() => onSelect(spec.name)}>
            {spec.name}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

export default SpecialityDropdown;
