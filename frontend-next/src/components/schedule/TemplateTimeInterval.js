import ButtonIcon from "@/components/ui/ButtonIcon";
import { X } from "lucide-react";

function TemplateTimeInterval({ day, startInput, endInput, onDelete }) {
  return (
    <div className="bg-surface border-border grid grid-cols-[1fr_auto] items-center justify-between gap-3 rounded-lg border p-3 shadow-sm sm:grid-cols-[auto_1fr_auto]">
      <span className="text-text-base font-medium">{day || ""}</span>

      {/* Time inputs */}
      <div className="grid grid-cols-[1fr_1fr] items-center gap-2 max-sm:col-span-2 max-sm:row-start-2 sm:grid-cols-[auto_1fr_auto_1fr]">
        <span className="text-text-muted text-sm">Start</span>
        {startInput}

        <span className="text-text-muted text-sm">End</span>
        {endInput}
      </div>

      {/* Delete Time Button */}
      <ButtonIcon
        type="button"
        onClick={onDelete}
        className="hover:bg-danger-muted hover:text-danger shrink-0"
        aria-label={`Remove ${day}`}
      >
        <X size={16} />
      </ButtonIcon>
    </div>
  );
}

export default TemplateTimeInterval;
