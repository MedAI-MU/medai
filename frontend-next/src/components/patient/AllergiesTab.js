import Heading from "@/components/ui/Heading";
import Grid from "@/components/ui/Grid";
import AllergyCard from "@/components/patient/AllergyCard";
import EmptyState from "@/components/ui/EmptyState";
import AllergyForm from "./AllergyForm";
import AddItemDialog from "../ui/AddItemDialog";

function AllergiesTab({ data }) {
  const { allergies = [] } = data;
  const hasAllergies = allergies?.length > 0;

  return (
    <>
      <Heading
        size="lg"
        title="Allergies"
        Tag="h2"
        subtitle="Total of 6 recorded sensitivities"
      >
        <AddItemDialog
          title="Add new allergy"
          description="Enter the details of the allergy to add it to your medical records."
          form={<AllergyForm patientId={data?.userId} />}
        >
          Add Allergy
        </AddItemDialog>
      </Heading>
      {!hasAllergies ? (
        <EmptyState
          title="No Allergies Recorded"
          description="Adding allergies helps doctors assess your health risks and provide more personalized care."
        />
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
