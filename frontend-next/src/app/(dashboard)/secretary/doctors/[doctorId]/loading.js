import SkeletonBox from "@/components/ui/SkeletonBox";
import Card from "@/components/ui/Card";

function Loading() {
  return (
    <div className="space-y-8">
      <div className="space-y-3">
        <SkeletonBox className="h-4 w-32 rounded" />
        <SkeletonBox className="h-8 w-64 rounded" />
        <SkeletonBox className="h-4 w-96 rounded" />
      </div>

      <Card className="flex flex-col gap-6 md:flex-row md:items-center">
        <SkeletonBox className="size-28 shrink-0 rounded-full" />
        <div className="flex-1 space-y-3">
          <SkeletonBox className="h-6 w-48 rounded" />
          <SkeletonBox className="h-4 w-32 rounded" />
          <SkeletonBox className="h-4 w-full rounded" />
        </div>
      </Card>

      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
        {Array.from({ length: 4 }).map((_, i) => (
          <Card key={i} className="space-y-4">
            <SkeletonBox className="size-12 rounded-lg" />
            <SkeletonBox className="h-5 w-36 rounded" />
            <SkeletonBox className="h-4 w-full rounded" />
            <SkeletonBox className="h-4 w-3/4 rounded" />
            <SkeletonBox className="h-10 w-24 rounded" />
          </Card>
        ))}
      </div>
    </div>
  );
}

export default Loading;
