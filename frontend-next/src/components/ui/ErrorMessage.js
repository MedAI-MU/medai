import { cn } from "@/lib/utils";

function ErrorMessage({ message, className }) {
  return (
    <p className={cn("text-danger mt-1 pl-1 text-sm font-semibold", className)}>
      {message}
    </p>
  );
}

export default ErrorMessage;
