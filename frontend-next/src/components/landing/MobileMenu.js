import Link from "next/link";
import Button from "../ui/Button";

function MobileMenu({ navLinks }) {
  return (
    <div className="mt-4 space-y-3 pb-4 md:hidden">
      {navLinks.map((link) => (
        <Link
          key={link.text}
          href={link.to}
          className="text-primary-dark block w-full py-2 text-left"
        >
          {link.text}
        </Link>
      ))}

      <div className="flex flex-col gap-2 pt-3 text-center">
        <Button variation="secondary" href="/auth/login">
          Login
        </Button>
        <Button href="/auth/signup">Sign Up</Button>
      </div>
    </div>
  );
}

export default MobileMenu;
