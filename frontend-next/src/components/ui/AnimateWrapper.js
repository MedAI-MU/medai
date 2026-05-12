"use client";

import { motion } from "framer-motion";

const variants = {
  fade: {
    initial: { opacity: 0 },
    animate: { opacity: 1 },
  },

  scale: {
    initial: { opacity: 0, scale: 0 },
    animate: { opacity: 1, scale: 1 },
  },

  slideUp: {
    initial: { opacity: 0, y: 20 },
    animate: { opacity: 1, y: 0 },
  },
};
export default function AnimateWrapper({
  children,
  type = "fade", // fade | scale | slide
  delay = 0,
  duration = 0.3,
  transitionOptions = {},
  once = true,
}) {
  const v = variants[type];
  return (
    <motion.div
      initial={v.initial}
      whileInView={v.animate}
      transition={{
        duration,
        delay,
        ...transitionOptions,
      }}
      viewport={{ once }}
    >
      {children}
    </motion.div>
  );
}
