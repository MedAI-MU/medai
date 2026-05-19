import AddEditTemplate from "@/components/schedule/AddEditTemplate";
import ScheduleTemplateList from "@/components/schedule/ScheduleTemplateList";
import ScheduleTemplateListSkeleton from "@/components/schedule/ScheduleTemplateListSkeleton";
import Heading from "@/components/ui/Heading";
import SearchBar from "@/components/ui/SearchBar";
import { Suspense } from "react";

async function WorkingHoursPage({ searchParams }) {
  const { templateName, pageNo } = (await searchParams) || {};

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

      <Suspense
        key={JSON.stringify({ templateName, pageNo })}
        fallback={<ScheduleTemplateListSkeleton />}
      >
        <ScheduleTemplateList name={templateName} pageNo={pageNo} />
      </Suspense>
    </>
  );
}

export default WorkingHoursPage;
