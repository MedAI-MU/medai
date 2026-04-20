"use client";

function ButtonIcon({ children, onClick, className = "" }) {
  return (
    <button
      className={`text-text-muted hover:bg-surface-overlay rounded-lg p-2 transition-colors active:scale-95 ${className}`}
      onClick={onClick}
    >
      {children}
    </button>
  );
}

export default ButtonIcon;
