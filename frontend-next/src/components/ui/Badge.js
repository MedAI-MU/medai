import { THEMES } from "@/constants/badgeColors";

function Badge({ text, color = "red", className = "" }) {
  return (
    <span
      className={`${THEMES[color].text} ${THEMES[color].bg} rounded-full px-2 py-0.5 text-[10px] font-bold tracking-wider uppercase ${className}`}
    >
      {text}
    </span>
  );
}

export default Badge;
