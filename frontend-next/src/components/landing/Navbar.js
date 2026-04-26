import Link from "next/link";

function Navbar({ NAV_LINKS }) {
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
