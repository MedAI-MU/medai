"use client";

import { endOfWeek, format, isThisWeek, startOfWeek } from "date-fns";
import { ChevronLeft, ChevronRight } from "lucide-react";
import ButtonIcon from "@/components/ui/ButtonIcon";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { formatDate, parseDate } from "@/lib/utils/DateTimeHelpers";

export default function WeekNavigator() {
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const router = useRouter();
  const startDateParam = searchParams.get("startDate");

  const startDate = startDateParam
    ? parseDate(startDateParam)
    : startOfWeek(new Date());
  const endDate = endOfWeek(startDate);

  function handlePrev() {
    const params = new URLSearchParams(searchParams);
    startDate.setDate(startDate.getDate() - 7);

    params.set("startDate", formatDate(startDate));
    router.replace(`${pathname}?${params.toString()}`);
  }

  function handleNext() {
    const params = new URLSearchParams(searchParams);
    startDate.setDate(startDate.getDate() + 7);

    params.set("startDate", formatDate(startDate));
    router.replace(`${pathname}?${params.toString()}`);
  }

  const isCurrentWeek = isThisWeek(startDate);
  const dateToDisplay = isCurrentWeek
    ? "Current Week"
    : `${format(startDate, "MMM dd")} – ${format(endDate, "MMM dd")}, ${new Date().getFullYear()}`;

  return (
    <div className="flex items-center gap-4">
      <ButtonIcon type="button" className="cursor-pointer" onClick={handlePrev}>
        <ChevronLeft size={18} />
      </ButtonIcon>

      <span
        className={`text-text-base whitespace-nowrap ${isCurrentWeek ? "font-bold italic" : ""}`}
      >
        {dateToDisplay}
      </span>

      <ButtonIcon type="button" className="cursor-pointer" onClick={handleNext}>
        <ChevronRight size={18} />
      </ButtonIcon>
    </div>
  );
}
