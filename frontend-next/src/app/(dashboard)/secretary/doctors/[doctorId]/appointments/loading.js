import SkeletonBox from "@/components/ui/SkeletonBox";
import AppointmentsSkeleton from "@/components/appointments/AppointmentsSkeleton";

function Loading() {
  return (
    <div className="space-y-8">
      <SkeletonBox className="mb-4 h-4 w-32 rounded" />
      <SkeletonBox className="h-8 w-64 rounded" />
      <AppointmentsSkeleton count={5} />
    </div>
  );
}

export default Loading;
