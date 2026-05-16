import AddEditTemplate from "@/components/schedule/AddEditTemplate";
import Button from "@/components/ui/Button";
import Heading from "@/components/ui/Heading";

function WorkingHoursPage() {
  return (
    <>
      <Heading
        title="Working Hours"
        subtitle="Define your recurring weekly patterns, then apply them to generate bookable slots."
      >
        <AddEditTemplate>
          <Button className="shrink-0">New Template</Button>
        </AddEditTemplate>
      </Heading>
    </>
  );
}

export default WorkingHoursPage;
