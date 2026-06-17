import DeleteButtonIcon from "../ui/DeleteButtonIcon";

function TemplateTimeInterval({
  startInput,
  endInput,
  onDelete,
  isSubmitting,
}) {
  return (
    <div className="bg-surface border-border grid grid-cols-1 items-center justify-between gap-3 rounded-lg border p-3 shadow-sm sm:grid-cols-[auto_1fr_auto_1fr_auto]">
      <span className="text-text-muted text-sm max-sm:ml-1">Start</span>
      {startInput}

      <span className="text-text-muted text-sm max-sm:ml-1">End</span>
      {endInput}

      <div className="flex justify-end">
        <DeleteButtonIcon
          onClick={onDelete}
          aria-label="Remove time interval"
          disabled={isSubmitting}
          className="cursor-pointer"
        />
      </div>
    </div>
  );
}

export default TemplateTimeInterval;
