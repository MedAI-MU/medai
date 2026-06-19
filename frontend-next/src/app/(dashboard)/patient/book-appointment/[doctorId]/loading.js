import SkeletonBox from "@/components/ui/SkeletonBox";
import Card from "@/components/ui/Card";

export default function Loading() {
  return (
    <div className="space-y-6">
      {/* Back button skeleton */}
      <SkeletonBox className="h-10 w-24 rounded-lg" />

      {/* Doctor Profile Card Skeleton */}
      <Card className="relative overflow-hidden">
        <div className="relative z-10 flex flex-col items-start gap-6 md:flex-row md:items-center">
          <SkeletonBox className="h-24 w-24 shrink-0 rounded-full md:h-28 md:w-28" />
          <div className="w-full flex-1 space-y-4">
            <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
              <div className="space-y-2">
                <SkeletonBox className="h-8 w-48" />
                <SkeletonBox className="h-4 w-32" />
              </div>
              <div className="sm:border-border flex gap-6 sm:border-l sm:pl-6">
                <SkeletonBox className="h-12 w-12" />
                <SkeletonBox className="h-12 w-12" />
              </div>
            </div>
            <div className="border-border mt-4 space-y-2 border-t border-dashed pt-3">
              <SkeletonBox className="h-4 w-full" />
              <SkeletonBox className="h-4 w-3/4" />
            </div>
          </div>
        </div>
      </Card>

      {/* Day Carousel Skeleton */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <SkeletonBox className="h-6 w-32" />
          <div className="flex justify-end gap-2">
            <SkeletonBox className="h-8 w-8 rounded-full" />
            <SkeletonBox className="h-8 w-8 rounded-full" />
          </div>
        </div>

        {/* Day Cards */}
        <div className="no-scrollbar -mx-1 flex gap-3 overflow-hidden px-1 py-2 pb-3">
          {[...Array(6)].map((_, i) => (
            <SkeletonBox key={i} className="h-24 w-24 shrink-0 rounded-xl" />
          ))}
        </div>
      </div>

      {/* Slots Panel Skeleton */}
      <Card className="p-6">
        <div className="border-border mb-6 flex items-center justify-between border-b pb-4">
          <SkeletonBox className="h-6 w-48" />
          <SkeletonBox className="h-4 w-24" />
        </div>
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
          {[...Array(10)].map((_, i) => (
            <SkeletonBox key={i} className="h-10 w-full rounded-lg" />
          ))}
        </div>
      </Card>
    </div>
  );
}
