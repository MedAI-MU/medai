import SkeletonBox from "@/components/ui/SkeletonBox";

function TabsSkeleton() {
  return (
    <div className="border-border flex gap-6 border-b">
      {Array.from({ length: 3 }).map((_, i) => (
        <SkeletonBox
          key={i}
          className="mb-2 h-5 w-20 rounded"
        />
      ))}
    </div>
  );
}

function AppointmentCardSkeleton() {
  return (
    <div className="border-border bg-surface flex flex-col items-center gap-6 rounded-xl border p-5 shadow-sm md:flex-row md:text-start">
      <SkeletonBox className="h-28 w-24 shrink-0 rounded-xl" />

      <div className="flex-1 space-y-3">
        <SkeletonBox className="h-5 w-48 rounded" />
        <SkeletonBox className="h-4 w-32 rounded" />
        <SkeletonBox className="h-4 w-40 rounded" />
      </div>

      <div className="flex w-full flex-col items-end gap-3 md:w-auto">
        <SkeletonBox className="h-6 w-20 rounded-full" />
        <SkeletonBox className="h-9 w-28 rounded-lg" />
      </div>
    </div>
  );
}

export default function AppointmentsSkeleton({ count = 5 }) {
  return (
    <div className="space-y-4">
      <TabsSkeleton />
      {Array.from({ length: count }).map((_, i) => (
        <AppointmentCardSkeleton key={i} />
      ))}
    </div>
  );
}
