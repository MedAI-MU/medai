"use client";

import AddButton from "../ui/AddButton";
import FormDialog from "../ui/FormDialog";
import AddSpecialityForm from "./AddEditSpecialityForm";

function AddSpeciality() {
  return (
    <FormDialog
      title="Add Speciality"
      description="Create a new medical speciality."
      form={<AddSpecialityForm />}
    >
      <AddButton>Add Speciality</AddButton>
    </FormDialog>
  );
}

export default AddSpeciality;
