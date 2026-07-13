import { getAllPatients } from "@/services/server/patient";
import Heading from "@/components/ui/Heading";
import PatientsList from "@/components/secretary/PatientsList";

export const metadata = {
  title: "Patients",
  description: "View and manage all patients in the system.",
};

export default async function SecretaryPatientsPage() {
  const patients = await getAllPatients();

  return (
    <div className="space-y-8">
      <Heading
        title="Patients"
        subtitle="View and manage all patients in the system."
      />
      <PatientsList patients={patients} />
    </div>
  );
}
