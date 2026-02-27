import { BriefcaseMedical, UserRound } from "lucide-react";

const ROLES = [
  { role: "doctor", icon: <BriefcaseMedical /> },
  { role: "patient", icon: <UserRound /> },
];

function RoleSelector({ value, setValue, className = "" }) {
  return (
    <div
      className={`bg-primary-blue/10 flex rounded-3xl p-1 ${className}`}
      role="radiogroup"
    >
      {ROLES.map(({ role, icon }) => (
        <Item
          key={role}
          role={role}
          icon={icon}
          selected={value}
          onChange={() => setValue(role)}
        />
      ))}
    </div>
  );
}

function Item({ role, icon, selected, onChange }) {
  const isActive = role === selected;
  return (
    <button
      role="radio"
      aria-checked={isActive}
      onClick={onChange}
      className={`flex flex-1 cursor-pointer items-center justify-center gap-2 rounded-2xl px-4 py-2.5 text-sm font-medium capitalize transition-all ${isActive ? "bg-primary-blue text-white" : "text-primary-blue bg-transparent"}`}
    >
      <span className="flex h-4 w-4 items-center justify-center">{icon}</span>
      {role}
    </button>
  );
}

export default RoleSelector;
