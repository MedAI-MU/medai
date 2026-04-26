import Link from "next/link";
import Container from "../ui/Container";
import Logo from "../ui/Logo";
import Copyright from "../ui/Copyright";

const Links = [
  { text: "Home", to: "#" },
  { text: "Services", to: "#services" },
  { text: "Doctors", to: "#doctors" },
  { text: "About", to: "#about" },
  { text: "Contact", to: "#contact" },
];

const Services = [
  "Telemedicine",
  "AI Diagnostics",
  "Health Monitoring",
  "Prescription Management",
];

const ContactInfo = [
  "123 Healthcare Ave",
  "New York, NY 10001",
  "Phone: +1 (555) 123-4567",
  "Email: contact@medai.com",
];

function Footer() {
  return (
    <footer className="bg-primary-dark px-6 py-12">
      <Container>
        <div className="mb-8 grid gap-8 md:grid-cols-4">
          {/* Logo & About */}
          <div className="space-y-4">
            <Logo />
            <p className="text-secondary-gray text-sm">
              Revolutionizing healthcare through AI-powered solutions and expert
              medical care.
            </p>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="mb-4 font-semibold text-white">Quick Links</h4>
            <div className="space-y-2">
              {Links.map((link) => (
                <Link
                  key={link.text}
                  href={link.to}
                  className="text-secondary-gray block text-sm transition-opacity hover:opacity-70"
                >
                  {link.text}
                </Link>
              ))}
            </div>
          </div>

          {/* Services */}
          <div>
            <h4 className="mb-4 font-semibold text-white">Services</h4>
            <div className="space-y-2">
              {Services.map((service) => (
                <p key={service} className="text-secondary-gray text-sm">
                  {service}
                </p>
              ))}
            </div>
          </div>

          {/* Contact Info */}
          <div>
            <h4 className="mb-4 font-semibold text-white">Contact Info</h4>
            <div className="space-y-2">
              {ContactInfo.map((info) => (
                <p key={info} className="text-secondary-gray text-sm">
                  {info}
                </p>
              ))}
            </div>
          </div>
        </div>

        <div className="border-t border-[#4a5568] pt-8 text-center">
          <Copyright className="text-secondary-gray" />
        </div>
      </Container>
    </footer>
  );
}

export default Footer;
