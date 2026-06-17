import ErrorMessage from "@/components/ui/ErrorMessage";
import FormInput from "@/components/ui/FormInput";
import TemplateTimeInterval from "./TemplateTimeInterval";
import Heading from "../ui/Heading";
import DayBox from "./DayBox";
import SlotsBox from "./SlotsBox";

function TemplateDayItem({
  group,
  title,
  register,
  errors,
  isSubmitting,
  onRemovePeriod,
  onAddPeriod,
  onTimeChange,
}) {
  const dayError = errors?.slots?.[group.dayNum]?.message;

  return (
    <DayBox className={`${dayError ? "border-danger" : "border-border"}`}>
      <Heading title={title} size="xs" Tag="h3" />
      <SlotsBox>
        {group.slots.map((slot, periodIndex) => (
          <TemplateTimeInterval
            key={slot.id}
            startInput={
              <FormInput
                type="time"
                {...register(`slots.${slot.originalIndex}.startTime`, {
                  onChange: (e) => onTimeChange(e, periodIndex, "startTime"),
                })}
                error={errors?.slots?.[slot.originalIndex]?.startTime?.message}
                disabled={isSubmitting}
              />
            }
            endInput={
              <FormInput
                type="time"
                {...register(`slots.${slot.originalIndex}.endTime`, {
                  onChange: (e) => onTimeChange(e, periodIndex, "endTime"),
                })}
                error={errors?.slots?.[slot.originalIndex]?.endTime?.message}
                disabled={isSubmitting}
              />
            }
            onDelete={() => onRemovePeriod(slot)}
            isSubmitting={isSubmitting}
          />
        ))}
      </SlotsBox>
      <div className="flex items-center justify-between gap-2">
        <button
          type="button"
          className="text-primary text-sm font-medium hover:underline"
          onClick={onAddPeriod}
          disabled={isSubmitting}
        >
          + Add Slot
        </button>
        {dayError && <ErrorMessage message={dayError} withBg />}
      </div>
    </DayBox>
  );
}
export default TemplateDayItem;
