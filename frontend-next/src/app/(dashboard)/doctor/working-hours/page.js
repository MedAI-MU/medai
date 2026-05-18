import AddEditTemplate from "@/components/schedule/AddEditTemplate";
import ScheduleTemplateList from "@/components/schedule/ScheduleTemplateList";
import Heading from "@/components/ui/Heading";
import SearchBar from "@/components/ui/SearchBar";

async function WorkingHoursPage({ searchParams }) {
  const { templateName } = (await searchParams) || {};

  return (
    <>
      <Heading
        title="Working Hours"
        subtitle="Define your recurring weekly patterns, then apply them to generate bookable slots."
        hideSubtitleOnMobile
        rowOnMobile
      >
        <AddEditTemplate />
      </Heading>
      <SearchBar queryKey="templateName" className="mt-10" />
      <ScheduleTemplateList name={templateName} />
    </>
  );
}

export default WorkingHoursPage;
