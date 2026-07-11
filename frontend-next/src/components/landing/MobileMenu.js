"use client";

import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import Button from "../ui/Button";
import { createPortal } from "react-dom";

const overlayVariants = {
  hidden: { opacity: 0 },
  visible: { opacity: 1 },
};

const menuVariants = {
  hidden: {
    opacity: 0,
    y: -12,
  },
  visible: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.25,
      ease: "easeOut",
      when: "beforeChildren",
      delayChildren: 0.1,
      staggerChildren: 0.05,
    },
  },
  exit: {
    opacity: 0,
    y: -12,
    transition: {
      duration: 0.2,
      ease: "easeIn",
      when: "afterChildren",
      staggerChildren: 0.03,
      staggerDirection: -1,
    },
  },
};

const itemVariants = {
  hidden: {
    opacity: 0,
    x: -16,
  },
  visible: {
    opacity: 1,
    x: 0,
    transition: {
      duration: 0.2,
      ease: "easeOut",
    },
  },
};

function MobileMenu({ navLinks, isOpen, onClose }) {
  return (
    <AnimatePresence>
      {isOpen && (
        <>
          {createPortal(
            <motion.div
              className="fixed inset-0 bg-black/30 backdrop-blur-sm lg:hidden"
              variants={overlayVariants}
              initial="hidden"
              animate="visible"
              exit="hidden"
              onClick={onClose}
            />,
            document.body,
          )}
          <motion.div
            className="bg-surface border-border absolute inset-x-0 top-full z-50 mt-2 rounded-xl border p-5 shadow-xl lg:hidden"
            variants={menuVariants}
            initial="hidden"
            animate="visible"
            exit="exit"
          >
            <nav className="space-y-1">
              {navLinks.map((link, i) => (
                <motion.div key={link.text} custom={i} variants={itemVariants}>
                  <Link
                    href={link.to}
                    onClick={onClose}
                    className="text-text-base hover:bg-surface-overlay hover:text-primary block rounded-lg px-4 py-2.5 text-sm font-medium transition-colors"
                  >
                    {link.text}
                  </Link>
                </motion.div>
              ))}
            </nav>
            <motion.div
              className="border-border mt-4 flex flex-col gap-2 border-t pt-4"
              custom={navLinks.length}
              variants={itemVariants}
              initial="hidden"
              animate="visible"
            >
              <Button
                variation="secondary"
                href="/auth/login"
                onClick={onClose}
              >
                Login
              </Button>
              <Button href="/auth/signup" onClick={onClose}>
                Sign Up
              </Button>
            </motion.div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}

export default MobileMenu;
