"use client";

import { useState } from "react";
import { useFieldArray, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { ScheduleTemplateSchema } from "@/lib/zod/schemas";
import { getUniqueDays, stripSeconds } from "@/lib/utils/DateTimeHelpers";
import { DAYS_OF_WEEK } from "@/constants/schedules";

function useTemplateForm({ template }) {
  const { name, slots, id: templateId } = template;
  const isEdit = Boolean(templateId);

  // 1) Form hooks
  const form = useForm({
    resolver: zodResolver(ScheduleTemplateSchema),
    defaultValues: isEdit
      ? {
          name,
          slots: slots.map((slot) => ({
            weekDay: slot.weekDay,
            startTime: stripSeconds(slot.startTime),
            endTime: stripSeconds(slot.endTime),
          })),
        }
      : { name: "", slots: [] },
  });
  const {
    control,
    reset,
    trigger,
    formState: { errors, isSubmitted },
  } = form;
  const fieldArray = useFieldArray({
    control,
    name: "slots",
  });
  const { fields, append, remove } = fieldArray;

  // 2) Checkbox State
  const [applyToAll, setApplyToAll] = useState(false);

  // 3) Derived data for slots
  const currentSlots = fields.map((f, i) => ({ ...f, originalIndex: i }));
  const uniqueSelectedDays = getUniqueDays(currentSlots).sort((a, b) => a - b);
  const firstDay = uniqueSelectedDays.length > 0 ? uniqueSelectedDays[0] : null;
  const groupedFields = uniqueSelectedDays.map((dayNum) => ({
    dayNum,
    dayName: DAYS_OF_WEEK[dayNum],
    slots: currentSlots.filter((f) => f.weekDay === dayNum),
  }));
  const slotsError = errors?.slots?.root?.message || errors?.slots?.message;

  // Slots handlers
  async function handleAddPeriod(dayNum) {
    if (applyToAll) {
      const newSlots = uniqueSelectedDays.map((day) => ({
        weekDay: day,
        startTime: "09:00",
        endTime: "17:00",
      }));
      append(newSlots);
    } else {
      append({ weekDay: dayNum, startTime: "09:00", endTime: "17:00" });
    }
    if (isSubmitted) await trigger("slots");
  }

  async function handleRemovePeriod(slotToRemove) {
    if (applyToAll) {
      const firstDaySlots = currentSlots.filter((f) => f.weekDay === firstDay);
      const periodIndex = firstDaySlots.findIndex(
        (f) => f.id === slotToRemove.id,
      );
      if (periodIndex >= 0) {
        const indicesToRemove = [];
        uniqueSelectedDays.forEach((day) => {
          const daySlots = currentSlots.filter((f) => f.weekDay === day);
          if (daySlots[periodIndex]) {
            indicesToRemove.push(daySlots[periodIndex].originalIndex);
          }
        });
        // Sort to aviod 'index shift problem'
        indicesToRemove.sort((a, b) => b - a).forEach((idx) => remove(idx));
      }
    } else {
      remove(slotToRemove.originalIndex);
    }
    if (isSubmitted) await trigger("slots");
  }

  async function handleToggle(dayNum) {
    const selectedIndices = currentSlots
      .filter((slot) => slot.weekDay === dayNum)
      .map((s) => s.originalIndex);

    if (selectedIndices.length > 0) {
      // Reverse to aviod 'index shift problem'
      selectedIndices.reverse().forEach((i) => remove(i));
    } else {
      let newSlotsToAdd = [
        { weekDay: dayNum, startTime: "09:00", endTime: "17:00" },
      ];

      if (applyToAll && firstDay !== null) {
        newSlotsToAdd = currentSlots
          .filter((f) => f.weekDay === firstDay)
          .map((slot) => ({
            weekDay: dayNum,
            startTime: slot.startTime,
            endTime: slot.endTime,
          }));
      }

      append(newSlotsToAdd);
    }

    if (isSubmitted) await trigger("slots");
  }

  function handleReset() {
    if (isEdit) {
      reset({
        name,
        slots: slots.map((slot) => ({
          weekDay: slot.weekDay,
          startTime: stripSeconds(slot.startTime),
          endTime: stripSeconds(slot.endTime),
        })),
      });
    } else {
      reset({ name: "", slots: [] });
    }
    setApplyToAll(false);
  }

  return {
    form,
    fieldArray,

    // States
    applyToAll,
    setApplyToAll,

    // derived
    currentSlots,
    firstDay,
    groupedFields,
    uniqueSelectedDays,
    slotsError,

    // handlers
    handleAddPeriod,
    handleRemovePeriod,
    handleToggle,
    handleReset,
  };
}

export { useTemplateForm };
