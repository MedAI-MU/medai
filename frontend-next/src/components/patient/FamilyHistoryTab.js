import Heading from "@/components/ui/Heading";
import Grid from "@/components/ui/Grid";
import EmptyState from "@/components/ui/EmptyState";
import AddItemDialog from "@/components/ui/AddItemDialog";
import FamilyHistoryCard from "./FamilyHistoryCard";
import FamilyHistoryForm from "./FamilyHistoryForm";

function FamilyHistoryTab({ data }) {
  const { familyHistories = [], userId } = data || {};

  const hasHistory = familyHistories?.length > 0;

  return (
    <>
      <Heading
        size="lg"
        title="Family History"
        Tag="h2"
        subtitle="Track health conditions present in your family"
      >
        <AddItemDialog
          title="Add family history"
          description="Enter details about a family member's health condition."
          form={<FamilyHistoryForm patientId={userId} />}
        >
          Add Family History
        </AddItemDialog>
      </Heading>

      {!hasHistory ? (
        <EmptyState
          title="No Family History Recorded"
          description="Adding family medical history helps doctors assess your genetic risks and provide more personalized care."
        />
      ) : (
        <Grid cols="two">
          {familyHistories.map((record) => (
            <FamilyHistoryCard
              key={record.id}
              record={record}
              patientId={userId}
            />
          ))}
        </Grid>
      )}
    </>
  );
}

export default FamilyHistoryTab;
