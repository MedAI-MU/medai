"use client";

import AddButton from "@/components/ui/AddButton";
import FormSheet from "@/components/ui/FormSheet";
import ScheduleSlotsForm from "./ScheduleSlotsForm";

function CreateScheduleSlots() {
  return (
    <FormSheet title="New Schedule Slots" form={<ScheduleSlotsForm />}>
      <AddButton>Add Slots</AddButton>
    </FormSheet>
  );
}

export default CreateScheduleSlots;
