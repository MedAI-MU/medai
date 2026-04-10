import MedicalStateCard from "@/components/patient/MedicalStateCard";
import { Activity, AlertTriangle, HeartCrack, Users } from "lucide-react";

function MedicalStates({ data }) {
  return (
    <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
      <MedicalStateCard
        title="allergies"
        color="orange"
        icon={<AlertTriangle />}
        value={data?.allergies?.length || 0}
      />
      <MedicalStateCard
        title="choronic diseases"
        color="red"
        icon={<HeartCrack />}
        value={data?.chronicDiseases?.length || 0}
      />
      <MedicalStateCard
        title="surgeries"
        color="blue"
        icon={<Users />}
        value={data?.surgeries?.length || 0}
      />
      <MedicalStateCard
        title="family history"
        color="green"
        icon={<Activity />}
        value={data?.familyHistories?.length || 0}
      />
    </div>
  );
}

export default MedicalStates;
