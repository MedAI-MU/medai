import SkeletonBox from "../ui/SkeletonBox";

function ScheduleGridSkeleton({ className = "" }) {
  return (
    <div
      className={`no-scrollbar bg-surface border-border mt-4 overflow-hidden overflow-x-auto rounded-lg border shadow-sm ${className}`}
    >
      <div className="flex min-w-[1000px] flex-col">
        {/* Header Skeleton */}
        <div className="border-border bg-surface/50 grid grid-cols-7 border-b backdrop-blur-xs">
          {Array.from({ length: 7 }).map((_, i) => (
            <div
              key={i}
              className="border-border flex flex-col items-center justify-center border-r p-4 text-center select-none last:border-r-0"
            >
              <SkeletonBox className="mb-2 h-3.5 w-12" />
              <SkeletonBox className="h-8 w-8 rounded-full" />
            </div>
          ))}
        </div>

        {/* Body Column Skeletons */}
        <div className="bg-surface divide-border grid min-h-[600px] grid-cols-7 divide-x">
          {Array.from({ length: 7 }).map((_, i) => {
            // Alternating slot skeleton counts for visual realism
            const slotCount = i % 3 === 0 ? 2 : i % 2 === 0 ? 1 : 0;
            return (
              <div key={i} className="flex flex-1 flex-col space-y-3 p-3">
                {slotCount === 0 ? (
                  <div className="border-border text-text-subtle bg-surface-bg/10 flex h-32 items-center justify-center rounded-lg border-2 border-dashed p-4 text-center text-sm font-medium select-none">
                    <SkeletonBox className="h-4 w-16" />
                  </div>
                ) : (
                  Array.from({ length: slotCount }).map((_, j) => (
                    <div
                      key={j}
                      className="bg-surface border-border border-l-border flex flex-col gap-3 rounded-sm border border-l-4 p-3 shadow-xs"
                    >
                      <SkeletonBox className="h-4 w-24" />
                      <SkeletonBox className="h-5 w-16 rounded-md" />
                    </div>
                  ))
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

export default ScheduleGridSkeleton;
