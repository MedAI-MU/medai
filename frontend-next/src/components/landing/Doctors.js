import Container from "../ui/Container";
import DoctorCard from "../ui/DoctorCard";
import Section from "./Section";
import SectionTitle from "./SectionTitle";

const doctors = [
  {
    name: "Dr. Sarah Johnson",
    specialty: "Cardiologist",
    rating: 4.9,
    image:
      "https://images.unsplash.com/photo-1706565029539-d09af5896340?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmZW1hbGUlMjBkb2N0b3IlMjBwb3J0cmFpdCUyMHByb2Zlc3Npb25hbHxlbnwxfHx8fDE3Njk5NTE5NzN8MA&ixlib=rb-4.1.0&q=80&w=1080",
  },
  {
    name: "Dr. Michael Chen",
    specialty: "Dermatologist",
    rating: 4.8,
    image:
      "https://images.unsplash.com/photo-1605504836193-e77d3d9ede8a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxhc2lhbiUyMGRvY3RvciUyMHBvcnRyYWl0fGVufDF8fHx8MTc2OTk1MTk3NHww&ixlib=rb-4.1.0&q=80&w=1080",
  },
  {
    name: "Dr. James Wilson",
    specialty: "Pediatrician",
    rating: 4.9,
    image:
      "https://images.unsplash.com/photo-1615177393114-bd2917a4f74a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtYWxlJTIwZG9jdG9yJTIwcG9ydHJhaXQlMjBwcm9mZXNzaW9uYWx8ZW58MXx8fHwxNzY5ODYwOTA4fDA&ixlib=rb-4.1.0&q=80&w=1080",
  },
];

function Doctors() {
  return (
    <Section id="doctors">
      <Container>
        <SectionTitle
          title="Meet Our Doctors"
          subTitle="Experienced professionals ready to care for you"
        />

        <div className="grid gap-8 md:grid-cols-3">
          {doctors.map((doctor, idx) => (
            <DoctorCard key={idx} doctor={doctor} />
          ))}
        </div>
      </Container>
    </Section>
  );
}

export default Doctors;
