import { cn } from "@/lib/utils";
import { getInitials } from "@/lib/utils/stringHelpers";

function UserFallback({ name, className }) {
  const initial = getInitials(name);

  return (
    <div
      className={cn(
        "bg-primary flex size-16 shrink-0 items-center justify-center rounded-full text-white",
        className,
      )}
    >
      {initial}
    </div>
  );
}

export default UserFallback;
