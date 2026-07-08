import { notFound } from "next/navigation";

import { getPatientById } from "@/services/server/patient";

import Heading from "@/components/ui/Heading";
import BackButton from "@/components/ui/BackButton";
import MedicalStates from "@/components/patient/MedicalStates";
import PatientReadOnlyView from "@/components/secretary/PatientReadOnlyView";

async function PatientDetailPage({ params }) {
  const { patientId } = await params;

  let patient;
  try {
    patient = await getPatientById(patientId);
  } catch (err) {
    if (err.statusCode === 404) notFound();
    throw err;
  }

  return (
    <div className="flex flex-col gap-8">
      <BackButton title="Back to Patients" />
      <Heading
        title={`${patient?.name || "Patient"}'s Medical Records`}
        subtitle="Manage patient health information."
      />
      <MedicalStates data={patient} />
      <PatientReadOnlyView data={patient} />
    </div>
  );
}

export default PatientDetailPage;
