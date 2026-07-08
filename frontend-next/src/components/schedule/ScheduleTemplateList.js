import ScheduleTemplateCard from "@/components/schedule/ScheduleTemplateCard";
import { getScheduleTemplates } from "@/services/server/schedule";
import Pagination from "@/components/ui/Pagination";
import { PAGE_SIZE } from "@/constants/pagination";
import AnimateWrapper from "@/components/ui/AnimateWrapper";
import ErrorState from "@/components/ui/ErrorState";
import EmptyState from "@/components/ui/EmptyState";

async function ScheduleTemplateList({ name, pageNo = 1 }) {
  let templates = [];
  let totalCount = 0;
  let currentPage = 1;
  let hasPrevious;
  let hasNext;

  try {
    const response = await getScheduleTemplates(name, PAGE_SIZE, pageNo);
    templates = response.data || [];
    totalCount = response.totalCount;
    currentPage = response.currentPage;
    hasPrevious = response.hasPrevious;
    hasNext = response.hasNext;
  } catch (err) {
    return (
      <ErrorState description="We couldn't load your schedule templates right now. Please check your connection or try again in a moment." />
    );
  }

  if (templates.length === 0 && name)
    return (
      <EmptyState
        title="No results found"
        description={
          <>
            No templates matched your search for{" "}
            <strong className="text-text-base">&quot;{name}&quot;</strong>. Try
            a different name.
          </>
        }
      />
    );

  if (templates.length === 0)
    return (
      <EmptyState
        title="No templates yet"
        description="Create your first schedule template to start managing your working hours."
      />
    );

  return (
    <>
      <div className="flex flex-col gap-6">
        {templates.map((template = {}, i) => (
          <AnimateWrapper
            key={template.id}
            type="slideUp"
            delay={i * 0.06}
            transitionOptions={{ type: "spring", damping: 15, stiffness: 500 }}
            triggerOnView={false}
          >
            <ScheduleTemplateCard template={template} />
          </AnimateWrapper>
        ))}
      </div>
      <Pagination
        totalCount={totalCount}
        currentPage={currentPage}
        hasPrevious={hasPrevious}
        hasNext={hasNext}
        pageSize={PAGE_SIZE}
      />
    </>
  );
}

export default ScheduleTemplateList;
