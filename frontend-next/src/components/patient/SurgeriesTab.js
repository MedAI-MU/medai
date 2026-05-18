import Heading from "@/components/ui/Heading";
import AddItemDialog from "@/components/ui/AddItemDialog";
import SurgeriesForm from "@/components/patient/SurgeriesForm";
import EmptyState from "@/components/ui/EmptyState";
import Timeline from "@/components/ui/Timeline";
import TimelineItem from "@/components/ui/TimelineItem";
import { deleteSurgery } from "@/services/client/patient";
import TimelineCard from "@/components/ui/TimelineCard";
import ActionButtons from "@/components/ui/ActionButtons";
import EditAction from "@/components/ui/EditAction";
import DeleteAction from "@/components/ui/DeleteAction";

function SurgeriesTab({ data }) {
  const { surgeries = [], userId } = data || {};
  const hasSurgeries = surgeries?.length > 0;

  return (
    <>
      <Heading
        Tag="h2"
        size="lg"
        title="Surgeries"
        subtitle="Timeline of your past surgical procedures"
        hideSubtitleOnMobile
        rowOnMobile
      >
        <AddItemDialog
          title="Add surgery"
          description="Enter the details of surgery to add it to your medical records."
          form={<SurgeriesForm patientId={userId} />}
        >
          Add Surgery
        </AddItemDialog>
      </Heading>
      {!hasSurgeries && (
        <EmptyState
          title="No surgeries recorded"
          description="Adding your surgical history helps doctors understand your medical background and provide better care."
        />
      )}
      {hasSurgeries && (
        <Timeline
          items={surgeries}
          sortBy="date"
          render={(item) => (
            <TimelineItem key={item.id} date={item.date}>
              <TimelineCard
                title={item.name}
                description={item.description}
                date={item.date}
                dateDescription="Performed at"
              >
                <ActionButtons>
                  <EditAction
                    title="Edit Surgery"
                    description="Update your surgery details to ensure your medical history remains accurate."
                    form={
                      <SurgeriesForm patientId={userId} surgeryToEdit={item} />
                    }
                  />
                  <DeleteAction
                    title="Delete Surgery"
                    description="This action cannot be undone. This will permanently remove this surgery from your medical records."
                    onConfirm={() => deleteSurgery(userId, item.id)}
                    successMessage="Surgery has been deleted successfully"
                    failMessage="Failed to delete surgery"
                  />
                </ActionButtons>
              </TimelineCard>
            </TimelineItem>
          )}
        />
      )}
    </>
  );
}

export default SurgeriesTab;
