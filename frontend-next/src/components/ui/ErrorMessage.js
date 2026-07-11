"use client";

import { cn } from "@/lib/utils";
import { motion } from "framer-motion";
import { CircleAlert } from "lucide-react";

function ErrorMessage({ message, withBg = false, className }) {
  return (
    <motion.p
      className={cn(
        "text-danger mt-1 flex items-center gap-1 pl-1 text-xs font-normal",
        withBg && "bg-danger-muted rounded-sm px-2 py-1",
        className,
      )}
      initial={{ y: -10, height: 0, opacity: 0 }}
      animate={{ y: 0, height: "auto", opacity: 1 }}
      transition={{ duration: 0.15 }}
    >
      <CircleAlert size={12} />
      {message}
    </motion.p>
  );
}

export default ErrorMessage;
