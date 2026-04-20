import Grid from "../ui/Grid";
import SkeletonBox from "../ui/SkeletonBox";

export default function MedicalRecordsSkeleton() {
  return (
    <div className="flex flex-col gap-8">
      {/* heading */}
      <div className="flex flex-col gap-2">
        <SkeletonBox className="h-8 max-w-64" />
        <SkeletonBox className="h-5 max-w-96" />
      </div>

      {/* stat cards */}
      <Grid>
        {Array.from({ length: 4 }).map((_, i) => (
          <div
            key={i}
            className="bg-surface border-border flex flex-col gap-3 rounded-xl border p-5"
          >
            <SkeletonBox className="h-12 w-12 rounded-lg" />
            <SkeletonBox className="h-3 w-3/5" />
            <div className="flex items-baseline gap-2">
              <SkeletonBox className="h-7 w-8" />
              <SkeletonBox className="h-3 w-20" />
            </div>
          </div>
        ))}
      </Grid>

      {/* tabs + form */}
      <div className="flex flex-col gap-4">
        <div className="border-border no-scrollbar flex gap-1 overflow-x-auto border-b">
          {Array.from({ length: 5 }).map((_, i) => (
            <SkeletonBox key={i} className="h-10 w-24 shrink-0 rounded-none" />
          ))}
        </div>
        <div className="bg-surface border-border flex flex-col gap-6 rounded-xl border p-8">
          <SkeletonBox className="h-6 max-w-48" />
          <Grid cols="two" gap="gap-x-12 gap-y-6">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="flex flex-col gap-2">
                <SkeletonBox className="h-3 w-20" />
                <SkeletonBox className="h-11 w-full rounded-lg" />
              </div>
            ))}
          </Grid>
          <div className="border-border flex justify-end gap-3 border-t pt-6">
            <SkeletonBox className="h-10 w-20 rounded-lg" />
            <SkeletonBox className="h-10 w-28 rounded-lg" />
          </div>
        </div>
      </div>
    </div>
  );
}
