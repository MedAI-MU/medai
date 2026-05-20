import PaginationSkeleton from "../ui/PaginationSkeleton";
import ScheduleTemplateCardSkeleton from "./ScheduleTemplateCardSkeleton";

function ScheduleTemplateListSkeleton() {
  return (
    <>
      <div className="mt-10 flex flex-col gap-6">
        {Array.from({ length: 6 }, (_, i) => (
          <ScheduleTemplateCardSkeleton key={i} />
        ))}
      </div>
      <PaginationSkeleton className="mt-14" />
    </>
  );
}

export default ScheduleTemplateListSkeleton;
