import { Suspense } from "react";
import About from "@/components/landing/About";
import Contact from "@/components/landing/Contact";
import Doctors from "@/components/landing/Doctors";
import DoctorCardSkeleton from "@/components/doctor/DoctorCardSkeleton";
import Footer from "@/components/landing/Footer";
import LandingHeader from "@/components/landing/LandingHeader";
import Hero from "@/components/landing/Hero";
import Services from "@/components/landing/Services";
import Container from "@/components/ui/Container";
import Section from "@/components/landing/Section";
import SectionTitle from "@/components/landing/SectionTitle";

function DoctorsFallback() {
  return (
    <Section id="doctors">
      <Container>
        <SectionTitle
          title="Meet Our Top Rated Doctors"
          subTitle="Experienced professionals ready to care for you"
        />
        <div className="grid gap-8 md:grid-cols-3">
          <DoctorCardSkeleton />
          <DoctorCardSkeleton />
          <DoctorCardSkeleton />
        </div>
      </Container>
    </Section>
  );
}

export default function Landing() {
  return (
    <div className="min-h-dvh">
      <LandingHeader />
      <Hero />
      <Services />
      <Suspense fallback={<DoctorsFallback />}>
        <Doctors />
      </Suspense>
      <About />
      <Contact />
      <Footer />
    </div>
  );
}
