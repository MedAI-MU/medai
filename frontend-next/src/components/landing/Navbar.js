import Link from "next/link";

const NAV_LINKS = [
  { text: "Home", to: "#" },
  { text: "Services", to: "#services" },
  { text: "Doctors", to: "#doctors" },
  { text: "About", to: "#about" },
  { text: "Contact", to: "#contact" },
];

function Navbar() {
  return (
    <div className="hidden items-center gap-8 lg:flex">
      {NAV_LINKS.map((link) => (
        <Link
          key={link.text}
          href={link.to}
          className="text-primary-dark transition-opacity hover:opacity-70"
        >
          {link.text}
        </Link>
      ))}
    </div>
  );
}

export default Navbar;
