import { Suspense } from "react";
import { notFound } from "next/navigation";
import { DoctorInfoProvider } from "@/contexts/DoctorInfoContext";

import { getDoctorById } from "@/services/server/doctors";
import AddEditTemplate from "@/components/schedule/AddEditTemplate";
import ScheduleTemplateList from "@/components/schedule/ScheduleTemplateList";
import ScheduleTemplateListSkeleton from "@/components/schedule/ScheduleTemplateListSkeleton";
import Heading from "@/components/ui/Heading";
import SearchBar from "@/components/ui/SearchBar";
import BackButton from "@/components/ui/BackButton";

async function WorkingHoursPage({ searchParams, params }) {
  const { templateName, pageNo } = (await searchParams) || {};
  const doctorId = Number((await params)?.doctorId);

  let doctor;
  try {
    doctor = await getDoctorById(doctorId);
  } catch (err) {
    if (err.statusCode === 404) notFound();
    throw err;
  }

  return (
    <DoctorInfoProvider doctorInfo={{ doctorId }}>
      <div className="space-y-8">
        <BackButton title="Back to Doctor" />

        <Heading
          title={`${doctor.name || "Doctor"}'s Working Hours`}
          subtitle="Manage recurring weekly patterns and apply them to generate bookable slots."
          hideSubtitleOnMobile
          rowOnMobile
        >
          <AddEditTemplate />
        </Heading>

        <SearchBar
          queryKey="templateName"
          placeholder="Search by template name"
        />

        <Suspense
          key={JSON.stringify({ templateName, pageNo })}
          fallback={<ScheduleTemplateListSkeleton />}
        >
          <ScheduleTemplateList
            name={templateName}
            pageNo={pageNo}
            doctorId={doctorId}
          />
        </Suspense>
      </div>
    </DoctorInfoProvider>
  );
}

export default WorkingHoursPage;
