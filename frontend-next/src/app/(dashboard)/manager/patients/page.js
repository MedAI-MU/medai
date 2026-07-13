import { getAllPatients } from "@/services/server/patient";
import Heading from "@/components/ui/Heading";
import ManagerPatientsList from "@/components/manager/ManagerPatientsList";

export const metadata = {
  title: "Patients",
  description: "View and manage all patients in the system.",
};

export default async function ManagerPatientsPage() {
  const patients = await getAllPatients();

  return (
    <div className="space-y-8">
      <Heading
        title="Patients"
        subtitle="View and manage all patients in the system."
      />
      <ManagerPatientsList patients={patients} />
    </div>
  );
}
