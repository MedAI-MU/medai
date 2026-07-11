import Image from "next/image";
import heroImg from "@/assets/hero-image.jpeg";
import Button from "@/components/ui/Button";
import Container from "@/components/ui/Container";
import Section from "./Section";

function Hero() {
  return (
    <Section id="#">
      <Container>
        <div className="grid items-center gap-12 md:grid-cols-2">
          <div className="space-y-6">
            <h1 className="text-text-base text-4xl leading-tight font-bold md:text-5xl lg:text-6xl">
              Your Health, Our Priority
            </h1>
            <p className="text-text-muted text-lg leading-relaxed md:text-xl">
              Experience the future of healthcare with AI-powered diagnostics,
              personalized treatment plans, and 24/7 access to expert medical
              professionals.
            </p>

            <div className="flex flex-wrap gap-4 pt-4">
              <Button href="/auth/login">Book Appointment</Button>
              <Button variation="secondary" href="#doctors">
                Find a Doctor
              </Button>
            </div>
          </div>

          <div className="relative aspect-video overflow-hidden rounded-2xl shadow-2xl">
            <Image
              src={heroImg}
              className="object-cover"
              placeholder="blur"
              fill
              alt="Doctor image"
            />
          </div>
        </div>
      </Container>
    </Section>
  );
}

export default Hero;
