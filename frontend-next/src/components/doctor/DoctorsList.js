import Grid from "@/components/ui/Grid";
import DoctorCard from "./DoctorCard";
import {
  searchDoctorsByName,
  searchDoctorsBySpeciality,
} from "@/services/server/doctors";
import ErrorState from "../ui/ErrorState";
import EmptyState from "../ui/EmptyState";
import { UserSearch } from "lucide-react";
import AnimateWrapper from "../ui/AnimateWrapper";

async function DoctorsList({ query }) {
  let hasError = false;
  let doctors = [];

  // 1) Returns all doctors with empty string
  if (!query) {
    try {
      doctors = await searchDoctorsByName("");
    } catch (err) {
      hasError = true;
    }
  }

  // 2) Returns search results
  else {
    const results = await Promise.allSettled([
      searchDoctorsByName(query),
      searchDoctorsBySpeciality(query),
    ]);
    const merge = [];

    // Merge two results
    results.forEach((res) => {
      if (res.status === "fulfilled") merge.push(...res.value);
      else hasError = true;
    });

    // Remove duplicates
    doctors = [...new Map(merge.map((doc) => [doc.userId, doc])).values()];
  }

  if (hasError && doctors.length === 0)
    return (
      <ErrorState
        description="We couldn't load the doctors right now. Please check your connection
          or try again in a moment."
      />
    );

  if (doctors.length === 0)
    return (
      <EmptyState
        title="No doctors found"
        icon={<UserSearch size={30} />}
        description={
          <>
            No doctors matched your search for{" "}
            <strong className="text-text-base">&quot;{query}&quot;</strong>. Try
            searching for a different name or speciality
          </>
        }
      />
    );

  return (
    <Grid cols="two" className="mt-10">
      {doctors.map((doctor, idx) => (
        <AnimateWrapper
          key={doctor.userId}
          type="slideUp"
          transitionOptions={{ type: "spring", damping: 15, stiffness: 500 }}
          delay={idx * 0.1}
        >
          <DoctorCard doctor={doctor} />
        </AnimateWrapper>
      ))}
    </Grid>
  );
}

export default DoctorsList;
