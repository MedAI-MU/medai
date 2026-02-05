import Link from "next/link";

function Navbar({ navLinks }) {
  return (
    <div className="hidden items-center gap-8 md:flex">
      {navLinks.map((link) => (
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
