import { Suspense } from "react";

import DoctorsList from "@/components/doctor/DoctorsList";
import DoctorsListSkeleton from "@/components/doctor/DoctorsListSkeleton";
import Heading from "@/components/ui/Heading";
import SearchBar from "@/components/ui/SearchBar";
import SpecialityDropdown from "@/components/doctor/SpecialityDropdown";
import { getSpecialities } from "@/services/server/doctors";

async function AllDoctorsPage({ searchParams }) {
  const { search = "", speciality = "" } = await searchParams;
  const specialities = await getSpecialities();

  return (
    <>
      <Heading
        className="mb-10"
        title="All Doctors"
        subtitle="Manage doctor specialties, schedules, and appointments."
      />
      <div className="flex flex-wrap items-start gap-4">
        <SearchBar queryKey="search" placeholder="Search by name..." />
        <SpecialityDropdown specialities={specialities} />
      </div>
      <Suspense
        key={`${search}-${speciality}`}
        fallback={<DoctorsListSkeleton />}
      >
        <DoctorsList
          query={search}
          speciality={speciality}
          actionLabel="Manage"
          basePath="/secretary/doctors"
        />
      </Suspense>
    </>
  );
}

export default AllDoctorsPage;
