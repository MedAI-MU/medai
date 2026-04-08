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

// Footer is always dark — pinned colors, not remapping tokens
function Footer() {
  return (
    <footer className="bg-[#1e293b] px-6 py-12">
      <Container>
        <div className="mb-8 grid gap-8 sm:grid-cols-2 lg:grid-cols-4">
          {/* Logo & About */}
          <div className="space-y-4">
            <Logo />
            <p className="text-sm text-slate-400">
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
                  className="block text-sm text-slate-400 transition-colors hover:text-white"
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
                <p key={service} className="text-sm text-slate-400">
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
                <p key={info} className="text-sm text-slate-400">
                  {info}
                </p>
              ))}
            </div>
          </div>
        </div>

        <div className="border-t border-slate-600 pt-8 text-center">
          <Copyright className="text-slate-400" />
        </div>
      </Container>
    </footer>
  );
}

export default Footer;
