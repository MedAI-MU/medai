import { getDoctorById, getSpecialities } from "@/services/server/doctors";

import Heading from "@/components/ui/Heading";
import BackButton from "@/components/ui/BackButton";
import AddDoctorSpeciality from "@/components/doctor/AddDoctorSpeciality";
import DoctorSpecialitiesTable from "@/components/doctor/DoctorSpecialitiesTable";
import EmptyState from "@/components/ui/EmptyState";
import Button from "@/components/ui/Button";

export default async function DoctorSpecialitiesPage({ params }) {
  const { doctorId } = await params;

  const [doctor, allSpecialities] = await Promise.all([
    getDoctorById(doctorId),
    getSpecialities(),
  ]);

  const systemHasSpecialities = allSpecialities?.length > 0;

  return (
    <div className="space-y-8">
      <BackButton title="Back to Doctor" />

      <Heading
        title="Doctor Specialities"
        subtitle={`Managing specialities for ${doctor?.name || "the doctor"}`}
        rowOnMobile
      >
        {systemHasSpecialities && (
          <AddDoctorSpeciality
            doctorId={doctorId}
            allSpecialities={allSpecialities}
          />
        )}
      </Heading>

      {systemHasSpecialities ? (
        <DoctorSpecialitiesTable
          doctorId={doctorId}
          specialities={doctor?.specialities || []}
          allSpecialities={allSpecialities}
        />
      ) : (
        <EmptyState
          title="System has no specialities yet."
          description="There is no specialities on the system, you need to add specialities first."
        >
          <Button href="/secretary/specialities">
            Add System Specialities
          </Button>
        </EmptyState>
      )}
    </div>
  );
}
