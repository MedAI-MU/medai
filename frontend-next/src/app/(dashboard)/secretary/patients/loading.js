import SkeletonBox from "@/components/ui/SkeletonBox";
import TableSkeleton from "@/components/ui/TableSkeleton";

function Loading() {
  return (
    <div className="space-y-8">
      <div className="space-y-2">
        <SkeletonBox className="h-8 w-48 rounded" />
        <SkeletonBox className="h-4 w-72 rounded" />
      </div>
      <TableSkeleton columns="0.5fr 1.5fr 0.7fr 0.7fr 0.7fr 0.5fr" rows={5} />
    </div>
  );
}

export default Loading;
