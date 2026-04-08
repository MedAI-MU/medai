import Image from "next/image";
import Container from "../ui/Container";
import Logo from "../ui/Logo";
import Section from "./Section";

function About() {
  return (
    <Section id="about" bgColor="bg-surface-bg">
      <Container>
        <div className="grid items-center gap-12 md:grid-cols-2">
          <div>
            <h2 className="mb-6 text-3xl font-bold text-text-base md:text-4xl">
              About MedAI
            </h2>
            <p className="mb-6 text-lg text-text-muted">
              MedAI is revolutionizing healthcare through artificial intelligence
              and cutting-edge technology. Our mission is to make quality
              healthcare accessible to everyone, anywhere, at any time.
            </p>
            <p className="mb-6 text-lg text-text-muted">
              We combine the expertise of world-class medical professionals with
              AI-powered diagnostics to provide personalized treatment plans and
              continuous health monitoring.
            </p>
            <div className="flex items-start gap-3 rounded-lg bg-primary/10 p-4">
              <Logo />
              <div>
                <h4 className="mb-1 font-semibold text-text-base">
                  Our Mission
                </h4>
                <p className="text-sm text-text-muted">
                  Empowering better health outcomes through innovative technology
                  and compassionate care.
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
