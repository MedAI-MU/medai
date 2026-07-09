import { Suspense } from "react";

import DoctorsList from "@/components/doctor/DoctorsList";
import DoctorsListSkeleton from "@/components/doctor/DoctorsListSkeleton";
import Heading from "@/components/ui/Heading";
import SearchBar from "@/components/ui/SearchBar";

async function AllDoctorsPage({ searchParams }) {
  const query = (await searchParams)?.search || "";

  return (
    <>
      <Heading
        className="mb-10"
        title="All Doctors"
        subtitle="Manage doctor specialties, schedules, and appointments."
      />
      <SearchBar
        queryKey="search"
        placeholder="Search by name or speciality..."
      />
      <Suspense key={query} fallback={<DoctorsListSkeleton />}>
        <DoctorsList
          query={query}
          actionLabel="Manage"
          basePath="/secretary/doctors"
        />
      </Suspense>
    </>
  );
}

export default AllDoctorsPage;
