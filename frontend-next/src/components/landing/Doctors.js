import Container from "../ui/Container";
import DoctorCard from "../doctor/DoctorCard";
import DoctorCardSkeleton from "../doctor/DoctorCardSkeleton";
import Section from "./Section";
import SectionTitle from "./SectionTitle";
import { getTopRatedDoctors } from "@/services/server/doctors";

async function Doctors() {
  let topDoctors = [];

  try {
    topDoctors = await getTopRatedDoctors();
  } catch (err) {
    console.error("Failed to fetch top-rated doctors:", err);
  }

  return (
    <Section id="doctors">
      <Container>
        <SectionTitle
          title="Meet Our Top Rated Doctors"
          subTitle="Experienced professionals ready to care for you"
        />

        <div className="grid gap-8 md:grid-cols-3">
          {topDoctors.length === 0
            ? Array.from({ length: 3 }).map((_, i) => (
                <DoctorCardSkeleton key={i} />
              ))
            : topDoctors.map((doctor) => (
                <DoctorCard
                  key={doctor.userId}
                  doctor={doctor}
                  basePath="/auth/login"
                  actionLabel="Login to Book"
                />
              ))}
        </div>
      </Container>
    </Section>
  );
}

export default Doctors;
