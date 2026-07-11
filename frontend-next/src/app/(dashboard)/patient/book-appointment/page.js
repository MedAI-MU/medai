import DoctorsList from "@/components/doctor/DoctorsList";
import DoctorsListSkeleton from "@/components/doctor/DoctorsListSkeleton";
import SpecialityDropdown from "@/components/doctor/SpecialityDropdown";
import Heading from "@/components/ui/Heading";
import SearchBar from "@/components/ui/SearchBar";
import { getSpecialities } from "@/services/server/doctors";
import { Suspense } from "react";

async function BookAppointmentPage({ searchParams }) {
  const { search = "", speciality = "" } = await searchParams;
  const specialities = await getSpecialities();

  return (
    <>
      <Heading
        className="mb-10"
        title="All Doctors"
        subtitle="Explore our network of doctors and book an appointment with ease."
      />
      <div className="flex flex-wrap items-start gap-4">
        <SearchBar queryKey="search" placeholder="Search by name..." />
        <SpecialityDropdown specialities={specialities} />
      </div>
      <Suspense
        key={`${search}-${speciality}`}
        fallback={<DoctorsListSkeleton />}
      >
        <DoctorsList query={search} speciality={speciality} />
      </Suspense>
    </>
  );
}

export default BookAppointmentPage;
