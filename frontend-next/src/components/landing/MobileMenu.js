import Link from "next/link";
import Button from "../ui/Button";

function MobileMenu({ navLinks }) {
  return (
    <div className="mt-4 space-y-3 pb-4 lg:hidden">
      {navLinks.map((link) => (
        <Link
          key={link.text}
          href={link.to}
          className="text-text-base hover:text-primary block w-full py-2 text-left transition-colors"
        >
          {link.text}
        </Link>
      ))}

      <div className="flex flex-col items-start gap-2 pt-3 text-center">
        <Button variation="secondary" href="/auth/login">
          Login
        </Button>
        <Button href="/auth/signup">Sign Up</Button>
      </div>
    </div>
  );
}

export default MobileMenu;
