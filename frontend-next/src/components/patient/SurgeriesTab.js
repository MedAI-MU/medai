import Heading from "@/components/ui/Heading";
import AddItemDialog from "@/components/ui/AddItemDialog";
import SurgeriesForm from "@/components/patient/SurgeriesForm";
import EmptyState from "@/components/ui/EmptyState";
import Timeline from "@/components/ui/Timeline";
import TimelineItem from "@/components/ui/TimelineItem";
import { deleteSurgery } from "@/services/client/patient";

function SurgeriesTab({ data }) {
  const { surgeries, userId } = data || {};
  const hasSurgeries = surgeries?.length > 0;
  const sortedSurgeries = surgeries.sort(
    (a, b) => new Date(b.date) - new Date(a.date),
  );
  console.log(data);

  return (
    <>
      <Heading
        Tag="h2"
        size="lg"
        title="Surgeries"
        subtitle="Timeline of diagnosed long-term conditions"
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
          description="Adding surgeries conditions helps doctors understand your medical history and provide better care."
        />
      )}
      {hasSurgeries && (
        <Timeline
          items={sortedSurgeries}
          render={(item) => (
            <TimelineItem
              key={item.id}
              title={item.name}
              description={item.description}
              date={item.date}
              dateDescription="Performed at"
              editTitle="Edit Surgery"
              editDescription="Edit your surgery to be precise to help doctor know your diagnose."
              editForm={
                <SurgeriesForm patientId={userId} surgeryToEdit={item} />
              }
              onDelete={() => deleteSurgery(userId, item.id)}
              deleteSuccessMessage="Surgery has been deleted successfully"
              deleteFailMessage="Failed to delete surgery"
            />
          )}
        />
      )}
    </>
  );
}

export default SurgeriesTab;
