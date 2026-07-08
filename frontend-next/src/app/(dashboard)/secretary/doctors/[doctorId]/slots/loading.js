import SkeletonBox from "@/components/ui/SkeletonBox";
import ScheduleGridSkeleton from "@/components/schedule/ScheduleGridSkeleton";

function Loading() {
  return (
    <div className="space-y-8">
      <SkeletonBox className="h-4 w-32 rounded" />
      <div className="flex items-center justify-between">
        <div className="space-y-2">
          <SkeletonBox className="h-8 w-64 rounded" />
          <SkeletonBox className="h-4 w-72 rounded" />
        </div>
        <SkeletonBox className="h-10 w-36 rounded-lg" />
      </div>
      <SkeletonBox className="h-10 w-full max-w-md rounded-lg" />
      <ScheduleGridSkeleton />
    </div>
  );
}

export default Loading;
