"use client";

import { useState, useTransition } from "react";
import { useRouter } from "next/navigation";
import { ChevronLeft, ChevronRight, CalendarCheck2 } from "lucide-react";
import { usePaginationNav } from "@/hooks/usePaginationNav";
import { cn } from "@/lib/utils";

import DayCard from "./DayCard";
import SlotsPanel from "./SlotsPanel";

function DayCarouselWrapper({ days }) {
  const navigateTo = usePaginationNav();
  const router = useRouter();
  const [isPending, startTransition] = useTransition();

  // Make all days has only available slots
  const daysWithAvailableSlots = days?.data?.map((day) => ({
    ...day,
    slots: day?.slots?.filter((slot) => slot.status === "available"),
  }));

  // Auto-select the first day that has slots, fallback to first day
  const firstAvailable =
    daysWithAvailableSlots?.find((d) => d.slots.length > 0) ?? days?.data[0];
  const [selectedDay, setSelectedDay] = useState(firstAvailable?.day ?? null);

  const currentPage = days?.currentPage;
  const selectedDayData = daysWithAvailableSlots.find(
    (d) => d.day === selectedDay,
  );

  function handleNav(newPage) {
    startTransition(() => {
      router.replace(navigateTo(newPage));
    });
  }

  return (
    <div className="space-y-6">
      {/* Section header */}
      <div className="flex items-center justify-between">
        <h3 className="text-text-base flex items-center gap-2 font-semibold">
          <CalendarCheck2 size={18} className="text-primary" />
          Select a Date
        </h3>

        {/* Prev / Next page controls */}
        <div className="flex gap-2">
          <button
            onClick={() => handleNav(currentPage - 1)}
            disabled={!days.hasPrevious || isPending}
            className={cn(
              "rounded-lg border p-2 transition-colors",
              "border-border bg-surface text-text-muted",
              "hover:bg-surface-overlay hover:text-primary",
              "disabled:cursor-not-allowed disabled:opacity-40",
            )}
            aria-label="Previous page"
          >
            <ChevronLeft size={18} />
          </button>
          <button
            onClick={() => handleNav(currentPage + 1)}
            disabled={!days.hasNext || isPending}
            className={cn(
              "rounded-lg border p-2 transition-colors",
              "border-border bg-surface text-text-muted",
              "hover:bg-surface-overlay hover:text-primary",
              "disabled:cursor-not-allowed disabled:opacity-40",
            )}
            aria-label="Next page"
          >
            <ChevronRight size={18} />
          </button>
        </div>
      </div>

      {/* Scrollable day strip */}
      <div
        className={cn(
          "no-scrollbar -mx-1 flex gap-3 overflow-x-auto px-1 py-2 pb-3",
          isPending && "pointer-events-none opacity-60",
        )}
      >
        {daysWithAvailableSlots.map((day) => (
          <DayCard
            key={day.day}
            date={day.day}
            hasSlots={day.slots.length > 0}
            isSelected={selectedDay === day.day}
            onClick={() => setSelectedDay(day.day)}
          />
        ))}
      </div>

      {/* Slots panel for selected day */}
      {selectedDay && (
        <SlotsPanel
          selectedDay={selectedDay}
          slots={selectedDayData?.slots ?? []}
        />
      )}
    </div>
  );
}

export default DayCarouselWrapper;
