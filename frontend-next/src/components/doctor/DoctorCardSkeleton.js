import Card from "@/components/ui/Card";
import SkeletonBox from "@/components/ui/SkeletonBox";

function DoctorCardSkeleton() {
  return (
    <Card>
      {/* Header */}
      <div className="mb-4 flex flex-col items-center gap-4 text-center sm:flex-row sm:items-start sm:text-start">
        <SkeletonBox className="bg-muted size-16 shrink-0 rounded-full" />
        <div className="flex flex-col items-center gap-2 sm:items-start">
          <SkeletonBox className="bg-muted h-5 w-32 rounded" />
          <SkeletonBox className="bg-muted h-4 w-16 rounded" />
        </div>
      </div>

      {/* Gender */}
      <div className="mb-4 flex items-center gap-2">
        <SkeletonBox className="bg-muted size-4 rounded" />
        <SkeletonBox className="bg-muted h-4 w-24 rounded" />
      </div>

      {/* Specialities */}
      <div className="flex flex-wrap gap-2">
        <SkeletonBox className="bg-muted h-6 w-20 rounded" />
        <SkeletonBox className="bg-muted h-6 w-24 rounded" />
        <SkeletonBox className="bg-muted h-6 w-16 rounded" />
      </div>

      {/* Footer */}
      <footer className="border-border mt-4 flex flex-col gap-3 border-t pt-4">
        <SkeletonBox className="bg-muted h-9 w-full rounded" />
        <SkeletonBox className="bg-muted h-9 w-full rounded" />
      </footer>
    </Card>
  );
}

export default DoctorCardSkeleton;
