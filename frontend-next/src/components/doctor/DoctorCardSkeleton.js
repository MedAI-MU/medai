import Card from "@/components/ui/Card";
import SkeletonBox from "@/components/ui/SkeletonBox";

function DoctorCardSkeleton() {
  return (
    <Card>
      <div className="mb-4 flex flex-col items-center gap-4 text-center sm:flex-row sm:items-start sm:text-start">
        <SkeletonBox className="size-12 shrink-0 rounded-full" />
        <div className="flex flex-col items-center gap-2 sm:items-start">
          <SkeletonBox className="h-5 w-32 rounded" />
          <SkeletonBox className="h-4 w-16 rounded" />
        </div>
      </div>

      <SkeletonBox className="mb-3 h-3 w-full rounded" />
      <SkeletonBox className="mb-4 h-3 w-3/4 rounded" />

      <div className="flex flex-wrap gap-2">
        <SkeletonBox className="h-6 w-20 rounded" />
        <SkeletonBox className="h-6 w-24 rounded" />
      </div>

      <div className="mt-3 flex items-center gap-2">
        <SkeletonBox className="size-4 rounded" />
        <SkeletonBox className="size-4 rounded" />
        <SkeletonBox className="size-4 rounded" />
        <SkeletonBox className="size-4 rounded" />
        <SkeletonBox className="h-3 w-8 rounded" />
      </div>

      <footer className="border-border mt-4 border-t pt-4">
        <SkeletonBox className="h-10 w-full rounded-lg" />
      </footer>
    </Card>
  );
}

export default DoctorCardSkeleton;
