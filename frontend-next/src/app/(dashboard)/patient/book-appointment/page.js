import DoctorsList from "@/components/doctor/DoctorsList";
import DoctorsListSkeleton from "@/components/doctor/DoctorsListSkeleton";
import Heading from "@/components/ui/Heading";
import SearchBar from "@/components/ui/SearchBar";
import { Suspense } from "react";

async function BookAppointmentPage({ searchParams }) {
  const query = (await searchParams)?.search || "";

  return (
    <>
      <Heading
        className="mb-10"
        title="All Doctors"
        subtitle="Explore our network of doctors and book an appointment with ease."
      />
      <SearchBar
        queryKey="search"
        placeholder="Search by name or speciality..."
      />
      <Suspense key={query} fallback={<DoctorsListSkeleton />}>
        <DoctorsList query={query} />
      </Suspense>
    </>
  );
}

export default BookAppointmentPage;
