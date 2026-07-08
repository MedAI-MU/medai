"use client";

import { Stethoscope } from "lucide-react";
import { deleteSpeciality } from "@/services/client/doctors";

import Table from "@/components/ui/Table";
import Badge from "@/components/ui/Badge";
import EmptyState from "@/components/ui/EmptyState";
import EditAction from "@/components/ui/EditAction";
import DeleteButtonIcon from "@/components/ui/DeleteButtonIcon";
import DeleteDialog from "@/components/ui/DeleteDialog";
import AddEditSpecialityForm from "./AddEditSpecialityForm";

export default function SpecialitiesTable({ specialities }) {
  if (!specialities?.length) {
    return (
      <EmptyState
        icon={<Stethoscope />}
        heading="No specialities yet"
        description="Specialities help organize doctors by their field of expertise. Add your first one to get started."
      />
    );
  }

  return (
    <Table columns="0.5fr 1.5fr 1fr 0.5fr">
      <Table.Header>
        <div>ID</div>
        <div>Name</div>
        <div>Created At</div>
        <div>Actions</div>
      </Table.Header>
      <Table.Body
        data={specialities}
        render={(speciality) => (
          <Table.Row key={speciality.id}>
            <div className="text-text-muted font-mono text-sm">
              {speciality.id}
            </div>
            <div>
              <Badge color="blue" text={speciality.name} />
            </div>
            <div className="text-text-muted text-sm">
              {speciality.createdAt
                ? new Date(speciality.createdAt).toLocaleDateString()
                : "-"}
            </div>
            <div className="flex items-center gap-2">
              <EditAction
                title="Edit Speciality"
                description="Update the speciality name."
                form={<AddEditSpecialityForm speciality={speciality} />}
              />
              <DeleteDialog
                title="Delete Speciality"
                description={`Are you sure you want to delete "${speciality.name}"? This action cannot be undone.`}
                onConfirm={() => deleteSpeciality(speciality?.id)}
                successMessage="Speciality deleted successfully"
                failMessage="Failed to delete speciality"
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
