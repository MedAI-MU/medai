"use client";

import { useState } from "react";
import { Search, UserRound, Stethoscope, Scan } from "lucide-react";

import Table from "@/components/ui/Table";
import Badge from "@/components/ui/Badge";
import Button from "@/components/ui/Button";
import FormInput from "@/components/ui/FormInput";
import EmptyState from "@/components/ui/EmptyState";

export default function PatientsList({ patients = [] }) {
  const [search, setSearch] = useState("");

  let filtered = patients;

  if (search?.trim())
    filtered = patients?.filter((p) =>
      p.name?.toLowerCase().includes(search.toLowerCase()),
    );

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
        <Table columns="0.5fr 1.5fr 0.7fr 0.7fr 0.7fr 0.6fr">
          <Table.Header>
            <div>ID</div>
            <div>Name</div>
            <div>Blood</div>
            <div>Height</div>
            <div>Weight</div>
            <div>Medical</div>
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
                  <Button
                    href={`/secretary/patients/${patient.userId}`}
                    size="sm"
                    variation="secondary"
                  >
                    View
                  </Button>
                </div>
              </Table.Row>
            )}
          />
        </Table>
      )}
    </div>
  );
}
