import MedicalStateCard from "@/components/patient/MedicalStateCard";
import { Activity, AlertTriangle, HeartCrack, Users } from "lucide-react";
import Grid from "../ui/Grid";

function MedicalStates({ data }) {
  return (
    <Grid>
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
    </Grid>
  );
}

export default MedicalStates;
