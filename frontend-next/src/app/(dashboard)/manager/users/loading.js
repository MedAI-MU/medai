import SkeletonBox from "@/components/ui/SkeletonBox";
import TableSkeleton from "@/components/ui/TableSkeleton";

export default function UsersLoading() {
  return (
    <div className="space-y-6 pt-6">
      <SkeletonBox className="h-10 w-96 rounded-lg" />
      <TableSkeleton columns="0.4fr 0.5fr 1.5fr 0.5fr 0.7fr 1fr" rows={5} />
    </div>
  );
}
