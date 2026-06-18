import { Suspense } from "react";
import AddEditTemplate from "@/components/schedule/AddEditTemplate";
import ScheduleTemplateList from "@/components/schedule/ScheduleTemplateList";
import ScheduleTemplateListSkeleton from "@/components/schedule/ScheduleTemplateListSkeleton";
import Heading from "@/components/ui/Heading";
import SearchBar from "@/components/ui/SearchBar";

async function WorkingHoursPage({ searchParams }) {
  const { templateName, pageNo } = (await searchParams) || {};

  return (
    <div className="space-y-8">
      <Heading
        title="Working Hours"
        subtitle="Define your recurring weekly patterns, then apply them to generate bookable slots."
        hideSubtitleOnMobile
        rowOnMobile
      >
        <AddEditTemplate />
      </Heading>

      <SearchBar
        queryKey="templateName"
        placeholder="Search by template name"
      />

      <Suspense
        key={JSON.stringify({ templateName, pageNo })}
        fallback={<ScheduleTemplateListSkeleton />}
      >
        <ScheduleTemplateList name={templateName} pageNo={pageNo} />
      </Suspense>
    </div>
  );
}

export default WorkingHoursPage;
