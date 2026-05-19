import SkeletonBox from "@/components/ui/SkeletonBox";

function PaginationSkeleton({ className = "" }) {
  return (
    <div
      className={`border-border bg-surface flex w-full items-center justify-between border-t px-6 py-4 ${className}`}
    >
      {/* Results text */}
      <SkeletonBox className="hidden h-4 w-48 sm:block" />

      {/* Buttons */}
      <div className="flex items-center gap-2">
        <SkeletonBox className="h-8 w-24 rounded-lg" />
        <SkeletonBox className="h-8 w-20 rounded-lg" />
      </div>
    </div>
  );
}

export default PaginationSkeleton;
