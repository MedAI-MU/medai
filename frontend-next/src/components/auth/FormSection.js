import Link from "next/link";
import SectionHeader from "../ui/SectionHeader";
import DarkmodeToggler from "../ui/DarkmodeToggler";

function FormSection({ title, subTitle, footer, children }) {
  return (
    <div className="border-border bg-surface relative w-full max-w-2xl rounded-2xl border p-8 shadow-xl sm:p-10">
      {/* Dark mode toggler */}
      <DarkmodeToggler className="bg-surface-overlay border-border hover:text-primary hover:border-primary/50 shadow-glow absolute top-4 right-4 border transition-all duration-300" />

      <SectionHeader as="h2" variant="form" title={title} subTitle={subTitle} />
      {/* Form as children */}
      {children}
      <p className="text-text-muted mt-10 text-center">
        {footer.text}
        <Link
          className="text-primary ml-1 font-bold whitespace-nowrap underline-offset-4 hover:underline"
          href={footer.href}
        >
          {footer.action}
        </Link>
      </p>
    </div>
  );
}

export default FormSection;
