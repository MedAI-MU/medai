import { Smartphone, Shield, Zap, Download } from "lucide-react";
import Container from "@/components/ui/Container";
import Button from "@/components/ui/Button";
import LandingHeader from "@/components/landing/LandingHeader";
import Footer from "@/components/landing/Footer";

const APP_URL = process.env.MOBILE_APP_URL;

const features = [
  {
    icon: Shield,
    title: "Secure & Private",
    description:
      "Your health data is encrypted and protected with industry-standard security.",
  },
  {
    icon: Zap,
    title: "Fast Access",
    description:
      "Book appointments, view records, and communicate with doctors instantly.",
  },
  {
    icon: Smartphone,
    title: "On-the-Go",
    description:
      "Manage your healthcare anywhere, anytime from your mobile device.",
  },
];

export const metadata = {
  title: "Download",
  description:
    "Download the MedAI mobile app for Android and manage your healthcare on the go.",
};

export default function DownloadPage() {
  return (
    <div className="min-h-dvh">
      <LandingHeader />

      <main>
        {/* Hero */}
        <section className="to-surface bg-linear-to-b from-blue-50 px-6 py-20 md:py-32 dark:from-blue-950/20">
          <Container>
            <div className="mx-auto max-w-3xl text-center">
              <div className="mb-6 inline-flex items-center gap-2 rounded-full bg-blue-100 px-4 py-1.5 text-sm font-medium text-blue-700 dark:bg-blue-900/30 dark:text-blue-300">
                <Smartphone size={16} />
                Mobile App
              </div>
              <h1 className="text-text-base mb-4 text-4xl leading-tight font-extrabold md:text-5xl lg:text-6xl">
                MedAI Mobile App
              </h1>
              <p className="text-text-muted mb-10 text-lg leading-relaxed md:text-xl">
                Take control of your healthcare journey with the MedAI mobile
                app. Book appointments, access medical records, get AI-powered
                insights, and stay connected with your doctors — all from your
                phone.
              </p>
              <Button
                variation="primary"
                size="lg"
                href={APP_URL}
                className="inline-flex items-center gap-2"
              >
                <Download size={20} />
                Download for Android
              </Button>
              <p className="text-text-subtle mt-4 text-xs">
                Direct download via Firebase App Distribution
              </p>
            </div>
          </Container>
        </section>

        {/* Features */}
        <section className="bg-surface px-6 py-20 md:py-24">
          <Container>
            <div className="mx-auto mb-16 max-w-2xl text-center">
              <h2 className="text-text-base mb-3 text-3xl font-bold">
                Why Use the MedAI App?
              </h2>
              <p className="text-text-muted text-lg">
                Designed to make healthcare management effortless and
                accessible.
              </p>
            </div>
            <div className="mx-auto grid max-w-5xl gap-8 md:grid-cols-3">
              {features.map(({ icon: Icon, title, description }) => (
                <div
                  key={title}
                  className="border-border/50 bg-surface-overlay/30 rounded-xl border p-6 text-center transition-shadow hover:shadow-md"
                >
                  <div className="mx-auto mb-4 flex size-14 items-center justify-center rounded-xl bg-blue-50 text-blue-600 dark:bg-blue-900/20">
                    <Icon size={28} />
                  </div>
                  <h3 className="text-text-base mb-2 text-lg font-semibold">
                    {title}
                  </h3>
                  <p className="text-text-muted text-sm leading-relaxed">
                    {description}
                  </p>
                </div>
              ))}
            </div>
          </Container>
        </section>

        {/* Download CTA */}
        <section className="bg-blue-600 px-6 py-20 md:py-24">
          <Container>
            <div className="mx-auto max-w-2xl text-center">
              <h2 className="mb-4 text-3xl font-bold text-white">
                Ready to Get Started?
              </h2>
              <p className="mb-8 text-lg text-blue-100">
                Download the MedAI app now and experience healthcare reimagined.
              </p>
              <Button
                variation="secondary"
                size="lg"
                href={APP_URL}
                className="border-white bg-white text-blue-600 hover:bg-blue-50"
                startIcon={<Download size={20} />}
              >
                Download Now
              </Button>
            </div>
          </Container>
        </section>
      </main>

      <Footer />
    </div>
  );
}
