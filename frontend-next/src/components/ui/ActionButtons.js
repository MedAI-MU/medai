import { cn } from "@/lib/utils";

function ActionButtons({ position = "top-4 right-4", absolute = true, className, children }) {
  return (
    <div className={cn("flex gap-1", absolute && "absolute", absolute && position, className)}>
      {children}
    </div>
  );
}

export default ActionButtons;
