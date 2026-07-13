import { notFound } from "next/navigation";

import { getPatientById } from "@/services/server/patient";
import { getPatientScans } from "@/services/server/scans";
import { getPatientDiagnoses } from "@/services/server/diagnosis";
import Heading from "@/components/ui/Heading";
import BackButton from "@/components/ui/BackButton";
import PatientRecordView from "@/components/patient/PatientRecordView";

export async function generateMetadata({ searchParams }) {
  const { patientUserId } = await searchParams;
  if (!patientUserId) return { title: "Patient Record" };
  try {
    const patient = await getPatientById(patientUserId);
    return {
      title: `${patient?.name || "Patient"}'s Medical Record`,
      description: "Manage patient health information, scans, and reports.",
    };
  } catch {
    return { title: "Patient Record" };
  }
}

export default async function SecretaryAppointmentDetailPage({
  params,
  searchParams,
}) {
  const { appointmentId } = await params;
  const { patientUserId } = await searchParams;

  if (!patientUserId) notFound();

  let patient, scans, diagnoses;
  try {
    [patient, scans, diagnoses] = await Promise.all([
      getPatientById(patientUserId),
      getPatientScans(patientUserId),
      getPatientDiagnoses(patientUserId),
    ]);
  } catch (err) {
    if (err?.statusCode === 404) notFound();
    throw err;
  }

  return (
    <div className="flex flex-col gap-8">
      <BackButton title="Back to Appointments" />
      <Heading
        title={`${patient?.name || "Patient"}'s Medical Record`}
        subtitle="Manage patient health information, scans, and reports."
      />
      <PatientRecordView
        patient={patient}
        scans={scans}
        diagnoses={diagnoses}
        role="secretary"
        appointmentId={Number(appointmentId)}
        patientUserId={Number(patientUserId)}
      />
    </div>
  );
}
