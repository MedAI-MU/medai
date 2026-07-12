import SkeletonBox from "./SkeletonBox";

function UserInfoSkeleton() {
  return (
    <div className="flex items-center gap-3">
      <div className="hidden md:flex md:flex-col md:items-end">
        <SkeletonBox className="mb-1 h-4 w-14" />
        <SkeletonBox className="h-3 w-20" />
      </div>

      {/* Avatar */}
      <SkeletonBox className="size-9 shrink-0 rounded-full md:size-10" />
    </div>
  );
}

export default UserInfoSkeleton;
