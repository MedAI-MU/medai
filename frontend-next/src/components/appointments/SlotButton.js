"use client";

import { Clock } from "lucide-react";
import { cn } from "@/lib/utils";
import { stripSeconds } from "@/lib/utils/DateTimeHelpers";
import FormDialog from "../ui/FormDialog";
import CreateAppointmentForm from "./CreateAppointmentForm";

function SlotButton({ slot, day }) {
  if (slot.status !== "available") return null;

  return (
    <FormDialog
      title="Confirm Appointment"
      form={<CreateAppointmentForm slot={slot} day={day} />}
    >
      <button
        className={cn(
          "group flex items-center gap-2 rounded-lg border px-4 py-3 text-sm font-medium shadow-sm transition-all duration-200",
          "border-border bg-surface text-text-base",
          "hover:border-primary hover:bg-primary/10 hover:text-primary hover:shadow-md",
          "cursor-pointer active:scale-95",
        )}
      >
        <Clock
          size={15}
          className="text-primary opacity-60 transition-opacity group-hover:opacity-100"
        />
        <span className="whitespace-nowrap">
          {stripSeconds(slot.startTime)} – {stripSeconds(slot.endTime)}
        </span>
      </button>
    </FormDialog>
  );
}

export default SlotButton;
