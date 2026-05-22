"use client";

import toast from "react-hot-toast";
import {
  createScheduleTemplate,
  updateScheduleTemplate,
} from "@/services/client/schedule";
import { useAuth } from "@/contexts/AuthContext";
import { useRouter } from "next/navigation";
import { useTemplateForm } from "@/hooks/schedule.js/useTemplateForm";
import { DAYS_OF_WEEK } from "@/constants/schedules";

import { SheetFooter } from "@/components/shadcn/sheet";
import FormInput from "@/components/ui/FormInput";
import FormLabel from "@/components/ui/FormLabel";
import Button from "@/components/ui/Button";
import Checkbox from "@/components/ui/Checkbox";
import ErrorMessage from "@/components/ui/ErrorMessage";
import SpinnerMini from "@/components/ui/SpinnerMini";
import DaySlotGroup from "./DaySlotGroup";

function TemplateForm({ templateToEdit = {}, closeSheet }) {
  const { id: templateId } = templateToEdit;
  const isEdit = Boolean(templateId);

  // 1) Hooks
  const router = useRouter();
  const { user } = useAuth();
  const {
    form,
    fieldArray,

    // states
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
  } = useTemplateForm({ template: templateToEdit });
  const {
    register,
    handleSubmit,
    setValue,
    trigger,
    formState: { errors, isSubmitted, isSubmitting },
    setError,
  } = form;
  const { fields, replace } = fieldArray;

  // 2) Handlers
  async function handleApplyAll(e) {
    const val = e.target.checked;
    if (val && uniqueSelectedDays.length > 0) {
      const sourceSlots = currentSlots.filter((f) => f.weekDay === firstDay);

      const newSlots = [];

      uniqueSelectedDays.forEach((day) => {
        sourceSlots.forEach((s) => {
          newSlots.push({
            weekDay: day,
            startTime: s.startTime,
            endTime: s.endTime,
          });
        });
      });

      replace(newSlots);
      if (isSubmitted) await trigger("slots");
    }
    setApplyToAll(val);
  }

  async function handleTimeChange(e, periodIndex, time) {
    if (applyToAll) {
      const val = e.target.value;
      uniqueSelectedDays.forEach((day, dayIdx) => {
        if (dayIdx > 0) {
          const daySlots = currentSlots.filter((f) => f.weekDay === day);
          if (daySlots[periodIndex]) {
            setValue(
              `slots.${daySlots[periodIndex].originalIndex}.${time}`,
              val,
            );
          }
        }
      });
    }
    if (isSubmitted) await trigger("slots");
  }

  async function onSubmit(data) {
    try {
      if (isEdit) await updateScheduleTemplate(data, user?.sub, templateId);
      else await createScheduleTemplate(data, user?.sub);

      closeSheet?.();
      router.refresh();
      toast.success(`Template ${isEdit ? "Updated" : "Created"} successfully`);
    } catch (err) {
      console.error(err);
      setError("root", { message: "Something went wrong, Try again" });
    }
  }

  // 3) Elements to render
  const renderGroups =
    applyToAll && groupedFields?.length > 0
      ? [groupedFields[0]]
      : groupedFields;

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="flex h-full flex-col overflow-hidden"
    >
      {/* Form Scrollable Content */}
      <div className="no-scrollbar flex-1 overflow-y-auto py-4">
        <div className="space-y-6 p-4">
          <FormInput
            label="Template Name"
            placeholder="e.g. Regular Schedule"
            required
            {...register("name")}
            error={errors?.name?.message}
            disabled={isSubmitting}
          />

          {/* Days of week */}
          <div>
            <FormLabel label="Working Days" required />
            <div className="flex flex-wrap gap-2 overflow-auto">
              {DAYS_OF_WEEK.map((day, index) => {
                const isSelected = fields.some((f) => f.weekDay === index);
                return (
                  <button
                    key={day}
                    type="button"
                    className={`${isSelected ? "border-primary bg-primary/10 text-primary" : "bg-surface-overlay/75 hover:bg-surface-overlay border-border text-text-muted"} min-w-[50px] flex-1 cursor-pointer rounded-md border py-2 text-center transition-colors`}
                    onClick={() => handleToggle(index)}
                    disabled={isSubmitting}
                  >
                    {day.slice(0, 3)}
                  </button>
                );
              })}
            </div>
            <p
              className={`mt-2 text-xs ${slotsError ? "text-danger" : "text-text-muted"}`}
            >
              {slotsError || "Click to select working days"}
            </p>
          </div>

          <hr className="border-border" />

          {/* Generated Time for each day */}
          {uniqueSelectedDays.length > 0 && (
            <div>
              <FormLabel label="Working Hours" />
              <Checkbox
                label="Apply same hours to all selected days"
                checked={applyToAll}
                onChange={handleApplyAll}
                disabled={isSubmitting}
              />
              <div className="mt-5">
                <div className="space-y-6">
                  {renderGroups?.map((group) => (
                    <DaySlotGroup
                      key={`${applyToAll ? "all" : group?.dayNum}`}
                      group={group}
                      title={applyToAll ? "All Selected Days" : group?.dayName}
                      register={register}
                      errors={errors}
                      isSubmitting={isSubmitting}
                      onRemovePeriod={handleRemovePeriod}
                      onAddPeriod={() => handleAddPeriod(group.dayNum)}
                      onTimeChange={handleTimeChange}
                    />
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      <SheetFooter className="border-border shrink-0 border-t pt-4">
        <Button
          variation="ghost"
          type="button"
          onClick={handleReset}
          disabled={isSubmitting}
        >
          Reset
        </Button>
        <div>
          <Button className="w-full" type="submit" disabled={isSubmitting}>
            {!isSubmitting && !isEdit && "Create Template"}
            {!isSubmitting && isEdit && "Edit Template"}
            {isSubmitting && <SpinnerMini />}
          </Button>
          {errors?.root?.message && (
            <ErrorMessage
              message={errors?.root?.message}
              className="text-center"
            />
          )}
        </div>
      </SheetFooter>
    </form>
  );
}

export default TemplateForm;
