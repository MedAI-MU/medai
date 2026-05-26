import MedicalStateCard from "@/components/patient/MedicalStateCard";
import { Activity, AlertTriangle, HeartCrack, Users } from "lucide-react";
import Grid from "@/components/ui/Grid";
import AnimateWrapper from "@/components/ui/AnimateWrapper";

function MedicalStates({ data }) {
  const CardsData = [
    {
      title: "allergies",
      color: "orange",
      icon: <AlertTriangle />,
      value: data?.allergies?.length || 0,
    },
    {
      title: "choronic diseases",
      color: "red",
      icon: <HeartCrack />,
      value: data?.chronicDiseases?.length || 0,
    },
    {
      title: "surgeries",
      color: "blue",
      icon: <Users />,
      value: data?.surgeries?.length || 0,
    },
    {
      title: "family history",
      color: "green",
      icon: <Activity />,
      value: data?.familyHistories?.length || 0,
    },
  ];

  return (
    <Grid>
      {CardsData.map(({ title, color, icon, value }, i) => (
        <AnimateWrapper
          key={title}
          type="slideUp"
          delay={i * 0.1}
          transitionOptions={{ type: "spring", damping: 20, stiffness: 900 }}
          triggerOnView={false}
        >
          <MedicalStateCard
            title={title}
            color={color}
            icon={icon}
            value={value}
          />
        </AnimateWrapper>
      ))}
    </Grid>
  );
}

export default MedicalStates;
