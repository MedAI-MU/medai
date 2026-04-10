import { THEMES } from "@/constants/badgeColors";

function IconBadge({ icon, color = "red", isRounded = false }) {
  return (
    <div
      className={`flex w-fit items-center justify-center p-4 ${THEMES[color].text} ${THEMES[color].bg} ${isRounded ? " rounded-full" : "rounded-lg "}`}
    >
      {icon}
    </div>
  );
}

export default IconBadge;
