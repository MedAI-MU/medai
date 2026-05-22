import { cn } from "@/lib/utils";

function ErrorMessage({ message, withBg = false, className }) {
  return (
    <p
      className={cn(
        "text-danger mt-1 pl-1 text-xs font-semibold",
        withBg && "bg-danger-muted rounded-sm px-2 py-1",
        className,
      )}
    >
      {message}
    </p>
  );
}

export default ErrorMessage;
