import { notFound } from "next/navigation";

import { getPatientById } from "@/services/server/patient";
import { getPatientScans } from "@/services/server/scans";
import { getPatientDiagnoses } from "@/services/server/diagnosis";

import Heading from "@/components/ui/Heading";
import BackButton from "@/components/ui/BackButton";
import PatientRecordView from "@/components/patient/PatientRecordView";

export async function generateMetadata({ params }) {
  const { patientId } = await params;
  try {
    const patient = await getPatientById(patientId);
    return {
      title: `${patient?.name || "Patient"}'s Medical Records`,
      description: "Manage patient health information.",
    };
  } catch {
    return { title: "Patient Medical Records" };
  }
}

async function PatientDetailPage({ params }) {
  const { patientId } = await params;

  let patient, scans, diagnoses;
  try {
    [patient, scans, diagnoses] = await Promise.all([
      getPatientById(patientId),
      getPatientScans(patientId),
      getPatientDiagnoses(patientId),
    ]);
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
      <PatientRecordView
        patient={patient}
        scans={scans}
        diagnoses={diagnoses}
        role="secretary"
        patientUserId={Number(patientId)}
      />
    </div>
  );
}

export default PatientDetailPage;
