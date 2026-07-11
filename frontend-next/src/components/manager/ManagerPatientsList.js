"use client";

import { useState } from "react";
import { Search, UserRound, Stethoscope, Trash2 } from "lucide-react";

import Table from "@/components/ui/Table";
import Badge from "@/components/ui/Badge";
import Button from "@/components/ui/Button";
import FormInput from "@/components/ui/FormInput";
import EmptyState from "@/components/ui/EmptyState";
import DeleteDialog from "@/components/ui/DeleteDialog";
import { deleteUser } from "@/services/client/manager";

export default function ManagerPatientsList({ patients = [] }) {
  const [search, setSearch] = useState("");

  const filtered = search?.trim()
    ? patients.filter((p) =>
        p.name?.toLowerCase().includes(search.toLowerCase()),
      )
    : patients;

  if (!patients?.length) {
    return (
      <EmptyState
        icon={<UserRound />}
        heading="No patients yet"
        description="Patients will appear here once they register in the system."
      />
    );
  }

  return (
    <div className="space-y-6">
      <FormInput
        containerClassName="max-w-md"
        placeholder="Search by patient name..."
        startIcon={<Search />}
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      {filtered.length === 0 ? (
        <EmptyState
          icon={<Stethoscope />}
          heading="No patients found"
          description={
            <>
              No patients matched your search for{" "}
              <strong className="text-text-base">&quot;{search}&quot;</strong>.
            </>
          }
        />
      ) : (
        <Table columns="0.5fr 1.5fr 0.7fr 0.7fr 0.7fr 1fr">
          <Table.Header>
            <div>ID</div>
            <div>Name</div>
            <div>Blood</div>
            <div>Height</div>
            <div>Weight</div>
            <div>Actions</div>
          </Table.Header>
          <Table.Body
            data={filtered}
            render={(patient = {}) => (
              <Table.Row key={patient.userId}>
                <div className="text-text-muted font-mono text-sm">
                  {patient.userId}
                </div>
                <div className="font-medium">{patient.name}</div>
                <div>
                  {patient.bloodType ? (
                    <Badge color="red" text={patient.bloodType} />
                  ) : (
                    <span className="text-text-muted text-sm">-</span>
                  )}
                </div>
                <div className="text-text-muted text-sm">
                  {patient.height ? `${patient.height} cm` : "-"}
                </div>
                <div className="text-text-muted text-sm">
                  {patient.weight ? `${patient.weight} kg` : "-"}
                </div>
                <div>
                  <DeleteDialog
                    title="Delete Patient"
                    description={`Are you sure you want to delete ${patient.name}? This action cannot be undone.`}
                    confirmLabel="Delete"
                    successMessage={`${patient.name} has been deleted.`}
                    failMessage="Failed to delete patient."
                    onConfirm={() => deleteUser(patient.userId)}
                  >
                    <Button size="sm" variation="dangerGhost">
                      <Trash2 size={14} />
                    </Button>
                  </DeleteDialog>
                </div>
              </Table.Row>
            )}
          />
        </Table>
      )}
    </div>
  );
}
