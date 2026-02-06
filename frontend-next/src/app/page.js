import About from "@/components/landing/About";
import Contact from "@/components/landing/Contact";
import Doctors from "@/components/landing/Doctors";
import Footer from "@/components/landing/Footer";
import Header from "@/components/landing/Header";
import Hero from "@/components/landing/Hero";
import Services from "@/components/landing/Services";

export default function Landing() {
  return (
    <div className="min-h-screen bg-white">
      <Header />
      <Hero />
      <Services />
      <Doctors />
      <About />
      <Contact />
      <Footer />
    </div>
  );
}
