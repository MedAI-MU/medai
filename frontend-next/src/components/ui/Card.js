import { cn } from "@/lib/utils";

function Card({ className = "", children }) {
  return (
    <div
      className={cn(
        "border-border bg-surface relative rounded-xl border p-5 shadow-sm transition-all",
        className,
      )}
    >
      {children}
    </div>
  );
}

export default Card;
