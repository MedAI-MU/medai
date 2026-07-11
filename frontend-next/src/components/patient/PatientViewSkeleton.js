import SkeletonBox from "@/components/ui/SkeletonBox";
import Grid from "@/components/ui/Grid";

function PatientViewSkeleton() {
  return (
    <div className="flex flex-col gap-8">
      <SkeletonBox className="h-5 w-36" />
      <div className="flex flex-col gap-2">
        <SkeletonBox className="h-8 max-w-80" />
        <SkeletonBox className="h-5 max-w-64" />
      </div>

      <div className="flex flex-col gap-4">
        <div className="border-border no-scrollbar flex gap-1 overflow-x-auto border-b">
          {Array.from({ length: 6 }).map((_, i) => (
            <SkeletonBox key={i} className="h-10 w-24 shrink-0 rounded-none" />
          ))}
        </div>
        <div className="bg-surface border-border rounded-xl border p-8">
          <Grid cols="two" gap="gap-x-12 gap-y-6">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="flex flex-col gap-2">
                <SkeletonBox className="h-3 w-20" />
                <SkeletonBox className="h-5 w-full rounded" />
              </div>
            ))}
          </Grid>
        </div>
      </div>
    </div>
  );
}

export default PatientViewSkeleton;
