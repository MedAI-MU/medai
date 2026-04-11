import { Plus } from "lucide-react";
import Button from "@/components/ui/Button";
import Heading from "@/components/ui/Heading";
import Grid from "@/components/ui/Grid";
import AllergyCard from "@/components/patient/AllergyCard";
import EmptyState from "@/components/ui/EmptyState";
import AddEditAllergy from "./AddEditAllergy";

function AllergiesTab({ data }) {
  const { allergies } = data;
  const hasAllergies = allergies?.length > 0;

  return (
    <>
      <div className="flex items-center justify-between">
        <Heading
          size="lg"
          title="Allergies"
          Tag="h2"
          subtitle="Total of 6 recorded sensitivities"
        />
        {hasAllergies && (
          <AddEditAllergy patientId={data?.userId}>
            <Button>
              <Plus />
              <span>Add Allergy</span>
            </Button>
          </AddEditAllergy>
        )}
      </div>
      {!hasAllergies ? (
        <EmptyState
          title="No Allergies Recorded"
          description="Adding allergies helps doctors assess your health risks and provide more personalized care."
        >
          <AddEditAllergy patientId={data?.userId}>
            <Button>
              <Plus />
              <span>Add Allergy</span>
            </Button>
          </AddEditAllergy>
        </EmptyState>
      ) : (
        <Grid cols="two">
          {allergies.map((el) => (
            <AllergyCard key={el.id} allergy={el} patientId={data?.userId} />
          ))}
        </Grid>
      )}
    </>
  );
}

export default AllergiesTab;
