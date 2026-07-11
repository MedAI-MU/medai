import Heading from "@/components/ui/Heading";
import AddItemDialog from "@/components/ui/AddItemDialog";
import ChronicDiseaseForm from "@/components/patient/ChronicDiseaseForm";
import EmptyState from "@/components/ui/EmptyState";
import Timeline from "@/components/ui/Timeline";
import TimelineItem from "@/components/ui/TimelineItem";
import TimelineCard from "@/components/ui/TimelineCard";
import ActionButtons from "@/components/ui/ActionButtons";
import EditAction from "@/components/ui/EditAction";
import DeleteAction from "@/components/ui/DeleteAction";
import { deleteChronicDisease } from "@/services/client/patient";

function ChronicDiseasesTab({ data, readOnly = false }) {
  const { chronicDiseases = [], userId } = data || {};
  const hasChronicDisease = chronicDiseases?.length > 0;

  return (
    <>
      <Heading
        Tag="h2"
        size="lg"
        title="Chronic Diseases"
        subtitle="Timeline of diagnosed long-term conditions"
        hideSubtitleOnMobile
        rowOnMobile
      >
        {!readOnly && (
          <AddItemDialog
            title="Add chronic disease"
            description="Enter the details of the chronic disease to add it to your medical records."
            form={<ChronicDiseaseForm patientId={userId} />}
          >
            Add Chronic Disease
          </AddItemDialog>
        )}
      </Heading>
      {!hasChronicDisease && (
        <EmptyState
          title="No chronic diseases recorded"
          description={
            readOnly
              ? "This patient has no chronic diseases recorded."
              : "Adding chronic conditions helps doctors understand your medical history and provide better care."
          }
        />
      )}
      {hasChronicDisease && (
        <Timeline
          items={chronicDiseases}
          sortBy="diagnosisDate"
          render={(item) => (
            <TimelineItem key={item.id} date={item.diagnosisDate}>
              <TimelineCard
                title={item.name}
                description={item.description}
                date={item.diagnosisDate}
                dateDescription="Diagnosed at"
              >
                {!readOnly && (
                  <ActionButtons>
                    <EditAction
                      title="Edit Chronic Disease"
                      description="Update your chronic disease details to ensure your medical history remains accurate."
                      form={
                        <ChronicDiseaseForm
                          patientId={userId}
                          ChronicToEdit={item}
                        />
                      }
                    />
                    <DeleteAction
                      title="Delete Chronic Disease"
                      description="This action cannot be undone. This will permanently remove this chronic disease from your medical records."
                      onConfirm={() => deleteChronicDisease(userId, item.id)}
                      successMessage="Chronic disease has been deleted successfully"
                      failMessage="Failed to delete chronic disease"
                    />
                  </ActionButtons>
                )}
              </TimelineCard>
            </TimelineItem>
          )}
        />
      )}
    </>
  );
}

export default ChronicDiseasesTab;
