import Heading from "@/components/ui/Heading";
import MedicalStates from "@/components/patient/MedicalStates";
import { getPatient } from "@/services/server/patient";
import MedicalTabs from "@/components/patient/MedicalTabs";
import ErrorState from "@/components/ui/ErrorState";

async function MedicalRecordsPage() {
  let data = {};
  try {
    data = await getPatient();
  } catch {
    return (
      <ErrorState description="Unable to load patient records at the moment. Please try again later." />
    );
  }

  return (
    <div className="flex flex-col gap-8">
      <Heading
        title="My Medical Records"
        subtitle="Manage and view your comprehensive medical history."
      />

      <MedicalStates data={data} />
      <MedicalTabs data={data} />
    </div>
  );
}

export default MedicalRecordsPage;
