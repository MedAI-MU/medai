"use client";

import { Check, X } from "lucide-react";
import { removeDoctorSpeciality } from "@/services/client/doctors";

import Table from "@/components/ui/Table";
import Badge from "@/components/ui/Badge";
import EmptyState from "@/components/ui/EmptyState";
import EditAction from "@/components/ui/EditAction";
import DeleteButtonIcon from "@/components/ui/DeleteButtonIcon";
import DeleteDialog from "@/components/ui/DeleteDialog";
import AddEditDoctorSpecialityForm from "./AddEditDoctorSpecialityForm";

export default function DoctorSpecialitiesTable({
  doctorId,
  specialities,
  allSpecialities,
}) {
  if (!specialities?.length) {
    return (
      <EmptyState
        icon={<Badge color="blue" text="MD" />}
        heading="No specialities assigned"
        description="This doctor hasn't been assigned any specialities yet. Add their areas of expertise."
      />
    );
  }

  return (
    <Table columns="0.5fr 1.5fr 0.5fr 0.5fr 0.5fr">
      <Table.Header>
        <div>id</div>
        <div>Speciality</div>
        <div>Primary</div>
        <div>Years</div>
        <div>Actions</div>
      </Table.Header>
      <Table.Body
        data={specialities}
        render={(item) => (
          <Table.Row key={item.id}>
            <div>{item?.id}</div>
            <div>
              <Badge color="blue" text={item.speciality?.name} />
            </div>
            <div>
              {item.isPrimary ? (
                <Check size={18} className="text-success" />
              ) : (
                <X size={18} className="text-text-subtle" />
              )}
            </div>
            <div className="text-text-base text-sm">
              {item.yearsOfExperience}
            </div>
            <div className="flex items-center gap-2">
              <EditAction
                title="Edit Speciality"
                description="Update years of experience and primary status."
                form={
                  <AddEditDoctorSpecialityForm
                    doctorId={doctorId}
                    speciality={item}
                    allSpecialities={allSpecialities}
                  />
                }
              />
              <DeleteDialog
                title="Remove Speciality"
                description={`Are you sure you want to remove "${item.speciality?.name}" from this doctor?`}
                onConfirm={() => removeDoctorSpeciality(doctorId, item?.id)}
                successMessage="Speciality removed successfully"
                failMessage="Failed to remove speciality"
              >
                <DeleteButtonIcon />
              </DeleteDialog>
            </div>
          </Table.Row>
        )}
      />
    </Table>
  );
}
