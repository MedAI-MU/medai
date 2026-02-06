import { Calendar, Clock, Stethoscope } from "lucide-react";
import Section from "./Section";
import SectionTitle from "./SectionTitle";
import ServicesCard from "./ServicesCard";
import Container from "../ui/Container";

function Services() {
  return (
    <Section id="services" bgColor="bg-light-gray">
      <Container>
        <SectionTitle
          title=" Our Services"
          subTitle="Comprehensive healthcare solutions tailored to your needs"
        />

        <div className="grid gap-8 md:grid-cols-3">
          <ServicesCard
            title="Expert Doctors"
            text="Connect with board-certified healthcare professionals specialized in
        various medical fields."
            icon={<Stethoscope size={32} className="text-primary-blue" />}
          />
          <ServicesCard
            title="Easy Booking"
            text="Schedule your appointments in just a few clicks with our intuitive
              booking system."
            icon={<Calendar size={32} className="text-primary-blue" />}
          />
          <ServicesCard
            title="24/7 Support"
            text="Round-the-clock medical assistance and support whenever you need
              it."
            icon={<Clock size={32} className="text-primary-blue" />}
          />
        </div>
      </Container>
    </Section>
  );
}

export default Services;
