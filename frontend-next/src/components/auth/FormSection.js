import Link from "next/link";
import SectionHeader from "../ui/SectionHeader";

function FormSection({ title, subTitle, footer, children }) {
  return (
    <div className="shadow-primary/5 border-primary-blue/10 w-full max-w-2xl rounded-2xl border bg-white p-8 shadow-xl sm:p-10">
      <SectionHeader as="h2" variant="form" title={title} subTitle={subTitle} />
      {/* Form as children */}
      {children}
      <p className="mt-10 text-center text-gray-600">
        {footer.text}
        <Link
          className="text-primary-blue ml-1 font-bold whitespace-nowrap underline-offset-4 hover:underline"
          href={footer.href}
        >
          {footer.action}
        </Link>
      </p>
    </div>
  );
}

export default FormSection;
