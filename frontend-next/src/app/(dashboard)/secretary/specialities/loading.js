import SkeletonBox from "@/components/ui/SkeletonBox";
import TableSkeleton from "@/components/ui/TableSkeleton";

function Loading() {
  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div className="space-y-2">
          <SkeletonBox className="h-8 w-56 rounded" />
          <SkeletonBox className="h-4 w-80 rounded" />
        </div>
        <SkeletonBox className="h-10 w-36 rounded-lg" />
      </div>
      <TableSkeleton columns="0.5fr 1.5fr 1fr 0.5fr" rows={4} />
    </div>
  );
}

export default Loading;
