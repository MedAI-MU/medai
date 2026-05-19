import Card from "@/components/ui/Card";
import SkeletonBox from "@/components/ui/SkeletonBox";

function ScheduleTemplateCardSkeleton() {
  return (
    <Card className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      {/* Left */}
      <div className="flex flex-1 flex-col gap-3">
        {/* Title */}
        <SkeletonBox className="h-5 w-40" />

        {/* Meta */}
        <div className="flex gap-4">
          <SkeletonBox className="h-4 w-32" />
          <SkeletonBox className="h-4 w-20" />
        </div>

        {/* Days */}
        <div className="flex flex-wrap gap-1.5">
          {Array.from({ length: 7 }).map((_, i) => (
            <SkeletonBox key={i} className="h-6 w-12 rounded-md" />
          ))}
        </div>
      </div>

      {/* Right Actions */}
      <div className="border-border flex items-center gap-2 border-t pt-4 sm:border-none sm:pt-0">
        <SkeletonBox className="h-10 w-28" />

        <SkeletonBox className="size-10 rounded-lg" />
        <SkeletonBox className="size-10 rounded-lg" />
      </div>
    </Card>
  );
}

export default ScheduleTemplateCardSkeleton;
