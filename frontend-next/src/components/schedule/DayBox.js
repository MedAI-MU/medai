import { cn } from "@/lib/utils";
import { motion } from "framer-motion";

function DayBox({ children, className }) {
  return (
    <motion.div
      className={cn(
        "bg-surface-overlay/30 border-border space-y-4 rounded-xl border p-4",
        className,
      )}
      initial={{ scale: 0, opacity: 0 }}
      animate={{ scale: 1, opacity: 1 }}
      transition={{
        type: "spring",
        stiffness: 900,
        damping: 25,
      }}
    >
      {children}
    </motion.div>
  );
}

export default DayBox;
