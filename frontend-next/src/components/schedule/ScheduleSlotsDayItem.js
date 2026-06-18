import { useFieldArray, useWatch } from "react-hook-form";
import { Trash2 } from "lucide-react";
import { format } from "date-fns";
import { AnimatePresence, motion } from "framer-motion";

import Heading from "@/components/ui/Heading";
import FormDatePicker from "@/components/ui/FormDatePicker";
import FormLabel from "@/components/ui/FormLabel";
import FormInput from "@/components/ui/FormInput";
import ErrorMessage from "@/components/ui/ErrorMessage";
import TemplateTimeInterval from "./TemplateTimeInterval";
import DayBox from "./DayBox";
import SlotsBox from "./SlotsBox";

function ScheduleSlotsDayItem({
  control,
  register,
  dayIndex,
  getValues,
  isSubmitting,
  trigger,
  errors,
  onDeleteDay,
  isRemovable,
}) {
  const {
    fields: slots,
    append: addSlot,
    remove: removeSlot,
  } = useFieldArray({
    control,
    name: `days.${dayIndex}.slots`,
  });
  const date = useWatch({ control, name: `days.${dayIndex}.date` });

  function handleAddSlot() {
    const currentSlots = getValues(`days.${dayIndex}.slots`);
    const lastEndTime = currentSlots?.[currentSlots.length - 1]?.endTime;

    if (!lastEndTime) addSlot({ startTime: "09:00", endTime: "10:00" });
    else {
      let [hoursStr = "09", mins = "00"] = lastEndTime.split(":");
      const nextHour = (Number(hoursStr) + 1) % 24;
      addSlot({
        startTime: lastEndTime,
        endTime: `${String(nextHour).padStart(2, "0")}:${mins.padStart(2, "0")}`,
      });
    }

    trigger();
  }

  const overlapError = errors?.days?.[dayIndex]?.message;
  const emptySlotsErr = errors?.days?.[dayIndex]?.slots?.root?.message;
  const slotsError = overlapError || emptySlotsErr;

  return (
    <DayBox>
      <div className="flex items-center justify-between">
        <Heading
          title={date ? format(date, "EEEE, MMM d") : null}
          size="xs"
          Tag="h3"
        />
        <AnimatePresence>
          {isRemovable && (
            <motion.div
              initial={{ scale: 0, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0, opacity: 0 }}
              transition={{ duration: 0.3, type: "spring" }}
            >
              <button
                type="button"
                className="text-danger hover:bg-danger-muted flex shrink-0 cursor-pointer items-center gap-1 rounded-sm px-1.5 py-0.5 text-sm transition-all"
                onClick={onDeleteDay}
                disabled={isSubmitting}
              >
                <Trash2 size={14} />
                <span>Remove Day</span>
              </button>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      <FormDatePicker
        label="Select Date"
        placeholder="yyyy-mm-dd"
        name={`days.${dayIndex}.date`}
        control={control}
        required
        disabledRange={{ before: new Date() }}
        disabled={isSubmitting}
      />
      {slots?.length > 0 && (
        <div className="flex flex-col">
          <FormLabel label="Time Slots" />
          <SlotsBox>
            {slots.map((slot, slotIdx) => {
              const startTimeErr =
                errors?.days?.[dayIndex]?.slots?.[slotIdx]?.startTime?.message;
              const endTimeErr =
                errors?.days?.[dayIndex]?.slots?.[slotIdx]?.endTime?.message;

              return (
                <TemplateTimeInterval
                  key={slot.id}
                  startInput={
                    <FormInput
                      type="time"
                      {...register(
                        `days.${dayIndex}.slots.${slotIdx}.startTime`,
                        {
                          onChange: () => {
                            if (startTimeErr || endTimeErr || overlapError)
                              trigger();
                          },
                        },
                      )}
                      error={startTimeErr}
                      disabled={isSubmitting}
                    />
                  }
                  endInput={
                    <FormInput
                      type="time"
                      {...register(
                        `days.${dayIndex}.slots.${slotIdx}.endTime`,
                        {
                          onChange: () => {
                            if (startTimeErr || endTimeErr || overlapError)
                              trigger();
                          },
                        },
                      )}
                      error={endTimeErr}
                      disabled={isSubmitting}
                    />
                  }
                  onDelete={() => removeSlot(slotIdx)}
                  isSubmitting={isSubmitting}
                />
              );
            })}
          </SlotsBox>
        </div>
      )}
      <div className="flex items-center justify-between gap-2">
        <button
          type="button"
          className="text-primary cursor-pointer text-sm font-medium hover:underline"
          onClick={handleAddSlot}
          disabled={isSubmitting}
        >
          + Add Slot
        </button>
        {slotsError && <ErrorMessage message={slotsError} withBg />}
      </div>
    </DayBox>
  );
}

export default ScheduleSlotsDayItem;
