import { Check } from "lucide-react";

function Checkbox({ label, ...props }) {
  return (
    <label className="flex items-center gap-2">
      <div className="relative flex items-center justify-center">
        <input
          type="checkbox"
          className="peer border-border bg-surface hover:border-primary/50 checked:border-primary focus-visible:ring-primary/40 size-5 appearance-none rounded-sm border-2 transition-all duration-150 focus-visible:ring-2 focus-visible:outline-none disabled:cursor-not-allowed disabled:opacity-60"
          {...props}
        />
        <span className="bg-primary pointer-events-none absolute inset-0 flex scale-0 rotate-0 items-center justify-center rounded-sm text-white duration-150 peer-checked:scale-110 peer-checked:rotate-360">
          <Check size={16} />
        </span>
      </div>
      {label && <span className="text-text-base">{label}</span>}
    </label>
  );
}

export default Checkbox;
