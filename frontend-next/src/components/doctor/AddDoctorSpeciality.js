"use client";

import AddButton from "../ui/AddButton";
import FormDialog from "../ui/FormDialog";
import AddEditDoctorSpecialityForm from "./AddEditDoctorSpecialityForm";

function AddDoctorSpeciality({ doctorId, allSpecialities }) {
  return (
    <FormDialog
      title="Add Speciality"
      description="Assign a medical speciality to this doctor."
      form={
        <AddEditDoctorSpecialityForm
          doctorId={doctorId}
          allSpecialities={allSpecialities}
        />
      }
    >
      <AddButton>Add Speciality</AddButton>
    </FormDialog>
  );
}

export default AddDoctorSpeciality;
