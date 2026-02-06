import Image from "next/image";
import Container from "../ui/Container";
import Logo from "../ui/Logo";
import Section from "./Section";

function About() {
  return (
    <Section id="about" bgColor="bg-light-gray">
      <Container>
        <div className="grid items-center gap-12 md:grid-cols-2">
          <div>
            <h2 className="text-primary-dark mb-6 text-3xl font-bold md:text-4xl">
              About MedAI
            </h2>
            <p className="text-primary-gray mb-6 text-lg">
              MedAI is revolutionizing healthcare through artificial
              intelligence and cutting-edge technology. Our mission is to make
              quality healthcare accessible to everyone, anywhere, at any time.
            </p>
            <p className="text-primary-gray mb-6 text-lg">
              We combine the expertise of world-class medical professionals with
              AI-powered diagnostics to provide personalized treatment plans and
              continuous health monitoring.
            </p>
            <div className="flex items-start gap-3 rounded-lg bg-[#E3F2FD] p-4">
              <Logo />
              <div>
                <h4 className="text-primary-dark mb-1 font-semibold">
                  Our Mission
                </h4>
                <p className="text-primary-gray text-sm">
                  Empowering better health outcomes through innovative
                  technology and compassionate care.
                </p>
              </div>
            </div>
          </div>

          <div className="relative aspect-video overflow-hidden rounded-2xl shadow-xl">
            <Image
              src="/about-image.jfif"
              fill
              alt="Healthcare technology"
              className="object-cover"
            />
          </div>
        </div>
      </Container>
    </Section>
  );
}

export default About;
