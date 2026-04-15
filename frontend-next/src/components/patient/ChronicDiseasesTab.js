import Heading from "@/components/ui/Heading";
import AddItemDialog from "@/components/ui/AddItemDialog";
import ChronicDiseaseForm from "@/components/patient/ChronicDiseaseForm";
import EmptyState from "@/components/ui/EmptyState";
import Timeline from "@/components/ui/Timeline";
import TimelineItem from "@/components/ui/TimelineItem";
import { deleteChronicDisease } from "@/services/client/patient";

function ChronicDiseasesTab({ data }) {
  const { chronicDiseases, userId } = data || {};
  const hasChronicDisease = chronicDiseases?.length > 0;
  const sortedChronic = chronicDiseases.sort(
    (a, b) => new Date(b.diagnosisDate) - new Date(a.diagnosisDate),
  );

  return (
    <>
      <Heading
        Tag="h2"
        size="lg"
        title="Chronic Diseases"
        subtitle="Timeline of diagnosed long-term conditions"
      >
        <AddItemDialog
          title="Add chronic disease"
          description="Enter the details of the chronic disease to add it to your medical records."
          form={<ChronicDiseaseForm patientId={userId} />}
        >
          Add Chronic Disease
        </AddItemDialog>
      </Heading>
      {!hasChronicDisease && (
        <EmptyState
          title="No chronic diseases recorded"
          description="Adding chronic conditions helps doctors understand your medical history and provide better care."
        />
      )}
      {hasChronicDisease && (
        <Timeline
          items={sortedChronic}
          render={(item) => (
            <TimelineItem
              key={item.id}
              title={item.name}
              description={item.description}
              date={item.diagnosisDate}
              dateDescription="Diagnosed at"
              editTitle="Edit Chronic Disease"
              editDescription="Edit your chronic disease to be precise to help doctor know your diagnose."
              editForm={
                <ChronicDiseaseForm patientId={userId} ChronicToEdit={item} />
              }
              onDelete={() => deleteChronicDisease(userId, item.id)}
              deleteSuccessMessage="Chrocic disease has been deleted successfully"
              deleteFailMessage="Failed to delete chrocic disease"
            />
          )}
        />
      )}
    </>
  );
}

export default ChronicDiseasesTab;
