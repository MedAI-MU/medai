import IconBadge from "../ui/IconBadge";
import { THEMES } from "@/constants/badgeColors";

function MedicalStateCard({ title, value, color = "red", icon }) {
  return (
    <div className="bg-surface border-border rounded-xl border p-5 shadow-md">
      <div className="mb-2 flex items-center justify-between">
        <IconBadge icon={icon} color={color} />
      </div>
      <p className="text-text-muted text-sm font-medium capitalize">{title}</p>
      <div className="flex items-baseline gap-2">
        <p className="text-text-base text-2xl font-bold">{value}</p>
        <p
          className={`text-xs font-bold tracking-wider ${THEMES[color].text} uppercase`}
        >
          {value > 0 ? "recorded" : "not-recorded"}
        </p>
      </div>
    </div>
  );
}

export default MedicalStateCard;
