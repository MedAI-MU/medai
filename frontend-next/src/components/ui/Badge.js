import { THEMES } from "@/constants/badgeColors";
import { cn } from "@/lib/utils";

function Badge({ text, color = "red", isRounded = true, className = "" }) {
  return (
    <span
      className={cn(
        "px-2 py-1 text-[10px] font-bold tracking-wider uppercase",
        `${THEMES[color].text} ${THEMES[color].bg} ${isRounded ? "rounded-full" : "rounded-sm"}`,
        className,
      )}
    >
      {text}
    </span>
  );
}

export default Badge;
