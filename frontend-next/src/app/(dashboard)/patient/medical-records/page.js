import Heading from "@/components/ui/Heading";
import MedicalStates from "@/components/patient/MedicalStates";
import { getPatientById } from "@/services/server/patient";
import { getUserFromToken } from "@/lib/session";
import { redirect } from "next/navigation";
import MedicalTabs from "@/components/patient/MedicalTabs";

export const metadata = {
  title: "My Medical Records",
  description: "Manage and view your comprehensive medical history.",
};

async function MedicalRecordsPage() {
  const user = await getUserFromToken();
  if (!user) redirect("/auth/login");

  const data = await getPatientById(user?.sub);

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
