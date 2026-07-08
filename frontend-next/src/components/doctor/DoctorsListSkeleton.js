import Grid from "@/components/ui/Grid";
import DoctorCardSkeleton from "./DoctorCardSkeleton";

function DoctorsListSkeleton() {
  return (
    <Grid cols="two" className="mt-10">
      {Array.from({ length: 6 }).map((_, idx) => (
        <DoctorCardSkeleton key={idx} />
      ))}
    </Grid>
  );
}

export default DoctorsListSkeleton;
