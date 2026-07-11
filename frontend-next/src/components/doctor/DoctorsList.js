import Grid from "@/components/ui/Grid";
import DoctorCard from "./DoctorCard";
import {
  searchDoctorsByName,
  searchDoctorsBySpeciality,
} from "@/services/server/doctors";
import ErrorState from "../ui/ErrorState";
import EmptyState from "../ui/EmptyState";
import { UserSearch, UserX } from "lucide-react";
import AnimateWrapper from "../ui/AnimateWrapper";

async function DoctorsList({ query, speciality, ...cardProps }) {
  let hasError = false;
  let doctors = [];

  if (query && speciality) {
    const q = query.toLowerCase();
    try {
      const bySpeciality = await searchDoctorsBySpeciality(speciality);
      doctors = bySpeciality.filter((doc) =>
        doc.name?.toLowerCase().includes(q),
      );
    } catch {
      hasError = true;
    }
  } else if (query) {
    try {
      doctors = await searchDoctorsByName(query);
    } catch {
      hasError = true;
    }
  } else if (speciality) {
    try {
      doctors = await searchDoctorsBySpeciality(speciality);
    } catch {
      hasError = true;
    }
  } else {
    try {
      doctors = await searchDoctorsByName("");
    } catch {
      hasError = true;
    }
  }

  if (hasError && doctors.length === 0)
    return (
      <ErrorState
        description="We couldn't load the doctors right now. Please check your connection
          or try again in a moment."
      />
    );

  if (doctors.length === 0) {
    if (query || speciality)
      return (
        <EmptyState
          title="No doctors found"
          icon={<UserSearch size={30} />}
          description={
            <>
              No doctors matched
              {query && (
                <>
                  {" "}
                  your search for{" "}
                  <strong className="text-text-base">
                    &quot;{query}&quot;
                  </strong>
                </>
              )}
              {query && speciality && <> and</>}
              {speciality && <> the selected speciality</>}. Try different
              search terms or filters.
            </>
          }
        />
      );
    else
      return (
        <EmptyState
          title="No doctors yet"
          icon={<UserX size={30} />}
          description="There is no doctors added to the system yet."
        />
      );
  }

  return (
    <Grid cols="two" className="mt-10">
      {doctors.map((doctor, idx) => (
        <AnimateWrapper
          key={doctor.userId}
          type="slideUp"
          transitionOptions={{ type: "spring", damping: 15, stiffness: 500 }}
          delay={idx * 0.1}
        >
          <DoctorCard doctor={doctor} {...cardProps} />
        </AnimateWrapper>
      ))}
    </Grid>
  );
}

export default DoctorsList;
