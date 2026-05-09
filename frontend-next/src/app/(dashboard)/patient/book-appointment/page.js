import DoctorsList from "@/components/doctor/DoctorsList";
import DoctorsListSkeleton from "@/components/doctor/DoctorsListSkeleton";
import Heading from "@/components/ui/Heading";
import SearchBar from "@/components/ui/SearchBar";
import { Suspense } from "react";

async function Page({ searchParams }) {
  const query = (await searchParams)?.search || "";

  return (
    <div>
      <Heading
        className="mb-10"
        title="All Doctors"
        subtitle="Browse and manage all registered physicians in the network"
      />
      <SearchBar queryKey="search" />
      <Suspense key={query} fallback={<DoctorsListSkeleton />}>
        <DoctorsList query={query} />
      </Suspense>
    </div>
  );
}

export default Page;
