"use client";

import { Clock, CalendarDays, Zap } from "lucide-react";
import { deleteScheduleTemplate } from "@/services/client/schedule";
import { useAuth } from "@/contexts/AuthContext";
import { DAYS_OF_WEEK } from "@/constants/schedules";

import Button from "@/components/ui/Button";
import Card from "@/components/ui/Card";
import Badge from "@/components/ui/Badge";
import DeleteAction from "@/components/ui/DeleteAction";
import FormDialog from "@/components/ui/FormDialog";
import AddEditTemplate from "./AddEditTemplate";
import ApplyTemplateForm from "./ApplyTemplateForm";
import { formatTime12h, getUniqueDays } from "@/lib/utils/DateTimeHelpers";

function ScheduleTemplateCard({ template }) {
  const { user } = useAuth();
  const { id: templateId, name, slots = [] } = template;

  const uniqueDays = getUniqueDays(slots);

  const startTime = slots[0]?.startTime;
  const endTime = slots[0]?.endTime;
  const dayCount = uniqueDays?.length;

  // all 7 days, highlight selected ones
  const selectedDays = uniqueDays;
  const uniqueTimeRanges = new Set(
    slots.map((slot) => `${slot.startTime}-${slot.endTime}`),
  );
  const hasDifferentTimes = uniqueTimeRanges.size > 1;

  return (
    <Card className="flex flex-col gap-4 transition-shadow hover:shadow-md sm:flex-row sm:items-center sm:justify-between">
      {/* Left — Info */}
      <div className="flex flex-col gap-3">
        <h3 className="text-text-base text-base font-semibold">{name}</h3>

        {/* Time + Day count */}
        <div className="text-text-muted flex flex-wrap items-center gap-3 text-sm">
          <span className="flex items-center gap-1.5">
            <Clock size={14} className="text-primary shrink-0" />
            {hasDifferentTimes
              ? "Variable schedule"
              : `${formatTime12h(startTime)} – ${formatTime12h(endTime)}`}
          </span>
          <span className="flex items-center gap-1.5">
            <CalendarDays size={14} className="text-primary shrink-0" />
            {dayCount} {dayCount === 1 ? "day" : "days"}
          </span>
        </div>

        {/* Day pills */}
        <div className="flex flex-wrap gap-1.5">
          {DAYS_OF_WEEK.map((day, index) => {
            const isSelected = selectedDays.includes(index);
            return (
              <Badge
                key={day}
                color={isSelected ? "blue" : "slate"}
                text={day.slice(0, 3)}
                isRounded={false}
              />
            );
          })}
        </div>
      </div>

      {/* Right — Actions */}
      <div className="border-border flex shrink-0 items-center justify-end gap-2 border-t pt-4 sm:border-none sm:pt-0">
        <FormDialog
          title="Apply Schedule Template"
          description="Define the start and end dates to apply this template to patient scheduling records."
          form={<ApplyTemplateForm templateId={templateId} />}
        >
          <Button className="mr-auto sm:mr-0" startIcon={<Zap size={14} />}>
            Apply
          </Button>
        </FormDialog>

        <AddEditTemplate template={template} />

        <DeleteAction
          successMessage="Template deleted successfully"
          failMessage="Failed to delete Template"
          onConfirm={() => deleteScheduleTemplate(user?.sub, templateId)}
        />
      </div>
    </Card>
  );
}

export default ScheduleTemplateCard;
