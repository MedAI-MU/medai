import { getSpecialities } from "@/services/server/doctors";

import Heading from "@/components/ui/Heading";
import AddSpeciality from "@/components/doctor/AddSpeciality";
import SpecialitiesTable from "@/components/doctor/SpecialitiesTable";

export default async function SpecialitiesPage() {
  const specialities = await getSpecialities();

  return (
    <div className="space-y-8">
      <Heading
        title="Specialities"
        subtitle="Manage medical specialities for the system."
        hideSubtitleOnMobile
        rowOnMobile
      >
        <AddSpeciality />
      </Heading>
      <SpecialitiesTable specialities={specialities} />
    </div>
  );
}
