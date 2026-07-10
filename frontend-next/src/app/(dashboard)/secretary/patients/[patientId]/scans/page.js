import { notFound } from "next/navigation";

import { getPatientById } from "@/services/server/patient";
import { getPatientScans } from "@/services/server/scans";

import Heading from "@/components/ui/Heading";
import BackButton from "@/components/ui/BackButton";
import ScansSection from "@/components/scans/ScansSection";

async function PatientScansPage({ params }) {
  const { patientId } = await params;

  let patient;
  let scans;
  try {
    [patient, scans] = await Promise.all([
      getPatientById(patientId),
      getPatientScans(patientId),
    ]);
  } catch (err) {
    if (err.statusCode === 404) notFound();
    throw err;
  }

  return (
    <div className="flex flex-col gap-8">
      <BackButton title="Back to Patients" />
      <Heading
        title={`${patient?.name || "Patient"}'s Scans & Reports`}
        subtitle="View and manage medical scans and reports."
      />
      <ScansSection patientId={patient?.userId} scans={scans || []} />
    </div>
  );
}

export default PatientScansPage;
